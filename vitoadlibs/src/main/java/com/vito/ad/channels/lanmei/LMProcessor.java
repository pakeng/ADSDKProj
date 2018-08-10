package com.vito.ad.channels.lanmei;

import android.graphics.Point;
import android.util.Base64;

import com.google.gson.Gson;
import com.vito.ad.base.entity.VideoDetail;
import com.vito.ad.base.interfaces.IAdBaseInterface;
import com.vito.ad.base.interfaces.IUrlBuildInterface;
import com.vito.ad.base.processor.IProcessor;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.channels.lanmei.view.LMLandView;
import com.vito.ad.configs.Constants;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.DownloadTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.video.interfaces.IVideoPlayListener;
import com.vito.utils.Log;
import com.vito.utils.MD5Util;
import com.vito.utils.StringUtil;
import com.vito.utils.network.NetHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashSet;

public class LMProcessor extends IProcessor {
    private static int sortNum = 0;
    private LMAdContent lmAdContent = null;
    private ADTask adTask;
    private DownloadTask downloadTask;

    private static IUrlBuildInterface iUrlBuildInterface  = new IUrlBuildInterface() {
        @Override
        public String build(String srcUrl) {
            //__AZMX__	鼠标或手指按下时相对于素材的X坐标
            // __AZMY__	鼠标或手指按下时相对于素材的Y坐标
            // __AZCX__ 鼠标或手指弹起时相对于素材的X坐标
            // __AZCY__	鼠标或手指弹起时相对于素材的Y坐标
            // __WIDTH__	设备中展示的实际广告位宽度，单位像素
            // __HEIGHT__	设备中展示的实际广告位高度，单位像素
            Point start_point, end_point, size;
            start_point = ViewManager.getInstance().getStart_point();
            end_point = ViewManager.getInstance().getEnd_point();
            size = ViewManager.getInstance().getSize();
            srcUrl = srcUrl.replaceAll("(?i)\\{__AZMX__\\}", start_point.x+"")
                    .replaceAll("(?i)\\{__AZMY__\\}", start_point.y+"")
                    .replaceAll("(?i)\\{__AZCX__\\}", end_point.x+"")
                    .replaceAll("(?i)\\{__AZCY__\\}", end_point.y+"")
                    .replaceAll("(?i)\\{__WIDTH__\\}", size.x+"")
                    .replaceAll("(?i)\\{__HEIGHT__\\}", size.y+"");
            return srcUrl;
        }
    };


    @Override
    public void getAdContent() {
        if (lmAdContent==null)
            return;
        // 重设sortNum
        sortNum = lmAdContent.getSortnum();
        // 生成ADTask
        adTask = new ADTask();
        Gson gson = new Gson();
        String adObjectStr = gson.toJson(lmAdContent);
        adTask.setADObject(adObjectStr);
        adTask.setId(AdTaskManager.getInstance().getNextADID());
        adTask.setOrientation(0); // 使用默认值
        adTask.setLanding_Page(lmAdContent.getVideo_page()); //
        adTask.setType(Config.ADTYPE);

        //处理回调
        LMTracker tracker = lmAdContent.getTracker();
        adTask.setEndCallBackUrls(new HashSet<>(tracker.getPlay_end_trackers()));
        adTask.setVideoStartCallBackUrls(new HashSet<>(tracker.getPlay_start_trackers()));
        adTask.setStartInstallCallBackUrls(new HashSet<>(tracker.getPage_star_install_trackers()));
        adTask.setDownloadStartCallBackUrls(new HashSet<>(tracker.getPage_star_down_trackers()));
        adTask.setDownloadEndCallBackUrls(new HashSet<>(tracker.getPage_down_trackers()));
        adTask.setInstallFinishCallBackUrls(new HashSet<>(tracker.getPage_install_trackers()));
        adTask.setClickCallBackUrls(new HashSet<>(tracker.getPage_click_trackers()));

        // 生成DownloadTask
        downloadTask = new DownloadTask();
        downloadTask.setId(adTask.getId());
        downloadTask.setType(Constants.ADVIDEO);
        downloadTask.setAd_type(Config.ADTYPE);
        downloadTask.setUrl(lmAdContent.getVideo_download_url());
        downloadTask.setPackageName(lmAdContent.getApk_pkg_name());
        downloadTask.setAppName(lmAdContent.getApk_name());
        downloadTask.setmAdname(lmAdContent.getApk_name());
        downloadTask.setPrice(lmAdContent.getApi_price()+"");
        downloadTask.setSortNum(sortNum);
        VideoDetail videoDetail = new VideoDetail(adTask.getId(), 0.0f);
        videoDetail.playTime = 0;
        downloadTask.setVideoDetail(videoDetail);
        try {
            URI uri = new URI(downloadTask.getUrl());
            String name = uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
            downloadTask.setName(name);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            downloadTask.setName(Base64.encodeToString(lmAdContent.getApk_name().getBytes(),Base64.URL_SAFE));
        }

        AdTaskManager.getInstance().pushTask(adTask);
        DownloadTaskManager.getInstance().pushTask(downloadTask);

    }


    public LMProcessor(){
        android.util.Log.e("ADSDK", "LMProcessor 注册");
        // 注册对应的回调方法
        IVideoPlayListener videoPlayerListener = new IVideoPlayListener() {

            @Override
            public void onStart() {
//            ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoStartCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onEnd() {
//            ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getEndCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onFirstQuartile() {
//            ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoFirstQuartileCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onMid() {
//            ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoMidCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onThirdQuartile() {
//            ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoThirdQuartileCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }
        };
        AdTaskManager.getInstance().registerIVideoPlayListener(Config.ADTYPE, videoPlayerListener);
        ViewManager.getInstance().registerLandPageView(Config.ADTYPE, new LMLandView());
        ViewManager.getInstance().registerUrlBuildInterface(Config.ADTYPE, iUrlBuildInterface);
        IAdBaseInterface iAdBaseInterface = new IAdBaseInterface() {
            @Override
            public void onShow() {

            }

            @Override
            public void onClose() {
                //
            }

            @Override
            public void onDownLoadStart() {
//            ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getDownloadStartCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onDownloadEnd() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getDownloadEndCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onDownloadError() {

            }

            @Override
            public void onInstallStart() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getStartInstallCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onInstallFinish() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getInstallCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onClick() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getClickCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }
        };
        AdTaskManager.getInstance().registerIAdBaseInterface(Config.ADTYPE, iAdBaseInterface);
    }


    @Override
    public String buildRequestInfo() {
        String media_key = Config.SECRETKEY;
        String result = null;
        if (StringUtil.isNotEmpty(paramsModel)) {
            JSONObject param = new JSONObject();
            try {
                param.put("lmver","1");//api版本号
                param.put("network", getNetwork(paramsModel.getConnectionType()));//网络类型（0: 无网络1:2G 2:3G  3:4G，4：5G，5：WiFi -1:其他）
                param.put("density",paramsModel.getDensity());//屏幕密度
                param.put("ua",paramsModel.getUa());//取Webview的UA
                param.put("appver", paramsModel.getAppVersion());//当前APP的版本号
                param.put("ip", paramsModel.getIp());//IP
                param.put("jmediakey", media_key);  //开发者Key
                param.put("addr", paramsModel.getAddr()); //地址
                String sdk = paramsModel.getSdk();
                String appPackage = paramsModel.getAppPackage();
                String sign = MD5Util.encrypt(media_key + sdk + appPackage).toUpperCase();
                String imei = paramsModel.getImei();
                String channel = paramsModel.getChannel();
                param.put("sign", sign);//签名
                param.put("sortnum", sortNum);//广告ID
                param.put("imsi", paramsModel.getImsi());
                param.put("imei", imei);
                param.put("model", paramsModel.getModel()); //型号
                param.put("brand", paramsModel.getBrand());//品牌
                param.put("android_id", paramsModel.getAndroidId());//AndroidID
                param.put("sys", paramsModel.getSys()); //Android系统
                param.put("sdk", sdk); //Android版本号
                param.put("package", appPackage); //包名
                param.put("channel", channel);//渠道号
                param.put("memory", paramsModel.getMemeory());//手机内存
                param.put("cpu", paramsModel.getCpu());//手机CPU
                param.put("ratio", paramsModel.getRatio()); //手机分辨率
                param.put("appname", paramsModel.getAppName()); //APP名称
                param.put("screen_orientation", paramsModel.getSo()); //横竖屏 /1竖屏  2横屏
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            try {
                result = NetHelper.doGetHttpResponse("http://api.bulemobi.cn:6001/api/api_request.aspx?param=" + URLEncoder.encode(param.toString(), "UTF-8"), 1);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            //判断返回数据是否返回广告
            if (StringUtil.isNotEmpty(result)) {
                Gson gson = new Gson();
                Log.e("ADTEST", "lm result = "+ result);
                LMAdResponse response = gson.fromJson(result, LMAdResponse.class);
                if (response.getRet_code()!=0){
                    Log.e("ADTEST", "get lm ad failed with result = "+result);
                    return result;
                }

                lmAdContent = response.getAd().get(0);  // 只获取一个广告
            }
        }
        return result;
    }

    @Override
    public ADTask getADTask() {
        return adTask;
    }

    @Override
    public DownloadTask getDownLoadTask() {
        return downloadTask;
    }

    @Override
    protected String buildLandingPage(String originUrl) {
        return null;
    }

    @Override
    public String buildCallBackParams() {
        return null;
    }

    @Override
    public int getType() {
        return Config.ADTYPE;
    }


    /*蓝莓判断网络类型*/
    private int getNetwork(String connectionType){
        int network = -1;
        if(connectionType.equalsIgnoreCase("2G")){
            network = 1;
        }else if(connectionType.equalsIgnoreCase("3G")){
            network = 2;
        }else if(connectionType.equalsIgnoreCase("4G")){
            network = 3;
        }else if(connectionType.equalsIgnoreCase("5G")){
            network = 4;
        }else if(connectionType.equalsIgnoreCase("WIFI")){
            network = 5;
        }
        return network;
    }
}
