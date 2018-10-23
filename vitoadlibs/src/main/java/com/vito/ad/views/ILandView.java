package com.vito.ad.views;

import android.content.Context;
import android.view.View;

import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.managers.AdTaskManager;

public abstract class ILandView {
    protected View coverLayout;
    public ILandView(){
    }

    public abstract void buildLandView(Context context, ADInfoTask adInfoTask);

    public View getView(Context context, ADInfoTask adInfoTask){
        buildLandView(context, adInfoTask);
        return coverLayout;
    }

    public void onClose(ADInfoTask adInfoTask){
        AdTaskManager.getInstance().onClose(adInfoTask);
    }
}
