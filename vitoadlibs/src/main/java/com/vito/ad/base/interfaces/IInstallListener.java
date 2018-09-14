package com.vito.ad.base.interfaces;

public
interface IInstallListener {
    void onInstallSuccess();
    void onUninstall();
    void onDownloadFailed();
    void onUpDateProcess(int p);
}