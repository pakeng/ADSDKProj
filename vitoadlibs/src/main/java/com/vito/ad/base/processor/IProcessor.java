package com.vito.ad.base.processor;

import com.vito.utils.Log;

import com.google.gson.Gson;
import com.vito.ad.managers.AdManager;
import com.vito.ad.base.entity.EquipmentParamsModel;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.utils.network.NetHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 *  处理广告原始内容
 */

public abstract class IProcessor {
    private String ProcessorName = "IProcessor";
    protected String AdContent ="";
    protected EquipmentParamsModel paramsModel;

    public IProcessor(){
        Gson gson = new Gson();
        paramsModel = gson.fromJson(AdManager.getInstance().getDeviceInfo(), EquipmentParamsModel.class);
    }

    // 入口方法
    public void startProcessor(){
       AdContent = buildRequestInfo();
       getAdContent();
    }

    // 请求数据
    public abstract void getAdContent();

    // 构造请求参数
    // 获取广告原始数据
    public abstract String buildRequestInfo();

    // 获取AdTask
    public abstract ADTask getADTask();

    // 获取AdTask
    public abstract DownloadTask getDownLoadTask();

    protected abstract String buildLandingPage(String originUrl) ;
    // 构造请求url
    protected URL buildGetURL(String baseUrl, Map valueMap){
        StringBuilder stringBuilder = new StringBuilder(baseUrl);
        String paramStr = "";
        if (!valueMap.isEmpty()&&valueMap!=null){
            paramStr = NetHelper.getUrlParamsByMap(valueMap);
        }
        if (baseUrl.endsWith("/")){
            stringBuilder.deleteCharAt(baseUrl.length()-1);
            stringBuilder.append("?").append(paramStr);

        }else if (baseUrl.endsWith("?")) {
            stringBuilder.append(paramStr);
        }else {
            stringBuilder.append("?").append(paramStr);
            Log.e("ADTEST", "stringBuilder = " +stringBuilder.toString());
        }
        URL url = null;
        try {
            url = new URL(stringBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public abstract String buildCallBackParams();

    public abstract int getType();
}
