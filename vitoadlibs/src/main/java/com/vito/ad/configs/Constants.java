package com.vito.ad.configs;

import com.vito.utils.Log;

public class Constants {
    public static final  int ADVIDEO = 0; // 视频广告下载
    public static final int APK_DOWNLOAD = 1; // 安装包下载
    public static final String AD_CONFIG_FILE_NAME = "ADCONFIG"; // 配置文件保存名称
    public static final String CLOSE_METHOD = "ad_video_close";
    public static final String GET_AD_ORDER = "get_ad_order";
//    private String ADSURL = "http://10.7.48.25:81/ads?method=";

    public static String getADSURL() {
        switch (Log.debugLevel){
            case 1:
                return "http://123.206.229.210:81/ads?method=";
            case 2:
                return "http://10.7.48.25:81/ads?method=";
            default:
                return "http://111.231.102.63:81/ads?method=";
        }
    }


//    public static final String ADSURL = "http://123.206.229.210:81/ads?method=";
}
