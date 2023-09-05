# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


LOCAL_PATH := $(call my-dir)
LIB_DIR := $(LOCAL_PATH)/libs

include $(CLEAR_VARS)

LOCAL_CPP_EXTENSION := .cpp .cc
LOCAL_MODULE    :=xdraw
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
LOCAL_SRC_FILES := clipboard.c xdraw.c input.c
LOCAL_LDLIBS += -landroid -llog
LOCAL_CFLAGS += -fPIC
LOCAL_C_INCLUDES += $(LOCAL_PATH)/include

LOCAL_STATIC_LIBRARIES :=android-support X11 xcb  Xtst Xext Xau Xdmcp
LOCAL_SHARED_LIBRARIES :=

ifeq ($(TARGET_ARCH_ABI),x86)
    LOCAL_CFLAGS += -ffast-math -mtune=atom -mssse3 -mfpmath=sse 
endif

include $(BUILD_SHARED_LIBRARY)



LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := X11
LOCAL_SRC_FILES := $(LIB_DIR)/libX11.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include

include $(PREBUILT_STATIC_LIBRARY)



LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := Xau
LOCAL_SRC_FILES := $(LIB_DIR)/libXau.a
include $(PREBUILT_STATIC_LIBRARY)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := xcb
LOCAL_SRC_FILES := $(LIB_DIR)/libxcb.a

include $(PREBUILT_STATIC_LIBRARY)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := Xdmcp
LOCAL_SRC_FILES := $(LIB_DIR)/libXdmcp.a
include $(PREBUILT_STATIC_LIBRARY)

LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE := android-support
LOCAL_SRC_FILES := $(LIB_DIR)/libandroid-support.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := Xtst
LOCAL_SRC_FILES := $(LIB_DIR)/libXtst.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := Xext
LOCAL_SRC_FILES := $(LIB_DIR)/libXext.a
include $(PREBUILT_STATIC_LIBRARY)


