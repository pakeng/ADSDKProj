package com.vito.ad.channels.adhub;

import com.google.gson.Gson;
import com.vito.ad.base.interfaces.ListenerFactory;
import com.vito.ad.base.processor.IProcessor;
import com.vito.ad.base.task.ADTask;
import com.vito.ad.base.task.DownloadTask;
import com.vito.ad.channels.adhub.request.AdHubRequestEnity;
import com.vito.ad.channels.adhub.request.AdReqInfo;
import com.vito.ad.channels.adhub.request.DevInfo;
import com.vito.ad.channels.adhub.request.EnvInfo;
import com.vito.ad.channels.adhub.response.AdHubResponseEnity;
import com.vito.ad.channels.adhub.response.AdResponse;
import com.vito.ad.channels.adhub.response.InteractInfo;
import com.vito.ad.channels.adhub.response.SpaceInfo;
import com.vito.ad.channels.adhub.response.ThirdpartInfo;
import com.vito.ad.channels.adhub.view.ADHubLandView;
import com.vito.ad.managers.AdTaskManager;
import com.vito.ad.managers.ViewManager;
import com.vito.ad.views.video.interfaces.IVideoPlayListener;
import com.vito.utils.Log;
import com.vito.utils.MD5Util;
import com.vito.utils.StringUtil;
import com.vito.utils.network.NetHelper;

import java.util.ArrayList;
import java.util.HashSet;

public class AdHubProcessor extends IProcessor {
    private AdHubResponseEnity AdhubContent = null;
    private ADTask adTask;
    private DownloadTask downloadTask;


    @Override
    public void getAdContent() {
        if (AdhubContent ==null)
            return;
        // 生成ADTask
        adTask = new ADTask();
        Gson gson = new Gson();
        String adObjectStr = gson.toJson(AdhubContent);
        adTask.setADObject(adObjectStr);
        adTask.setId(AdTaskManager.getInstance().getNextADID());

        adTask.setType(Config.ADTYPE);

        //处理回调

        // 获取广告内容

        if (AdhubContent.getSpaceInfo()==null||AdhubContent.getSpaceInfo().isEmpty()){
            return;
        }
        SpaceInfo spaceInfo = AdhubContent.getSpaceInfo().get(0);
        if (spaceInfo.getAdResponse()==null||spaceInfo.getAdResponse().isEmpty()){
            return;
        }
        AdResponse adResponse = spaceInfo.getAdResponse().get(0);
        if (adResponse.getInteractInfo()==null){
            return;
        }
        InteractInfo interactInfo = adResponse.getInteractInfo();
        if (interactInfo.getThirdpartInfo()==null&&interactInfo.getThirdpartInfo().isEmpty()){
            return;
        }

        HashSet<String> clickurl = new HashSet<>();
        HashSet<String> viewurl = new HashSet<>();
        // 没有处理
        HashSet<String> converturl = new HashSet<>();
        // 遍历
        for (ThirdpartInfo t : interactInfo.getThirdpartInfo()){
            if (t.getClickUrl()!=null&&!t.getClickUrl().isEmpty())
                clickurl.add(t.getClickUrl());
            if (t.getViewUrl()!=null&&!t.getViewUrl().isEmpty())
                viewurl.add(t.getViewUrl());
            if (t.getConvertUrl()!=null&&!t.getConvertUrl().isEmpty())
                converturl.add(t.getConvertUrl());
        }

        adTask.setClickCallBackUrls(clickurl);
        adTask.setShowCallBackUrls(viewurl);

        AdTaskManager.getInstance().pushTask(adTask);
        // 生成DownloadTask
        downloadTask = new DownloadTask();


    }

    public AdHubProcessor(){
        android.util.Log.e("ADSDK", "AdHubProcessor  注册");
        // 注册对应的回调方法
        IVideoPlayListener videoPlayerListener = ListenerFactory.getDefaultListener();
        AdTaskManager.getInstance().registerIVideoPlayListener(Config.ADTYPE, videoPlayerListener);
        ViewManager.getInstance().registerLandPageView(Config.ADTYPE, new ADHubLandView());
        AdTaskManager.getInstance().registerIAdBaseInterface(Config.ADTYPE, ListenerFactory.getDefaultIAdBaseInterface());
    }



    @Override
    public String buildRequestInfo() {
        String result;
        if (StringUtil.isNotEmpty(paramsModel)) {
            AdHubRequestEnity requestEnity = new AdHubRequestEnity();

//            // make test start
//            // TODO
            Gson gson = new Gson();
//            AdHubRequestEnity testRequestEnity = gson.fromJson(Config.testString, AdHubRequestEnity.class);
//            requestEnity = testRequestEnity;
//            //make test end

            DevInfo  devInfo = new DevInfo();
            devInfo.setAndroidID(paramsModel.getAndroidId());
            devInfo.setBrand(paramsModel.getBrand());
            devInfo.setDensity(paramsModel.getDensity());
            devInfo.setDevType(1);
            devInfo.setImei(paramsModel.getImei());
            devInfo.setLanguage("zh");
            devInfo.setMac(paramsModel.getMac());
            devInfo.setModel(paramsModel.getModel());
            devInfo.setOs(paramsModel.getSys());
            devInfo.setPlatform(2);
            devInfo.setResolution(paramsModel.getRatio().replace("X", "_"));
//            devInfo.setScreenSize();
            devInfo.setSdkUID(MD5Util.encrypt(paramsModel.getImei()));

            EnvInfo envInfo = new EnvInfo();
            envInfo.setNet(getNetType());
            envInfo.setIp(NetHelper.getIP());
            //经纬度不传
//            Geo geo = new Geo();
//            geo.setLatitude(paramsModel.getAddr().substring(paramsModel.));
//            envInfo.setGeo();
            envInfo.setUserAgent(paramsModel.getUa());
            envInfo.setIsp(getIsp());

            AdReqInfo adReqInfo = new AdReqInfo();
            adReqInfo.setScreenStatus(1);
            adReqInfo.setSpaceID(Config.spaceID);

            requestEnity.setAppid(Config.appid);
            requestEnity.setAppVersion(paramsModel.getAppVersion());
            requestEnity.setVersion(Config.Version);
            requestEnity.setSrcType(1);
            requestEnity.setReqType(1);
            requestEnity.setTimeStamp(getTimeStamp());
            requestEnity.setDevInfo(devInfo);
            requestEnity.setEnvInfo(envInfo);
            ArrayList<AdReqInfo> adReqInfos = new ArrayList<AdReqInfo>();
            adReqInfos.add(adReqInfo);
            requestEnity.setAdReqInfo(adReqInfos);


            String param = gson.toJson(requestEnity);


            result = NetHelper.doPostRequest(Config.ADBASEURL, param, 1);

            //判断返回数据是否返回广告
            if (StringUtil.isNotEmpty(result)) {
                Log.e("ADTEST", "ADHUB result = "+ result);
                AdhubContent = gson.fromJson(result, AdHubResponseEnity.class);
                return result;
            }
        }
        return "";
    }

    @Override
    public ADTask getADTask() {
        return adTask;
    }

    @Override
    public DownloadTask getDownLoadTask() {
        return downloadTask;
    }

    @Override
    protected String buildLandingPage(String originUrl) {
        return null;
    }

    @Override
    public String buildCallBackParams() {
        return null;
    }

    @Override
    public int getType() {
        return Config.ADTYPE;
    }

    private int getNetType(){
        if (paramsModel.getConnectionType().equalsIgnoreCase("2G")){
            return Config.NetType.NET_2G.ordinal();
        }else if (paramsModel.getConnectionType().equalsIgnoreCase("3G")){
            return Config.NetType.NET_3G.ordinal();
        }else if (paramsModel.getConnectionType().equalsIgnoreCase("4G")){
            return Config.NetType.NET_4G.ordinal();

        }else if (paramsModel.getConnectionType().equalsIgnoreCase("wifi")){
            return Config.NetType.NET_WIFI.ordinal();
        }else {
            return Config.NetType.NET_UNKNOWN.ordinal();
        }
    }

    private int getIsp() {
        if (paramsModel.getOperator().contentEquals("移动")){
            return Config.IspType.ISP_CN_MOBILE.ordinal();
        }else if (paramsModel.getOperator().contentEquals("联通")){
            return Config.IspType.ISP_CN_UNICOM.ordinal();
        }else if (paramsModel.getOperator().contentEquals("电信")){
            return Config.IspType.ISP_CN_TEL.ordinal();
        }else{
            return Config.IspType.ISP_UNKNOWN.ordinal();
        }
    }

    private long getTimeStamp() {
        return System.currentTimeMillis()/1000L;
    }

}
