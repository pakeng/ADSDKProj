package com.vito.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.base.task.ADDownloadTask;
import com.vito.ad.configs.Constants;
import com.vito.ad.managers.ADDownloadTaskManager;
import com.vito.ad.managers.AdManager;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ReceiverManager;
import com.vito.utils.Log;

public class InstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent){
		String packageName = intent.getDataString();
		//接收安装广播
		if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())||Intent.ACTION_PACKAGE_INSTALL.equals(intent.getAction())) {

			for (int id : ReceiverManager.getInstance().getCheckInstallList()) {
				ADDownloadTask task = ADDownloadTaskManager.getInstance().getDownloadTaskByADId(id);
				if (task!=null){
					Log.e("adTest", "onReceive　needCheckPN = "+task.getPackageName() +"  packName = "+ packageName);
					if (("package:"+task.getPackageName()).equalsIgnoreCase(packageName)){
						if (task.getType() == Constants.APK_DOWNLOAD_URL){
							AdManager.getInstance().notifyApkInstalled(task.getPackageName(), task);
							return;
						}

						ADInfoTask adInfoTask = AdTaskManager.getInstance().getAdTaskByADID(task.getOriginId());
						//TODO  make more
						if (adInfoTask ==null)
							return;
						adInfoTask.setRemove(true);  // 安装成功移除广告
						Log.e("adTest", "install callback");
						AdTaskManager.getInstance().getIAdBaseInterface(adInfoTask).onInstallFinish();
					}
				}
			}
		}else if (Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())||Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())){
//			for (int id : ReceiverManager.getInstance().getCheckInstallList()) {
//				ADDownloadTask task = ADDownloadTaskManager.getInstance().getDownloadTaskByADId(id);
//				if (task!=null){
//					Log.e("adTest", "onReceive　needCheckPN = "+task.getPackageName() +"  packName = "+ packageName);
//					if (("package:"+task.getPackageName()).equalsIgnoreCase(packageName)){
//						ADInfoTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getOriginId());
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
			// TODO 移除暂时不统计
//			AdManager.getInstance().notifyApkUninstalled(packageName, );
//			for (int id : ReceiverManager.getInstance().getCheckInstallList()) {
//				ADDownloadTask task = ADDownloadTaskManager.getInstance().getDownloadTaskByADId(id);
//				if (task!=null){
//					Log.e("adTest", "onReceive　needCheckPN = "+task.getPackageName() +"  packName = "+ packageName);
//					if (("package:"+task.getPackageName()).equalsIgnoreCase(packageName)){
//						ADInfoTask adTask = AdTaskManager.getInstance().getAdTaskByADID(task.getOriginId());
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