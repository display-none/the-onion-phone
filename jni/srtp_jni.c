#include <jni.h>
#include <stdlib.h>
#include <srtp.h>
#include <simple_rtp.h>

#define SRTP_KEY_SIZE 30
#define KEY_SIZE 16
#define SALT_SIZE 14

rtp_context_t* rtpContext;
srtp_t* srtpContext;


JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpProtocol_initSender
	(JNIEnv *env, jobject obj, jbyteArray keyByteArray, jbyteArray saltByteArray, jint codecType, jint sampleCount) {

	srtp_policy_t policy;
	char key[SRTP_KEY_SIZE];

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
	crypto_policy_set_rtp_default(&policy.rtp);
	crypto_policy_set_rtcp_default(&policy.rtcp);

	policy.ssrc.type  = ssrc_specific;
	policy.ssrc.value = rtpContext->mSsrc;
	policy.key  = (uint8_t *) key;
	policy.next = NULL;
	policy.rtp.sec_serv = sec_serv_conf_and_auth;
	policy.rtcp.sec_serv = sec_serv_none;  /* we don't do RTCP anyway */

	//create an srtp stream
	status = srtp_create(srtpContext, &policy);

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
	(*env)->GetByteArrayRegion(env, saltByteArray, 0, SALT_SIZE, &key[KEY_SIZE]);

	//set up crypto policies
	crypto_policy_set_aes_cm_128_hmac_sha1_32(&policy.rtp);
	crypto_policy_set_rtcp_default(&policy.rtcp);

	policy.ssrc.type  = ssrc_specific;
	policy.ssrc.value = ssrc;
	policy.key  = (uint8_t *) key;
	policy.next = NULL;
	policy.rtp.sec_serv = sec_serv_conf_and_auth;
	policy.rtcp.sec_serv = sec_serv_none;  /* we don't do RTCP anyway */

	//add an srtp stream
	err_status_t status = srtp_add_stream((*srtpContext), &policy);

	return status;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_protocol_SrtpProtocol_deinit
	(JNIEnv *env, jobject obj) {

	err_status_t status = srtp_dealloc((*srtpContext));
	if(status) {
		return status;
	}

	status = srtp_deinit();
	return status;
}

