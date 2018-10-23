package com.vito.ad;

import android.net.Uri;

import com.vito.ad.managers.ADDownloadTaskManager;
import com.vito.utils.Log;

import com.vito.ad.base.interfaces.IJsCallbackInterface;
import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.base.task.ADDownloadTask;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ViewManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;

public class JavaScriptBridge{

    private static JavaScriptBridge instance = null;

    public boolean parse(String url, IJsCallbackInterface jscallback) {
        Uri uri = Uri.parse(url);
        // game://AdCallBack?action="playEnd"&params=json
        if (uri.getScheme().equals("game")) {
            if (uri.getAuthority().equals("AdCallBack")) {
                // 可以在协议上带有参数并传递到Android上
                Log.e("jsCallback", uri.toString());
                String action = uri.getQueryParameter("action"); //解析参数
                String params_json = URLDecoder.decode(uri.getQueryParameter("params"));
                JSONObject params = null;
                if (!params_json.isEmpty()&&params_json!=null){
                    try {
                        params = new JSONObject(params_json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.e("jsCallback", "action" + action);
                if (jscallback != null) {
                    switch (action) {
                        case "playEnd":
                            Log.e("jsCallback", "playEnd");
                            parseCallbackUrlsJson(params_json);
                            jscallback.onPlayEnd();
                            break;
                        case "playTime":
                            Log.e("jsCallback", "playTime");
                            jscallback.setPlayTime(params.optInt("time"));

                            break;
                        case "playClose":
                            Log.e("jsCallback", "playClose");
                            break;
                        case "playError":
                            Log.e("jsCallback", "playError");
                            break;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void parseCallbackUrlsJson(String jsonStr){
        Log.e("adTest", "end with params = "+jsonStr);
       // String j = "{\"start_dowm\":[\"a1\",\"a2\"],\"end_down\":[\"b1\",\"b2\"],\"start_install\":[\"c1\",\"c2\"],\"end_install\":[\"d1\",\"d2\"]}";
        try {
            JSONObject json = new JSONObject(jsonStr);
            JSONArray start_dowm = json.getJSONArray("start_dowm");
            for(int i=0; i<start_dowm.length();i++){
                Log.e("jsonTest", "start_d "+start_dowm.getString(i));
            }
            JSONArray end_down = json.getJSONArray("end_down");
            for(int i=0; i<end_down.length();i++){
                Log.e("jsonTest", "end_d "+end_down.getString(i));

            }
            JSONArray start_install = json.getJSONArray("start_install");
            for(int i=0; i<start_install.length();i++){
                Log.e("jsonTest", "start_i "+start_install.getString(i));

            }
            JSONArray end_install = json.getJSONArray("end_install");
            for(int i=0; i<end_install.length();i++){
                Log.e("jsonTest", "end_i "+ end_install.getString(i));
            }
        } catch (JSONException e) {
            Log.e("jsonTest", "error = " + e.toString());
            e.printStackTrace();
        }

    }


    public static JavaScriptBridge getInstance() {
        if (instance == null){
            synchronized (JavaScriptBridge.class){
                instance = new JavaScriptBridge();
            }
        }
        return instance;
    }

    private JavaScriptBridge(){};

    public boolean parseDownload(String url) {
        Uri uri = Uri.parse(url);
        String path = uri.getPath();

        if (path.endsWith(".apk")||path.endsWith(".jar")){
            Log.e("adTest", "download url ="+ url);
                // 拉起下载服务开始下载并且安装
            // 构建广告APK下载任务
                ADInfoTask task = AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId());
                ADDownloadTask currentADDownloadTask = ADDownloadTaskManager.getInstance().getDownloadTaskByADId(task.getId());
                if (currentADDownloadTask !=null)
                    currentADDownloadTask.setApkDownload(true);
                ADDownloadTask ADDownloadTask = ADDownloadTaskManager.getInstance().buildDownloadTaskByADTask(task);
                ADDownloadTask.setUrl(ViewManager.getInstance().rebuildDownloadUrl(task, url));
                ADDownloadTaskManager.getInstance().pushTask(ADDownloadTask);
                AdTaskManager.getInstance().onClose(task);
                return true;
            }
        return false;
    }
}