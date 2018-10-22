package com.vito.ad.channels.vlion.response;

import java.util.List;

public class VlionResponse {
    private int status;
    private int adt;
    private String cid;
    private int w;
    private int h;
    private int ctype;
    private String imgurl;
    private String ldp; // 落地页
    private String deeplink; // 优先调用,后台申请没有deeplink
    private String adm; // 广告内容
    private List<String> imp_tracking; // 曝光上报  使用webview的ua
    private List<String> clk_tracking; // 点击上报  替换宏
    private int interact_type; // 0 打开网页， 1 下载
    private boolean is_gdt = false; // 是否是广点通广告 如果是 ldp 地址返回json 需要解析并下载
    private List<Conv_tracking> conv_tracking; // 转化地址  interact_type = 1 有效
    private List<String> dp_tracking; // 调用deeplink 之后的上报
    private String traffic;
    private String pkgname; // interact_type = 1 的时候可能返回 下载的apk包名
    private String app_name;// 应用名称

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAdt() {
        return adt;
    }

    public void setAdt(int adt) {
        this.adt = adt;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getCtype() {
        return ctype;
    }

    public void setCtype(int ctype) {
        this.ctype = ctype;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getLdp() {
        return ldp;
    }

    public void setLdp(String ldp) {
        this.ldp = ldp;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public String getAdm() {
        return adm;
    }

    public void setAdm(String adm) {
        this.adm = adm;
    }

    public List<String> getImp_tracking() {
        return imp_tracking;
    }

    public void setImp_tracking(List<String> imp_tracking) {
        this.imp_tracking = imp_tracking;
    }

    public List<String> getClk_tracking() {
        return clk_tracking;
    }

    public void setClk_tracking(List<String> clk_tracking) {
        this.clk_tracking = clk_tracking;
    }

    public int getInteract_type() {
        return interact_type;
    }

    public void setInteract_type(int interact_type) {
        this.interact_type = interact_type;
    }

    public boolean isIs_gdt() {
        return is_gdt;
    }

    public void setIs_gdt(boolean is_gdt) {
        this.is_gdt = is_gdt;
    }

    public List<Conv_tracking> getConv_tracking() {
        return conv_tracking;
    }

    public void setConv_tracking(List<Conv_tracking> conv_tracking) {
        this.conv_tracking = conv_tracking;
    }

    public List<String> getDp_tracking() {
        return dp_tracking;
    }

    public void setDp_tracking(List<String> dp_tracking) {
        this.dp_tracking = dp_tracking;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public String getPkgname() {
        return pkgname;
    }

    public void setPkgname(String pkgname) {
        this.pkgname = pkgname;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }
}

