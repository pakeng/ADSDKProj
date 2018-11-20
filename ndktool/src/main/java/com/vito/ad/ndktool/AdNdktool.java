package com.vito.ad.ndktool;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AdNdktool {
    static Class clazz;

    // jni调用
    public static void onGetCallBack(String json){
        Log.e("onGetCallBack", json);
        try {
            Method method = clazz.getDeclaredMethod("onGetPriorityInfoByGame", String.class);
            method.setAccessible(true);
            method.invoke(null, json);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public native static void getPriorityInfo(String channel, String DeviceID);

    public static void getPriorityInfo(String subChannelStr, String mDeviceID, Class aClass) {
        clazz = aClass;
        getPriorityInfo(subChannelStr, mDeviceID);
    }
}
