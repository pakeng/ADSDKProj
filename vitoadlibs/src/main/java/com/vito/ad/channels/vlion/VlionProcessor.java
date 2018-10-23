package com.vito.ad.channels.vlion;

import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.vito.ad.base.interfaces.IAdBaseInterface;
import com.vito.ad.base.interfaces.IUrlBuildInterface;
import com.vito.ad.base.interfaces.ListenerFactory;
import com.vito.ad.base.processor.IProcessor;
import com.vito.ad.base.task.ADDownloadTask;
import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.channels.vlion.request.VlionRequest;
import com.vito.ad.channels.vlion.response.Conv_tracking;
import com.vito.ad.channels.vlion.response.VlionResponse;
import com.vito.ad.channels.vlion.view.VlionLandView;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.video.interfaces.IVideoPlayListener;
import com.vito.utils.Log;
import com.vito.utils.StringUtil;
import com.vito.utils.network.NetHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class VlionProcessor extends IProcessor {
    private VlionResponse vlionContent = null;
    private ADInfoTask adInfoTask;
    private ADDownloadTask ADDownloadTask;

    private static IUrlBuildInterface iUrlBuildInterface  = new IUrlBuildInterface() {
        @Override
        public String build(String srcUrl) {

            Point start_point, end_point, size;
            start_point = ViewManager.getInstance().getStart_point();
            end_point = ViewManager.getInstance().getEnd_point();
            srcUrl = srcUrl.replaceAll("__CLICK_DOWN_X__", start_point.x+"")
                    .replaceAll("__CLICK_DOWN_Y__", start_point.y+"")
                    .replaceAll("__CLICK_UP_X__", end_point.x+"")
                    .replaceAll("__CLICK_UP_Y__", end_point.y+"");
            return srcUrl;
        }
    };


    @Override
    public void getAdContent() {
        if (vlionContent ==null)
            return;

        // 生成ADTask
        adInfoTask = new ADInfoTask();
        Gson gson = new Gson();
        String adObjectStr = gson.toJson(vlionContent);
        adInfoTask.setADObject(adObjectStr);
        adInfoTask.setId(AdTaskManager.getInstance().getNextADID());
        adInfoTask.setOrientation(0); // 使用默认值

        if (vlionContent.isIs_gdt()){
            adInfoTask.setDownloadApkUrl(vlionContent.getLdp());
        }
        if (vlionContent.getClk_tracking()!=null)
            adInfoTask.setClickCallBackUrls(new HashSet<String>(vlionContent.getClk_tracking()));
        if (vlionContent.getImp_tracking()!=null)
            adInfoTask.setShowCallBackUrls(new HashSet<String>(vlionContent.getImp_tracking()));
        if (vlionContent.getDp_tracking()!=null)
            adInfoTask.setDownloadStartCallBackUrls(new HashSet<String>(vlionContent.getDp_tracking()));
        adInfoTask.setType(Config.ADTYPE);

        if (vlionContent.getConv_tracking()!=null){
            for (Conv_tracking tracking : vlionContent.getConv_tracking()){
                    /**
                     * 5-下载开始
                     *
                     * 6-安装完成
                     *
                     * 7-下载完成
                     *
                     * 8-安装开始
                     *
                     * 9-应用激活
                     */
                    switch (tracking.getTrack_type()){
                        case 5:
                            adInfoTask.getDownloadStartCallBackUrls().add(tracking.getUrl());
                            break;
                        case 6:
                            adInfoTask.getInstallCallBackUrls().add(tracking.getUrl());
                            break;
                        case 7:
                            adInfoTask.getDownloadEndCallBackUrls().add(tracking.getUrl());
                            break;
                        case 8:
                            adInfoTask.getStartInstallCallBackUrls().add(tracking.getUrl());
                            break;
                        case 9:
                            // TODO  激活检查 HOW TODO
                            break;
                    }

            }
        }



//        // 生成DownloadTask
//        ADDownloadTask = new ADDownloadTask();
//        ADDownloadTask.setId(adInfoTask.getId());
//        ADDownloadTask.setType(Constants.ADVIDEO);
//        ADDownloadTask.setAd_type(Config.ADTYPE);
//        ADDownloadTask.setUrl(vlionContent.getVideo_download_url());
//        ADDownloadTask.setPackageName(vlionContent.getApk_pkg_name());
//        ADDownloadTask.setAppName(vlionContent.getApk_name());
//        ADDownloadTask.setmAdname(vlionContent.getApk_name());
//        ADDownloadTask.setPrice(vlionContent.getApi_price()+"");
//        ADDownloadTask.setSortNum(sortNum);
//        VideoDetail videoDetail = new VideoDetail(adInfoTask.getId(), 0.0f);
//        videoDetail.playTime = 0;
//        ADDownloadTask.setVideoDetail(videoDetail);
//        try {
//            URI uri = new URI(ADDownloadTask.getUrl());
//            String name = uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
//            ADDownloadTask.setName(name);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            ADDownloadTask.setName(Base64.encodeToString(vlionContent.getApk_name().getBytes(),Base64.URL_SAFE));
//        }

        AdTaskManager.getInstance().pushTask(adInfoTask);
//        ADDownloadTaskManager.getInstance().pushTask(ADDownloadTask);

    }


    public VlionProcessor(){
        android.util.Log.e("ADSDK", "VlionProcessor 注册");
        // 注册对应的回调方法
        IVideoPlayListener videoPlayerListener = ListenerFactory.getDefaultListener();
        AdTaskManager.getInstance().registerIVideoPlayListener(Config.ADTYPE, videoPlayerListener);
        ViewManager.getInstance().registerLandPageView(Config.ADTYPE, new VlionLandView());
        ViewManager.getInstance().registerUrlBuildInterface(Config.ADTYPE, iUrlBuildInterface);
        IAdBaseInterface iAdBaseInterface = ListenerFactory.getDefaultIAdBaseInterface();
        AdTaskManager.getInstance().registerIAdBaseInterface(Config.ADTYPE, iAdBaseInterface);
    }


    @Override
    public String buildRequestInfo() {
        String result = null;
        if (StringUtil.isNotEmpty(paramsModel)) {

            VlionRequest vlionRequest = new VlionRequest();
            vlionRequest.setTagid(Config.tagid);
            vlionRequest.setAppid(Config.appid);
            vlionRequest.setAdt(1); // 插屏广告
            vlionRequest.setAnid(paramsModel.getAndroidId());
            vlionRequest.setAppname(URLEncoder.encode("点点消宝藏"));
            vlionRequest.setAppversion(paramsModel.getAppVersion());
            vlionRequest.setPkgname(paramsModel.getAppPackage());
            vlionRequest.setCarrier(getCarrierType()); // 运营商
            vlionRequest.setConn(getNetwork(paramsModel.getConnectionType()));
            vlionRequest.setImei(paramsModel.getImei());
            vlionRequest.setOsv(paramsModel.getSys());
            vlionRequest.setIp(NetHelper.getIP());
            vlionRequest.setMake(paramsModel.getBrand());
            vlionRequest.setModel(paramsModel.getModel());
            vlionRequest.setDevicetype(isPad()?2:1);
            vlionRequest.setSh(getScreenPixels(1));
            vlionRequest.setSw(getScreenPixels(0));
            vlionRequest.setUa(URLEncoder.encode(paramsModel.getUa()));
            // 转化
            Gson gson = new Gson();
            String json = gson.toJson(vlionRequest);
            JSONObject jsonObject = null;
            HashMap<String, Object> param = new HashMap<>();
            try {
                jsonObject = new JSONObject(json);
                Iterator<String> stringIterable = jsonObject.keys();
                while (stringIterable.hasNext()){
                    String key = stringIterable.next();
                    param.put(key, jsonObject.get(key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            result = NetHelper.doGetHttpResponse(Config.BASE_URL+NetHelper.getUrlParamsByMap(param), 1);

            //判断返回数据是否返回广告
            if (StringUtil.isNotEmpty(result)) {
                Log.e("ADTEST", "vlion result = "+ result);
                VlionResponse response = gson.fromJson(result, VlionResponse.class);
                if (response.getStatus()==0||response.getStatus()==101||response.getStatus()==500){
                    vlionContent = response;
                    return result;
                }else {
                    Log.e("ADTEST", "get vlion ad failed with result = "+result);
                    return result;
                }
            }
        }
        return result;
    }

    private int getCarrierType() {

        if (paramsModel.getOperator().contentEquals("移动")){
            return 1;
        }else if (paramsModel.getOperator().contentEquals("联通")){
            return 2;
        }else if (paramsModel.getOperator().contentEquals("电信")){
            return 3;
        }else{
            return 0;
        }
    }

    @Override
    public ADInfoTask getADTask() {
        return adInfoTask;
    }

    @Override
    public ADDownloadTask getDownLoadTask() {
        return ADDownloadTask;
    }

    @Override
    protected String buildLandingPage(String originUrl) {
        return originUrl;
    }

    @Override
    public String buildCallBackParams() {
        return null;
    }

    @Override
    public int getType() {
        return Config.ADTYPE;
    }

    private int getNetwork(String connectionType){
        int network = 0;
        if(connectionType.equalsIgnoreCase("2G")){
            network = 2;
        }else if(connectionType.equalsIgnoreCase("3G")){
            network = 3;
        }else if(connectionType.equalsIgnoreCase("4G")){
            network = 4;
        }else if(connectionType.equalsIgnoreCase("WIFI")){
            network = 1;
        }
        return network;
    }

    private boolean isPad() {
        return (AdManager.mContext.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    // 获取分辨率
    private int getScreenPixels(int type){
        DisplayMetrics displayMetrics=new DisplayMetrics();
        AdManager.mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels=displayMetrics.widthPixels;

        return type==0?widthPixels:heightPixels;
    }
}
