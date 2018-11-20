package cn.pinode.chat.gdt_advertisement_sdklibrary;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.FrameLayout;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.comm.util.AdError;

public class GDTManager {

    private static GDTManager instance = null;
    private Activity activity;
    private BannerView bv;
    private GDTADListener gdtListener;
    public static final int SUCCESS = 1000;
    public static final int ERROR = 1001;
    /**
     * 获取单例
     * @param activity 上下文Activity
     */
    public static GDTManager getInstance(Activity activity){
        if (instance==null){
            synchronized (GDTManager.class){
                if (instance==null){
                    instance = new GDTManager(activity);
                }
            }
        }
        return instance;
    }

    public static void initSDK(String appid){
        Constants.setAppid(appid);
    }


    private GDTManager(Activity activity){
        if (activity!=null){
            this.activity = activity;
        }

    }

    /**
     * 展示BannerView
     * @param gravity 布局位置
     * @param refresh 设置广告轮播时间，为0或30~120之间的数字，单位为s,0标识不自动轮播
     * @param act 当前的Activity 如果没有改变传入null
     */
    public void showBannerView(int gravity, int refresh, String posId, @Nullable Activity act) {
        Activity curActivity = null;
        if (act != null){
            curActivity = act;
        }else {
            curActivity = activity;
        }
        bv = new BannerView(curActivity, ADSize.BANNER, Constants.getAppid(), posId);
        bv.setRefresh(refresh);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError error) {
                Log.i(
                        Constants.TAG,
                        String.format("Banner onNoAD，eCode = %d, eMsg = %s", error.getErrorCode(),
                                error.getErrorMsg()));
            }
            @Override
            public void onADReceiv() {
                Log.i(Constants.TAG, "ONBannerReceive");
            }
        });
        bv.loadAD();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        lp.gravity = gravity;
        curActivity.addContentView(bv, lp);
    }

    public void ShowNativeAdView(String posId){
        Intent intent = new Intent();
        intent.setClass(activity, NativeExpressActivity.class);
        intent.putExtra(Constants.POS_ID, posId);
        activity.startActivity(intent);
    }

    public void destoryBanner(){
        if (bv != null){
            bv.destroy();
        }
    }

    public void showInterstitialAD(String posId){

        final InterstitialAD iad = new InterstitialAD(activity, Constants.getAppid(), posId);
        iad.setADListener(new AbstractInterstitialADListener() {
            @Override
          public void onNoAD(AdError error) {
            Log.i("AD_DEMO", String.format("LoadInterstitialAd Fail, error code: %d, error msg: %s", error.getErrorCode(), error.getErrorMsg()));

                if (gdtListener!=null){
                    gdtListener.onClose(false, ERROR);
                }
            }

          @Override
          public void onADReceive() {
                iad.showAsPopupWindow();
                if (gdtListener!=null){
                    gdtListener.onClose(true, SUCCESS);
                }
            }
        });
        iad.loadAD();
    }

    public void setListener(GDTADListener listener){
        this.gdtListener = listener;
    }

    protected GDTADListener getGdtListener() {
        return gdtListener;
    }

    public interface GDTADListener{
        void onClose(boolean result, int code);
    }

}
