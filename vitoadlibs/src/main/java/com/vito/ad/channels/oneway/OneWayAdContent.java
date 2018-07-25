package com.vito.ad.channels.oneway;

import com.vito.ad.managers.AdManager;
import com.vito.utils.DeviceInfo;
import com.vito.utils.WifiAdmin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class OneWayAdContent {
//    appIcon String 推广应用图标URL (可能为空)
//    appId Integer 推广应用ID (可能为空)
//    appName String 推广应用 (可能为空)
//    rating Float 推广应用平均评分 (可能为空)
//    ratingCount Integer 推广应用评价数 (可能为空)
//    appStoreId String 应用商店标识，对于iOS为 storeId，对于Android为包名 (可能为空)
//    sessionId String 唯一回话ID，标准36位UUID
//    downloadType Number APP下载类型
//    orientation String 推广应用素材横竖屏方向，可能值： H, V, HV, VH
//    campaignId Long 广告计划ID
//    videoUrl String 推广应用视频素材URL
//    videoDuration Float 视频时长
//    clickUrl String 点击URL模板
//    imgUrls Array<String> 推广应用图片素材URL列表
//    trackingEvents Map<String,List<String>> 需额外上报的事件，对应的url列表。当该值不为空时，必须上报否则会出现不计费情况
//    clickMode Integer 点击模式 0: 默认(落地页下载按钮可点) 1: 落地页全屏可点 2: 播放过程中全屏可点，落地页全屏可点

    private String appIcon = "";
    private int appId ;
    private String appName = "";
    private float rating ;
    private float ratingCount;
    private String appStoreId = "";
    private String sessionId = "";
    private String downloadType = "";
    private String orientation = "";
    private long campaignId ;
    private String videoUrl = "";
    private float videoDuration;
    private String clickUrl = "";
    private List<String> imgUrls;
    private TrackingEventsURL trackingEvents ;
    private int clickMode;
    public String getAppIcon() {
        return appIcon;
    }

    public int getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }

    public float getRating() {
        return rating;
    }

    public float getRatingCount() {
        return ratingCount;
    }

    public String getAppStoreId() {
        return appStoreId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public String getOrientation() {
        return orientation;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public float getVideoDuration() {
        return videoDuration;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public TrackingEventsURL getTrackingEvents() {
        return trackingEvents;
    }

    public int getClickMode() {
        return clickMode;
    }

    public JSONObject getCallbackParams(String type){
        String result = "{\"eventName\":\"show\",\"eventId\":\"07ae8b8e-ff7e-4cbc-8c46-84eb8fd194e7\",\"campaignId\":2310,\"publishId\":\"br54mybf77zxseiv\"," +
                "\"sessionId\":\"815293e0-767b-495b-99f6-a29b4456527c\",\"deviceId\":\"321AF168-19E6-4402-876F-1BFD87234E73\",\"bundleId\":\"mobi.oneway.ad\"," +
                "\"bundleVersion\":\"2.0.9\",\"connectionType\":\"wifi\",\"countryCode\":\"CN\",\"language\":\"zh_CN\",\"timeZone\":\"GMT+08:00\"," +
                "\"userAgent\":\"Mozilla/5.0 (Linux; Android 5.1; m2 note Build/LMY47D) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/40.0.2214.127 Mobile Safari/537.36\"," +
                "\"osVersion\":\"5.1\",\"apiLevel\":22,\"imei\":\"868800027334261\",\"androidId\":\"eecfb0660827d2df\",\"deviceMake\":\"HUAWEI\"," +
                "\"deviceModel\":\"HUAWEI MT7-TL10\",\"mac\":\"68:3e:34:4d:03:ed\",\"wifiBSSID\":\"5c:0e:8b:eb:27:b0\",\"wifiSSID\":\"BZU\"}";
        DeviceInfo deviceInfo = new DeviceInfo(AdManager.mContext);
        try {
            JSONObject jsonObject = new JSONObject(result);
            WifiAdmin wifiAdmin = new WifiAdmin(AdManager.mContext);
            wifiAdmin.getScanResult();
            jsonObject.put("eventName", type)
            .put("evetId", UUID.randomUUID())
            .put("campaignId", campaignId)
            .put("publishId", Config.PUBLISHID)
            .put("sessionId", sessionId)
            .put("deviceId", null)
            .put("bundleId", AdManager.mContext.getPackageName())
            .put("bundleVersion", deviceInfo.getVersionName())
            .put("connectionType", deviceInfo.GetNetworkType())
            .put("userAgent", deviceInfo.getUA())
            .put("osVersion", deviceInfo.getOSVersion())
            .put("apiLevel", deviceInfo.getSDKVERSION())
            .put("imei", deviceInfo.getDeviceImei())
            .put("androidId", deviceInfo.getAndroidId())
            .put("deviceMake", deviceInfo.getManufacturer())
            .put("deviceModel", deviceInfo.getModel())
            .put("mac", deviceInfo.getDeviceInfoMac())
            .put("wifiBSSID", wifiAdmin.getBSSID())
            .put("wifiSSID", wifiAdmin.getSSID());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
