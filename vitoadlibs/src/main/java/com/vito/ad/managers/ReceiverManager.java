package com.vito.ad.managers;

import android.content.Intent;
import android.content.IntentFilter;
import com.vito.utils.Log;

import com.vito.receivers.InstallReceiver;

import java.util.ArrayList;

public class ReceiverManager {

    private static ReceiverManager instance = null;
    private InstallReceiver installBroadcast;
//    private ArrayList<int> checkInstalllist = new ArrayList<>();
    private ArrayList<Integer> checkInstallList = new ArrayList<>();
    private ArrayList<String> checkInstallPackageNameList = new ArrayList<>();

    public static ReceiverManager getInstance(){
        if (instance == null){
            synchronized (ReceiverManager.class){
                if (instance == null)
                    instance = new ReceiverManager();
            }
        }
        return instance;
    }

    private ReceiverManager(){};


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

    public ArrayList<Integer> getCheckInstallList() {
        return checkInstallList;
    }


    public ArrayList<String> getCheckInstallPackageNameList() {
        return checkInstallPackageNameList;
    }
}
