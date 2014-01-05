LOCAL_PATH := $(call my-dir)/../sources

include $(CLEAR_VARS)
LOCAL_MODULE    := srtp

SRTP_SRC_DIR := .
RTP_SRC_DIR := ../../simple_rtp/sources
			
LOCAL_C_INCLUDES += $(LOCAL_PATH)/crypto/include \
			$(LOCAL_PATH)/include \
			$(LOCAL_PATH)/../../simple_rtp/sources/include
#			$(LOCAL_PATH)/../build/srtp


#LOCAL_CFLAGS := $(MY_PJSIP_FLAGS)

LOCAL_SRC_FILES := $(SRTP_SRC_DIR)/crypto/cipher/cipher.c $(SRTP_SRC_DIR)/crypto/cipher/null_cipher.c      \
		$(SRTP_SRC_DIR)/crypto/cipher/aes.c $(SRTP_SRC_DIR)/crypto/cipher/aes_icm.c             \
		$(SRTP_SRC_DIR)/crypto/cipher/aes_cbc.c \
		$(SRTP_SRC_DIR)/crypto/hash/null_auth.c $(SRTP_SRC_DIR)/crypto/hash/sha1.c \
        $(SRTP_SRC_DIR)/crypto/hash/hmac.c $(SRTP_SRC_DIR)/crypto/hash/auth.c \
		$(SRTP_SRC_DIR)/crypto/replay/rdb.c $(SRTP_SRC_DIR)/crypto/replay/rdbx.c               \
		$(SRTP_SRC_DIR)/crypto/replay/ut_sim.c \
		$(SRTP_SRC_DIR)/crypto/math/datatypes.c $(SRTP_SRC_DIR)/crypto/math/stat.c \
		$(SRTP_SRC_DIR)/crypto/rng/rand_source.c $(SRTP_SRC_DIR)/crypto/rng/prng.c \
		$(SRTP_SRC_DIR)/crypto/rng/ctr_prng.c \
		$(SRTP_SRC_DIR)/pjlib/srtp_err.c \
		$(SRTP_SRC_DIR)/crypto/kernel/crypto_kernel.c  $(SRTP_SRC_DIR)/crypto/kernel/alloc.c   \
		$(SRTP_SRC_DIR)/crypto/kernel/key.c \
		$(SRTP_SRC_DIR)/srtp/srtp.c \
		$(RTP_SRC_DIR)/simple_rtp.c \
		../../srtp_jni.c

LOCAL_STATIC_LIBRARIES += libgcc


include $(BUILD_SHARED_LIBRARY)
