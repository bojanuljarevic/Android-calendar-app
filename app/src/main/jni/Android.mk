LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := DurationLib
LOCAL_SRC_FILES := durationJni.c

include $(BUILD_SHARED_LIBRARY)