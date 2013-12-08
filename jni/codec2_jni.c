
#include <jni.h>
#include <stdlib.h>
#include <codec2.h>

#define BITS_SIZE	((CODEC2_BITS_PER_FRAME + 7) / 8)

void *codec2 = NULL;


JNIEXPORT jint JNICALL Java_pl_net_hola_theonionphone_audio_codecs_Codec2_codec2_init
	(JNIEnv *env, jobject obj) {

	codec2 = codec2_create();

	if(codec2 == NULL) {
		return (jint)1;
	}
	return (jint)0;
}

JNIEXPORT jint JNICALL Java_pl_net_hola_theonionphone_audio_codecs_Codec2_codec2_encode
    (JNIEnv *env, jobject obj, jshortArray input, jbyteArray output) {

	if(codec2 == NULL) {
		return (jint)1;
	}

	jbyte output_bits[BITS_SIZE];
	jshort* buffer = (*env)->GetShortArrayElements(env, input, NULL);

	codec2_encode(codec2, (unsigned char*)output_bits, buffer);

	(*env)->ReleaseShortArrayElements(env, input, buffer, JNI_ABORT);
	(*env)->SetByteArrayRegion(env, output, 0, BITS_SIZE, output_bits);

	return (jint)0;
}

JNIEXPORT jint JNICALL Java_pl_net_hola_theonionphone_audio_codecs_Codec2_codec2_decode
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

JNIEXPORT void JNICALL Java_pl_net_hola_theonionphone_audio_codecs_Codec2_codec2_release
	(JNIEnv *env, jobject obj) {

	codec2_destroy(codec2);
}

