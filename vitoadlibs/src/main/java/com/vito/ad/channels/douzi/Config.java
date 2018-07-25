package com.vito.ad.channels.douzi;

import com.vito.utils.Log;

public class Config {
    public static final int ADTYPE = 99;
    public static final String GET_AD_METHOD = "get_ad_info";

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
}
