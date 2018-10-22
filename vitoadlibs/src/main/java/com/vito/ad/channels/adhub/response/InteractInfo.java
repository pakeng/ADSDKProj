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
public class InteractInfo {

    private List<ThirdpartInfo> thirdpartInfo;
    private String landingPageUrl;
    public void setThirdpartInfo(List<ThirdpartInfo> thirdpartInfo) {
         this.thirdpartInfo = thirdpartInfo;
     }
     public List<ThirdpartInfo> getThirdpartInfo() {
         return thirdpartInfo;
     }

    public void setLandingPageUrl(String landingPageUrl) {
         this.landingPageUrl = landingPageUrl;
     }
     public String getLandingPageUrl() {
         return landingPageUrl;
     }

}