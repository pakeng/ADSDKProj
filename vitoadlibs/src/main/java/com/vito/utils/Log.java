package com.vito.utils;

public class Log {
    private static final String TAG = "ADSDK";
    public static boolean isDebug = false;
    public static int debugLevel = 0;  // 0  正式服 1 外网测试  2 内网

    public static void e(String tag, String msg){
        if (isDebug)
            android.util.Log.e(tag, msg);
    }

    public static void d(String tag, String msg){
        if (isDebug)
            android.util.Log.d(tag, msg);
    }


    public static void i(String tag, String msg){
        if (isDebug)
            android.util.Log.i(tag, msg);
    }

    public static void v(String tag, String msg){
        if (isDebug)
            android.util.Log.v(tag, msg);
    }

    public static void v(String msg){
        if (isDebug)
            android.util.Log.v(TAG, msg);
    }

    public static void e(String msg){
        if (isDebug)
            android.util.Log.e(TAG, msg);
    }

    public static void d(String msg){
        if (isDebug)
            android.util.Log.d(TAG, msg);
    }


    public static void i(String msg){
        if (isDebug)
            android.util.Log.i(TAG, msg);
    }
}
