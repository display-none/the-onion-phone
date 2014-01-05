
#include <jni.h>
#include <stdlib.h>
#include <codec2.h>

#define BYTES_SIZE	((CODEC2_BITS_PER_FRAME + 7) / 8)

void *codec2 = NULL;


JNIEXPORT jint JNICALL Java_org_theonionphone_audio_codecs_Codec2_codec2Init
	(JNIEnv *env, jobject obj) {

	codec2 = codec2_create();

	if(codec2 == NULL) {
		return (jint)1;
	}
	return (jint)0;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_audio_codecs_Codec2_codec2Encode
    (JNIEnv *env, jobject obj, jshortArray input, jbyteArray output) {

	if(codec2 == NULL) {
		return (jint)1;
	}

	jbyte output_bytes[BYTES_SIZE];		//TODO change from allocating statically to getting a region of java array
	jshort* buffer = (*env)->GetShortArrayElements(env, input, NULL);

	codec2_encode(codec2, (unsigned char*)output_bytes, buffer);

	(*env)->ReleaseShortArrayElements(env, input, buffer, JNI_ABORT);
	(*env)->SetByteArrayRegion(env, output, 0, BYTES_SIZE, output_bytes);

	return (jint)0;
}

JNIEXPORT jint JNICALL Java_org_theonionphone_audio_codecs_Codec2_codec2Decode
	(JNIEnv *env, jobject obj, jshortArray output, jbyteArray input) {

	if(codec2 == NULL) {
			return (jint)1;
		}

	jshort output_shorts[CODEC2_SAMPLES_PER_FRAME];
	jbyte* buffer = (*env)->GetByteArrayElements(env, input, NULL);

	codec2_decode(codec2, output_shorts, (unsigned char*)buffer);

	(*env)->ReleaseByteArrayElements(env, input, buffer, JNI_ABORT);
	(*env)->SetShortArrayRegion(env, output, 0, CODEC2_SAMPLES_PER_FRAME, output_shorts);

	return (jint)0;
}

JNIEXPORT void JNICALL Java_org_theonionphone_audio_codecs_Codec2_codec2Release
	(JNIEnv *env, jobject obj) {

	codec2_destroy(codec2);
}

