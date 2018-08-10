package com.vito.ad.managers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vito.ad.base.interfaces.IAdBaseInterface;
import com.vito.ad.base.jdktool.ConcurrentHashSet;
import com.vito.ad.base.task.ADTask;
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

    public ConcurrentHashSet<ADTask> getReadOnlyAdTasks() {
        return adTasks;
    }

    public ConcurrentHashSet<ADTask> pushTask(ADTask task){
        if (task!=null) {
            synchronized (this){
                adTasks.add(task);
                upDateSaveFile();
            }

        }
        return adTasks;
    }

    // 更新保存的任务文件
    private void upDateSaveFile() {
        // 序列化
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new UriSerializer())
                .create();
        String json = gson.toJson(adTasks);
        SharedPreferencesUtil.putStringValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "adtasks",json );
        SharedPreferencesUtil.putIntValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "currentadid", currentADID);
    }

    public void setAdTasks(ConcurrentHashSet<ADTask> adTasks) {
        this.adTasks = adTasks;
    }

    private ConcurrentHashSet<ADTask> adTasks = new ConcurrentHashSet<ADTask>();
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
        Type type = new TypeToken<ConcurrentHashSet<ADTask>>(){}.getType();
        ConcurrentHashSet<ADTask> tasks = gson.fromJson(adtasks_json, type);
        if (tasks!=null){
            // 移除无效的
            Iterator<ADTask> it = tasks.iterator();
            for(int i=0; i<tasks.size(); i++){
                ADTask task = it.next();
                if (task.isRemove()){
                    it.remove();
                    i--;
                }
            }
            adTasks = tasks;
        }
        currentADID = SharedPreferencesUtil.getIntValue(AdManager.mContext, Constants.AD_CONFIG_FILE_NAME, "currentadid");
    }


    public ADTask getAdTaskByADID(int adid) {
        for (ADTask task : adTasks) {
            if (task.getId() == adid){
                return task;
            }
        }
        return null;
    }

    public IVideoPlayListener getVideoPlayerListener(final ADTask adTask) {
       return iVideoPlayListenerHashMap.get(adTask.getType());
    }

    public void onShowCallBack(ADTask adTask) {
        if (getIAdBaseInterface(adTask)!=null)
            getIAdBaseInterface(adTask).onShow();
    }

    public void onClose(ADTask adTask){
        if (getIAdBaseInterface(adTask)!=null)
            getIAdBaseInterface(adTask).onClose();
        if (targetAdActivity!=null){
            targetAdActivity.finish();
            targetAdActivity = null;
        }
        if (adTask!=null){
            AdManager.getInstance().onADClose(true); // 不为空就返回下发道具
            removeTask(adTask);
        }else {
            AdManager.getInstance().onADClose(false);
        }

    }

    private void removeTask(ADTask adTask) {
        if (adTask.isRemoveOnClose()){
            adTask.setRemove(true);
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

    public IAdBaseInterface getIAdBaseInterface(final ADTask adTask) {
        return iAdBaseInterfaceHashMap.get(adTask.getType());
    }

    public void bindTargetAdActivity(Activity targetAdActivity) {
        this.targetAdActivity = targetAdActivity;
    }

}
