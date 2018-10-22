package com.vito.ad.base.interfaces;

import com.vito.ad.base.task.DownloadTask;

public interface IPullAppEventListener {
    void onClick(int type, String packageName, String appName); // 点击icon
    void onDownloadStart(DownloadTask downloadTask); // 开始下载
    void onDownloadSuccess(DownloadTask downloadTask); // 下载完成
    void onInstallStart(DownloadTask downloadTask);  // 开始安装
    void onInstallSuccess(DownloadTask downloadTask); // 安装成功
    void onUninstall(DownloadTask downloadTask);  // 卸载
    void onDownloadFailed(DownloadTask downloadTask); // 下载失败
    void onPull(int type, String packageName, String appName);

    void onUpDateProcess(int p); // 更新下载状态
}

