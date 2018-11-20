//
// Created by Administrator on 2018/11/7.
//

#ifndef ADVERTISEMENT_SDK_ADPROXYINTERFACE_H
#define ADVERTISEMENT_SDK_ADPROXYINTERFACE_H

#include <string>

class AdProxyInterface{

public:
    virtual void getPriorityInfo(std::string channel, std::string DeviceID) = 0;
};

#endif //ADVERTISEMENT_SDK_ADPROXYINTERFACE_H
