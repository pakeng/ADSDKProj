package com.vito.ad.views.activitys;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.DownloadTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.video.MyVideo;
import com.vito.ad.views.video.interfaces.IVideoplayInfo;
import com.vito.utils.Log;
import com.vito.utils.file.FileUtil;

import vito.com.vitoadlibs.R;

public class PlayerActivity extends AppCompatActivity implements  MediaPlayer.OnErrorListener {
    private MyVideo videoView;
    private View video_layout;
    private ADTask adTask;
    private int playADid;
    private DownloadTask downLoadTask;
    private RelativeLayout landing_parentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getSupportActionBar()!=null)
            getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.player_view);
        AdTaskManager.getInstance().bindTargetAdActivity(this);
        prepareData();
        initView();
        initVideo();
    }

    private void prepareData() {
        playADid = AdManager.getInstance().getOneAdId();
        if (playADid == -1){
            Log.e("get playADid == -1");
            AdTaskManager.getInstance().onClose(adTask);
        }
        downLoadTask = DownloadTaskManager.getInstance().getDownloadTaskByADId(playADid);
        if (downLoadTask == null){
            DownloadTaskManager.getInstance().removeTaskByADId(playADid);
            Log.e("download task = null");
            prepareData();
        }
        if (downLoadTask!=null&&downLoadTask.getVideoDetail()!=null)
            downLoadTask.getVideoDetail().playTime++;
        adTask = AdTaskManager.getInstance().getAdTaskByADID(playADid);
        if (adTask == null) {
            Log.e("ad task = null");
            DownloadTaskManager.getInstance().removeTaskByADId(playADid);
            prepareData();
        }
        if (downLoadTask.getVideoDetail()!=null&&downLoadTask.getVideoDetail().playTime>=1) // 播放3次之后就设置本次播放完之后移除这个广告
            adTask.setRemoveOnClose(true);
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

        videoView.setScreenOrientation(this, adTask.getOrientation(), video_layout);
        videoView.setVideoURI(downLoadTask.getStoreUri());
        videoView.setiVideoplayInfo(new IVideoplayInfo() {
            @Override
            public void updateSecond(int seconds) {
            }

            @Override
            public void updateStatus(int state) {

            }
        });
        videoView.setiVideoPlayListener(AdTaskManager.getInstance().getVideoPlayerListener(adTask));
        videoView.start();
    }

    private void initView() {
        video_layout = findViewById(R.id.video_layout);
        videoView = findViewById(R.id.video_player);
        //String testuUrl = "http://cdn.oneway.mobi/cre/109/884a004bdd4e538c8219fd363e05b8c8.jpeg";
        View landingView = ViewManager.getInstance().buildLandingPageView(this, adTask);
        landing_parentLayout = findViewById(R.id.landing_page_layout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        if (landingView!=null)
            landing_parentLayout.addView(landingView, layoutParams);
        else {
            Log.e("LandView == null");
            AdTaskManager.getInstance().onClose(adTask);
        }
        landing_parentLayout.setVisibility(View.GONE);
        AdTaskManager.getInstance().onShowCallBack(adTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    protected void onDestroy() {
        videoView.suspend();
        super.onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("error what = "+ what+ " extra = "+ extra);
        downLoadTask.setDownloadCompleted(false);
        downLoadTask.setReDownload();
        DownloadTaskManager.getInstance().notifyUpDate();
        FileUtil.deleteFile(downLoadTask.getStoreUri());
        videoView.stopPlayback(); //播放异常，则停止播放，防止弹窗使界面阻塞
        videoView.setVisibility(View.INVISIBLE);
        video_layout.setVisibility(View.INVISIBLE);
        landing_parentLayout.setVisibility(View.VISIBLE);
        return true;
    }
}
