package vito.com.myadsdkdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;

import cn.pinode.chat.gdt_advertisement_sdklibrary.GDTManager;

public class GDTTestActivity extends Activity {

    GDTManager gdtManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdttest);
        gdtManager = GDTManager.getInstance(this);
        GDTManager.initSDK("1106652960");
//        showBannerView();
//        showNativeAdView();
        gdtManager.showInterstitialAD("4070948470808960");
    }

    private void showNativeAdView() {
        gdtManager.ShowNativeAdView("4050046400609913");
    }


    private void showBannerView(){
        gdtManager.showBannerView(Gravity.BOTTOM, 10, "4070749460407888",null);
    }


    @Override
    protected void onDestroy() {
        gdtManager.destoryBanner();
        super.onDestroy();
    }
}
