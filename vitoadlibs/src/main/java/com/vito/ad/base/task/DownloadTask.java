package com.vito.ad.base.task;

import android.net.Uri;

import com.vito.ad.base.entity.VideoDetail;

import java.io.Serializable;

public class DownloadTask implements Serializable {
    private int type = 0; // 下载类型
    private int id = 0; // id 用来查广告
    private int originId = 0; // 原始id  当派生出apk 下载task的时候应当设置该id
    private String url = ""; // 链接地址
    private String name = ""; // 文件名称
    private String path = "Vito_temp"; // 文件下载的本地路径
    private long size = 0L; // 文件大小
    private int currentDownloading = 0; // 已经下载的百分比
    private VideoDetail videoDetail = null; // 如果是视频任务记录视频的相关数据
    private long downloadId = -1L ; // 下载id
    private boolean isDwonloading = false;
    private Uri storeUri;
    private boolean downloadCompleted = false;
    private String mAdname;
    private String price = "";
    private String appName;
    private int sortNum;
    private int pullType = -1; // 拉起类型

    public boolean isApkDownload() {
        return isApkDownload;
    }

    public void setApkDownload(boolean apkDownload) {
        isApkDownload = apkDownload;
    }

    private int ad_type; // 广告类型
    private boolean isApkDownload = false; // 是否发起了下载

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private String packageName = ""; // apk 文件包名


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getCurrentDownloading() {
        return currentDownloading;
    }

    public void setCurrentDownloading(int currentDownloading) {
        this.currentDownloading = currentDownloading;
    }

    public VideoDetail getVideoDetail() {
        return videoDetail;
    }

    public void setVideoDetail(VideoDetail videoDetail) {
        this.videoDetail = videoDetail;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        isDwonloading = true;
        this.downloadId = downloadId;
    }

    public void setReDownload() {
        isDwonloading = false;
    }

    public boolean isDwonloading() {
        return isDwonloading;
    }

    public void setStoreUri(Uri downIdUri) {
        storeUri = downIdUri;
    }

    public Uri getStoreUri() {
        return storeUri;
    }

    public void setDownloadCompleted(boolean downloadCompleted) {
        this.downloadCompleted = downloadCompleted;
    }
    public boolean isDownloadCompleted() {
        return downloadCompleted;
    }

    public int getOriginId() {
        return originId;
    }

    public void setOriginId(int originId) {
        this.originId = originId;
    }

    public String getADname() {
        return mAdname;
    }


    public void setmAdname(String mAdname) {
        this.mAdname = mAdname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getSortNum() {
        return sortNum;
    }

    public void setSortNum(int sortNum) {
        this.sortNum = sortNum;
    }

    public int getAD_Type() {
        return ad_type;
    }

    public void setAd_type(int ad_type) {
        this.ad_type = ad_type;
    }

    public int getPullType() {
        return pullType;
    }

    public void setPullType(int pullType) {
        this.pullType = pullType;
    }
}
