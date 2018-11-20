package com.vito.ad.managers;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.vito.ad.base.interfaces.IPullAppEventListener;
import com.vito.ad.base.jdktool.ConcurrentHashSet;
import com.vito.ad.base.task.ADDownloadTask;
import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.configs.Constants;
import com.vito.ad.services.DownloadService;
import com.vito.utils.APPUtil;
import com.vito.utils.Log;
import com.vito.utils.MD5Util;
import com.vito.utils.SharedPreferencesUtil;
import com.vito.utils.gsonserializer.UriDeserializer;
import com.vito.utils.gsonserializer.UriSerializer;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ADDownloadTaskManager {
    private DownloadService.DownloadBinder binder = null;
    private boolean isNeedStartTask = false;
    private ConcurrentHashSet<ADDownloadTask> DownloadingTasks = new ConcurrentHashSet<>();

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


    private static ADDownloadTaskManager instance = null;
    public static ADDownloadTaskManager getInstance(){
         if (instance == null){
              synchronized (ADDownloadTaskManager.class){
                  if (instance == null)
                    instance = new ADDownloadTaskManager();
              }
         }
         return instance;
    }

    private ADDownloadTaskManager(){
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
        Type type = new TypeToken<ConcurrentHashSet<ADDownloadTask>>(){}.getType();
        ConcurrentHashSet<ADDownloadTask> tasks = gson.fromJson(downloadtasks_json, type);
        if (tasks!=null){
            DownloadingTasks = tasks;
        }
    }

    public ADDownloadTask getTaskByJson(JSONObject json){
        Gson gson = new Gson();
        ADDownloadTask task = gson.fromJson(json.toString(), ADDownloadTask.class);
        Log.e("ADTEST", " parser json to downTask = "+gson.toString());
        return task;
    }

    public ADDownloadTask getTaskByJson(String jsonString){
        Gson gson = new Gson();
        ADDownloadTask task = gson.fromJson(jsonString, ADDownloadTask.class);
        Log.e("ADTEST", " parser json to downTask = "+gson.toString());
        return task;
    }


    public boolean pushTask(final ADDownloadTask adDownloadTask){
        if (adDownloadTask == null)
            return false;
        synchronized (this) {
            DownloadingTasks.add(adDownloadTask); // 添加任务
            upDateSaveFile();
        }
        // 原来的下载方式
        // return downloadWithDownloadManager(adDownloadTask);
        // 使用新的下载方式
        return downloadWithOKDownloader(adDownloadTask);
    }

    private boolean downloadWithOKDownloader(final ADDownloadTask adDownloadTask) {
        // 检查文件是否存在
        File f = checkFile(adDownloadTask.getName());
        if (f.exists()){
            adDownloadTask.setDownloadCompleted(true);
            adDownloadTask.setStoreUri(Uri.fromFile(f));
            onFileDownloadSuccess(adDownloadTask);
            return true;
        }

        DownloadTask downloadTask = new DownloadTask.Builder(adDownloadTask.getUrl(), getRootFilePath())
                .setFilename(adDownloadTask.getName())
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(30)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();

        downloadTask.enqueue(new DownloadListener() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                adDownloadTask.setDownloadId(task.getId());
                if (adDownloadTask.getType()!=Constants.ADVIDEO){
                    ADInfoTask adInfoTask = AdManager.getInstance().getCurrentShowAdTask();
                    if (adInfoTask !=null)
                        AdTaskManager.getInstance().getIAdBaseInterface(adInfoTask).onDownLoadStart();
                }

            }

            @Override
            public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {

            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {

            }

            @Override
            public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {

            }

            @Override
            public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                onFileDownloadSuccess(adDownloadTask, task);
            }
        });
        return true;
    }

    private boolean downloadWithDownloadManager(ADDownloadTask adDownloadTask) {

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

    private void onFileDownloadSuccess(ADDownloadTask adDownloadTask, DownloadTask task) {
        adDownloadTask.setDownloadCompleted(true);
        adDownloadTask.setStoreUri(Uri.fromFile(task.getFile()));
        onFileDownloadSuccess(adDownloadTask);
    }

    private void onFileDownloadSuccess(ADDownloadTask adDownloadTask) {

        if (adDownloadTask.getType() != Constants.ADVIDEO) {
            ADInfoTask adInfoTask = AdManager.getInstance().getCurrentShowAdTask();
            if (adInfoTask != null) {
                AdTaskManager.getInstance().getIAdBaseInterface(adInfoTask).onDownloadEnd();
                AdTaskManager.getInstance().getIAdBaseInterface(adInfoTask).onInstallStart();
            }
            if (adDownloadTask.getType()==Constants.APK_DOWNLOAD_URL){
                IPullAppEventListener callback = AdManager.getInstance().getPullEventListeners().get(adDownloadTask.getPackageName());
                if (callback!=null){
                    callback.onDownloadSuccess(adDownloadTask);
                    callback.onInstallStart(adDownloadTask);
                }
            }
            APPUtil.installApk(AdManager.mContext, adDownloadTask.getStoreUri(), adDownloadTask);
        }else {
            AdManager.getInstance().refreshAllReadyADCount();
            AdManager.getInstance().notifyPrepare(true, adDownloadTask.getId());
        }
    }

    private File checkFile(String name) {
        File f = new File(getRootFilePath().toString()+File.separator+name);
        return f;
    }

    private File getRootFilePath() {
        File externalSaveDir = AdManager.mContext.getExternalFilesDir("Vito_temp");
        if (externalSaveDir == null) {
            return new File(AdManager.mContext.getFilesDir().toString()+File.separator+"Vito_temp");
        } else {
            return externalSaveDir;
        }
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

    public ConcurrentHashSet<ADDownloadTask> getReadOnlyDownloadingTasks() {
        return DownloadingTasks;
    }

    public void setDownloadingTasks(ConcurrentHashSet<ADDownloadTask> downloadingTasks) {
        DownloadingTasks = downloadingTasks;
    }

    public ADDownloadTask getDownloadTaskByDownloadId(long id){
        ConcurrentHashSet<ADDownloadTask> Tasks = DownloadingTasks;
        for (ADDownloadTask task : Tasks) {
            if (task.getDownloadId() == id){
                return task;
            }
        }
        return null;
    }

    // 修改 类型 之前是long 现在改为int 保持和ADId 类型一致
    public ADDownloadTask getDownloadTaskByADId(int id){
        ConcurrentHashSet<ADDownloadTask> Tasks = DownloadingTasks;
        for (ADDownloadTask task : Tasks) {
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

    /**
     *  创建下载任务
     * @param mADInfoTask 原始的广告任务
     * @return 新创建的下载任务
     */

    public ADDownloadTask buildDownloadTaskByADTask(ADInfoTask mADInfoTask) {
        ADDownloadTask ADDownloadTask = new ADDownloadTask();
        ADDownloadTask.setType(Constants.APK_DOWNLOAD);
        ADDownloadTask.setId(AdTaskManager.getInstance().getNextADID());
        String packageName = "";
        String name = "";
        ADDownloadTask.setUrl(mADInfoTask.getDownloadApkUrl());
        if (ADDownloadTaskManager.getInstance().getDownloadTaskByADId(mADInfoTask.getId())!=null) {
            packageName = ADDownloadTaskManager.getInstance().getDownloadTaskByADId(mADInfoTask.getId()).getPackageName();
            name = ADDownloadTaskManager.getInstance().getDownloadTaskByADId(mADInfoTask.getId()).getName();
            name = name.substring(0, name.lastIndexOf(".")+1)+"apk";
        }else {
            name = MD5Util.encrypt(ADDownloadTask.getUrl())+".apk";
        }
        ADDownloadTask.setPackageName(packageName);
        ADDownloadTask.setOriginId(mADInfoTask.getId());
        ADDownloadTask.setName(name);
        return ADDownloadTask;
    }

    /**
     * fix bug  2018年9月14日14:15:09 之前删除实际上对比的是downloadid  有问题
     * @param id
     */
    public void removeTaskByADId(int id) {
        for (ADDownloadTask task : DownloadingTasks) {
            if (task.getOriginId() == id){
                DownloadingTasks.remove(task);
                upDateSaveFile();
                return;
            }
        }
    }

    /**
     * 通过downloadId 删除
     * @param id downloadId
     */
    public void removeTaskByDownloadId(long id) {
        for (ADDownloadTask task : DownloadingTasks) {
            if (task.getDownloadId() == id){
                DownloadingTasks.remove(task);
                upDateSaveFile();
                return;
            }
        }
    }

    // 使用url下载 APK文件
    public ADDownloadTask getDownloadTaskByURL(String url, String packageName) {
        ADDownloadTask ADDownloadTask = new ADDownloadTask();
        ADDownloadTask.setType(Constants.APK_DOWNLOAD_URL);
        ADDownloadTask.setId(AdTaskManager.getInstance().getNextADID());
        ADDownloadTask.setUrl(url);
        ADDownloadTask.setPackageName(packageName);
        String name = url.substring(url.lastIndexOf("/") + 1);
        name = name.substring(0, name.lastIndexOf(".")+1)+"apk";
        ADDownloadTask.setOriginId(Constants.NoOriginId);
        ADDownloadTask.setName(name);
        return ADDownloadTask;
    }

}
