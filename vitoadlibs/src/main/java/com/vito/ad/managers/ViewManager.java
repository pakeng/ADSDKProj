package com.vito.ad.managers;

import android.content.Context;
import android.graphics.Point;
import android.view.View;

import com.vito.ad.base.interfaces.IUrlBuildInterface;
import com.vito.ad.base.task.ADInfoTask;
import com.vito.ad.views.ILandView;
import com.vito.utils.Log;

import java.util.HashMap;

public class ViewManager {
    private static ViewManager viewManager = null;
    private HashMap<Integer, ILandView> landViewHashMap = new HashMap<>();
    private HashMap<Integer, IUrlBuildInterface> iUrlBuildInterfaceMap = new HashMap<>();

    private ViewManager(){};
    private Point start_point = new Point(0,0);
    private Point end_point = new Point(0,0);
    private Point size = new Point(0, 0);
    private IUrlBuildInterface iUrlBuildInterface;
    public static ViewManager getInstance(){
        if (viewManager==null){
            synchronized (ViewManager.class){
                if (viewManager == null)
                    viewManager = new ViewManager();
            }
        }
        return viewManager;
    }

    public void registerLandPageView(int type, ILandView view){
        this.landViewHashMap.put(type, view);
    }

    // 创建 落地页
    public View buildLandingPageView(Context context, ADInfoTask task){
        if (task == null)
            Log.e("buildLandingPageView with null task");
        if (task==null||this.landViewHashMap.get(task.getType()) == null){
            Log.e("buildLandingPageView with null landViewHashMap get type");
            return null;
        }

        return this.landViewHashMap.get(task.getType()).getView(context, task);

//
//        ILandView landView = new ImplLandView2(context, task);
//        return landView.getView();
    }

    public Point getStart_point() {
        return start_point;
    }

    public void setStart_point(Point start_point) {
        this.start_point = start_point;
    }

    public Point getEnd_point() {
        return end_point;
    }

    public void setEnd_point(Point end_point) {
        this.end_point = end_point;
    }

    public Point getSize() {
        return size;
    }

    public void setSize(Point size) {
        this.size = size;
    }


    public String rebuildDownloadUrl(ADInfoTask adInfoTask, String url) {
        if (iUrlBuildInterfaceMap.get(adInfoTask.getType())!=null)
            url = iUrlBuildInterfaceMap.get(adInfoTask.getType()).build(url);
        return url;
    }

    public void registerUrlBuildInterface(int type, IUrlBuildInterface iUrlBuildInterface) {
        this.iUrlBuildInterfaceMap.put(type, iUrlBuildInterface);
    }
}
