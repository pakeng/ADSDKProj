package cn.pinode.chat.gdt_advertisement_sdklibrary;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;

import java.util.List;


public class NativeExpressActivity extends Activity implements
        NativeExpressAD.NativeExpressADListener{

  private static String TAG = Constants.TAG;
  private ViewGroup container;
  private NativeExpressAD nativeExpressAD;
  private NativeExpressADView nativeExpressADView;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_native_express);
    container = (ViewGroup) findViewById(R.id.container);
    refreshAd();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (nativeExpressADView != null) {
      nativeExpressADView.destroy();
    }
  }

  private String getPosId() {
    return getIntent().getStringExtra(Constants.POS_ID);
  }


  private void refreshAd() {
    try {

      hideSoftInput();
      /**
       *  如果选择支持视频的模版样式，请使用{@link Constants#NativeExpressSupportVideoPosID}
       */
      nativeExpressAD = new NativeExpressAD(this, getMyADSize(), Constants.getAppid(), getPosId(), this); // 这里的Context必须为Activity
      nativeExpressAD.setVideoOption(new VideoOption.Builder()
              .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // 设置什么网络环境下可以自动播放视频
              .setAutoPlayMuted(true) // 设置自动播放视频时，是否静音
              .build()); // setVideoOption是可选的，开发者可根据需要选择是否配置
      nativeExpressAD.loadAD(1);
    } catch (NumberFormatException e) {
      Log.w(TAG, "ad size invalid.");
    }
  }

  private ADSize getMyADSize() {
    return new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
  }


  /**
   * 获取广告数据
   *
   * @param nativeExpressADView
   * @return
   */
  private String getAdInfo(NativeExpressADView nativeExpressADView) {
    AdData adData = nativeExpressADView.getBoundData();
    if (adData != null) {
      StringBuilder infoBuilder = new StringBuilder();
      infoBuilder.append("title:").append(adData.getTitle()).append(",")
          .append("desc:").append(adData.getDesc()).append(",")
          .append("patternType:").append(adData.getAdPatternType());
      if (adData.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
        infoBuilder.append(", video info: ").append(getVideoInfo(adData.getProperty(AdData.VideoPlayer.class)));
      }
      return infoBuilder.toString();
    }
    return null;
  }

  /**
   * 获取播放器实例
   *
   * 仅当视频回调{@link NativeExpressMediaListener#onVideoInit(NativeExpressADView)}调用后才会有返回值
   *
   * @param videoPlayer
   * @return
   */
  private String getVideoInfo(AdData.VideoPlayer videoPlayer) {
    if (videoPlayer != null) {
      StringBuilder videoBuilder = new StringBuilder();
      videoBuilder.append("{state:").append(videoPlayer.getVideoState()).append(",")
          .append("duration:").append(videoPlayer.getDuration()).append(",")
          .append("position:").append(videoPlayer.getCurrentPosition()).append("}");
      return videoBuilder.toString();
    }
    return null;
  }

  @Override
  public void onNoAD(AdError adError) {
    Log.i(
        TAG,
        String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
            adError.getErrorMsg()));

    GDTManager.getInstance(null).getGdtListener().onClose(false, GDTManager.ERROR);
  }

  @Override
  public void onADLoaded(List<NativeExpressADView> adList) {
    Log.i(TAG, "onADLoaded: " + adList.size());
    // 释放前一个展示的NativeExpressADView的资源
    if (nativeExpressADView != null) {
      nativeExpressADView.destroy();
    }

    if (container.getVisibility() != View.VISIBLE) {
      container.setVisibility(View.VISIBLE);
    }

    if (container.getChildCount() > 0) {
      container.removeAllViews();
    }

    nativeExpressADView = adList.get(0);
    Log.i(TAG, "onADLoaded, video info: " + getAdInfo(nativeExpressADView));
    if (nativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
      nativeExpressADView.setMediaListener(mediaListener);
    }
    // 广告可见才会产生曝光，否则将无法产生收益。
    container.addView(nativeExpressADView);
    nativeExpressADView.render();
  }

  @Override
  public void onRenderFail(NativeExpressADView adView) {
    Log.i(TAG, "onRenderFail");
    GDTManager.getInstance(null).getGdtListener().onClose(false, GDTManager.ERROR);
  }

  @Override
  public void onRenderSuccess(NativeExpressADView adView) {
    Log.i(TAG, "onRenderSuccess");
  }

  @Override
  public void onADExposure(NativeExpressADView adView) {
    Log.i(TAG, "onADExposure");

  }

  @Override
  public void onADClicked(NativeExpressADView adView) {
    Log.i(TAG, "onADClicked");
  }

  @Override
  public void onADClosed(NativeExpressADView adView) {
    Log.i(TAG, "onADClosed");
    // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，释放资源，不可以再用来展示。
    if (container != null && container.getChildCount() > 0) {
      container.removeAllViews();
      container.setVisibility(View.GONE);
    }
    GDTManager.getInstance(null).getGdtListener().onClose(true, GDTManager.SUCCESS);
    finish();
  }

  @Override
  public void onADLeftApplication(NativeExpressADView adView) {
    Log.i(TAG, "onADLeftApplication");
  }

  @Override
  public void onADOpenOverlay(NativeExpressADView adView) {
    Log.i(TAG, "onADOpenOverlay");
  }

  @Override
  public void onADCloseOverlay(NativeExpressADView adView) {
    Log.i(TAG, "onADCloseOverlay");
  }

  private void hideSoftInput() {
    if (getCurrentFocus() == null || getCurrentFocus().getWindowToken() == null) {
      return;
    }

    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
        NativeExpressActivity.this.getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);
  }


  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  private NativeExpressMediaListener mediaListener = new NativeExpressMediaListener() {
    @Override
    public void onVideoInit(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoInit: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoLoading(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoLoading");
    }

    @Override
    public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
      Log.i(TAG, "onVideoReady");
    }

    @Override
    public void onVideoStart(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoStart: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoPause(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoPause: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoComplete(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoComplete: "
          + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
    }

    @Override
    public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
      Log.i(TAG, "onVideoError");
    }

    @Override
    public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoPageOpen");
    }

    @Override
    public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
      Log.i(TAG, "onVideoPageClose");
    }
  };

  @Override
  public void onBackPressed() {
    return;
  }
}
