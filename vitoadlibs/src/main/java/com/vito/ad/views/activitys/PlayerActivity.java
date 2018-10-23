package com.vito.ad.views.activitys;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.vito.ad.base.task.ADDownloadTask;
import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.channels.adhub.AdHubProcessor;
import com.vito.ad.channels.vlion.VlionProcessor;
import com.vito.ad.managers.ADDownloadTaskManager;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.video.MyVideo;
import com.vito.ad.views.video.interfaces.IVideoplayInfo;
import com.vito.utils.Log;
import com.vito.utils.file.FileUtil;

import vito.com.vitoadlibs.R;

public class PlayerActivity extends AppCompatActivity implements  MediaPlayer.OnErrorListener {
    private MyVideo videoView;
    private View video_layout;
    private ADInfoTask adInfoTask;
    private int playADid;
    private ADDownloadTask downLoadTaskAD;
    private RelativeLayout landing_parentLayout;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what==1001){
                initNativeView();
            }else if (msg.what == 1002){
                AdTaskManager.getInstance().onClose(adInfoTask);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar()!=null)
            getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AdTaskManager.getInstance().bindTargetAdActivity(this);
        prepareData();
        if (playADid>-2){
            AdManager.getInstance().setCurrentShowAdTaskId(playADid);
            setContentView(R.layout.player_view);
            initView();
            initVideo();
        }else{
            // 使用Native 广告
            setContentView(R.layout.native_ad_activity_layout);
            prepareNativeData();
        }
    }

    private void initNativeView() {
        landing_parentLayout = findViewById(R.id.landing_page_layout);
        View view = ViewManager.getInstance().buildLandingPageView(this, adInfoTask);
        if (view!=null){
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            landing_parentLayout.addView(view, layoutParams);
        }
    }

    private void prepareNativeData(){
        getNativeAdContent(0-playADid);
    }

    private void prepareData() {
        playADid = AdManager.getInstance().getOneAdId();
        // TODO　
        if (playADid < -1){
            // 返回-2 使用native广告
            return;
        }
        if (playADid == -1){
            Log.e("get playADid == -1");
            AdTaskManager.getInstance().onClose(adInfoTask);
        }
        downLoadTaskAD = ADDownloadTaskManager.getInstance().getDownloadTaskByADId(playADid);
        if (downLoadTaskAD == null){
            ADDownloadTaskManager.getInstance().removeTaskByADId(playADid);
            Log.e("download task = null");
            prepareData();
        }
        if (downLoadTaskAD !=null&& downLoadTaskAD.getVideoDetail()!=null)
            downLoadTaskAD.getVideoDetail().playTime++;
        adInfoTask = AdTaskManager.getInstance().getAdTaskByADID(playADid);
        if (adInfoTask == null) {
            Log.e("ad task = null");
            ADDownloadTaskManager.getInstance().removeTaskByADId(playADid);
            prepareData();
        }
        if (downLoadTaskAD.getVideoDetail()!=null&& downLoadTaskAD.getVideoDetail().playTime>=1) // 播放3次之后就设置本次播放完之后移除这个广告
            adInfoTask.setRemoveOnClose(true);
    }

    private void initVideo() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVisibility(View.INVISIBLE);
                video_layout.setVisibility(View.INVISIBLE);
                landing_parentLayout.setVisibility(View.VISIBLE);
                //videoView.setScreenOrientation(PlayerActivity.this, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, video_layout);
            }
        });

        videoView.setOnErrorListener(this);

        videoView.setScreenOrientation(this, adInfoTask.getOrientation(), video_layout);
        videoView.setVideoURI(downLoadTaskAD.getStoreUri());
        videoView.setiVideoplayInfo(new IVideoplayInfo() {
            @Override
            public void updateSecond(int seconds) {
            }

            @Override
            public void updateStatus(int state) {

            }
        });
        videoView.setiVideoPlayListener(AdTaskManager.getInstance().getVideoPlayerListener(adInfoTask));
        videoView.start();
    }

    private void initView() {
        video_layout = findViewById(R.id.video_layout);
        videoView = findViewById(R.id.video_player);
        View landingView = ViewManager.getInstance().buildLandingPageView(this, adInfoTask);
        landing_parentLayout = findViewById(R.id.landing_page_layout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        if (landingView!=null)
            landing_parentLayout.addView(landingView, layoutParams);
        else {
            Log.e("LandView == null");
            AdTaskManager.getInstance().onClose(adInfoTask);
        }
        landing_parentLayout.setVisibility(View.GONE);
        AdTaskManager.getInstance().onShowCallBack(adInfoTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView!=null)
            videoView.pause();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView!=null)
            videoView.start();
    }

    @Override
    protected void onDestroy() {
        if (videoView!=null)
            videoView.suspend();
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("error what = "+ what+ " extra = "+ extra);
        downLoadTaskAD.setDownloadCompleted(false);
        downLoadTaskAD.setReDownload();
        ADDownloadTaskManager.getInstance().notifyUpDate();
        FileUtil.deleteFile(downLoadTaskAD.getStoreUri());
        videoView.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
        videoView.setVisibility(View.INVISIBLE);
        video_layout.setVisibility(View.INVISIBLE);
        landing_parentLayout.setVisibility(View.VISIBLE);
        return true;
    }
    private void getNativeAdContent(int type){
        int i = AdManager.getInstance().getPriority().indexOf(type);
        AdManager.getInstance().getPriority().remove(new Integer(type));
        switch (type){
            case 2:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AdHubProcessor adHubProcessor = new AdHubProcessor();
                        adHubProcessor.startProcessor();
                        adInfoTask = adHubProcessor.getADTask();
                        if (adInfoTask ==null){
                            handler.sendEmptyMessage(1002);
                            return;
                        }
                        AdManager.getInstance().setCurrentShowAdTaskId(adInfoTask.getId());
                        handler.sendEmptyMessage(1001);
                    }
                }).start();
                break;
            case 3:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        VlionProcessor processor = new VlionProcessor();
                        processor.startProcessor();
                        adInfoTask = processor.getADTask();
                        if (adInfoTask ==null){
                            handler.sendEmptyMessage(1002);
                            return;
                        }

                        AdManager.getInstance().setCurrentShowAdTaskId(adInfoTask.getId());
                        handler.sendEmptyMessage(1001);

                    }
                }).start();
                break;
                default:
                    break;
        }
    }
}
