package com.vito.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.DownloadTaskManager;
import com.vito.ad.managers.ReceiverManager;
import com.vito.utils.Log;

public class InstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent){
		String packageName = intent.getDataString();
		//接收安装广播
		if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())||Intent.ACTION_PACKAGE_INSTALL.equals(intent.getAction())) {

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
						Log.e("adTest", "install callback");
						AdTaskManager.getInstance().getIAdBaseInterface(adTask).onInstallFinish();
					}
				}
			}
		}else if (Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())||Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())){
//			for (int id : ReceiverManager.getInstance().getCheckInstallList()) {
//				DownloadTask task = DownloadTaskManager.getInstance().getDownloadTaskByADId(id);
//				if (task!=null){
//					Log.e("adTest", "onReceive　needCheckPN = "+task.getPackageName() +"  packName = "+ packageName);
//					if (("package:"+task.getPackageName()).equalsIgnoreCase(packageName)){
//						ADTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getOriginId());
//						//TODO  make more
//						if (adTask==null)
//							return;
//						adTask.setRemove(true);  // 安装成功移除广告
//						Log.e("adTest", "install callback");
//						AdTaskManager.getInstance().getIAdBaseInterface(adTask).onAPKRelaced();
//					}
//				}
//			}
		}else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())){
//			for (int id : ReceiverManager.getInstance().getCheckInstallList()) {
//				DownloadTask task = DownloadTaskManager.getInstance().getDownloadTaskByADId(id);
//				if (task!=null){
//					Log.e("adTest", "onReceive　needCheckPN = "+task.getPackageName() +"  packName = "+ packageName);
//					if (("package:"+task.getPackageName()).equalsIgnoreCase(packageName)){
//						ADTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getOriginId());
//						//TODO  make more
//						if (adTask==null)
//							return;
//						adTask.setRemove(true);  // 安装成功移除广告
//						Log.e("adTest", "install callback");
//						AdTaskManager.getInstance().getIAdBaseInterface(adTask).onAPKDeleted();
//					}
//				}
//			}
		}
	}
}