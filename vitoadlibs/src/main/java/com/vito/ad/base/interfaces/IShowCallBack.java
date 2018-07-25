package com.vito.ad.base.interfaces;

public interface IShowCallBack {
    void onClose(int where);
    void onCancel(int where);
    void onError(int where);
}
