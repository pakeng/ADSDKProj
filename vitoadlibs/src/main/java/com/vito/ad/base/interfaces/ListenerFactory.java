package com.vito.ad.base.interfaces;

import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.video.interfaces.IVideoPlayListener;
import com.vito.utils.Log;
import com.vito.utils.network.NetHelper;

public class ListenerFactory {

    public static IVideoPlayListener getDefaultListener(){
        return new IVideoPlayListener() {

            @Override
            public void onStart() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoStartCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onEnd() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getEndCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onFirstQuartile() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoFirstQuartileCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onMid() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoMidCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onThirdQuartile() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getVideoThirdQuartileCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }
        };
    }

    public static IAdBaseInterface getDefaultIAdBaseInterface(){
        return new IAdBaseInterface() {
            @Override
            public void onShow() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getShowCallBackUrls()) {
                    Log.d("Callback", "onShow = " + url);
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onClose() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getCloseCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onDownLoadStart() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getDownloadStartCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onDownloadEnd() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getDownloadEndCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onDownloadError() {

            }

            @Override
            public void onInstallStart() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getStartInstallCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onInstallFinish() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getInstallCallBackUrls()) {
                    NetHelper.sendGetRequest(url);
                }
            }

            @Override
            public void onClick() {
                for (String url : AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()).getClickCallBackUrls()) {
                    // 修改 url中的宏
                    url = ViewManager.getInstance().rebuildDownloadUrl(AdTaskManager.getInstance().getAdTaskByADID(AdManager.getInstance().getCurrentShowAdTaskId()), url);
                    NetHelper.sendGetRequest(url);
                }
            }
        };
    }
}
