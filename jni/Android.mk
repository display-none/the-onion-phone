LOCAL_PATH := $(call my-dir)
JNI_PATH := $(LOCAL_PATH)

APP_OPTIM:=debug

include $(JNI_PATH)/codec2/android_toolchain/Android.mk
include $(JNI_PATH)/srtp/android_toolchain/Android.mk