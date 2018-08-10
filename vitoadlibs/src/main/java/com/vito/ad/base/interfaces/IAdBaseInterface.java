package com.vito.ad.base.interfaces;

public interface IAdBaseInterface {
    void onShow();
    void onClose();
    void onDownLoadStart();
    void onDownloadEnd();
    void onDownloadError();
    void onInstallStart();
    void onInstallFinish();

    void onClick();
}
