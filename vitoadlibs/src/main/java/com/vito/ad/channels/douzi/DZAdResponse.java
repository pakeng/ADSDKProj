/**
  * Copyright 2018 bejson.com 
  */
package com.vito.ad.channels.douzi;

import java.util.List;

public class DZAdResponse {

    private int ret_code;
    private String ret_msg;
    private List<DZAdContent> ad;
    public void setRet_code(int ret_code) {
         this.ret_code = ret_code;
     }
     public int getRet_code() {
         return ret_code;
     }

    public void setRet_msg(String ret_msg) {
         this.ret_msg = ret_msg;
     }
     public String getRet_msg() {
         return ret_msg;
     }

    public void setAd(List<DZAdContent> ad) {
         this.ad = ad;
     }
     public List<DZAdContent> getAd() {
         return ad;
     }

}