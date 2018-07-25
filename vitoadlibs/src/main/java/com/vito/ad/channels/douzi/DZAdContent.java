/**
  * Copyright 2018 bejson.com 
  */
package com.vito.ad.channels.douzi;

public class DZAdContent {

    private int video_type;
    private String apk_pkg_name;
    private String apk_download_url;
    private String apk_ico_url;
    private String video_download_url;
    private String video_page;
    private String apk_name;
    public void setVideo_type(int video_type) {
         this.video_type = video_type;
     }
     public int getVideo_type() {
         return video_type==0?1:0;
     }

    public void setApk_pkg_name(String apk_pkg_name) {
         this.apk_pkg_name = apk_pkg_name;
     }
     public String getApk_pkg_name() {
         return apk_pkg_name;
     }

    public void setApk_download_url(String apk_download_url) {
         this.apk_download_url = apk_download_url;
     }
     public String getApk_download_url() {
         return apk_download_url;
     }

    public void setApk_ico_url(String apk_ico_url) {
         this.apk_ico_url = apk_ico_url;
     }
     public String getApk_ico_url() {
         return apk_ico_url;
     }


    public void setVideo_download_url(String video_download_url) {
         this.video_download_url = video_download_url;
     }
     public String getVideo_download_url() {
         return video_download_url;
     }


    public void setVideo_page(String video_page) {
         this.video_page = video_page;
     }
     public String getVideo_page() {
         return video_page;
     }

    public String getApk_name() {
        return apk_name;
    }

    public void setApk_name(String apk_name) {
        this.apk_name = apk_name;
    }
}