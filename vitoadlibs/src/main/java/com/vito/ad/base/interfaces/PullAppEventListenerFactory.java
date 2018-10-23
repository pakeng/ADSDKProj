package com.vito.ad.base.interfaces;

import com.vito.ad.base.task.ADDownloadTask;
import com.vito.ad.configs.Constants;
import com.vito.user.UserInfo;
import com.vito.utils.network.NetHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class PullAppEventListenerFactory {
    public static IPullAppEventListener getInstance(){

        return new DefaultImpl(null);
    }

    public static IPullAppEventListener getInstanceWithProcessListener(IPullAppEventListener listener){

        return new DefaultImpl(listener);
    }
}

class DefaultImpl implements IPullAppEventListener{
    IPullAppEventListener listener = null;
    DefaultImpl(IPullAppEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(int type, String packageName, String appName) {
        JSONObject params = new JSONObject();
        try {
            params.put("uid", UserInfo.getInstance().getUid());
            params.put("status", 0);
            params.put("channel", UserInfo.getInstance().getChannel());
            params.put("device", UserInfo.getInstance().getDeviceId());
            params.put("type", type);
            params.put("app", appName);
            params.put("package", packageName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetHelper.callWithNoResponse(Constants.getADSURL(), Constants.AD_EVENT_METHOD, params);

    }

    @Override
    public void onDownloadStart(ADDownloadTask ADDownloadTask) {
        makeCallBackWithState(1, ADDownloadTask);
    }

    @Override
    public void onDownloadSuccess(ADDownloadTask ADDownloadTask) {
        makeCallBackWithState(2, ADDownloadTask);
    }

    @Override
    public void onInstallStart(ADDownloadTask ADDownloadTask) {
        makeCallBackWithState(3, ADDownloadTask);
    }

    @Override
    public void onInstallSuccess(ADDownloadTask ADDownloadTask) {
        makeCallBackWithState(4, ADDownloadTask);
    }

    @Override
    public void onUninstall(ADDownloadTask ADDownloadTask) {

    }

    @Override
    public void onDownloadFailed(ADDownloadTask ADDownloadTask) {

    }

    @Override
    public void onPull(int type, String packageName, String appName) {
        JSONObject params = new JSONObject();
        try {
            params.put("uid", UserInfo.getInstance().getUid());
            params.put("status", 5);
            params.put("channel", UserInfo.getInstance().getChannel());
            params.put("device", UserInfo.getInstance().getDeviceId());
            params.put("type", type);
            params.put("app", appName);
            params.put("package", packageName);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetHelper.callWithNoResponse(Constants.getADSURL(), Constants.AD_EVENT_METHOD, params);
    }

    @Override
    public void onUpDateProcess(int p) {
        if (this.listener!=null){
            listener.onUpDateProcess(p);
        }
    }

    private void makeCallBackWithState(int state, ADDownloadTask ADDownloadTask){
        JSONObject params = new JSONObject();
        try {
            params.put("uid", UserInfo.getInstance().getUid());
            params.put("status", state);
            params.put("channel", UserInfo.getInstance().getChannel());
            params.put("device", UserInfo.getInstance().getDeviceId());
            params.put("type", ADDownloadTask.getPullType());
            params.put("app", ADDownloadTask.getAppName());
            params.put("package", ADDownloadTask.getPackageName());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        NetHelper.callWithNoResponse(Constants.getADSURL(), Constants.AD_EVENT_METHOD, params);

    }
}