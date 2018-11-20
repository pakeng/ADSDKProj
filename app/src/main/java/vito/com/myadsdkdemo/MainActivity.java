package vito.com.myadsdkdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vito.ad.base.interfaces.IPrepareCompleteCallBack;
import com.vito.ad.managers.AdManager;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button showBtn;
    private Button preBtn;
    private TextView tv;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("ad_ndktool");
    }
    private AdManager adManager = null;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        preBtn = findViewById(R.id.prepare_ad);
        showBtn = findViewById(R.id.show_ad);
        initAD();
        mHandler = new Handler(Looper.getMainLooper());
        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adManager.PrepareAD();
//                        adManager.testDownloadAndInstall();
                    }
                }, 500L);
            }
        });
        showBtn.setEnabled(false);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                adManager.ShowAd(jsonObject, 0);
            }
        });

    }

    private void initAD() {
        com.vito.utils.Log.debugLevel = 1;
        com.vito.utils.Log.isDebug = true;
        adManager = AdManager.InitAdManager(MainActivity.this);
        adManager.setPrepareListener(new IPrepareCompleteCallBack() {
            @Override
            public void onSuccess(final int Adid, final int allReadyAd) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showBtn.setEnabled(true);
                        Log.e("ADTEST", "ready_id = "+ Adid+" allReadyAd = "+ allReadyAd);
                        tv.setText("准备好了"+allReadyAd+"个广告，刚刚下载的id是"+Adid);
                    }
                });

            }

            @Override
            public void onFailed(final int Adid, final int allReadyAd) {
                Log.e("ADTEST", "failed_id = "+ Adid+" allReadyAd = "+ allReadyAd);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (allReadyAd>0)
                            showBtn.setEnabled(true);
                        tv.setText("准备好了"+allReadyAd+"个广告，刚刚下载失败的id是"+Adid);
                    }
                });
            }

            @Override
            public void onReadyPlay(final int count) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("准备好了"+count+"个广告");
                        showBtn.setEnabled(true);
                    }
                });
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdManager.getInstance().exit();
    }
}
