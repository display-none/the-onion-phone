#include <jni.h>
#include <stdlib.h>
#include <srtp.h>
#include <simple_rtp.h>

#define SRTP_KEY_SIZE 30
#define KEY_SIZE 16
#define SALT_SIZE 14

rtp_context_t* rtpContext;
srtp_t* srtpContextSend;
srtp_t* srtpContextRecv;
int payloadSize;
int rtpPacketSize;
int srtpPacketSize;


JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpProtocol_initSender
	(JNIEnv *env, jobject obj, jbyteArray keyByteArray, jbyteArray saltByteArray, jint codecType, jint sampleCount,
			jint payloadSizeInBytes, jint rtpPacketSizeInBytes, jint srtpPacketSizeInBytes) {

	srtp_policy_t policy;
	char key[SRTP_KEY_SIZE];
	payloadSize = payloadSizeInBytes;
	rtpPacketSize = rtpPacketSizeInBytes;
	srtpPacketSize = srtpPacketSizeInBytes;

	// copy key and salt to an array needed by srtp
	(*env)->GetByteArrayRegion(env, keyByteArray, 0, KEY_SIZE, key);
	(*env)->GetByteArrayRegion(env, saltByteArray, 0, SALT_SIZE, key + KEY_SIZE);

	//initialize rtp
	err_status_t status = initRtp(&rtpContext, codecType, sampleCount);
	if(status) {
		return status;
	}

	//initialize srtp
	status = srtp_init();
	if(status) {
		return status;
	}

	//set up crypto policies
	crypto_policy_set_aes_cm_128_hmac_sha1_32(&policy.rtp);
	crypto_policy_set_rtcp_default(&policy.rtcp);

	policy.ssrc.type  = ssrc_specific;
	policy.ssrc.value = rtpContext->mSsrc;
	policy.key  = (uint8_t *) key;
	policy.next = NULL;
	policy.rtp.sec_serv = sec_serv_conf_and_auth;
	policy.rtcp.sec_serv = sec_serv_none;  /* we don't do RTCP anyway */

	//create an srtp stream
	srtpContextSend = malloc(sizeof(srtp_t*));
	if(srtpContextSend == NULL) {
		return 69;
	}
	status = srtp_create(srtpContextSend, &policy);

	return status;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpProtocol_getSsrc
	(JNIEnv *env, jobject obj) {

	return (jint)rtpContext->mSsrc;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpProtocol_initReceiver
	(JNIEnv *env, jobject obj, jint ssrc, jbyteArray keyByteArray, jbyteArray saltByteArray) {

	srtp_policy_t policy;
	char key[SRTP_KEY_SIZE];

	// copy key and salt to an array needed by srtp
	(*env)->GetByteArrayRegion(env, keyByteArray, 0, KEY_SIZE, key);
	(*env)->GetByteArrayRegion(env, saltByteArray, 0, SALT_SIZE, key + KEY_SIZE);

	//set up crypto policies
	crypto_policy_set_aes_cm_128_hmac_sha1_32(&policy.rtp);
	crypto_policy_set_rtcp_default(&policy.rtcp);

	policy.ssrc.type  = ssrc_specific;
	policy.ssrc.value = ssrc;
	policy.key  = (uint8_t *) key;
	policy.next = NULL;
	policy.rtp.sec_serv = sec_serv_conf_and_auth;
	policy.rtcp.sec_serv = sec_serv_none;  /* we don't do RTCP anyway */

	//create an srtp stream
	srtpContextRecv = malloc(sizeof(srtp_t*));
		if(srtpContextRecv == NULL) {
			return 69;
		}
	err_status_t status = srtp_create(srtpContextRecv, &policy);

	return status;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpProtocol_deinit
	(JNIEnv *env, jobject obj) {

	err_status_t status = srtp_dealloc((*srtpContextSend));
	if(status) {
		return status;
	}

	status = srtp_dealloc((*srtpContextRecv));
	if(status) {
		return status;
	}

	status = srtp_deinit();
	if(status) {
		return status;
	}

	free(srtpContextSend);
	free(srtpContextRecv);

	return status;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpWrapper_wrapPayloadNative
	(JNIEnv *env, jobject obj, jbyteArray packet) {

	jbyte* packetNative = (*env)->GetPrimitiveArrayCritical(env, packet, NULL);
	if(packetNative == NULL) {
		return 1;
	}

	getNextPacketHeader(rtpContext, packetNative);
	int packetLength = rtpPacketSize;
	err_status_t status = srtp_protect((*srtpContextSend), packetNative, &packetLength);

	(*env)->ReleasePrimitiveArrayCritical(env, packet, packetNative, 0);
	return status;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpUnwrapper_unwrapPacketNative
	(JNIEnv *env, jobject obj, jbyteArray packet) {

	jbyte* packetNative = (*env)->GetPrimitiveArrayCritical(env, packet, NULL);
	if(packetNative == NULL) {
		return 1;
	}

	int packetLength = srtpPacketSize;
	err_status_t status = srtp_unprotect((*srtpContextRecv), packetNative, &packetLength);

	(*env)->ReleasePrimitiveArrayCritical(env, packet, packetNative, 0);
	return status;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpProtocol_getHeaderSize
	(JNIEnv *env, jobject obj) {

	return getHeaderSize();
}
