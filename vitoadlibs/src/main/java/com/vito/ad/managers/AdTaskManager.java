package com.vito.ad.managers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vito.ad.base.interfaces.IAdBaseInterface;
import com.vito.ad.base.jdktool.ConcurrentHashSet;
import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.configs.Constants;
import com.vito.ad.views.video.interfaces.IVideoPlayListener;
import com.vito.utils.SharedPreferencesUtil;
import com.vito.utils.gsonserializer.UriDeserializer;
import com.vito.utils.gsonserializer.UriSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

public class AdTaskManager {
    private static AdTaskManager instance = null;
    private int currentADID = -1;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, IVideoPlayListener> iVideoPlayListenerHashMap = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, IAdBaseInterface> iAdBaseInterfaceHashMap = new HashMap<>();
    private Activity targetAdActivity = null; // 广告sdk的载体
    private AdTaskManager(){ Init();}

    public ConcurrentHashSet<ADInfoTask> getReadOnlyAdTasks() {
        return adInfoTasks;
    }

    public ConcurrentHashSet<ADInfoTask> pushTask(ADInfoTask task){
        if (task!=null) {
            synchronized (this){
                adInfoTasks.add(task);
                upDateSaveFile();
            }

        }
        return adInfoTasks;
    }

    // 更新保存的任务文件
    private void upDateSaveFile() {
        // 序列化
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        String json = gson.toJson(adInfoTasks);
        SharedPreferencesUtil.putStringValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "adtasks",json );
        SharedPreferencesUtil.putIntValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "currentadid", currentADID);
    }

    public void setAdInfoTasks(ConcurrentHashSet<ADInfoTask> adInfoTasks) {
        this.adInfoTasks = adInfoTasks;
    }

    private ConcurrentHashSet<ADInfoTask> adInfoTasks = new ConcurrentHashSet<ADInfoTask>();
    public static AdTaskManager getInstance(){
        if (instance==null){
            synchronized (AdTaskManager.class){
                if (instance == null)
                    instance = new AdTaskManager();
            }
        }
        return  instance;
    }

    /**
     * 初始化
     */
    private void Init(){

        InitWithConfig();
    }

    /**
     * 获取新的广告id
     */

    public int getNextADID(){
        return currentADID++;
    }

    private void InitWithConfig() {
        // TODO  读取文件，初始化之前的数据
        String adtasks_json = SharedPreferencesUtil.getStringValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "adtasks");
        // 反序列化
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriDeserializer())
                .create();
        Type type = new TypeToken<ConcurrentHashSet<ADInfoTask>>(){}.getType();
        ConcurrentHashSet<ADInfoTask> tasks = gson.fromJson(adtasks_json, type);
        if (tasks!=null){
            // 移除无效的
            Iterator<ADInfoTask> it = tasks.iterator();
            for(int i=0; i<tasks.size(); i++){
                ADInfoTask task = it.next();
                if (task.isRemove()){
                    it.remove();
                    i--;
                }
            }
            adInfoTasks = tasks;
        }
        currentADID = SharedPreferencesUtil.getIntValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "currentadid");
    }


    public ADInfoTask getAdTaskByADID(int adid) {
        for (ADInfoTask task : adInfoTasks) {
            if (task.getId() == adid){
                return task;
            }
        }
        return null;
    }

    public IVideoPlayListener getVideoPlayerListener(final ADInfoTask adInfoTask) {
       return iVideoPlayListenerHashMap.get(adInfoTask.getType());
    }

    public void onShowCallBack(ADInfoTask adInfoTask) {
        if (getIAdBaseInterface(adInfoTask)!=null)
            getIAdBaseInterface(adInfoTask).onShow();
    }

    public void onClose(ADInfoTask adInfoTask){
        if (getIAdBaseInterface(adInfoTask)!=null)
            getIAdBaseInterface(adInfoTask).onClose();
        if (targetAdActivity!=null){
            targetAdActivity.finish();
            targetAdActivity = null;
        }
        if (adInfoTask !=null){
            AdManager.getInstance().onADClose(true); // 不为空就返回下发道具
            removeTask(adInfoTask);
        }else {
            AdManager.getInstance().onADClose(false);
        }

    }

    private void removeTask(ADInfoTask adInfoTask) {
        if (adInfoTask.isRemoveOnClose()){
            adInfoTask.setRemove(true);
            upDateSaveFile();
            // 刷新广告数量
            AdManager.getInstance().PrepareAD();
        }
    }


    public void registerIVideoPlayListener(int adType, IVideoPlayListener listener) {
        this.iVideoPlayListenerHashMap.put(adType, listener);
    }

    public void registerIAdBaseInterface(int adType, IAdBaseInterface listener) {
        this.iAdBaseInterfaceHashMap.put(adType, listener);
    }

    public IAdBaseInterface getIAdBaseInterface(final ADInfoTask adInfoTask) {
        if (adInfoTask ==null)
            return new IAdBaseInterface() {
                @Override
                public void onShow() {

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onDownLoadStart() {

                }

                @Override
                public void onDownloadEnd() {

                }

                @Override
                public void onDownloadError() {

                }

                @Override
                public void onInstallStart() {

                }

                @Override
                public void onInstallFinish() {

                }

                @Override
                public void onClick() {

                }
            };
        IAdBaseInterface callback = iAdBaseInterfaceHashMap.get(adInfoTask.getType());
        if (callback==null)
            return new IAdBaseInterface() {
                @Override
                public void onShow() {

                }

                @Override
                public void onClose() {

                }

                @Override
                public void onDownLoadStart() {

                }

                @Override
                public void onDownloadEnd() {

                }

                @Override
                public void onDownloadError() {

                }

                @Override
                public void onInstallStart() {

                }

                @Override
                public void onInstallFinish() {

                }

                @Override
                public void onClick() {

                }
            };

        return callback;
    }

    public void bindTargetAdActivity(Activity targetAdActivity) {
        this.targetAdActivity = targetAdActivity;
    }

}
