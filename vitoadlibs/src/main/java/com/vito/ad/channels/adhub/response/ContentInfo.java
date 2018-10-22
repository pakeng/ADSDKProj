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
public class ContentInfo {

    private int renderType;
    private List<AdcontentSlot> adcontentSlot;
    private String template;
    public void setRenderType(int renderType) {
         this.renderType = renderType;
     }
     public int getRenderType() {
         return renderType;
     }

    public void setAdcontentSlot(List<AdcontentSlot> adcontentSlot) {
         this.adcontentSlot = adcontentSlot;
     }
     public List<AdcontentSlot> getAdcontentSlot() {
         return adcontentSlot;
     }

    public void setTemplate(String template) {
         this.template = template;
     }
     public String getTemplate() {
         return template;
     }

}