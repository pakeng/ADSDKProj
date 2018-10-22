package com.vito.ad.channels.vlion.response;

public class DownloadDetail {
    private String dstlink; //	是	string	下载地址

    public String getDstlink() {
        return dstlink;
    }

    public String getClickid() {
        return clickid;
    }

    private String clickid; // 缓存并且替换 conv tracking里面的__CLICK_ID__
}
