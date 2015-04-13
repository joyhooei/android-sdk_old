//
//  Common.h
//  WGPlatform
//
//  Created by fly chen on 2/21/13.
//  Copyright (c) 2013 tencent.com. All rights reserved.
//

#ifndef WGPlatform_Common_h
#define WGPlatform_Common_h
#include <string>
#include <vector>
#include "WGPublicDefine.h"

#include <android/log.h>
#include <jni.h>

#define LOGI(fmt, ...)   __android_log_print(ANDROID_LOG_INFO, "WeGame  cpp", fmt, __VA_ARGS__)
#define LOGD(fmt, ...)   __android_log_print(ANDROID_LOG_DEBUG, "WeGame  cpp", fmt, __VA_ARGS__)
#define LOGW(fmt, ...)   __android_log_print(ANDROID_LOG_WARN, "WeGame  cpp", fmt, __VA_ARGS__)
#define LOGE(fmt, ...)   __android_log_print(ANDROID_LOG_ERROR, "WeGame  cpp", fmt, __VA_ARGS__)

typedef struct {
    std::string ip;
    int port;
} SchedulingInfo;
// 获取某个java对象的值(String), 再赋值给本地对象
#define JniGetAndSetStringField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "Ljava/lang/String;"); \
jstring j##fieldName##FieldValue = (jstring) (env->GetObjectField(jOriginObj, j##fieldName##FieldId)); \
if (j##fieldName##FieldValue == NULL) {\
    targetObj.fieldName = ""; \
} else { \
    char const * c##fieldName##FieldValue = env->GetStringUTFChars(j##fieldName##FieldValue, NULL); \
    targetObj.fieldName = c##fieldName##FieldValue; \
    LOGD("c%sFieldValue %s", fieldNameStr, c##fieldName##FieldValue); \
    env->ReleaseStringUTFChars(j##fieldName##FieldValue, c##fieldName##FieldValue); \
} \
env->DeleteLocalRef(j##fieldName##FieldValue);

#define JniGetAndSetIntField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "I"); \
targetObj.fieldName = (int) (env->GetIntField(jOriginObj, j##fieldName##FieldId));

// 获取某个java对象的值(long), 再赋值给本地对象
#define JniGetAndSetLongField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "J"); \
targetObj.fieldName = (int) (env->GetLongField(jOriginObj, j##fieldName##FieldId));

// 获取某个java对象的值(float), 再赋值给本地对象
#define JniGetAndSetFloatField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "F"); \
targetObj.fieldName = (int) (env->GetFloatField(jOriginObj, j##fieldName##FieldId));

// 获取某个java对象的值(boolean), 再赋值给本地对象
#define JniGetAndSetBooleanField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "Z"); \
targetObj.fieldName = (int) (env->GetBooleanField(jOriginObj, j##fieldName##FieldId));

// 获取某个java对象的值(double), 再赋值给本地对象
#define JniGetAndSetDoubleField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "D"); \
targetObj.fieldName = (double) (env->GetDoubleField(jOriginObj, j##fieldName##FieldId));

typedef struct {
    int type;
    std::string value;
    long long expiration;
}TokenRet;

typedef struct loginRet_ {
    int flag;               //返回标记，标识成功和失败类型
    std::string desc;       //返回描述
    int platform;           //当前登录的平台
    std::string open_id;
    std::vector<TokenRet> token;
    std::string user_id;    //用户ID，先保留，等待和微信协商
    std::string pf;
    std::string pf_key;
}LoginRet;

typedef void(*CallbackFun)(LoginRet lr);

typedef struct
{
    std::string key;
    std::string value;
    
}KVPair;

typedef struct
{
    int flag;                //错误码
    int platform;               //被什么平台唤起
    std::string media_tag_name; //wx回传得meidaTagName
    std::string open_id;        //qq传递的openid
    std::string desc;           //描述
    std::string lang;          //语言     目前只有微信5.1以上用，手Q不用
    std::string country;       //国家     目前只有微信5.1以上用，手Q不用
    std::string messageExt;    //游戏分享传入自定义字符串，平台拉起游戏不做任何处理返回         目前只有微信5.1以上用，手Q不用
    std::vector<KVPair> extInfo;  //游戏－平台携带的自定义参数手Q专用
}WakeupRet;

#endif
