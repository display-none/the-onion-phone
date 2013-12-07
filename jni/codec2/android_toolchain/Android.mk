LOCAL_PATH := $(call my-dir)



include $(CLEAR_VARS)

LOCAL_MODULE := codec2_codec
CODEC2_PATH := ../sources
CODEC2_GEN_PATH := ../generated

# codec2
LOCAL_C_INCLUDES += $(LOCAL_PATH)/$(CODEC2_PATH)
LOCAL_SRC_FILES := $(CODEC2_PATH)/dump.c \
	$(CODEC2_PATH)/lpc.c \
	$(CODEC2_PATH)/nlp.c \
	$(CODEC2_PATH)/postfilter.c \
	$(CODEC2_PATH)/sine.c \
	$(CODEC2_PATH)/codec2.c \
	$(CODEC2_PATH)/fft.c \
	$(CODEC2_PATH)/kiss_fft.c \
	$(CODEC2_PATH)/interp.c \
	$(CODEC2_PATH)/lsp.c \
	$(CODEC2_PATH)/phase.c \
	$(CODEC2_PATH)/quantise.c \
	$(CODEC2_PATH)/pack.c \
	$(CODEC2_GEN_PATH)/codebook.c \
	$(CODEC2_GEN_PATH)/codebookd.c \
	$(CODEC2_GEN_PATH)/codebookdvq.c \
	../../codec2_jni.c
	

LOCAL_STATIC_LIBRARIES += libgcc

include $(BUILD_SHARED_LIBRARY)
include $(CLEAR_VARS)
