/**
  * Copyright 2018 bejson.com 
  */
package com.vito.ad.base.entity;

import java.util.List;

/**
 * Auto-generated: 2018-07-03 15:31:25
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ADServerResponse {
    private List<Integer> data;
    private int ret;
    public List<Integer> getData() {
        return data;
    }
    public void setRet(int ret) {
        this.ret = ret;
    }
    public boolean getRet() {
        return ret==1000;
    }

}