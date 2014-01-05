
#ifndef SIMPLE_RTP_H_
#define SIMPLE_RTP_H_


typedef struct {
	int mSampleCount;

	uint32_t mCodecMagic;

	uint16_t mSequence;
	uint32_t mTimestamp;
	uint32_t mSsrc;
} rtp_context_t;

/*
 * Initializes an rtp context.
 *
 * requires codecType - payload type for rtp
 * and sampleCount - count of samples in one packet
 */
 err_status_t initRtp(rtp_context_t* ctx, int codecType, int sampleCount);


/*
 * Writes a 12-byte packet header to output assuming space there is allocated.
 */
void getNextPacketHeader(rtp_context_t* ctx, void* output);


/*
 * Returns packet header size in 32-bit ints
 */
int getHeaderSize();

#endif /* SIMPLE_RTP_H_ */
