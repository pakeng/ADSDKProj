/**
  * Copyright 2018 bejson.com 
  */
package com.vito.ad.channels.lanmei;
import java.util.List;

/**
 * Auto-generated: 2018-07-02 10:50:58
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class LMAdResponse {

    private int ret_code;
    private String ret_msg;
    private List<LMAdContent> ad;
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

    public void setAd(List<LMAdContent> ad) {
         this.ad = ad;
     }
     public List<LMAdContent> getAd() {
         return ad;
     }

}