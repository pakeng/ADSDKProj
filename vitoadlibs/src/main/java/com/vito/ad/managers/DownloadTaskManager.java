package com.vito.ad.managers;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import com.vito.utils.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vito.ad.base.jdktool.ConcurrentHashSet;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.configs.Constants;
import com.vito.ad.services.DownloadService;
import com.vito.utils.SharedPreferencesUtil;
import com.vito.utils.gsonserializer.UriDeserializer;
import com.vito.utils.gsonserializer.UriSerializer;

import org.json.JSONObject;

import java.lang.reflect.Type;

public class DownloadTaskManager {
    private DownloadService.DownloadBinder binder = null;
    private boolean isNeedStartTask = false;
    private ConcurrentHashSet<DownloadTask> DownloadingTasks = new ConcurrentHashSet<>();

    private ServiceConnection downloadServerConnect = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.DownloadBinder) service;
            if (isNeedStartTask)
                binder.getService().startTask();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private static DownloadTaskManager instance = null;
    public static DownloadTaskManager getInstance(){
         if (instance == null){
              synchronized (DownloadTaskManager.class){
                  if (instance == null)
                    instance = new DownloadTaskManager();
              }
         }
         return instance;
    }

    private DownloadTaskManager(){
        InitWithConfig();
        // 开启下载服务
        Intent mIntent = new Intent();
        mIntent.setClass(AdManager.mContext, DownloadService.class);
        AdManager.mContext.startService(mIntent);

        Intent mIntent1 = new Intent();
        mIntent1.setClass(AdManager.mContext, DownloadService.class);
        AdManager.mContext.bindService(mIntent1, downloadServerConnect, Service.BIND_AUTO_CREATE);

    }

    private void InitWithConfig() {
        // TODO  读取文件，初始化之前的数据
        String downloadtasks_json = SharedPreferencesUtil.getStringValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "downloadingtasks");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .create();
        Type type = new TypeToken<ConcurrentHashSet<DownloadTask>>(){}.getType();
        ConcurrentHashSet<DownloadTask> tasks = gson.fromJson(downloadtasks_json, type);
        if (tasks!=null){
            DownloadingTasks = tasks;
        }
    }

    public DownloadTask getTaskByJson(JSONObject json){
        Gson gson = new Gson();
        DownloadTask task = gson.fromJson(json.toString(), DownloadTask.class);
        Log.e("ADTEST", " parser json to downTask = "+gson.toString());
        return task;
    }

    public DownloadTask getTaskByJson(String jsonString){
        Gson gson = new Gson();
        DownloadTask task = gson.fromJson(jsonString, DownloadTask.class);
        Log.e("ADTEST", " parser json to downTask = "+gson.toString());
        return task;
    }

    public boolean pushTask( DownloadTask task){
        if (task == null)
            return false;
        synchronized (this) {
            DownloadingTasks.add(task); // 添加任务
            upDateSaveFile();
        }
        if (binder==null){
            Intent mIntent = new Intent();
            mIntent.setClass(AdManager.mContext, DownloadService.class);
            AdManager.mContext.bindService(mIntent, downloadServerConnect, Service.BIND_AUTO_CREATE);
            isNeedStartTask = true;
            return false;
        }

        binder.getService().startTask();

        return true;
    }

    public void notifyUpDate(){
        upDateSaveFile();
    }

    // 更新保存的任务文件
    private void upDateSaveFile() {
        // 序列化
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        String json = gson.toJson(DownloadingTasks);
        SharedPreferencesUtil.putStringValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "downloadingtasks",json );
    }

    public ConcurrentHashSet<DownloadTask> getReadOnlyDownloadingTasks() {
        return DownloadingTasks;
    }

    public void setDownloadingTasks(ConcurrentHashSet<DownloadTask> downloadingTasks) {
        DownloadingTasks = downloadingTasks;
    }

    public DownloadTask getDownloadTaskByDownloadId(long id){
        ConcurrentHashSet<DownloadTask> Tasks = DownloadingTasks;
        for (DownloadTask task : Tasks) {
            if (task.getDownloadId() == id){
                return task;
            }
        }
        return null;
    }

    public DownloadTask getDownloadTaskByADId(long id){
        ConcurrentHashSet<DownloadTask> Tasks = DownloadingTasks;
        for (DownloadTask task : Tasks) {
            if (task.getId() == id){
                return task;
            }
        }
        return null;
    }

    public Service getService() {
        if (binder!=null)
            return binder.getService();
        return null;
    }

    public void exit() {
        if (AdManager.mContext!=null&&downloadServerConnect!=null)
            AdManager.mContext.unbindService(downloadServerConnect);
    }

    public DownloadTask getDownloadTaskByADTask(ADTask mADTask) {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.setType(Constants.APK_DOWNLOAD);
        downloadTask.setId(AdTaskManager.getInstance().getNextADID());
        downloadTask.setUrl(mADTask.getDownloadApkUrl());
        downloadTask.setPackageName(DownloadTaskManager.getInstance().getDownloadTaskByADId(mADTask.getId()).getPackageName());
        String name = DownloadTaskManager.getInstance().getDownloadTaskByADId(mADTask.getId()).getName();
        name = name.substring(0, name.lastIndexOf(".")+1)+"apk";
        downloadTask.setOriginId(mADTask.getId());
        downloadTask.setName(name);
        return downloadTask;
    }
}
