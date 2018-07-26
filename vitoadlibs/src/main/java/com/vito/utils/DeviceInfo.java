package com.vito.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

public class DeviceInfo {
    protected static String uuid = null;
    private Activity m_pContext = null;
    protected static final String PREFS_FILE = "gank_device_id.xml";
    protected static final String PREFS_DEVICE_ID = "gank_device_id";
    private static String ua = "";
    public DeviceInfo(Activity ctx){
        m_pContext = ctx;
    }

    public String getDeviceId()
    {
        if( uuid ==null ) {
            synchronized (DeviceInfo.class) {
                if( uuid == null) {
                    final SharedPreferences prefs = m_pContext.getSharedPreferences( PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null );

                    if (id != null) {
                        uuid = id;
                    } else {

                        final String androidId = Settings.Secure.getString(m_pContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                            } else {
                                    final String deviceId = ((TelephonyManager) m_pContext.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                                    uuid = deviceId!=null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
                    }
                }
            }
        }
        return uuid;
    }

    //是否为双卡手机
    public String getDeviceCount(){
        String count=null;
        TelephonyManager telephonyManager= (TelephonyManager) m_pContext.getSystemService(m_pContext.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            count = telephonyManager.getPhoneCount() + "";
        }
        return count;
    }

    //双IMEI
    public String getDeviceImei2(){
        String imei=null;
        TelephonyManager telephonyManager= (TelephonyManager) m_pContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if(telephonyManager.getDeviceId() == null || telephonyManager.getDeviceId().equals("")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    imei = telephonyManager.getDeviceId(1);
                }
            }else{
                imei = telephonyManager.getDeviceId();
            }
        }catch (Exception e)
        {
            return "null";
        }
        return imei;
    }

    //获取手机信息传输类型
    public String getPhoneType(){
        String phoneType=null;
        TelephonyManager telephonyManager= (TelephonyManager) m_pContext.getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephonyManager.getPhoneType();
        switch (type){
            case TelephonyManager.PHONE_TYPE_CDMA:
                phoneType="CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                phoneType="GSM";
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                phoneType="SIP";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                phoneType="NONE";
                break;
        }
        return phoneType;
    }

    //获取手机IMEI
    public String getDeviceImei(){
        String deviceImei;
        TelephonyManager telephonyManager= (TelephonyManager) m_pContext.getSystemService(Context.TELEPHONY_SERVICE);
        deviceImei = telephonyManager.getDeviceId();
        return deviceImei;
    }

    //手机品牌
    public String getManufacturer() {
        String manufacturer = android.os.Build.MANUFACTURER;
        return manufacturer;
    }

    //手机型号
    public String getModel() {
        String model = android.os.Build.MODEL;
        return model;
    }

    //安卓版本
    public String getOSVersion() {
        String osversion = android.os.Build.VERSION.RELEASE;
        return osversion;
    }

    // 获取 IMSI
    public String getImsi(){
        TelephonyManager telephonyManager= (TelephonyManager) m_pContext.getSystemService(Context.TELEPHONY_SERVICE);
        String str = telephonyManager.getSubscriberId();
        return str;
    }

    // 获取Android_id
    public String getAndroidId () {
        String ANDROID_ID = Settings.System.getString(m_pContext.getContentResolver(), Settings.System.ANDROID_ID);
        return ANDROID_ID;
    }

    // 获取packagename
    public String getPackageName(){
        return m_pContext.getPackageName();
    }

    // 获取cpu型号
    public String getCPU(){
        return Build.CPU_ABI;
    }
    // 获取cpu名称
    public String getCpuName(){
        try{
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+",2);
            for(int i = 0; i < array.length; i++){
            }
            return array[1];
        }catch (IOException e){
            e.printStackTrace();
        }
        return "null";
    }

    // 获取内存RAM
    public int getRAM(){
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(firstLine != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(firstLine) / (1024)).doubleValue()));
        }

        return totalRam;//返回MB
    }
    // 获取sdk版本
    public int getSDKVERSION(){

        return Build.VERSION.SDK_INT;
    }
    // 获取sys数值
    public String getSys(){
        return Build.VERSION.RELEASE;
    }

    // 获取分辨率
    public String getScreenPixels(){
        DisplayMetrics displayMetrics=new DisplayMetrics();
        ((Activity)m_pContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels=displayMetrics.widthPixels;

        return heightPixels+"X"+widthPixels;
    }

    private String getLocation() {
        //获取位置管理服务
        //获取地理位置管理器
        LocationManager locationManager = (LocationManager) m_pContext.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider;
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else{
            return "null";
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location!=null){
            //不为空,显示地理位置经纬度
            return  location.getLongitude()+"##"+location.getLatitude();
        }
        return "null";
    }

    public String GetNetworkType()
    {
        String strNetworkType = "";

        NetworkInfo networkInfo = ((ConnectivityManager)m_pContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                strNetworkType = "WIFI";
            }
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                String _strSubTypeName = networkInfo.getSubtypeName();
                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))
                        {
                            strNetworkType = "3G";
                        }
                        else
                        {
                            strNetworkType = _strSubTypeName;
                        }
                        break;
                }
            }
        }

        return strNetworkType;
    }

    public String getVersionCode(){
        PackageManager packageManager=m_pContext.getPackageManager();
        PackageInfo packageInfo;
        String versionCode="";
        try {
            packageInfo=packageManager.getPackageInfo(m_pContext.getPackageName(),0);
            versionCode=packageInfo.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public String getVersionName(){
        PackageManager packageManager=m_pContext.getPackageManager();
        PackageInfo packageInfo;
        String versionName="";
        try {
            packageInfo=packageManager.getPackageInfo(m_pContext.getPackageName(),0);
            versionName=packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public String getDeviceInfoMac() {
        WifiManager wifiManager = (WifiManager) m_pContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getMacAddress();
    }

    public String getUA(){
        if (ua.isEmpty()){
            WebView web = new WebView(m_pContext);
            WebSettings webSettings = web.getSettings();
            ua = webSettings.getUserAgentString();
            Log.e("DeviceInfo", "ua = "+  ua);
        }
        return ua;
    }

    public  String getOperator( ) {


        String ProvidersName = "";
        TelephonyManager telephonyManager = (TelephonyManager) m_pContext.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        if (IMSI != null) {
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")  || IMSI.startsWith("46006")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
            return ProvidersName;
        } else {
            return "null";
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }


    public float getDensity() {
        DisplayMetrics metric = new DisplayMetrics();
        m_pContext.getWindowManager().getDefaultDisplay().getMetrics(metric);

        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        return densityDpi;

    }

    public String getADInfoString(){
        JSONObject json = new JSONObject();
        try {
            json.put("imsi", getImsi()==null?"null":getImsi())
                    .put("imei", getDeviceImei()==null?"null":getDeviceImei())
                    .put("model", getModel())   //手机型号
                    .put("brand",getManufacturer())  // 制造商
                    .put("androidId",getAndroidId()==null?"null":getAndroidId())
                    .put("sys", getOSVersion())  // 系统版本
                    .put("sdk",getSDKVERSION()) // api版本号
                    .put("addr", getLocation())  // 经纬度  经度##维度
                    .put("appPackage",getPackageName())
                    .put("channel", "")   // 渠道  总是为空
                    .put("memeory", getRAM()) // 手机内存大小 MB
                    .put("cpu", getCPU())  // 手机cpu类型
                    .put("ratio", getScreenPixels()) // 屏幕分辨率
                    .put("appName", "点点消宝藏")
                    .put("so", 1) // screen_orientation  // 横竖屏  1 竖屏 2 横屏
                    .put("ip",getLocalIpAddress())
                    .put("appVersion", getVersionName())  // 游戏版本号 1.0.0
                    .put("mac", getDeviceInfoMac())  // mac 地址
                    .put("ua", getUA())
                    .put("density", getDensity())  // 屏幕密度 160 / 240  / ……
                    .put("operator", getOperator())  //  运营商
                    .put("connectionType", GetNetworkType());  // 链接类型  2g 3g 4g wifi  unknown

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("DeviceInfo", json.toString());
        return json.toString();
    }
}
