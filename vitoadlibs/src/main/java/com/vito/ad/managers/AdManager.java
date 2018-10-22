package com.vito.ad.managers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vito.ad.base.entity.ADServerResponse;
import com.vito.ad.base.interfaces.IPrepareCompleteCallBack;
import com.vito.ad.base.interfaces.IPullAppEventListener;
import com.vito.ad.base.interfaces.IShowCallBack;
import com.vito.ad.base.interfaces.PullAppEventListenerFactory;
import com.vito.ad.base.processor.IProcessor;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.channels.douzi.DZProcessor;
import com.vito.ad.channels.lanmei.LMProcessor;
import com.vito.ad.channels.oneway.OneWayProcessor;
import com.vito.ad.configs.Constants;
import com.vito.ad.utils.CallBackRequestUtil;
import com.vito.ad.views.activitys.PlayerActivity;
import com.vito.user.UserInfo;
import com.vito.utils.APPUtil;
import com.vito.utils.DeviceInfo;
import com.vito.utils.Log;
import com.vito.utils.SharedPreferencesUtil;
import com.vito.utils.ThreadExecutor;
import com.vito.utils.network.NetHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.vito.utils.Log.isDebug;

/**
 * 广告管理类
 * Created by Vito_d on 2018/5/16.
 */

public class AdManager {
    private static AdManager instance = null;
    public static Activity mContext;
    private String mADDeviceInfo;
    private IPrepareCompleteCallBack mPrepareCompleteCallback = null;
    private volatile int mAllReadyADCount;
    private int currentWhere; // 当前打开广告的地方，类似于透传
    private IShowCallBack showCallBack; // 播放回调
    private int currentShowAdTaskId;
    private List<Integer> priority = new ArrayList<>();
    private String showToken = "";
    private String mUid;
    private String mDeviceID;
    private String mChannel;
    private String subChannelStr;
    private boolean hasNativeAdFlag;
    private Map<String, IPullAppEventListener> pullEventListeners = new HashMap<>();

    // 独立下载的配置数据
    private Map<String, Integer> apkDownloadMap;

    public static AdManager InitAdManager(Activity ctx){
        if (instance==null){
            synchronized (AdManager.class){
                if (instance == null) {
                    instance = new AdManager(ctx);
                    DownloadTaskManager.getInstance();
                    AdTaskManager.getInstance();
                    CallBackRequestUtil.Init(ctx);
                    // 注册 processor
                    OneWayProcessor oneWayProcessor = new OneWayProcessor();
                    LMProcessor lmProcessor = new LMProcessor();
                    DZProcessor dzProcessor = new DZProcessor();
                    ProcesserManager.getInstance().registerProcesser(oneWayProcessor.getType(), oneWayProcessor );
                    ProcesserManager.getInstance().registerProcesser(dzProcessor.getType(), dzProcessor );
                    ProcesserManager.getInstance().registerProcesser(lmProcessor.getType(), lmProcessor);
                }
            }
        }
        return instance;
    }

    // 预留方法
    public static AdManager InitAdManager(Activity ctx, String uid){
        if (instance==null){
            synchronized (AdManager.class){
                if (instance == null) {
                    instance = new AdManager(ctx);
                    DownloadTaskManager.getInstance();
                    AdTaskManager.getInstance();
                    CallBackRequestUtil.Init(ctx);
                    instance.prepareAdInfo();
                    // 注册 processer
                    OneWayProcessor oneWayProcesser = new OneWayProcessor();
                    LMProcessor lmProcesser = new LMProcessor();
                    DZProcessor dzProcesser = new DZProcessor();
                    ProcesserManager.getInstance().registerProcesser(oneWayProcesser.getType(),oneWayProcesser );
                    ProcesserManager.getInstance().registerProcesser(dzProcesser.getType(),dzProcesser );
                    ProcesserManager.getInstance().registerProcesser(lmProcesser.getType(), lmProcesser);
                }
            }
        }
        return instance;
    }

    public static AdManager getInstance(){
        if (instance==null){
            Log.e("ADTEST", "please init it first");
        }

        return instance;
    }

    // 添加读取配置的方法
    private AdManager(Activity ctx){
        mContext = ctx;
        InitApkDownloadMap();
        InitAnalysisConfigData();
        prepareAdInfo();

    }

    private void InitApkDownloadMap() {
        // TODO  读取文件，初始化之前的数据
        String map_json = SharedPreferencesUtil.getStringValue(AdManager.mContext, Constants.APK_DOWNLOAD_MAP_CONFIG, "apk_download_map");
        // 反序列化
        Gson gson = new Gson();
        Type type = new TypeToken<ConcurrentHashMap<String, Integer>>(){}.getType();
        ConcurrentHashMap<String, Integer> map = gson.fromJson(map_json, type);
        if (map!=null){
            apkDownloadMap = map;
        }else {
            apkDownloadMap = new ConcurrentHashMap<>();
        }
    }

    private String prepareAdInfo(){
        DeviceInfo deviceInfo = new DeviceInfo(mContext);
        NetHelper.setUA(deviceInfo.getUA());
        mADDeviceInfo = deviceInfo.getADInfoString();
        return mADDeviceInfo;
    }

    public void PrepareAD(){
        if (getAllReadyADCount()>0&getOneAdId()!=-1){
            if (mPrepareCompleteCallback!=null)
                mPrepareCompleteCallback.onReadyPlay(mAllReadyADCount);
            return;
        }else if (hasNativeAd()){
            if (mPrepareCompleteCallback!=null)
                mPrepareCompleteCallback.onReadyPlay(mAllReadyADCount+1);
            return;
        }
        getPriorityInfo(getPrepareTask());
    }

    private Runnable getPrepareTask(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!prepareADs()) {
                    Log.e("error prepareAD");
                    if (mPrepareCompleteCallback!=null){
                        mPrepareCompleteCallback.onFailed(-1, 0);
                    }
                }else{
                    if (mPrepareCompleteCallback!=null){
                        mPrepareCompleteCallback.onReadyPlay(mAllReadyADCount+1);
                    }
                }
            }
        };
        return task;
    }

    private boolean prepareADs(){
        int count = 0;
        for (IProcessor processor : ProcesserManager.getInstance().getProcessers()){
            if (priority!=null&&priority.contains(processor.getType())){
                processor.startProcessor();
                DownloadTask task = processor.getDownLoadTask();
                ADTask adTask = processor.getADTask();
                AdTaskManager.getInstance().pushTask(adTask);
                DownloadTaskManager.getInstance().pushTask(task);
                count++;
            }
        }
        return count>0||hasNativeAd();
    }

    private String buildGetUrl(String url, String paramStr, int id){
        StringBuilder sb = new StringBuilder(url);
        String paramsWithUrlEncode = URLEncoder.encode(paramStr); // 使用默认的utf8编码
        sb.append("?param=")
                .append(paramsWithUrlEncode).append("&uid=")
                .append(UserInfo.getInstance().getUid())
                .append("&where=").append(id);
        return sb.toString();
    }

    public void setPrepareListener(IPrepareCompleteCallBack callBack){
        mPrepareCompleteCallback = callBack;
    }

    // 使用广告id通知下载完成
    public void notifyPrepare(boolean isSuccess, int id) {
        DownloadTaskManager.getInstance().notifyUpDate();
        int count = getAllReadyADCount();
        if (mPrepareCompleteCallback!=null){
            // TODO 优化判断 2 是ADHub的Adtype 3是vlion
            if (isSuccess||priority.contains(3)||priority.contains(2))
                mPrepareCompleteCallback.onSuccess(id, count);
            else
                mPrepareCompleteCallback.onFailed(id, count);
        }
    }

    private int getAllReadyADCount() {
        // TODO  优化需求， 在没有改动广告数据的时候不要再次遍历直接返回之前遍历的数据。

        return checkReadyAd();
    }

    public void refreshAllReadyADCount() {
        checkReadyAd();
        SharedPreferencesUtil.putIntValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "allreadyadcount",mAllReadyADCount);
    }

    public boolean ShowAd(JSONObject params, int where){
        if (params.has("token")) {
            showToken = params.optString("token");
            UserInfo.getInstance().setToken(showToken);
        }
        if (params.has("uid")) {
            mUid = params.optString("uid");
            UserInfo.getInstance().setUid(mUid);
        }
        if (params.has("deviceid")) {
            mDeviceID = params.optString("deviceid");
            UserInfo.getInstance().setDeviceId(mDeviceID);
        }
        if (params.has("channel")) {
            mChannel = params.optString("channel");
            UserInfo.getInstance().setChannel(mChannel);
        }
        currentWhere = where;
        if ((getAllReadyADCount()>0)){
            Intent m = new Intent();
            m.setClass(mContext, PlayerActivity.class);
            mContext.startActivity(m);
            return true;
        }else if (hasNativeAdFlag||priority.contains(3)||priority.contains(2)){
            Intent m = new Intent();
            m.setClass(mContext, PlayerActivity.class);
            mContext.startActivity(m);
            return true;
        }
        PrepareAD();
        return false;
    }

    public int getOneAdId() {
        if (getAllReadyADCount()>0){
            if(priority.size()>0){
                return getOneAdIdWithTable(priority);
            }else
                return getOneAdIdNOTable();
        }else{
            // 没有视频广告的时候 检查是否有原生广告可以使用
            if(priority.size()>0){
                return getOneAdIdWithTable(priority);
            }
//            else
//                return getOneAdIdNOTable();
//
//            if (hasNativeAd()){
//                return -2;
//            }

        }
        return -1;
    }

    public String getDeviceInfo() {

        return mADDeviceInfo;
    }

    public void exit() {
        DownloadTaskManager.getInstance().exit();

    }

    public int getCurrentWhere() {
        return currentWhere;
    }

    public void setCurrentWhere(int currentWhere) {
        this.currentWhere = currentWhere;
    }

    public IShowCallBack getShowCallBack() {
        return showCallBack;
    }

    public void setShowCallBack(IShowCallBack showCallBack) {
        this.showCallBack = showCallBack;
    }

    public void onADClose(boolean status) {
        if (priority.isEmpty()){
            refreshPriorityInfo();
        }
        if (showCallBack!=null&&status){
            showCallBack.onClose(currentWhere);
        }
        // 发送 完成广告的回调给游戏服务器
        DownloadTask downloadTask = DownloadTaskManager.getInstance().getDownloadTaskByADId(currentShowAdTaskId);
        JSONObject params = new JSONObject();
        try {
            params.put("uid", mUid);
            params.put("token", showToken);
            params.put("status", status?1:0);  // TODO
            params.put("channel", mChannel);
            params.put("device", mDeviceID);

            params.put("where", currentWhere/1000);
            params.put("level", currentWhere%1000);
            if (downloadTask!=null){
                params.put("ad_type", downloadTask.getAD_Type());
                params.put("app", downloadTask.getAppName()+" ");
                params.put("ad", downloadTask.getADname()+" ");
                params.put("ad_id", downloadTask.getSortNum()+"");
                params.put("package", downloadTask.getPackageName());
                params.put("price", downloadTask.getPrice());
                params.put("is_down", downloadTask.isApkDownload()?1:0);
            }else {
                params.put("ad_type", 3);
                params.put("app", "");
                params.put("ad", " ");
                params.put("ad_id", "");
                params.put("package", " ");
                params.put("price", " ");
                params.put("is_down", 0);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetHelper.callWithNoResponse(Constants.getADSURL(), Constants.CLOSE_METHOD, params);
    }

    public int getCurrentShowAdTaskId() {
        return currentShowAdTaskId;
    }

    // 检查可用的广告数量
    private int checkReadyAd(){
        mAllReadyADCount = 0;
        for (ADTask task : AdTaskManager.getInstance().getReadOnlyAdTasks()) {
            if (!task.isRemove()){
                DownloadTask downloadTask = DownloadTaskManager.getInstance().getDownloadTaskByADId(task.getId());
                if (downloadTask!=null&&downloadTask.isDownloadCompleted())
                    mAllReadyADCount++;
            }
        }
        return mAllReadyADCount;
    }

    private int getOneAdIdWithTable(List<Integer> priority_table) {
        for (int target : priority_table) {
            if (target==2||target==3){
                return 0-target;
            }
            for (DownloadTask task : DownloadTaskManager.getInstance().getReadOnlyDownloadingTasks()) {
                if (task.getType() == Constants.ADVIDEO && task.isDownloadCompleted()) {
                    ADTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getId());
                    if (adTask != null) {
                        if (!adTask.isRemove()&&adTask.getType() == target) {
                            currentShowAdTaskId = task.getId();
                            return currentShowAdTaskId;
                        }
                    }
                }
            }
        }
        return -1;
    }


    private int getOneAdIdNOTable() {
        for (DownloadTask task : DownloadTaskManager.getInstance().getReadOnlyDownloadingTasks()) {
            if (task.getType() == Constants.ADVIDEO && task.isDownloadCompleted()) {
                ADTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getId());
                if (adTask != null) {
                    if (!adTask.isRemove()) {
                        currentShowAdTaskId = task.getId();
                        return currentShowAdTaskId;
                    }
                }
            }
        }
        return -1;
    }

    @NonNull
    private void getPriorityInfo(final Runnable callbackTask){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(mADDeviceInfo);
                    jsonObject.put("channel", subChannelStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonObject = new JSONObject();
                    Log.e("ADTEST", subChannelStr+ "make error");
                }
                String result = NetHelper.callWithResponse(Constants.getADSURL(), Constants.GET_AD_ORDER, jsonObject);
                Gson gson = new Gson();
                ADServerResponse response = gson.fromJson(result, ADServerResponse.class);
                if (response!=null&&response.getRet()){
                    priority = response.getData();
                    ThreadExecutor.getInstance().addTask(callbackTask);
                }
                Log.e("ADTEST", result);
            }
        };
        ThreadExecutor.getInstance().addTask(task);
    }

    private void refreshPriorityInfo(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(mADDeviceInfo);
                    jsonObject.put("channel", subChannelStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonObject = new JSONObject();
                    Log.e("ADTEST", subChannelStr+ "make error");
                }
                String result = NetHelper.callWithResponse(Constants.getADSURL(), Constants.GET_AD_ORDER, jsonObject);
                Gson gson = new Gson();
                ADServerResponse response = gson.fromJson(result, ADServerResponse.class);
                if (response!=null&&response.getRet()){
                    priority = response.getData();
                }
                Log.e("ADTEST", result);
            }
        };
        ThreadExecutor.getInstance().addTask(task);
    }

    private void InitAnalysisConfigData(){
        ApplicationInfo appInfo = null;
        try {
            appInfo = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (appInfo != null) {
            subChannelStr = appInfo.metaData.getString("SubChannelName");
            isDebug = isDebug||appInfo.metaData.getInt("dbclf") > 100;   // debug config level flag 当大于100的时候就是测试版本
        }else{
            subChannelStr = "error";
        }
    }

    public void setInstallListener(String packageName, IPullAppEventListener listener){
        this.pullEventListeners.put(packageName, listener);
    }

    public void notifyApkInstalled(String packageName, DownloadTask downloadTask) {
        if (pullEventListeners !=null&& pullEventListeners.size()>0){
              IPullAppEventListener listener = pullEventListeners.get(packageName);
              if (listener!=null){
                  listener.onInstallSuccess(downloadTask);
              }
              else{
                  PullAppEventListenerFactory.getInstance().onInstallSuccess(downloadTask);
              }
        }

        // 安装成功移除对应的任务记录
        apkDownloadMap.remove(downloadTask.getUrl());
//        DownloadTaskManager.getInstance().removeTaskByDownloadId(downloadTask.getDownloadId());
    }

    public void notifyApkUninstalled(String packageName, int pullType) {
        // TODO  not use
//        if (pullEventListeners !=null&& pullEventListeners.size()>0){
//            IPullAppEventListener listener = pullEventListeners.get(packageName);
//            if (listener!=null)
//                listener.onUninstall(pullType);
//        }

    }

    public void startDownloadApkWithUrl(String url, String packageName, String appName, int pullType){

        startDownloadApkWithUrl(url, packageName, appName, pullType, PullAppEventListenerFactory.getInstance());

    }

    public void startDownloadApkWithUrl(String url, String packageName, String appName,int pullType, IPullAppEventListener listener){
        if (apkDownloadMap.get(url)!=null){
            downloadSuccessToInstall(apkDownloadMap.get(url));
            return;
        }
        pullEventListeners.put(packageName, listener);
        DownloadTask downloadTask = DownloadTaskManager.getInstance().getDownloadTaskByURL(url, packageName);
        downloadTask.setPullType(pullType);
        downloadTask.setAppName(appName);
        if (listener!=null){
            listener.onDownloadStart(downloadTask);
        }
        DownloadTaskManager.getInstance().pushTask(downloadTask);
        apkDownloadMap.put(url, downloadTask.getId());
        saveApkDownloadMap();
    }

    /**
     * @param url like this: "odapp://com.odao.fish"
     * @return true 可以拉起， false 不能拉起
     */
    public boolean checkAppStrap(String url){
        PackageManager packageManager = mContext.getPackageManager();
        Intent mIntent = new Intent();
        mIntent.setData(Uri.parse(url));
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mIntent, 0);
        return activities.size()>0;
    }

    /**
     * @param url like this: "odapp://com.odao.fish"
     * @return true 拉起成功， false 拉起失败
     */
    public boolean pullAppWithUrl(String url,int pulltype, String packageName, String appName, Bundle bundle){
        PackageManager packageManager = mContext.getPackageManager();
        Intent mIntent = new Intent();
        if (bundle!=null)
            mIntent.putExtra("StartBundle", bundle);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.setData(Uri.parse(url));
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mIntent, 0);
        if (activities.size()>0){
            IPullAppEventListener listener = pullEventListeners.get(packageName);
            if (listener!=null){
                listener.onPull(pulltype, packageName, appName);
            }else{
                PullAppEventListenerFactory.getInstance().onPull(pulltype, packageName, appName);
            }
            mContext.startActivity(mIntent);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 下载失败回调
     */
    public void notifyDownloadApkFailed(long downloadId) {
        DownloadTask downloadTask = DownloadTaskManager.getInstance().getDownloadTaskByDownloadId(downloadId);
        if (downloadTask!=null){
            IPullAppEventListener listener = pullEventListeners.get(downloadTask.getPackageName());
            if (listener!=null){
                listener.onDownloadFailed(downloadTask);
            }
            apkDownloadMap.remove(downloadTask.getUrl());
            saveApkDownloadMap();
            DownloadTaskManager.getInstance().removeTaskByDownloadId(downloadTask.getDownloadId());
        }
    }

    public Map<String, Integer> getApkDownloadMap() {
        return apkDownloadMap;
    }

    /**
     * 更新进度条
     * @param packageName 包名
     * @param p 进度 1-100;
     */
    public void updateDownloadProgress(String packageName, int p) {
        if (pullEventListeners !=null&& pullEventListeners.size()>0){
            IPullAppEventListener listener = pullEventListeners.get(packageName);
            if (listener!=null)
                listener.onUpDateProcess(p);
        }
    }

    private void downloadSuccessToInstall(int id){
        DownloadTask task = DownloadTaskManager.getInstance().getDownloadTaskByADId(id);
        if (task!=null&&task.isDownloadCompleted()){
            APPUtil.installApkWithTask(mContext, task);
        }
    }

    private void saveApkDownloadMap(){
        // 序列化
        Gson gson = new Gson();
        String json = gson.toJson(apkDownloadMap);
        SharedPreferencesUtil.putStringValue(AdManager.mContext, Constants.APK_DOWNLOAD_MAP_CONFIG, "apk_download_map",json );
    }

    public Map<String, IPullAppEventListener> getPullEventListeners() {
        return pullEventListeners;
    }

    public void setPullEventListeners(Map<String, IPullAppEventListener> pullEventListeners) {
        this.pullEventListeners = pullEventListeners;
    }

    public void clickIcon(int pullType, String packageName, String appName){

        IPullAppEventListener listener = pullEventListeners.get(packageName);
        if (listener!=null){
            listener.onClick(pullType, packageName, appName);
        }else{
            PullAppEventListenerFactory.getInstance().onClick(pullType, packageName, appName);
        }

    }

    public void setUserInfo(JSONObject params){
        if (params.has("uid")) {
            mUid = params.optString("uid");
            UserInfo.getInstance().setUid(mUid);
        }
        if (params.has("deviceid")) {
            mDeviceID = params.optString("deviceid");
            UserInfo.getInstance().setDeviceId(mDeviceID);
        }
        if (params.has("channel")) {
            mChannel = params.optString("channel");
            UserInfo.getInstance().setChannel(mChannel);
        }
    }

    private boolean hasNativeAd(){
        // TODO 优化判断条件
        if (hasNativeAdFlag){
            return true;
        }
        if (priority!=null&&(priority.contains(3)||priority.contains(2))){
            hasNativeAdFlag = true;
            return  true;
        }
        return false;
    }

    public void setCurrentShowAdTaskId(int showAdTaskId) {
        this.currentShowAdTaskId = showAdTaskId;
    }

    public List<Integer> getPriority() {
        return priority;
    }


}
