#include <jni.h>
#include <string>
#include "AdNdktool.h"
#include <android/log.h>

#define TAG "AdNdktool"

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)



// TODO jni
extern "C"
JNIEXPORT void JNICALL
Java_com_vito_ad_ndktool_AdNdktool_getPriorityInfo(JNIEnv *env, jclass type, jstring channel_,
                                                   jstring DeviceID_) {
    const char *channel = env->GetStringUTFChars(channel_, 0);
    const char *DeviceID = env->GetStringUTFChars(DeviceID_, 0);

    JavaVM* pJavaVM = NULL;
    env->GetJavaVM(&pJavaVM);
    AdNdkTool::getInstance()->setJniEnv(pJavaVM);

    AdNdkTool::getPriorityInfo(channel, DeviceID);

    env->ReleaseStringUTFChars(channel_, channel);
    env->ReleaseStringUTFChars(DeviceID_, DeviceID);
}

static AdNdkTool* instance = NULL;
static AdProxyInterface* adProxyInterface = NULL;

void AdNdkTool::getPriorityInfo(std::string channel, std::string deviceid) {
    // TODO
    if (adProxyInterface!= NULL){
        adProxyInterface->getPriorityInfo(channel, deviceid);
    }
}

AdNdkTool::AdNdkTool() {

}

AdNdkTool::~AdNdkTool() {

}

AdNdkTool *AdNdkTool::getInstance() {

    if (instance== NULL){
        instance = new AdNdkTool();
    }

    return instance;
}

// 回调方法
void AdNdkTool::onCallBack(std::string result) {
    JNIEnv* jniEnv = AdNdkTool::getInstance()->getJniEnv();
    if (jniEnv == NULL){
        LOGE("onCallBack jniEnv == NULL");
        return;
    }
    jclass cls = jniEnv->FindClass("com/vito/ad/ndktool/AdNdktool");
    jmethodID mid = jniEnv->GetMethodID(cls, "onGetCallBack", "(Ljava/lang/String;)V");
    if (mid!=NULL){
        //构造参数并调用对象的方法
        jstring arg = jniEnv->NewStringUTF(result.c_str());
        jniEnv->CallStaticVoidMethod(cls, mid, arg);

    }else{
        LOGE("onCallBack mid == null");
    }

}

void AdNdkTool::setAdProxy(AdProxyInterface *proxyInterface) {
    adProxyInterface = proxyInterface;
}

JNIEnv *AdNdkTool::getJniEnv() {

    int status;
    JNIEnv* _jniEnv = NULL;
    status = this->javaVM->GetEnv((void **)&_jniEnv, JNI_VERSION_1_6);
    LOGE("getJniEnv jniEnv == NULL with GetEnv");
    if(status < 0)
    {
        status = this->javaVM->AttachCurrentThread(&_jniEnv, NULL);
        if(status < 0)
        {
            LOGE("getJniEnv jniEnv == NULL with AttachCurrentThread");
            _jniEnv = NULL;
        }
    }

    return _jniEnv;
}

void AdNdkTool::setJniEnv(JavaVM *pVM) {
    this->javaVM = pVM;
}
