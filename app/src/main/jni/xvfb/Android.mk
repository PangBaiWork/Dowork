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
LOCAL_MODULE :=xserver

LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
LOCAL_SRC_FILES :=startxvfb.c
LOCAL_LDLIBS +=-landroid    -llog 
LOCAL_SHARED_LIBRARIES := bz2  Xau Xdmcp
LOCAL_STATIC_LIBRARIES := android-support  xvfb  Xfont2  pixman  xshmfence crypto freetype png16 fontenc z android-shmem

ifeq ($(TARGET_ARCH_ABI),x86)
    LOCAL_CFLAGS += -ffast-math -mtune=atom -mssse3 -mfpmath=sse 
endif

include $(BUILD_SHARED_LIBRARY)





include $(CLEAR_VARS)
LOCAL_MODULE := android-shmem
LOCAL_SRC_FILES := $(LIB_DIR)/libandroid-shmem.a
include $(PREBUILT_STATIC_LIBRARY)



include $(CLEAR_VARS)
LOCAL_MODULE := xvfb
LOCAL_SRC_FILES := $(LIB_DIR)/libXvfb.a
include $(PREBUILT_STATIC_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := bz2
LOCAL_SRC_FILES := $(LIB_DIR)/libbz2.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := crypto
LOCAL_SRC_FILES := $(LIB_DIR)/libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := fontenc
LOCAL_SRC_FILES := $(LIB_DIR)/libfontenc.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := freetype
LOCAL_SRC_FILES := $(LIB_DIR)/libfreetype.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := pixman
LOCAL_SRC_FILES := $(LIB_DIR)/libpixman-1.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := png16
LOCAL_SRC_FILES := $(LIB_DIR)/libpng16.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := Xfont2
LOCAL_SRC_FILES := $(LIB_DIR)/libXfont2.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := xshmfence
LOCAL_SRC_FILES := $(LIB_DIR)/libxshmfence.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := z
LOCAL_SRC_FILES := $(LIB_DIR)/libz.a
include $(PREBUILT_STATIC_LIBRARY)
