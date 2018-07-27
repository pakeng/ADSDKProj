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
    private IVideoPlayListener videoPlayerListener = new IVideoPlayListener() {
        @Override
        public void onStart() {
            JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("start");
            for (String url: AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getmVideoStartCallBackUrls()) {
                NetHelper.sendPostRequest(url, json, 1);
            }
        }

        @Override
        public void onEnd() {
            JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("end");
            for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getmEndCallBackUrls()) {
                NetHelper.sendPostRequest(url, json, 1);
            }
        }
        @Override
        public void onFirstQuartile() {
            JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("firstQuartile");
            for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getmVideoFirstQuartileCallBackUrls()) {
                NetHelper.sendPostRequest(url, json, 1);
            }
        }

        @Override
        public void onMid() {
            JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("midpoint");
            for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getmVideoMidCallBackUrls()) {
                NetHelper.sendPostRequest(url, json, 1);
            }
        }

        @Override
        public void onThirdQuartile() {
            JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("thirdQuartile");
            for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getmVideoThirdQuartileCallBackUrls()) {
                NetHelper.sendPostRequest(url, json, 1);
            }
        }
    };

    private IAdBaseInterface iAdBaseInterface = new IAdBaseInterface() {
        @Override
        public void onShow() {
            JSONObject json = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("show");
            for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getmShowCallBackUrls()) {
                NetHelper.sendPostRequest(url, json, 1);
            }
        }

        @Override
        public void onClose() {
            JSONObject json =AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getADObject(OneWayAdContent.class).getCallbackParams("close");
            for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getmCloseCallBackUrls()) {
                NetHelper.sendPostRequest(url, json, 1);
            }
        }
    };

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
        if (oneWayAdContent.getOrientation().contains("V")){
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
            adTask.setmVideoStartCallBackUrls(new HashSet<String>(oneWayAdContent.getTrackingEvents().getStart()));
            adTask.setmVideoFirstQuartileCallBackUrls(new HashSet<String>(oneWayAdContent.getTrackingEvents().getFirstQuartile()));
            adTask.setmVideoMidCallBackUrls(new HashSet<String>(oneWayAdContent.getTrackingEvents().getMidpoint()));
            adTask.setmEndCallBackUrls(new HashSet<String>(oneWayAdContent.getTrackingEvents().getEnd()));
            adTask.setmClickCallBackUrls(new HashSet<String>(oneWayAdContent.getTrackingEvents().getClick()));
            adTask.setmCloseCallBackUrls(new HashSet<String>(oneWayAdContent.getTrackingEvents().getClose()));
            //adTask.setTrackingEventsURL(oneWayAdContent.getTrackingEvents());
        }
        long ts = System.currentTimeMillis();
            Map<String, String> setMap = new HashMap<>();
            setMap.put("publishId", Config.PUBLISHID);
            setMap.put("ts", ts+"");
            setMap.put("eventName","show");
            setMap.put("token",oneWayToken);
            URL url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getmShowCallBackUrls().add(url.toString());
        setMap.put("eventName","thirdQuartile");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getmVideoThirdQuartileCallBackUrls().add(url.toString());
        setMap.put("eventName","firstQuartile");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getmVideoFirstQuartileCallBackUrls().add(url.toString());
        setMap.put("eventName","end");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getmEndCallBackUrls().add(url.toString());
        setMap.put("eventName","close");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getmCloseCallBackUrls().add(url.toString());
        setMap.put("eventName","midpoint");
        url = buildGetURL(Config.AD_CALLBACK_URL, setMap);
        adTask.getmVideoMidCallBackUrls().add(url.toString());

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
        AdTaskManager.getInstance().registerIVideoPlayListener(Config.ADTYPE, videoPlayerListener);
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
        String result = "";
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
            String channel = paramsModel.getChannel();

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
