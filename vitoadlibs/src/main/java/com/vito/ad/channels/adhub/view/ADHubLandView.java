package com.vito.ad.channels.adhub.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.channels.adhub.response.AdHubResponseEnity;
import com.vito.ad.channels.adhub.response.AdLogo;
import com.vito.ad.channels.adhub.response.AdResponse;
import com.vito.ad.channels.adhub.response.ContentInfo;
import com.vito.ad.channels.adhub.response.SpaceInfo;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.ILandView;
import com.vito.ad.views.webview.MyWebView;
import com.vito.utils.Log;

import java.util.HashSet;
import java.util.List;

import vito.com.vitoadlibs.R;

public class ADHubLandView extends ILandView {
    MyWebView webView;
    ImageView imageView;
    @Override
    public void buildLandView(Context context, final ADTask adTask) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        coverLayout = layoutInflater.inflate(R.layout.native_ad_layout, null, false);

        // 获取点击事件位置
        View coverView = coverLayout.findViewById(R.id.native_cover_view);
        coverView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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


        // 通过传入的adTask 初始化内容
        AdHubResponseEnity adHubResponseEnity = adTask.getADObject(AdHubResponseEnity.class);
        if (!(adHubResponseEnity!=null&&adHubResponseEnity.getErrcode().equalsIgnoreCase("0"))){
            // 失败 关闭广告
            onClose(adTask);
            return;
        }
        // 继续显示
        // 显示H5内容
        webView = coverLayout.findViewById(R.id.native_content_view);
        if (checkEmpty(adHubResponseEnity.getSpaceInfo())){
            // 失败 关闭广告
            onClose(adTask);
            return;
        }
        SpaceInfo spaceInfo = adHubResponseEnity.getSpaceInfo().get(0);
        if (spaceInfo!=null){
            if (checkEmpty(spaceInfo.getAdResponse())){
                onClose(adTask);
                return;
            }

            // 获取广告内容
            final AdResponse adResponse = spaceInfo.getAdResponse().get(0);
            if (adResponse!=null){
                if (checkEmpty(adResponse.getContentInfo())){
                    onClose(adTask);
                    return;
                }
                // 广告素材信息
                ContentInfo contentInfo = adResponse.getContentInfo().get(0);
                if (contentInfo!=null) { // 获取到数据
                    if (contentInfo.getRenderType() == 3) { //RENDER_H5	3
                        AdTaskManager.getInstance().getIAdBaseInterface(adTask).onShow();
                        webView.loadData(contentInfo.getTemplate(),null, null);
                    }
                }
                // AD logo 信息显示
                AdLogo adLogo = adResponse.getAdLogo();
                if (adLogo!=null){
                    ImageView left_img = coverView.findViewById(R.id.native_left_img);
                    ImageView right_img = coverView.findViewById(R.id.native_right_img);
                    TextView left_text = coverView.findViewById(R.id.native_left_text);
                    TextView right_text = coverView.findViewById(R.id.native_right_text);
                    if (adLogo.getAdLabelUrl()!=null&&!adLogo.getAdLabelUrl().isEmpty()){
                        left_text.setVisibility(View.GONE);
                        left_img.setVisibility(View.VISIBLE);
                        Glide.with(context.getApplicationContext())
                                .setDefaultRequestOptions(
                                        new RequestOptions()
                                                .frame(1000000))
                                .load(adLogo.getAdLabelUrl())
                                .into(left_img);
                    }else {
                        left_text.setText(adLogo.getAdLabel());
                    }
                    if (adLogo.getSourceUrl()!=null&&!adLogo.getSourceUrl().isEmpty()){
                        right_text.setVisibility(View.GONE);
                        right_img.setVisibility(View.VISIBLE);
                        Glide.with(context.getApplicationContext())
                                .setDefaultRequestOptions(
                                        new RequestOptions()
                                                .frame(1000000))
                                .load(adLogo.getSourceUrl())
                                .into(right_img);
                    }else {
                        right_text.setText(adLogo.getSourceLabel());
                    }
                }
                coverView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adResponse.getInteractInfo().getLandingPageUrl()!=null
                                &&!adResponse.getInteractInfo().getLandingPageUrl().isEmpty()){
                            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(adResponse.getInteractInfo().getLandingPageUrl()));
                            AdManager.mContext.startActivity(it);
                            if (AdTaskManager.getInstance().getIAdBaseInterface(adTask)!=null) {
                                HashSet<String> urls = new HashSet<>();
                                for (String url: adTask.getClickCallBackUrls()){
                                    url = url.replace(".UTC_TS.", System.currentTimeMillis()/1000L+"")
                                    .replace(".AD_CLK_PT_DOWN_X.", ViewManager.getInstance().getStart_point().x+"")
                                    .replace(".AD_CLK_PT_DOWN_Y.", ViewManager.getInstance().getStart_point().y+"")
                                    .replace("..AD_CLK_PT_UP_X..", ViewManager.getInstance().getEnd_point().x+"")
                                    .replace(".AD_CLK_PT_UP_X.", ViewManager.getInstance().getEnd_point().y+"")
                                    .replace(".SCRN_CLK_PT_DOWN_X.", ViewManager.getInstance().getStart_point().x+"")
                                    .replace(".SCRN_CLK_PT_DOWN_Y.", ViewManager.getInstance().getStart_point().y+"")
                                    .replace(".SCRN_CLK_PT_UP_X.", ViewManager.getInstance().getEnd_point().x+"")
                                    .replace(".SCRN_CLK_PT_UP_Y.", ViewManager.getInstance().getEnd_point().y+"");
                                    urls.add(url);
                                }
                                adTask.setClickCallBackUrls(urls);

                                AdTaskManager.getInstance().getIAdBaseInterface(adTask).onClick();

                            }
                            onClose(adTask);
                        }

                    }
                });
                // 广告素材资源  用来获取显示广告时长
                // 暂时不使用
//                if (checkEmpty(contentInfo.getAdcontentSlot())){
//                    return;
//                }
//                AdcontentSlot adcontentSlot = contentInfo.getAdcontentSlot().get(0);
//                if (adcontentSlot!=null){
//                    CountDownView countDownView = coverView.findViewById(R.id.native_count_down_view);
//                    countDownView.setCountdownTime(adcontentSlot.getPlayTime()<=1?5:adcontentSlot.getPlayTime());
//                    countDownView.setAddCountDownListener(new CountDownView.OnCountDownFinishListener() {
//                        @Override
//                        public void countDownFinished() {
//
//                        }
//                    });
//                }
            }
        }



        // 关闭按钮事件
        Button closeBtn = coverLayout.findViewById(R.id.native_close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ADTEST", "click close");
                onClose(adTask);
            }
        });

        ViewManager.getInstance().getSize().x = coverView.getWidth();
        ViewManager.getInstance().getSize().y = coverView.getHeight();
    }

    @Override
    public void onClose(ADTask adTask) {
        super.onClose(adTask);

    }


    private boolean checkEmpty(List o){
        if (o==null||o.isEmpty()){
            return true;
        }else {
            return false;
        }
    }

}
