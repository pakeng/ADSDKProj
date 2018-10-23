package com.vito.ad.base.interfaces;

import com.vito.ad.base.task.ADDownloadTask;

public interface IPullAppEventListener {
    void onClick(int type, String packageName, String appName); // 点击icon
    void onDownloadStart(ADDownloadTask ADDownloadTask); // 开始下载
    void onDownloadSuccess(ADDownloadTask ADDownloadTask); // 下载完成
    void onInstallStart(ADDownloadTask ADDownloadTask);  // 开始安装
    void onInstallSuccess(ADDownloadTask ADDownloadTask); // 安装成功
    void onUninstall(ADDownloadTask ADDownloadTask);  // 卸载
    void onDownloadFailed(ADDownloadTask ADDownloadTask); // 下载失败
    void onPull(int type, String packageName, String appName);

    void onUpDateProcess(int p); // 更新下载状态
}

