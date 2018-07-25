package com.vito.ad.views;

import android.content.Context;
import android.view.View;

import com.vito.ad.base.task.ADTask;
import com.vito.ad.managers.AdTaskManager;

public abstract class ILandView {
    protected View coverLayout;
    public ILandView(){
    }

    public abstract void buildLandView(Context context, ADTask adTask);

    public View getView(Context context, ADTask adTask){
        buildLandView(context, adTask);
        return coverLayout;
    }

    public void onClose(ADTask adTask){
        AdTaskManager.getInstance().onClose(adTask);
    }
}
