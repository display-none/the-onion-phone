/*
 * This implementation is based on android's
 * implementation of AudioGroup
 */

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <fcntl.h>
//#include <netinet/in.h>
#include <unistd.h>

#include <srtp_priv.h>
#include <simple_rtp.h>


 err_status_t initRtp(rtp_context_t* ctx, int codecType, int sampleCount) {
	ctx = malloc(sizeof(rtp_context_t));

	ctx->mCodecMagic = (0xA000 | codecType) << 16;	//version - 2, padding - 1, payload type - codecType

	ctx->mSampleCount = sampleCount;

	int gRandom = open("/dev/urandom", O_RDONLY);
	read(gRandom, &ctx->mSequence, sizeof(ctx->mSequence));
	read(gRandom, &ctx->mTimestamp, sizeof(ctx->mTimestamp));
	read(gRandom, &ctx->mSsrc, sizeof(ctx->mSsrc));

	return 0;
}

void getNextPacketHeader(rtp_context_t* ctx, void* output) {

	++ctx->mSequence;
	ctx->mTimestamp += ctx->mSampleCount;

	int32_t* header = (int32_t*) output;
	header[0] = htonl(ctx->mCodecMagic | ctx->mSequence);
	header[1] = htonl(ctx->mTimestamp);
	header[2] = htonl(ctx->mSsrc);

}

int getHeaderSize() {
	return sizeof(srtp_hdr_t)/sizeof(uint32_t);
}

void deinit(rtp_context_t* ctx) {
	free(ctx);
}
