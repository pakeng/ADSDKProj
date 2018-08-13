package com.vito.ad.managers;

import android.content.Intent;
import android.content.IntentFilter;

import com.google.gson.Gson;
import com.vito.ad.base.entity.CheckInstallList;
import com.vito.receivers.InstallReceiver;
import com.vito.utils.Log;
import com.vito.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class ReceiverManager {

    private static ReceiverManager instance = null;
    private InstallReceiver installBroadcast;
//    private ArrayList<int> checkInstalllist = new ArrayList<>();
    private List<Integer> checkList = new ArrayList<>();
    private CheckInstallList checkInstallList = null;
    public static ReceiverManager getInstance(){
        if (instance == null){
            synchronized (ReceiverManager.class){
                if (instance == null)
                    instance = new ReceiverManager();
            }
        }
        return instance;
    }

    private ReceiverManager(){
        // BUG
        String src = SharedPreferencesUtil.getStringValue(AdManager.mContext, "checkinstalllist", "checklist");
        if (src.isEmpty()){
            try {
                Gson gson = new Gson();
                checkInstallList = gson.fromJson(src, CheckInstallList.class);

            }catch (Exception e){
                Log.e("error"+e.toString());
            }finally {
                if (checkInstallList ==null){
                    checkList = new ArrayList<>();
                }else {
                    checkList = checkInstallList.getCheckInstallList();
                }
            }
        }
    }


    /**
     * 注册广播
     */
    public void registerBroadcast() {
        Log.e("adTest", "registerBroadcast");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);

        intentFilter.addDataScheme("package");
        installBroadcast = new InstallReceiver();
        DownloadTaskManager.getInstance().getService().registerReceiver(installBroadcast, intentFilter);
    }

    /**
     * 注销广播
     */
    public void unregisterBroadcast() {
        Log.e("adTest", "unregisterBroadcast");
        if (installBroadcast != null) {
            DownloadTaskManager.getInstance().getService().unregisterReceiver(installBroadcast);
            installBroadcast = null;
        }
    }

    public List<Integer> getCheckInstallList() {
        return checkList;
    }


    public void notifyUpdate() {
        Gson gson = new Gson();
        checkInstallList.setCheckInstallList(checkList);
        String src = gson.toJson(checkInstallList);
        SharedPreferencesUtil.putStringValue(AdManager.mContext, "checkinstalllist", "checklist", src);

    }
}
