package com.vito.ad.base.task;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashSet;

public class ADTask implements Serializable {
    private int orientation = 0;
    private boolean isRemoveOnClose = false;
    private boolean isRemove;
    private String downloadApkUrl;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
//<T> T fromJson(String json, Class<T> classOfT)
    public <T> T getADObject(Class<T> classOfT) {
        Gson gson = new Gson();
        return gson.fromJson(ADObject, classOfT);
    }
    public void setADObject(String ADObject) {
        this.ADObject = ADObject;
    }

    private int type = 0; // 类型
    private int id = 0; // id
    private String Landing_Page="";
    private HashSet<String> mInstallCallBackUrls;
    private HashSet<String> mStartInstallCallBackUrls;
    private HashSet<String> mClickCallBackUrls;
    private HashSet<String> mDownloadStartCallBackUrls;
    private HashSet<String> mDownloadEndCallBackUrls;
    private HashSet<String> mShowCallBackUrls;
    private HashSet<String> mVideoStartCallBackUrls;
    private HashSet<String> mVideoMidCallBackUrls; // 1/2
    private HashSet<String> mVideoFirstQuartileCallBackUrls; // 1/4
    private HashSet<String> mVideoThirdQuartileCallBackUrls; // 3/4
    private HashSet<String> mSkipCallBackUrls; // 跳过
    private HashSet<String> mCloseCallBackUrls; //关闭

    public void setmInstallCallBackUrls(HashSet<String> mInstallCallBackUrls) {
        this.mInstallCallBackUrls = mInstallCallBackUrls;
    }

    public void setmClickCallBackUrls(HashSet<String> mClickCallBackUrls) {
        this.mClickCallBackUrls = mClickCallBackUrls;
    }

    public void setmDownloadStartCallBackUrls(HashSet<String> mDownloadStartCallBackUrls) {
        this.mDownloadStartCallBackUrls = mDownloadStartCallBackUrls;
    }

    public void setmDownloadEndCallBackUrls(HashSet<String> mDownloadEndCallBackUrls) {
        this.mDownloadEndCallBackUrls = mDownloadEndCallBackUrls;
    }

    public void setmShowCallBackUrls(HashSet<String> mShowCallBackUrls) {
        this.mShowCallBackUrls = mShowCallBackUrls;
    }

    public void setmVideoStartCallBackUrls(HashSet<String> mVideoStartCallBackUrls) {
        this.mVideoStartCallBackUrls = mVideoStartCallBackUrls;
    }

    public void setmVideoMidCallBackUrls(HashSet<String> mVideoMidCallBackUrls) {
        this.mVideoMidCallBackUrls = mVideoMidCallBackUrls;
    }

    public void setmVideoFirstQuartileCallBackUrls(HashSet<String> mVideoFirstQuartileCallBackUrls) {
        this.mVideoFirstQuartileCallBackUrls = mVideoFirstQuartileCallBackUrls;
    }

    public void setmVideoThirdQuartileCallBackUrls(HashSet<String> mVideoThirdQuartileCallBackUrls) {
        this.mVideoThirdQuartileCallBackUrls = mVideoThirdQuartileCallBackUrls;
    }

    public void setmSkipCallBackUrls(HashSet<String> mSkipCallBackUrls) {
        this.mSkipCallBackUrls = mSkipCallBackUrls;
    }

    public void setmEndCallBackUrls(HashSet<String> mEndCallBackUrls) {
        this.mEndCallBackUrls = mEndCallBackUrls;
    }

    private HashSet<String> mEndCallBackUrls; // 结束
    public String ADObject = "";

    public HashSet<String> getmInstallCallBackUrls() {
        if (mInstallCallBackUrls == null)
            mInstallCallBackUrls = new HashSet<>();
        return mInstallCallBackUrls;
    }

    public HashSet<String> getmClickCallBackUrls() {
        if (mClickCallBackUrls == null)
            mClickCallBackUrls = new HashSet<>();
        return mClickCallBackUrls;
    }

    public HashSet<String> getmDownloadStartCallBackUrls() {
        if (mDownloadStartCallBackUrls == null)
            mDownloadStartCallBackUrls = new HashSet<>();
        return mDownloadStartCallBackUrls;
    }

    public HashSet<String> getmDownloadEndCallBackUrls() {
        if (mDownloadEndCallBackUrls == null)
            mDownloadEndCallBackUrls = new HashSet<>();
        return mDownloadEndCallBackUrls;
    }

    public HashSet<String> getmShowCallBackUrls() {
        if (mShowCallBackUrls == null)
            mShowCallBackUrls = new HashSet<>();
        return mShowCallBackUrls;
    }

    public HashSet<String> getmVideoStartCallBackUrls() {
        if (mVideoStartCallBackUrls == null)
            mVideoStartCallBackUrls = new HashSet<>();
        return mVideoStartCallBackUrls;
    }

    public HashSet<String> getmVideoMidCallBackUrls() {
        if (mVideoMidCallBackUrls == null)
            mVideoMidCallBackUrls = new HashSet<>();
        return mVideoMidCallBackUrls;
    }

    public HashSet<String> getmVideoFirstQuartileCallBackUrls() {
        if (mVideoFirstQuartileCallBackUrls == null)
            mVideoFirstQuartileCallBackUrls = new HashSet<>();
        return mVideoFirstQuartileCallBackUrls;
    }

    public HashSet<String> getmVideoThirdQuartileCallBackUrls() {
        if (mVideoThirdQuartileCallBackUrls == null)
            mVideoThirdQuartileCallBackUrls = new HashSet<>();
        return mVideoThirdQuartileCallBackUrls;
    }

    public HashSet<String> getmSkipCallBackUrls() {
        if (mSkipCallBackUrls == null)
            mSkipCallBackUrls = new HashSet<>();
        return mSkipCallBackUrls;
    }

    public HashSet<String> getmEndCallBackUrls() {
        if (mEndCallBackUrls == null)
            mEndCallBackUrls = new HashSet<>();
        return mEndCallBackUrls;
    }

    public String getLanding_Page() {
        return Landing_Page;
    }

    public void setLanding_Page(String landing_Page) {
        Landing_Page = landing_Page;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public HashSet<String> getmCloseCallBackUrls() {
        if (mCloseCallBackUrls==null)
            mCloseCallBackUrls = new HashSet<>();
        return mCloseCallBackUrls;
    }

    public void setmCloseCallBackUrls(HashSet<String> mCloseCallBackUrls) {
        this.mCloseCallBackUrls = mCloseCallBackUrls;
    }

    public HashSet<String> getmStartInstallCallBackUrls() {
        if (mStartInstallCallBackUrls==null)
            mStartInstallCallBackUrls = new HashSet<>();
        return mStartInstallCallBackUrls;
    }

    public void setmStartInstallCallBackUrls(HashSet<String> mStartInstallCallBackUrls) {
        this.mStartInstallCallBackUrls = mStartInstallCallBackUrls;
    }

    public boolean isRemoveOnClose() {
        return isRemoveOnClose;
    }

    public void setRemoveOnClose(boolean removeOnClose) {
        isRemoveOnClose = removeOnClose;
    }

    public boolean isRemove() {
        return isRemove;
    }

    public void setRemove(boolean remove) {
        isRemove = remove;
    }

    public String getDownloadApkUrl() {
        return downloadApkUrl;
    }

    public void setDownloadApkUrl(String downloadApkUrl) {
        this.downloadApkUrl = downloadApkUrl;
    }
}
