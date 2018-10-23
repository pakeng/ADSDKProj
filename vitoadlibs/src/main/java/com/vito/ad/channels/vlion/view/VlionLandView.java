package com.vito.ad.channels.vlion.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.vito.ad.base.task.ADDownloadTask;
import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.channels.vlion.response.DownloadDetail;
import com.vito.ad.channels.vlion.response.Gdt_Download_Bean;
import com.vito.ad.channels.vlion.response.VlionResponse;
import com.vito.ad.managers.ADDownloadTaskManager;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.ILandView;
import com.vito.ad.views.webview.MyWebView;
import com.vito.utils.Log;
import com.vito.utils.ThreadExecutor;
import com.vito.utils.network.NetHelper;

import java.util.HashSet;

import jp.wasabeef.glide.transformations.BlurTransformation;
import vito.com.vitoadlibs.R;

public class VlionLandView extends ILandView {
    MyWebView webView;
    ImageView imageView;
    Context mContext;
    private static BlurTransformation blurTransformation = new BlurTransformation( 14, 3);
    @Override
    public void buildLandView(Context context, final ADInfoTask adInfoTask) {
        mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        coverLayout = layoutInflater.inflate(R.layout.native_ad_layout_img, null, false);
        // 通过传入的adTask 初始化内容
        final VlionResponse adResponse = adInfoTask.getADObject(VlionResponse.class);
        AdTaskManager.getInstance().onShowCallBack(adInfoTask);

        webView = coverLayout.findViewById(R.id.native_content_view);
        imageView = coverLayout.findViewById(R.id.native_image_view);
        if (adResponse.getCtype() == 1) {
            webView.loadData(adResponse.getAdm(), "text/html","utf-8");
            webView.setVisibility(View.VISIBLE);
        } else if (adResponse.getCtype() == 2|| adResponse.getCtype() == 3){
            imageView.setVisibility(View.VISIBLE);
            Glide.with(context.getApplicationContext())
                    .setDefaultRequestOptions(
                            new RequestOptions()
                                    .frame(1000000)
                                    .centerInside())
                    .load(adResponse.getImgurl())
                    .into(imageView);
            Glide.with(context.getApplicationContext())
                    .setDefaultRequestOptions(RequestOptions.bitmapTransform(blurTransformation))
                    .load(adResponse.getImgurl())
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                coverLayout.setBackground(resource);
                            else
                                coverLayout.setBackgroundDrawable(resource);
                        }
                    });
        }
        else {
            webView.loadUrl(adResponse.getLdp());
            webView.setVisibility(View.VISIBLE);
        }
        // 获取点击事件位置
        View coverView = coverLayout.findViewById(R.id.native_cover_view);
        coverView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
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
        if (adResponse.getCtype()!=1&&adResponse.getLdp()!=null){
            coverView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (adResponse.getInteract_type()==1){
                        if (adResponse.isIs_gdt()){
                            // 获取广点通 返回的json然后解析构建下载
                            ThreadExecutor.getInstance().addTask(buildRunnable(adInfoTask, adResponse));
                            onClose(adInfoTask);
                          return;
                        }
                        //构建下载
                        String url = ViewManager.getInstance().rebuildDownloadUrl(adInfoTask, adResponse.getLdp());
                        adInfoTask.setDownloadApkUrl(url);
                        ADDownloadTask ADDownloadTask = ADDownloadTaskManager.getInstance().buildDownloadTaskByADTask(adInfoTask);
                        ADDownloadTaskManager.getInstance().pushTask(ADDownloadTask);
                        onClose(adInfoTask);
                        return;

                    }

                    String url = ViewManager.getInstance().rebuildDownloadUrl(adInfoTask, adResponse.getLdp());
                    Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    AdManager.mContext.startActivity(it);
                    AdTaskManager.getInstance().getIAdBaseInterface(adInfoTask).onClick();
                    onClose(adInfoTask);
                }
            });
        }


        // 关闭按钮事件
        Button closeBtn = coverLayout.findViewById(R.id.native_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ADTEST", "click close");
                onClose(adInfoTask);
            }
        });

        ViewManager.getInstance().getSize().x = coverView.getWidth();
        ViewManager.getInstance().getSize().y = coverView.getHeight();


    }



    @Override
    public void onClose(ADInfoTask adInfoTask) {
        super.onClose(adInfoTask);

    }

    private Runnable buildRunnable(final ADInfoTask adInfoTask, final VlionResponse adResponse){
        return new Runnable() {
            @Override
            public void run() {
                // 获取下载内容
                String url = ViewManager.getInstance().rebuildDownloadUrl(adInfoTask, adResponse.getLdp());
                String result = NetHelper.doGetHttpResponse(url, 1);
                Gson gson = new Gson();
                try {
                    Gdt_Download_Bean bean = gson.fromJson(result, Gdt_Download_Bean.class);
                    if (bean.isSuccess()){
                        DownloadDetail detail = bean.getData();
                        if (detail!=null){
                            adInfoTask.setDownloadApkUrl(detail.getDstlink());
                            buildConvTrackingUrls(detail.getClickid());
                        }
                    }
                }catch (Exception e){
                    Log.e("build gdt download bean error"+ e.getMessage());
                    return;
                }


                ADDownloadTask ADDownloadTask = ADDownloadTaskManager.getInstance().buildDownloadTaskByADTask(adInfoTask);
                ADDownloadTaskManager.getInstance().pushTask(ADDownloadTask);
                onClose(adInfoTask);
            }

            private void buildConvTrackingUrls(String clickid) {
                HashSet<String> t1 = new HashSet<>();
                for (String url : adInfoTask.getDownloadStartCallBackUrls()){
                    url = url.replace("__CLICK_ID__", clickid);
                    t1.add(url);
                }
                adInfoTask.setDownloadStartCallBackUrls(t1);
                // install end
                HashSet<String> t2 = new HashSet<>();
                for (String url : adInfoTask.getInstallCallBackUrls()){
                    url = url.replace("__CLICK_ID__", clickid);
                    t2.add(url);
                }
                adInfoTask.setInstallFinishCallBackUrls(t2);
                // download finish
                HashSet<String> t3 = new HashSet<>();
                for (String url : adInfoTask.getDownloadEndCallBackUrls()){
                    url = url.replace("__CLICK_ID__", clickid);
                    t3.add(url);
                }
                adInfoTask.setDownloadEndCallBackUrls(t3);
                // install start
                HashSet<String> t4 = new HashSet<>();
                for (String url : adInfoTask.getStartInstallCallBackUrls()){
                    url = url.replace("__CLICK_ID__", clickid);
                    t4.add(url);
                }
                adInfoTask.setStartInstallCallBackUrls(t4);
                // TODO 激活 HOW TODO
//                                    HashSet<String> t1 = new HashSet<>();
//                                    for (String url : adInfoTask.getDownloadStartCallBackUrls()){
//                                        url = url.replace("__CLICK_ID__", clickid);
//                                        t1.add(url);
//                                    }
//                                    adInfoTask.setDownloadStartCallBackUrls(t1);
            }
        };
    }
}
