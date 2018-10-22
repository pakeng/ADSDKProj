/**
  * Copyright 2018 bejson.com 
  */
package com.vito.ad.channels.adhub.response;
import java.util.List;

/**
 * Auto-generated: 2018-10-10 15:48:39
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class AdHubResponseEnity {

    private int status;
    private String errcode;
    private String errmsg;
    private List<SpaceInfo> spaceInfo;
    private long ts;
    public void setStatus(int status) {
         this.status = status;
     }
     public int getStatus() {
         return status;
     }

    public void setErrcode(String errcode) {
         this.errcode = errcode;
     }
     public String getErrcode() {
         return errcode;
     }

    public void setErrmsg(String errmsg) {
         this.errmsg = errmsg;
     }
     public String getErrmsg() {
         return errmsg;
     }

    public void setSpaceInfo(List<SpaceInfo> spaceInfo) {
         this.spaceInfo = spaceInfo;
     }
     public List<SpaceInfo> getSpaceInfo() {
         return spaceInfo;
     }

    public void setTs(long ts) {
         this.ts = ts;
     }
     public long getTs() {
         return ts;
     }

}