package com.vito.ad.channels.lanmei.view;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.DownloadTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.ILandView;
import com.vito.ad.views.webview.MyWebView;
import com.vito.utils.Log;

import vito.com.vitoadlibs.R;

public class LMLandView extends ILandView {

    @Override
    public void buildLandView(Context context, final ADTask adTask) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        coverLayout = layoutInflater.inflate(R.layout.landing_page_web, null, false);
        MyWebView webView = coverLayout.findViewById(R.id.myWebView);
        View coverView = coverLayout.findViewById(R.id.webCover);
        coverView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ADTEST", "clidk coverView");
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ViewManager.getInstance().getStart_point().x = (int) event.getX();
                        ViewManager.getInstance().getStart_point().y = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        ViewManager.getInstance().getEnd_point().x = (int) event.getX();
                        ViewManager.getInstance().getEnd_point().y = (int) event.getY();
                        break;
                }
                return false;
            }
        });
        coverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adTask!=null&&adTask.getDownloadApkUrl()!=null&&adTask.getDownloadApkUrl().endsWith(".apk")){
                    DownloadTask downloadTask = DownloadTaskManager.getInstance().buildDownloadTaskByADTask(adTask);
                    DownloadTaskManager.getInstance().pushTask(downloadTask);
                }
                AdTaskManager.getInstance().getIAdBaseInterface(adTask).onClick();
                onClose(adTask);
            }
        });
        Button cloaeBtn = coverLayout.findViewById(R.id.close_ad);
        cloaeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ADTEST", "click close");
                onClose(adTask);
            }
        });
        String landdata = new String(Base64.decode(adTask.getLanding_Page(), Base64.DEFAULT));
        webView.loadDataWithBaseURL(null, landdata,"text/html", "utf-8", null);
        ViewManager.getInstance().getSize().x = webView.getWidth();
        ViewManager.getInstance().getSize().y = webView.getHeight();
    }

    @Override
    public void onClose(ADTask adTask) {
        super.onClose(adTask);

    }
}
