package com.vito.ad.managers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.vito.ad.base.entity.ADServerResponse;
import com.vito.ad.base.interfaces.IPrepareCompleteCallBack;
import com.vito.ad.base.interfaces.IShowCallBack;
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
import com.vito.utils.DeviceInfo;
import com.vito.utils.Log;
import com.vito.utils.SharedPreferencesUtil;
import com.vito.utils.ThreadExecutor;
import com.vito.utils.network.NetHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
    private int mAllReadyADCount;
    private int currentWhere; // 当前打开广告的地方，类似于透传
    private IShowCallBack showCallBack; // 播放回调
    private int currentShowAdTaskId;
    private List<Integer> priority = new ArrayList<>();
    private String showToken = "";
    private String mUid;
    private String mDeviceID;
    private String mChannel;
    private String subChannelStr;

    public static AdManager InitAdManager(Activity ctx){
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


    private AdManager(Activity ctx){
        mContext = ctx;
        InitAnalysisConfigData();
        getChanelInfo();
    }

    private String prepareAdInfo(){
        DeviceInfo deviceInfo = new DeviceInfo(mContext);
        mADDeviceInfo = deviceInfo.getADInfoString();
        return mADDeviceInfo;
    }

    public void PrepareAD(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (!prepareADs())
                    Log.e("error prepareAD");
            }
        };

        ThreadExecutor.getInstance().addTask(task);
    }

    private boolean prepareADs(){
        for (IProcessor processor : ProcesserManager.getInstance().getProcessers()){
            if (priority!=null&&priority.contains(processor.getType())){
                processor.startProcessor();
                DownloadTask task = processor.getDownLoadTask();
                ADTask adTask = processor.getADTask();
                AdTaskManager.getInstance().pushTask(adTask);
                DownloadTaskManager.getInstance().pushTask(task);
            }
        }
        return true;
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
            if (isSuccess)
                mPrepareCompleteCallback.onSuccess(id, count);
            else
                mPrepareCompleteCallback.onFailed(id, count);
        }
    }

    private int getAllReadyADCount() {
        checkReadyAd();
        return mAllReadyADCount;
    }

    public void AddAllReadyADCount() {
        checkReadyAd();
        SharedPreferencesUtil.putIntValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "allreadyadcount",mAllReadyADCount);
    }

    public boolean ShowAd(JSONObject params, int where){
        if (params.has("token"))
            showToken = params.optString("token");
        if (params.has("uid"))
            mUid = params.optString("uid");
        if (params.has("deviceid"))
            mDeviceID = params.optString("deviceid");
        if (params.has("channel"))
            mChannel = params.optString("channel");
        currentWhere = where;
        if (getAllReadyADCount()>0&&getAllReadyADCount()!=-1){
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

    public void onADClose() {
        if (showCallBack!=null){
            showCallBack.onClose(currentWhere);
        }
        // 发送 完成广告的回调给游戏服务器
        DownloadTask downloadTask = DownloadTaskManager.getInstance().getDownloadTaskByADId(currentShowAdTaskId);
        JSONObject params = new JSONObject();
        try {
            params.put("uid", mUid);
            params.put("token", showToken);
            params.put("status", 1);
            params.put("channel", mChannel);
            params.put("device", mDeviceID);
            params.put("is_down", downloadTask.isApkDownload()?1:0);
            params.put("where", currentWhere/1000);
            params.put("level", currentWhere%1000);
            params.put("ad_type", downloadTask.getAD_Type());
            params.put("app", downloadTask.getAppName()+" ");
            params.put("ad", downloadTask.getADname()+" ");
            params.put("ad_id", downloadTask.getSortNum()+"");
            params.put("package", downloadTask.getPackageName());
            params.put("price", downloadTask.getPrice());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetHelper.callWithNoResponse(Constants.getADSURL(), Constants.CLOSE_METHOD, params);
    }

    public int getCurrentShowAdTaskId() {
        return currentShowAdTaskId;
    }

    // 检查可用的广告数量
    private void checkReadyAd(){
        mAllReadyADCount = 0;
        for (ADTask task : AdTaskManager.getInstance().getReadOnlyAdTasks()) {
            if (!task.isRemove()){
                DownloadTask downloadTask = DownloadTaskManager.getInstance().getDownloadTaskByADId(task.getId());
                if (downloadTask!=null&&downloadTask.isDownloadCompleted())
                    mAllReadyADCount++;
            }
        }
    }

    private int getOneAdIdWithTable(List<Integer> priority_table) {
        for (int target : priority_table) {
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

    private void getChanelInfo(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("channel", subChannelStr);
                } catch (JSONException e) {
                    e.printStackTrace();
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
//            isDebug = false;
        }
  }


//  public void testDownloadAndInstall(){
//      new Thread(new Runnable() {
//          @Override
//          public void run() {
//              DownloadTask d = new DownloadTask();
//              d.setType(Constants.APK_DOWNLOAD);
//              d.setUrl("http://dl.lianwifi.com/download/android/WifiKey-3213-guanwang.apk");
//              d.setAppName("mianfeiwifi.apk");
//              d.setName("mianfeiwifi.apk");
//              d.setId(555);
//              d.setOriginId(1);
//              DownloadTaskManager.getInstance().pushTask(d);
//          }
//      }).start();
//  }


}
