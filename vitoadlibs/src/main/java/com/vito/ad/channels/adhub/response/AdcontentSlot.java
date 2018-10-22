/**
  * Copyright 2018 bejson.com 
  */
package com.vito.ad.channels.adhub.response;

/**
 * Auto-generated: 2018-10-10 15:48:39
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class AdcontentSlot {

    private String md5;
    private String content;
    private int playTime;
    public void setMd5(String md5) {
         this.md5 = md5;
     }
     public String getMd5() {
         return md5;
     }

    public void setContent(String content) {
         this.content = content;
     }
     public String getContent() {
         return content;
     }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
}