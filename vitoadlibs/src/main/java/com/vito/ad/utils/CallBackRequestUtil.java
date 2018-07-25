package com.vito.ad.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import com.vito.utils.Log;

import com.vito.utils.network.NetHelper;
import com.vito.receivers.InstallReceiver;

import java.util.ArrayList;
import java.util.List;

public class CallBackRequestUtil {
    private static Context mContext;
    private static CallBackRequestUtil instance = null;
	private static Handler handler = new Handler(Looper.getMainLooper());
	private InstallReceiver installBroadcast = null;
	private String needCheckPackageName = "";
	private ArrayList<String> needCallbackUrls;

	public static CallBackRequestUtil getInstance() {
        if (instance == null){
			Log.e("ADTEST", "please init CallBackRequestUtil");
        }
		return instance;
	}

	public static CallBackRequestUtil Init(Context context) {
		mContext = context;
		if (instance == null){
			synchronized (CallBackRequestUtil.class){
				instance = new CallBackRequestUtil();
			}
		}
		return instance;
	}

	private CallBackRequestUtil(){};

	public void checkInstallWithPackageName(final String packageName, final ArrayList<String> callbackurls) {

	}

	public void doCallBackRequest(final ArrayList<String> callbackurls) {
		handler.post(new Runnable() {

			@Override
			public void run() {
                for (int i=0;i<callbackurls.size(); i++)
					NetHelper.sendGetRequest(callbackurls.get(i));
			}
			//		}, 3*60*1000L);
		});
	}


	private boolean check(String packageName) {
		PackageManager pm = mContext.getPackageManager();
	    List<PackageInfo> packages = pm.getInstalledPackages(0);
	    for (PackageInfo packageInfo : packages) {
	        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
	        {
	            if (packageName.equalsIgnoreCase(packageInfo.packageName)) {
	            	return true;
	            }
	        }
	    }
	    return false;
	}
	



}
