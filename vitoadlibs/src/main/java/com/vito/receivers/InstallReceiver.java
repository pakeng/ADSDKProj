package com.vito.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.vito.utils.Log;

import com.vito.ad.base.task.ADTask;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.DownloadTaskManager;
import com.vito.ad.managers.ReceiverManager;
import com.vito.ad.base.task.DownloadTask;
import com.vito.utils.network.NetHelper;

public class InstallReceiver extends BroadcastReceiver {

	@Override
		public void onReceive(Context context, Intent intent){
			//接收安装广播
			if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())||Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())) {
				String packageName = intent.getDataString();
				for (int id : ReceiverManager.getInstance().getCheckInstallList()) {
					DownloadTask task = DownloadTaskManager.getInstance().getDownloadTaskByADId(id);
					if (task!=null){
						Log.e("adTest", "onReceive　needCheckPN = "+task.getPackageName() +"  packName = "+ packageName);
						if (("package:"+task.getPackageName()).equalsIgnoreCase(packageName)){
							ADTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getOriginId());
							//TODO  make more
							if (adTask==null)
								return;
							adTask.setRemove(true);  // 安装成功移除广告
							for (String url : adTask.getmInstallCallBackUrls()){
								Log.e("adTest", "install callback");
								NetHelper.sendGetRequest(url);
							}
						}
					}
				}
			}
		}
}