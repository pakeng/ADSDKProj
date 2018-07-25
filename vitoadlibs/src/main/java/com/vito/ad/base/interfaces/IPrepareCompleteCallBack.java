package com.vito.ad.base.interfaces;

public interface IPrepareCompleteCallBack {
    void onSuccess(int Adid, int allReadyAd);
    void onFailed(int Adid, int allReadyAd);
    void onReadyPlay(int count);
}
