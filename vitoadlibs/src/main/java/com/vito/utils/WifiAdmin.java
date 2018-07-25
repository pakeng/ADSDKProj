package com.vito.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import java.util.List;

public class WifiAdmin {
    private final static String TAG = "WifiAdmin";
    private StringBuffer mStringBuffer = new StringBuffer();
    private List<ScanResult> listResult;
    private ScanResult mScanResult;
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    WifiLock mWifiLock;

    /**
     * 构造方法
     */
    public WifiAdmin(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /**
     * 打开Wifi网卡
     */
    public void openNetCard() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭Wifi网卡
     */
    public void closeNetCard() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 检查当前Wifi网卡状态
     */
    public void checkNetCardState() {
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
            Log.i(TAG, "网卡正在关闭");
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            Log.i(TAG, "网卡已经关闭");
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            Log.i(TAG, "网卡正在打开");
        } else if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            Log.i(TAG, "网卡已经打开");
        } else {
            Log.i(TAG, "没有获取到状态-_-");
        }
    }

    /**
     * 扫描周边网络
     */
    private void scan() {
        mWifiManager.startScan();
        listResult = mWifiManager.getScanResults();
        if (listResult != null) {
            Log.i(TAG, "当前区域存在无线网络，请查看扫描结果");
        } else {
            Log.i(TAG, "当前区域没有无线网络");
        }
    }

    /**
     * 得到扫描结果 获取无线信号列表
     */
    public String getScanResult() {
        // 每次点击扫描之前清空上一次的扫描结果
        if (mStringBuffer != null) {
            mStringBuffer = new StringBuffer();
        }
        // 开始扫描网络
        scan();
        listResult = mWifiManager.getScanResults();
        if (listResult != null) {
            for (int i = 0; i < listResult.size(); i++) {
                mScanResult = listResult.get(i);
                mStringBuffer = mStringBuffer.append("NO.").append(i + 1)
                        .append(" :").append(mScanResult.SSID).append("->")
                        .append(mScanResult.BSSID).append("->")
                        .append(mScanResult.capabilities).append("->")
                        .append(mScanResult.frequency).append("->")
                        .append(mScanResult.level).append("->")
                        .append(mScanResult.describeContents()).append("\n\n");
            }
        }
        Log.i(TAG, mStringBuffer.toString());
        return mStringBuffer.toString();
    }

    /**
     * 连接指定网络
     */
    public void connect() {

        mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo != null) {
            Log.d("wifiInfo", mWifiInfo.toString());
            Log.d("SSID",mWifiInfo.getSSID());
        }
    }

    /**
     * 断开当前连接的网络
     */
    public void disconnectWifi() {
        int netId = getNetworkId();
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
        mWifiInfo = null;
    }

    /**
     * 检查当前网络状态
     *
     * @return String
     */
    public void checkNetWorkState() {
        if (mWifiInfo != null) {
            Log.i(TAG, "网络正常工作");
        } else {
            Log.i(TAG, "网络已断开");
        }
    }

    /**
     * 得到连接的ID
     */
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 得到IP地址
     */
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index >= mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的BSSID
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 得到SSID
    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    // 添加一个网络并连接
    public int addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(mWifiConfiguration.get(3));
        mWifiManager.enableNetwork(wcgID, true);
        return wcgID;
    }
}