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
public class AdResponse {

    private String extInfo;
    private List<ContentInfo> contentInfo;
    private InteractInfo interactInfo;
    private AdLogo adLogo;
    public void setExtInfo(String extInfo) {
         this.extInfo = extInfo;
     }
     public String getExtInfo() {
         return extInfo;
     }

    public void setContentInfo(List<ContentInfo> contentInfo) {
         this.contentInfo = contentInfo;
     }
     public List<ContentInfo> getContentInfo() {
         return contentInfo;
     }

    public void setInteractInfo(InteractInfo interactInfo) {
         this.interactInfo = interactInfo;
     }
     public InteractInfo getInteractInfo() {
         return interactInfo;
     }

    public void setAdLogo(AdLogo adLogo) {
         this.adLogo = adLogo;
     }
     public AdLogo getAdLogo() {
         return adLogo;
     }

}