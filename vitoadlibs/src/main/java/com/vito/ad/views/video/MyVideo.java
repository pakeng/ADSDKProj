package com.vito.ad.views.video;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import com.vito.ad.views.video.interfaces.IVideoPlayListener;
import com.vito.ad.views.video.interfaces.IVideoplayInfo;

import java.util.Timer;
import java.util.TimerTask;

public class MyVideo extends VideoView{

    public static final int PLAY_START = 0;
    public static final int PLAY_PAUSE = 1;
    public static final int PLAY_ERROR = -1;
    public static final int PLAY_COMPLETION = 2;
    private IVideoplayInfo iVideoplayInfo;
    private int notifyTimes = 0;
    private IVideoPlayListener iVideoPlayListener;
    private boolean isStart = false;
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            int currentPosition = MyVideo.this.getCurrentPosition();
            if (iVideoplayInfo!=null){
                iVideoplayInfo.updateSecond(currentPosition);
            }
            notifyPlayListener(currentPosition);
        }
    };

    private void notifyPlayListener(int currentPosition) {
        if (iVideoPlayListener==null)
            return;
        if (notifyTimes==1&&getDuration()/4>=currentPosition) {
            notifyTimes++;
            iVideoPlayListener.onFirstQuartile();
        }
        if (notifyTimes==2&&getDuration()/2>=currentPosition) {
            notifyTimes++;
            iVideoPlayListener.onMid();
        }
        if (notifyTimes==3&&getDuration()/4>=currentPosition) {
            notifyTimes++;
            iVideoPlayListener.onThirdQuartile();
        }
    }

    public MyVideo(Context context) {
        super(context);
    }

    public MyVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyVideo(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //必须得注掉 不然的话就会默认的返回布局中的宽高
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取设备中的的总宽高
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //将这些值返回到布局当中使得可以使用
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    public void setScreenOrientation(Activity context, int orientation, View video_container){
            //设置全屏
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //设置手机屏幕横向
            context.setRequestedOrientation(orientation);

            //需要修改VieoView 的高度  如果不修改 当它横屏时不会去充满整个屏幕
            //首先获得原来的高度
            int mHeight = video_container.getHeight();
            //重新制定高度
//            ViewGroup.LayoutParams params = video_container.getLayoutParams();
//            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            video_container.setLayoutParams(params);
    }

    @Override
    public void start() {
        super.start();
        if (iVideoplayInfo!=null&&!isStart){
            iVideoplayInfo.updateStatus(MyVideo.PLAY_START);
        }
        if (iVideoPlayListener!=null&&!isStart){
            notifyTimes++;
            iVideoPlayListener.onStart();
        }
        if (!isStart)
            timer.schedule(timerTask, 0,1000L);
        isStart=true;
    }

    @Override
    public void pause() {
        super.pause();
        if(iVideoplayInfo!=null)
            iVideoplayInfo.updateStatus(MyVideo.PLAY_PAUSE);
    }

    @Override
    public void setOnCompletionListener(final MediaPlayer.OnCompletionListener l) {
        MediaPlayer.OnCompletionListener ll = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                l.onCompletion(mp);
                if (iVideoplayInfo!=null)
                    iVideoplayInfo.updateStatus(MyVideo.PLAY_COMPLETION);
                if (iVideoPlayListener!=null)
                    iVideoPlayListener.onEnd();
                timer.cancel();
            }
        };
        super.setOnCompletionListener(ll);
    }

    public void setiVideoplayInfo(IVideoplayInfo iVideoplayInfo) {
        this.iVideoplayInfo = iVideoplayInfo;
    }

    public IVideoPlayListener getiVideoPlayListener() {
        return iVideoPlayListener;
    }

    public void setiVideoPlayListener(IVideoPlayListener iVideoPlayListener) {
        this.iVideoPlayListener = iVideoPlayListener;
    }
}
