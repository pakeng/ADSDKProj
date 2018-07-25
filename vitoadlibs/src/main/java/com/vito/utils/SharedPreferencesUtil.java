package com.vito.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * SharedPreferences工具类，可以保存object对象
 */
public class SharedPreferencesUtil {

    /**
     * 存放实体类以及任意类型
     *
     * @param context 上下文对象
     * @param key
     * @param obj
     */
    public static void putBean(Context context, String name, String key, Object obj) {
        if (obj instanceof Serializable) {// obj必须实现Serializable接口，否则会出问题
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(), 0));
                SharedPreferences.Editor editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();
                editor.putString(key, string64).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("the obj must implement Serializble");
        }
    }

    public static void putStringValue(Context context, String name, String key, String value) {
        String string64 = new String(Base64.encode(value.getBytes(), Base64.DEFAULT));
        SharedPreferences.Editor editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();
        editor.putString(key, string64).commit();
    }

    public static String getStringValue(Context context, String name, String key) {
        String base64 = context.getSharedPreferences(name, Context.MODE_PRIVATE).getString(key, "");
        if (base64.equals("")) {
            return "";
        }
        return new String(Base64.decode(base64.getBytes(), Base64.DEFAULT));
    }

    public static Object getBean(Context context, String name, String key) {
        Object obj = null;
        try {
            String base64 = context.getSharedPreferences(name, Context.MODE_PRIVATE).getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static void putIntValue(Context context, String name, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value).commit();
    }

    public static int getIntValue(Context context, String name, String key) {
        int value = context.getSharedPreferences(name, Context.MODE_PRIVATE).getInt(key, 0);
        return value;
    }
}