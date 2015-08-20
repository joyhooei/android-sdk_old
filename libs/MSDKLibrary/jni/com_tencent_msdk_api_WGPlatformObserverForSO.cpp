#include "com_tencent_msdk_api_WGPlatformObserverForSO.h"
#include "CommonFiles/WGPlatform.h"
#include "CommonFiles/WGPlatformObserver.h"
#include "CommonFiles/WGCommon.h"

#include <android/log.h>

JNIEXPORT void JNICALL Java_com_tencent_msdk_api_WGPlatformObserverForSO_OnLoginNotify(JNIEnv * env, jclass jc,
        jobject jLoginRet) {
    LOGD("OnLoginNotify start%s", "");
    LoginRet lr;
    jclass jLoginRetClass = env->GetObjectClass(jLoginRet);
    jboolean isCopy;
    JniGetAndSetIntField(flag, "flag", jLoginRetClass, jLoginRet, lr);
    JniGetAndSetStringField(desc, "desc", jLoginRetClass, jLoginRet, lr);
    JniGetAndSetIntField(platform, "platform", jLoginRetClass, jLoginRet, lr);
    JniGetAndSetStringField(open_id, "open_id", jLoginRetClass, jLoginRet, lr);

    jfieldID vctId = env->GetFieldID(jLoginRetClass, "token", "Ljava/util/Vector;");
    jobject tokenList = env->GetObjectField(jLoginRet, vctId);
    jclass tokenRetVectorClass = env->GetObjectClass(tokenList);

    jmethodID vectorSizeM = env->GetMethodID(tokenRetVectorClass, "size", "()I");
    jmethodID vectorGetM = env->GetMethodID(tokenRetVectorClass, "get", "(I)Ljava/lang/Object;");
    jint len = env->CallIntMethod(tokenList, vectorSizeM);

    LOGD( "Java_com_tencent_msdk_api_WGPlatformObserverForSO_OnLoginNotify: tokenListSize: %d", len);
    for (int i = 0; i < len; i++) {
        TokenRet cToken;
        jobject jTokenRetObject = env->CallObjectMethod(tokenList, vectorGetM, i);
        jclass jTokenRetClass = env->GetObjectClass(jTokenRetObject);

        JniGetAndSetIntField(type, "type", jTokenRetClass, jTokenRetObject, cToken);
        JniGetAndSetStringField(value, "value", jTokenRetClass, jTokenRetObject, cToken);
        JniGetAndSetLongField(expiration, "expiration", jTokenRetClass, jTokenRetObject, cToken);

        LOGD( "WGPlatformObserverForSO_OnLoginNotify: type: %d", cToken.type);
        LOGD( "WGPlatformObserverForSO_OnLoginNotify: value: %s", cToken.value.c_str());
        LOGD( "WGPlatformObserverForSO_OnLoginNotify: expiration: %lld", cToken.expiration);

        lr.token.push_back(cToken);

        env->DeleteLocalRef(jTokenRetObject);
        env->DeleteLocalRef(jTokenRetClass);
    }

    JniGetAndSetStringField(user_id, "user_id", jLoginRetClass, jLoginRet, lr);
    JniGetAndSetStringField(pf, "pf", jLoginRetClass, jLoginRet, lr);
    JniGetAndSetStringField(pf_key, "pf_key", jLoginRetClass, jLoginRet, lr);

    if (WGPlatform::GetInstance()->GetObserver() != NULL) {
		LOGD("OnLoginNotify GetObserver()->OnLoginNotify start%s", "");
        WGPlatform::GetInstance()->GetObserver()->OnLoginNotify(lr);
        LOGD("OnLoginNotify GetObserver()->OnLoginNotify end%s", "");
    } else {
        WGPlatform::GetInstance()->setLoginRet(lr);
    }

    env->DeleteLocalRef(jLoginRetClass);
    env->DeleteLocalRef(jLoginRet);
    LOGD("OnLoginNotify end%s", "");

}

JNIEXPORT void JNICALL Java_com_tencent_msdk_api_WGPlatformObserverForSO_OnWakeupNotify(JNIEnv *env, jclass jc,
        jobject jWakeupRetObject) {
    LOGD("OnWakeupNotify start%s", "");
    jclass jWakeupRetClass = env->GetObjectClass(jWakeupRetObject);
    WakeupRet wr;
    jboolean isCopy;
    JniGetAndSetIntField(flag, "flag", jWakeupRetClass, jWakeupRetObject, wr);
    JniGetAndSetIntField(platform, "platform", jWakeupRetClass, jWakeupRetObject, wr);
    JniGetAndSetStringField(open_id, "open_id", jWakeupRetClass, jWakeupRetObject, wr);
    JniGetAndSetStringField(media_tag_name, "media_tag_name", jWakeupRetClass, jWakeupRetObject, wr);
    JniGetAndSetStringField(desc, "desc", jWakeupRetClass, jWakeupRetObject, wr);
    JniGetAndSetStringField(lang, "lang", jWakeupRetClass, jWakeupRetObject, wr);
    JniGetAndSetStringField(country, "country", jWakeupRetClass, jWakeupRetObject, wr);
    JniGetAndSetStringField(messageExt, "messageExt", jWakeupRetClass, jWakeupRetObject, wr);

    jfieldID jVectorMethodId = env->GetFieldID(jWakeupRetClass, "extInfo", "Ljava/util/Vector;");
    jobject extInfoVector = env->GetObjectField(jWakeupRetObject, jVectorMethodId);
    jclass extInfoVectorClass = env->GetObjectClass(extInfoVector);

    jmethodID vectorSizeM = env->GetMethodID(extInfoVectorClass, "size", "()I");
    jmethodID vectorGetM = env->GetMethodID(extInfoVectorClass, "get", "(I)Ljava/lang/Object;");
    jint len = env->CallIntMethod(extInfoVector, vectorSizeM);


    LOGD( "Java_com_tencent_msdk_api_WGPlatformObserverForSO_OnWakeupNotify: extInfoSize: %s", "");
	for (int i = 0; i < len; i++) {
		KVPair cKVPair;
		jobject jKVPair = env->CallObjectMethod(extInfoVector, vectorGetM, i);
		jclass jKVPairClass = env->GetObjectClass(jKVPair);

		JniGetAndSetStringField(key, "key", jKVPairClass, jKVPair, cKVPair);
		JniGetAndSetStringField(value, "value", jKVPairClass, jKVPair, cKVPair);

		LOGD( "Java_com_tencent_msdk_api_WGPlatformObserverForSO_OnWakeupNotify: key: %s", cKVPair.key.c_str());
		LOGD( "Java_com_tencent_msdk_api_WGPlatformObserverForSO_OnWakeupNotify: value: %s", cKVPair.value.c_str());

		wr.extInfo.push_back(cKVPair);

		env->DeleteLocalRef(jKVPair);
		env->DeleteLocalRef(jKVPairClass);
	}
	env->DeleteLocalRef(extInfoVector);
	env->DeleteLocalRef(extInfoVectorClass);


    if (WGPlatform::GetInstance()->GetObserver()) {
        WGPlatform::GetInstance()->GetObserver()->OnWakeupNotify(wr);
    } else {
        WGPlatform::GetInstance()->setWakeup(wr);
    }
    env->DeleteLocalRef(jWakeupRetObject);
    env->DeleteLocalRef(jWakeupRetClass);

    LOGD("OnWakeupNotify end%s", "");
}



