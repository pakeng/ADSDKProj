package com.vito.ad.channels.douzi;

import android.util.Base64;

import com.google.gson.Gson;
import com.vito.ad.base.entity.VideoDetail;
import com.vito.ad.base.processor.IProcessor;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.channels.douzi.view.DZLandView;
import com.vito.ad.configs.Constants;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.DownloadTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.video.interfaces.IVideoPlayListener;
import com.vito.utils.Log;
import com.vito.utils.StringUtil;
import com.vito.utils.network.NetHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class DZProcessor extends IProcessor {
    private DZAdContent dzAdContent = null;
    private ADTask adTask;
    private DownloadTask downloadTask;


    @Override
    public void getAdContent() {
        if (dzAdContent ==null)
            return;
        // 生成ADTask
        adTask = new ADTask();
        Gson gson = new Gson();
        String adObjectStr = gson.toJson(dzAdContent);
        adTask.setADObject(adObjectStr);
        adTask.setId(AdTaskManager.getInstance().getNextADID());
        adTask.setOrientation(dzAdContent.getVideo_type());
        adTask.setLanding_Page(dzAdContent.getVideo_page()); //
        adTask.setType(Config.ADTYPE);

        //处理回调

        // 生成DownloadTask
        downloadTask = new DownloadTask();
        downloadTask.setId(adTask.getId());
        downloadTask.setType(Constants.ADVIDEO);
        downloadTask.setAd_type(Config.ADTYPE);
        downloadTask.setUrl(dzAdContent.getVideo_download_url());
        downloadTask.setPackageName(dzAdContent.getApk_pkg_name());
        downloadTask.setAppName(dzAdContent.getApk_name());
        downloadTask.setmAdname(dzAdContent.getApk_name());
        VideoDetail videoDetail = new VideoDetail(adTask.getId(), 0.0f);
        videoDetail.playTime = 0;
        downloadTask.setVideoDetail(videoDetail);
        try {
            URI uri = new URI(downloadTask.getUrl());
            String name = uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
            downloadTask.setName(name);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            downloadTask.setName(Base64.encodeToString(dzAdContent.getApk_name().getBytes(),Base64.URL_SAFE));
        }

        AdTaskManager.getInstance().pushTask(adTask);
        DownloadTaskManager.getInstance().pushTask(downloadTask);



    }

    public DZProcessor(){
        android.util.Log.e("ADSDK", "DZProcessor  注册");
        // 注册对应的回调方法
        IVideoPlayListener videoPlayerListener = new IVideoPlayListener() {

            @Override
            public void onStart() {
//                ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoStartCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onEnd() {
//                ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getEndCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onFirstQuartile() {
//                ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoFirstQuartileCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onMid() {
//                ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoMidCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onThirdQuartile() {
//                ADTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoThirdQuartileCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }
        };
        AdTaskManager.getInstance().registerIVideoPlayListener(Config.ADTYPE, videoPlayerListener);
        ViewManager.getInstance().registerLandPageView(Config.ADTYPE, new DZLandView());
    }



    @Override
    public String buildRequestInfo() {
        String result;
        if (StringUtil.isNotEmpty(paramsModel)) {
            JSONObject param = new JSONObject();
            try {
                param.put("platform", 1); // 0 ios , 1 android
                param.put("density",paramsModel.getDensity());//屏幕密度
                param.put("ua",paramsModel.getUa());//取WebView的UA
                param.put("appver", paramsModel.getAppVersion());//当前APP的版本号
                param.put("ip", paramsModel.getIp());//IP
                param.put("addr", paramsModel.getAddr()); //地址
                String sdk = paramsModel.getSdk();
                String appPackage = paramsModel.getAppPackage();
                String imei = paramsModel.getImei();
                String channel = paramsModel.getChannel();
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
                return "";
            }

            result = NetHelper.callWithResponse(Config.getADSURL(), Config.GET_AD_METHOD, param);
            //判断返回数据是否返回广告
            if (StringUtil.isNotEmpty(result)) {
                Gson gson = new Gson();
                Log.e("ADTEST", "DZ result = "+ result);
                DZAdResponse response = gson.fromJson(result, DZAdResponse.class);
                if (response.getRet_code()!=1000){
                    Log.e("ADTEST", "get DZ ad failed with result = "+result);
                    return result;
                }
                if (response.getAd().size()>0)
                    dzAdContent = response.getAd().get(0);  // 只获取一个广告
                else {
                    return result;
                }
            }
        }
        return "";
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
}
