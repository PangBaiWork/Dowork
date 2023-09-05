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
APP_PLATFORM := android-24
LOCAL_PATH := $(call my-dir)

#include $(CLEAR_VARS)
#LOCAL_MODULE := busybox
#LOCAL_MODULE_CLASS := EXECUTABLES
#LOCAL_MODULE_SUFFIX := $(TARGET_EXECUTABLE_SUFFIX)
#LOCAL_SRC_FILES := $(LOCAL_MODULE)$(TARGET_EXECUTABLE_SUFFIX)
#include $(BUILD_PREBUILT)


include $(call all-makefiles-under,$(LOCAL_PATH))
