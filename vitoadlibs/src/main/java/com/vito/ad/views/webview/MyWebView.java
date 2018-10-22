package com.vito.ad.views.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vito.ad.JavaScriptBridge;
import com.vito.ad.base.interfaces.IJsCallbackInterface;
import com.vito.utils.Log;

public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
        setBackgroundColor(Color.TRANSPARENT);
        this.setWebViewClient(client);
        initWebViewSettings();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.TRANSPARENT);
        this.setWebViewClient(client);
        initWebViewSettings();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.TRANSPARENT);
        this.setWebViewClient(client);
        initWebViewSettings();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackgroundColor(Color.TRANSPARENT);
        this.setWebViewClient(client);
        initWebViewSettings();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        setBackgroundColor(Color.TRANSPARENT);
        this.setWebViewClient(client);
        initWebViewSettings();
    }

    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isPlayStart= false;
    public IJsCallbackInterface getJscallback() {
        return jscallback;
    }

    public void setJscallback(IJsCallbackInterface jscallback) {
        this.jscallback = jscallback;
    }

    private IJsCallbackInterface jscallback = null;

    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         * 拦截获取数据
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("adTest", "load url = "+ url);
            // 根据协议的参数，判断是否是所需要的url
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            if (JavaScriptBridge.getInstance().parse(url, jscallback)){
                return true;
            }
            if (JavaScriptBridge.getInstance().parseDownload(url))
                return true;
            String str = Uri.parse(url).getAuthority();
            Log.e("adTest", "load url authority = "+ str);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            super.onPageFinished(view, url);
        }
    };



    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setUseWideViewPort(true);
        //	webSetting.setLoadWithOverviewMode(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSetting.setMediaPlaybackRequiresUserGesture(false);
        }
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // 设置无边框
		webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
// web内容强制满屏
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setScrollContainer(false);
    }

    /**
     * 使WebView不可滚动
     * */
    @Override
    public void scrollTo(int x, int y){
        super.scrollTo(0,0);
    }

}
