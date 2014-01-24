package org.theonionphone.protocol;

public interface SrtpErrorCode {

	final static int OK           = 0;  /**< nothing to report                       */
	final static int FAIL         = 1;  /**< unspecified failure                     */
	final static int BAD_PARAM    = 2;  /**< unsupported parameter                   */
	final static int ALLOC_FAIL   = 3;  /**< couldn't allocate memory                */
	final static int DEALLOC_FAIL = 4;  /**< couldn't deallocate properly            */
	final static int INIT_FAIL    = 5;  /**< couldn't initialize                     */
	final static int TERMINUS     = 6;  /**< can't process as much data as requested */
	final static int AUTH_FAIL    = 7;  /**< authentication failure                  */
	final static int CIPHER_FAIL  = 8;  /**< cipher failure                          */
	final static int REPLAY_FAIL  = 9;  /**< replay check failed (bad index)         */
	final static int REPLAY_OLD   = 10; /**< replay check failed (index too old)     */
	final static int ALGO_FAIL    = 11; /**< algorithm failed test routine           */
	final static int NO_SUCH_OP   = 12; /**< unsupported operation                   */
	final static int NO_CTX       = 13; /**< no appropriate context found            */
	final static int CANT_CHECK   = 14; /**< unable to perform desired validation    */
	final static int KEY_EXPIRED  = 15; /**< can't use key any more                  */
	final static int SOCKET_ERR   = 16; /**< error in use of socket                  */
	final static int SIGNAL_ERR   = 17; /**< error in use POSIX signals              */
	final static int NONCE_BAD    = 18; /**< nonce check failed                      */
	final static int READ_FAIL    = 19; /**< couldn't read data                      */
	final static int WRITE_FAIL   = 20; /**< couldn't write data                     */
	final static int PARSE_ERR    = 21; /**< error pasring data                      */
	final static int ENCODE_ERR   = 22; /**< error encoding data                     */
	final static int SEMAPHORE_ERR = 23;/**< error while using semaphores            */
	final static int PFKEY_ERR    = 24;  /**< error while using pfkey                 */
}
