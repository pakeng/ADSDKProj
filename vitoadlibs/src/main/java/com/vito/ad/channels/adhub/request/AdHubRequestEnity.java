/**
  * Copyright 2018 bejson.com 
  */
package com.vito.ad.channels.adhub.request;
import java.util.List;

/**
 * Auto-generated: 2018-10-10 15:43:21
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class AdHubRequestEnity {

    private String version;
    private int srcType;
    private int reqType;
    private long timeStamp;
    private String appid;
    private String appVersion;
    private DevInfo devInfo;
    private EnvInfo envInfo;
    private List<AdReqInfo> adReqInfo;
    public void setVersion(String version) {
         this.version = version;
     }
     public String getVersion() {
         return version;
     }

    public void setSrcType(int srcType) {
         this.srcType = srcType;
     }
     public int getSrcType() {
         return srcType;
     }

    public void setReqType(int reqType) {
         this.reqType = reqType;
     }
     public int getReqType() {
         return reqType;
     }

    public void setTimeStamp(long timeStamp) {
         this.timeStamp = timeStamp;
     }
     public long getTimeStamp() {
         return timeStamp;
     }

    public void setAppid(String appid) {
         this.appid = appid;
     }
     public String getAppid() {
         return appid;
     }

    public void setAppVersion(String appVersion) {
         this.appVersion = appVersion;
     }
     public String getAppVersion() {
         return appVersion;
     }

    public void setDevInfo(DevInfo devInfo) {
         this.devInfo = devInfo;
     }
     public DevInfo getDevInfo() {
         return devInfo;
     }

    public void setEnvInfo(EnvInfo envInfo) {
         this.envInfo = envInfo;
     }
     public EnvInfo getEnvInfo() {
         return envInfo;
     }

    public void setAdReqInfo(List<AdReqInfo> adReqInfo) {
         this.adReqInfo = adReqInfo;
     }
     public List<AdReqInfo> getAdReqInfo() {
         return adReqInfo;
     }

}