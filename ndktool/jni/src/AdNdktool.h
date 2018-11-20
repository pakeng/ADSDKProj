#ifndef ADVERTISEMENT_SDK_ADNDKTOOL_H
#define ADVERTISEMENT_SDK_ADNDKTOOL_H

#include <jni.h>
#include <string>
#include "AdProxyInterface.h"

class AdNdkTool{

public:
    AdNdkTool();
    ~AdNdkTool();
    static void getPriorityInfo(std::string channel, std::string deviceid); // 请求优先级列表
    static AdNdkTool* getInstance();
    static void onCallBack(std::string); // 回调优先级列表
    static void setAdProxy(AdProxyInterface* proxyInterface);
    JNIEnv* getJniEnv();

    void setJniEnv(JavaVM *pEnv);

private:
    JavaVM* javaVM;
};

#endif
