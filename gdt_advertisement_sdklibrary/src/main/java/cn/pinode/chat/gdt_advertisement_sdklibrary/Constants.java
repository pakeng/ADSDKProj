package cn.pinode.chat.gdt_advertisement_sdklibrary;

public class Constants {
    public static final String POS_ID = "pos_id";
    public static String TAG = "GDT_SDK";
    private static String appid = "";
    public static void setAppid(String appid){
        Constants.appid = appid;
    }


    public static String getAppid() {
        return appid;
    }



}
