package com.vito.ad.channels.oneway;

import android.util.Base64;

import com.google.gson.Gson;
import com.vito.ad.base.entity.VideoDetail;
import com.vito.ad.base.interfaces.IAdBaseInterface;
import com.vito.ad.base.processor.IProcessor;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.channels.oneway.view.ImplLandView2;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.video.interfaces.IVideoPlayListener;
import com.vito.utils.DeviceInfo;
import com.vito.utils.Log;
import com.vito.utils.StringUtil;
import com.vito.utils.network.NetHelper;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class OneWayProcessor extends IProcessor {
    private String oneWayToken = "";
    private ADTask adTask;
    private DownloadTask downloadTask;
    private String sessionId;

    @Override
    public void getAdContent() {
        Gson gson = new Gson();
        Log.e("ADTEST", "AdContent json = "+ AdContent);
        OneWayAdContentResponse oneWayAdContentResponse = gson.fromJson(AdContent, OneWayAdContentResponse.class);
        if (oneWayAdContentResponse == null)
            return;
        if (!oneWayAdContentResponse.isSuccess())
            return;
        OneWayAdContent oneWayAdContent = oneWayAdContentResponse.getData();
        adTask = new ADTask();
        String str = gson.toJson(oneWayAdContent);
        adTask.setADObject(str);
        //推广应用素材横竖屏方向，可能值： H, V, HV, VH
        if (oneWayAdContent.getOrientation().startsWith("V")){
            adTask.setOrientation(1);
        }else {
            adTask.setOrientation(0);
        }
        adTask.setId(AdTaskManager.getInstance().getNextADID());
        adTask.setType(Config.ADTYPE);
        sessionId = oneWayAdContent.getSessionId();
        adTask.setDownloadApkUrl(buildLandingPage(oneWayAdContent.getClickUrl()));
        // TODO  setCallbackURL
        //https://track.oneway.mobi/event?eventName={eventName}&publishId={publishId}&token={token}&ts={timestamp}
        if (oneWayAdContent.getTrackingEvents()!=null){
            adTask.setVideoStartCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getStart()));
            adTask.setVideoFirstQuartileCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getFirstQuartile()));
            adTask.setVideoMidCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getMidpoint()));
            adTask.setVideoThirdQuartileCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getThirdQuartile()));
            adTask.setEndCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getEnd()));
            adTask.setClickCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getClick()));
            adTask.setCloseCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getClose()));
            adTask.setShowCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getShow()));

            adTask.setSkipCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getSkip()));
            adTask.setDownloadStartCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getApkDownloadStart()));
            adTask.setDownloadEndCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getApkDownloadFinish()));
            adTask.setInstallFinishCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getPackageAdded()));

//            adTask.setmCloseCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getClose()));
//            adTask.setmCloseCallBackUrls(new HashSet<>(oneWayAdContent.getTrackingEvents().getClose()));
        }
        //show, start, end, click, apkDownloadStart, apkDownloadFinish, packageAdded
        long ts = System.currentTimeMillis();
        Map<String, String> setMap = new HashMap<>();
        setMap.put("publishId", Config.PUBLISHID);
        setMap.put("ts", ts+"");
        setMap.put("eventName","show");
        setMap.put("token",oneWayToken);
        URL url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getShowCallBackUrls().add(url.toString());
        // start
        setMap.put("eventName","start");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getVideoStartCallBackUrls().add(url.toString());
        // skip
        setMap.put("eventName","skip");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getSkipCallBackUrls().add(url.toString());
        // end
        setMap.put("eventName","end");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getEndCallBackUrls().add(url.toString());
        // click
        setMap.put("eventName","click");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getClickCallBackUrls().add(url.toString());
        // apkDownloadStart
        setMap.put("eventName","apkDownloadStart");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getDownloadStartCallBackUrls().add(url.toString());
        //apkDownloadFinish
        setMap.put("eventName","apkDownloadFinish");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getDownloadEndCallBackUrls().add(url.toString());
        //packageAdded
        setMap.put("eventName","packageAdded");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getInstallCallBackUrls().add(url.toString());

        setMap.put("eventName","thirdQuartile");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getVideoThirdQuartileCallBackUrls().add(url.toString());

        setMap.put("eventName","firstQuartile");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getVideoFirstQuartileCallBackUrls().add(url.toString());

        setMap.put("eventName","close");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getCloseCallBackUrls().add(url.toString());

        setMap.put("eventName","midpoint");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getVideoMidCallBackUrls().add(url.toString());

        adTask.setRemoveOnClose(true);

        downloadTask = new DownloadTask();
        downloadTask.setId(adTask.getId());
        downloadTask.setUrl(oneWayAdContent.getVideoUrl());
        downloadTask.setAd_type(Config.ADTYPE);
        try {
            URI uri = new URI(downloadTask.getUrl());
            String name = uri.getPath().substring(uri.getPath().lastIndexOf("/")+1);
            downloadTask.setName(name);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            downloadTask.setName(Base64.encodeToString(oneWayAdContent.getAppName().getBytes(),Base64.URL_SAFE));
        }
        downloadTask.setPackageName(oneWayAdContent.getAppStoreId());
        downloadTask.setAppName(oneWayAdContent.getAppName());
        downloadTask.setmAdname(oneWayAdContent.getAppName());
        downloadTask.setVideoDetail(new VideoDetail(adTask.getId(), oneWayAdContent.getVideoDuration()));


    }

    public OneWayProcessor(){
        // 注册对应的回调方法
        android.util.Log.e("ADSDK", "onewayProcessor  注册");
        IVideoPlayListener videoPlayerListener = new IVideoPlayListener() {
            @Override
            public void onStart() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("start");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoStartCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            @Override
            public void onEnd() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("end");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getEndCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            @Override
            public void onFirstQuartile() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("firstQuartile");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoFirstQuartileCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            @Override
            public void onMid() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("midpoint");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoMidCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            @Override
            public void onThirdQuartile() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("thirdQuartile");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoThirdQuartileCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }
        };
        AdTaskManager.getInstance().registerIVideoPlayListener(Config.ADTYPE, videoPlayerListener);
        IAdBaseInterface iAdBaseInterface = new IAdBaseInterface() {
            @Override
            public void onShow() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("show");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getShowCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            @Override
            public void onClose() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("close");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getCloseCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            //•apkDownloadStart : 下载开始时回传
            //•apkDownloadFinish : 下载完成时回传
            //•apkDownloadError : 下载错误时回传
            //•packageAdded : 安装完成时回传
            //•packageReplaced : 升级时回传
            //•packageRemoved : 卸载时回传
            @Override
            public void onDownLoadStart() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
                        .getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("apkDownloadStart");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
                        .getCurrentShowAdTaskId()).getDownloadStartCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            @Override
            public void onDownloadEnd() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID
                        (AdManager.getInstance().getCurrentShowAdTaskId())
                        .getADObject(OneWayAdContent.class).getCallbackParams("apkDownloadFinish");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
                        .getCurrentShowAdTaskId()).getDownloadEndCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            @Override
            public void onDownloadError() {
//            JSONObject json =AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
//                    .getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("apkDownloadError");
//            for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
//                    .getCurrentShowAdTaskId()).()) {
//                NetHelper.sendPostRequest(url, json, 1);
//            }
            }

            @Override
            public void onInstallStart() {
//            JSONObject json =AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
//                    .getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("packageAdded");
//            for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
//                    .getCurrentShowAdTaskId()).getmInstallCallBackUrls()) {
//                NetHelper.sendPostRequest(url, json, 1);
//            }
            }

            @Override
            public void onInstallFinish() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
                        .getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("packageAdded");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
                        .getCurrentShowAdTaskId()).getInstallCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }

            @Override
            public void onClick() {
                JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
                        .getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("click");
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance()
                        .getCurrentShowAdTaskId()).getClickCallBackUrls()) {
                    NetHelper.sendPostRequest(url, json, 1);
                }
            }
        };
        AdTaskManager.getInstance().registerIAdBaseInterface(Config.ADTYPE, iAdBaseInterface);
        ViewManager.getInstance().registerLandPageView(Config.ADTYPE, new ImplLandView2());
    }


    @Override
    protected String buildLandingPage(String clickUrl) {

        clickUrl = clickUrl.replaceAll("(?i)\\{aff_click_id\\}", sessionId);
        clickUrl = clickUrl.replaceAll("(?i)\\{sub_aff_id\\}", Config.PUBLISHID)
                .replaceAll("(?i)\\{source\\}", Config.PUBLISHID)
                .replaceAll("(?i)\\{device_id\\}", "")
                .replaceAll("(?i)\\{advertising_id\\}", "")
                .replaceAll("(?i)\\{device_model\\}", paramsModel.getModel())
                .replaceAll("(?i)\\{device_os\\}", "android")
                .replaceAll("(?i)\\{android_id\\}", paramsModel.getAndroidId())
                .replaceAll("(?i)\\{imei\\}", paramsModel.getImei())
                .replaceAll("(?i)\\{ip\\}", DeviceInfo.getLocalIpAddress())
                .replaceAll("(?i)\\{idfa\\}", "");

        return clickUrl;
    }

    @Override
    public String buildCallBackParams() {
            return null;
    }

    @Override
    public int getType() {
        return Config.ADTYPE;
    }


    @Override
    public String buildRequestInfo() {
        String result;
        long ts = System.currentTimeMillis();
        if (StringUtil.isEmpty(oneWayToken)) {
            Map<String, String> setMap = new HashMap<>();
            setMap.put("publishId", Config.PUBLISHID);
            setMap.put("ts", ts+"");
            URL url = buildGetURL(Config.ADSETTING_URL, setMap);
            Log.e("ADTEST", "setting url = "+ url.toString());
            result = NetHelper.doGetHttpResponse(url.toString(),2);
            Gson gson = new Gson();
            Log.e("ADTEST", "result = "+ result);
            OneWayAdSettingResponse setJson = gson.fromJson(result, OneWayAdSettingResponse.class);

            if (StringUtil.isNotEmpty(setJson) && setJson.isSuccess()) {
                oneWayToken = setJson.getData().getAppToken();
            }
        }

        if (!StringUtil.isEmpty(paramsModel) && StringUtil.isNotEmpty(oneWayToken)) {
            Map<String, Object> param = new HashMap<>();
            String deviceModel = paramsModel.getModel();
            String androidId = paramsModel.getAndroidId();
            String imei = paramsModel.getImei();
//            String channel = paramsModel.getChannel();

            param.put("placementId", Config.PLACEMENTID);//广告位ID
            param.put("deviceId", "");//	设备的广告ID （iOS填IDFA，Android填GAID）
            param.put("imei", imei);//Android设备必填，iOS不用填
            param.put("androidId", androidId);//Android设备必填，iOS不用填
            param.put("userAgent", paramsModel.getUa());//浏览器userAgent
            param.put("osVersion", paramsModel.getSys());//操作系统版本
            param.put("apiLevel", paramsModel.getSdk());
            param.put("bundleId", paramsModel.getAppPackage());//APP的bundleId
            param.put("bundleVersion", paramsModel.getAppVersion());
            param.put("deviceOS", "android");
            param.put("connectionType", paramsModel.getConnectionType());//网络连接类型（wifi，cellular ）
            param.put("deviceMake", paramsModel.getBrand());//设备产商
            param.put("deviceModel", deviceModel);//设备型号
            param.put("language", "zh_CN");//语种
            param.put("timeZone", "GMT+08:00");//时区
            param.put("mac",paramsModel.getMac()); //mac 地址
            param.put("networkOperator",paramsModel.getImsi().substring(0, 5));
            param.put("simOperator",paramsModel.getImsi().substring(0, 5));
            param.put("imsi",paramsModel.getImsi());


            String paramJson = StringUtil.map2Json(param);
            String AdUrl = Config.AD_GETCAMPAIGN_URL+"?publishId=" + Config.PUBLISHID + "&token=" + oneWayToken + "&ts=" + ts;
            result = NetHelper.doPostRequest(AdUrl, paramJson, 2);
        }else {
            result = "";
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



}
