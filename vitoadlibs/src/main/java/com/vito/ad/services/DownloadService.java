package com.vito.ad.services;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.vito.ad.base.interfaces.IAdBaseInterface;
import com.vito.ad.base.jdktool.ConcurrentHashSet;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.configs.Constants;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.DownloadTaskManager;
import com.vito.utils.APPUtil;
import com.vito.utils.Log;
import com.vito.utils.file.FileUtil;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

//import com.vito.ad.services.fixsysbugs.DownloadManager;

/**
 * 用DownloadManager来实现版本更新
 *
 * @author Phoenix
 * @date 2016-8-24 14:06
 */
public class DownloadService extends Service {
    private static final String TAG = DownloadService.class.getSimpleName();
    private ArrayList<DownloadTask> taskList = new ArrayList<>();
    public static final int HANDLE_DOWNLOAD = 0x001;
    public static final float UNBIND_SERVICE = 2.0F;
    @SuppressLint("HandlerLeak")
    public static Handler downLoadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (HANDLE_DOWNLOAD == msg.what) {
                //被除数可以为0，除数必须大于0
                if (msg.arg1 >= 0 && msg.arg2 > 0) {
                }
            }
        }
    };

    private DownloadBinder binder;
    private DownloadManager downloadManager;
    private DownloadChangeObserver downloadObserver;
    private BroadcastReceiver downLoadBroadcast;
    private ScheduledExecutorService scheduledExecutorService;



    @Override
    public void onCreate() {
        super.onCreate();
        binder = new DownloadBinder();
        registerBroadcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        downloadUrl = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL);
//        Log.i(TAG, "Apk下载路径传递成功：" + downloadUrl);
//        downloadApk(downloadUrl);
        return binder;
    }

    /**
     * 下载APK
     */
    private void downloadApk(DownloadTask task) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(task.getUrl()));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setAllowedOverRoaming(false);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(getApplicationContext(), task.getPath(), task.getName());
        task.setDownloadId(downloadManager.enqueue(request));
    }

    /**
     * 下载Video
     */
    private void downloadVideo(DownloadTask task) {
        Log.e("ADTEST", "start download video");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(task.getUrl()));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setAllowedOverRoaming(false);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationInExternalFilesDir(getApplicationContext(), task.getPath(), task.getName());
        task.setDownloadId(downloadManager.enqueue(request));
    }



    /**
     * 注册广播
     */
    private void registerBroadcast() {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(downLoadBroadcast = new DownLoadBroadcast(), intentFilter);
    }

    /**
     * 注销广播
     */
    private void unregisterBroadcast() {
        if (downLoadBroadcast != null) {
            unregisterReceiver(downLoadBroadcast);
            downLoadBroadcast = null;
        }
    }

    /**
     * 注册ContentObserver
     */
    private void registerContentObserver() {
        /** observer download change **/
        if (downloadObserver != null) {
            getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), false, downloadObserver);
        }
    }

    /**
     * 注销ContentObserver
     */
    private void unregisterContentObserver() {
        if (downloadObserver != null) {
            getContentResolver().unregisterContentObserver(downloadObserver);
        }
    }

    /**
     * 关闭定时器，线程等操作
     */
    private void close() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }


    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
     *
     * @param downloadId
     * @return
     */
    private int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[]{
                -1, -1, 0
        };
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载文件大小
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //下载文件的总大小
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //下载状态
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bytesAndStatus;
    }

    public void startTask() {
        ConcurrentHashSet<DownloadTask> Tasks = DownloadTaskManager.getInstance().getReadOnlyDownloadingTasks();
        if (downloadManager==null)
            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (downloadObserver==null)
            downloadObserver = new DownloadChangeObserver();
        // TODO  make Observer work

        for (DownloadTask task : Tasks) {
            if (task.isDwonloading()||task.isDownloadCompleted()){
                continue;
            }else if (FileUtil.isFileExists(
                        Uri.withAppendedPath(
                                Uri.fromFile(
                                        FileUtil.getDestinationInExternalPublicDir(
                                                AdManager.mContext.getApplicationContext(),
                                                task.getPath())),
                                task.getName()))) {
                if (task.getType()!=Constants.APK_DOWNLOAD)
                    onDownloadCompleted(Uri.withAppendedPath(Uri.fromFile(FileUtil.getDestinationInExternalPublicDir(AdManager.mContext.getApplicationContext(), task.getPath())), task.getName()), task);
                continue;
            }else{
                taskList.add(task);
            }
            startNextTask();
        }
    }

    /**
     * 接受下载完成广播
     */
    private class DownLoadBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            switch (intent.getAction()) {
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    if (downId != -1 && downloadManager != null) {
                        Uri downIdUri = downloadManager.getUriForDownloadedFile(downId);

                        if (downIdUri != null) {
                            Log.i(TAG, "广播监听下载完成，存储路径为 ：" + downIdUri.getPath());
                            DownloadTask task = DownloadTaskManager.getInstance().getDownloadTaskByDownloadId(downId);
                            if (task != null){
                                onDownloadCompleted(downIdUri, task);
                            }
                        }
                    }
                    startNextTask();
                    break;

                default:
                    break;
            }
        }
    }

    private void startNextTask() {
        if (taskList!=null&&!taskList.isEmpty()){
            DownloadTask task = taskList.get(0);
            if (task.getType() == Constants.ADVIDEO){
                downloadVideo(task);
            }else {
                ADTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getOriginId());
                if (adTask!=null)
                    AdTaskManager.getInstance().getIAdBaseInterface(adTask).onDownLoadStart();
                downloadApk(task);
            }
            taskList.remove(task);
        }



    }

    private void onDownloadCompleted(Uri downIdUri, DownloadTask task) {
        task.setStoreUri(downIdUri);
        task.setDownloadCompleted(true);
        if (task.getType()==Constants.APK_DOWNLOAD){
            ADTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getOriginId());
            if (adTask==null)
                return;
            IAdBaseInterface callback = AdTaskManager.getInstance().getIAdBaseInterface(adTask);
            callback.onDownloadEnd();
            callback.onInstallStart();
            APPUtil.installApkWithTask(AdManager.mContext, task);
        }else{
            AdManager.getInstance().refreshAllReadyADCount();
            AdManager.getInstance().notifyPrepare(true, task.getId());
        }
    }


    /**
     * 监听下载进度
     */
    private class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(downLoadHandler);
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        /**
         * 当所监听的Uri发生改变时，就会回调此方法
         *
         * @param selfChange 此值意义不大, 一般情况下该回调值false
         */
        @Override
        public void onChange(boolean selfChange) {

        }
    }

    public class DownloadBinder extends Binder {
        /**
         * 返回当前服务的实例
         *
         * @return
         */
        public DownloadService getService() {
            return DownloadService.this;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        unregisterContentObserver();
        Log.i(TAG, "下载任务服务销毁");
    }
}
