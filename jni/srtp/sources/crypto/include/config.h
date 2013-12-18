// /* $Id: srtp_config.h 4537 2013-06-19 06:47:43Z riza $ */
// /* 
 // * Copyright (C) 2003-2007 Benny Prijono <benny@prijono.org>
 // *
 // * This program is free software; you can redistribute it and/or modify
 // * it under the terms of the GNU General Public License as published by
 // * the Free Software Foundation; either version 2 of the License, or
 // * (at your option) any later version.
 // *
 // * This program is distributed in the hope that it will be useful,
 // * but WITHOUT ANY WARRANTY; without even the implied warranty of
 // * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 // * GNU General Public License for more details.
 // *
 // * You should have received a copy of the GNU General Public License
 // * along with this program; if not, write to the Free Software
 // * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 // */
// #ifndef __SRTP_CONFIG_H__
// #define __SRTP_CONFIG_H__

// //#include <pj/types.h>

// /* We'll just define CISC if it's x86 family */
// #if defined (PJ_M_I386) || defined(_i386_) || defined(i_386_) || \
    // defined(_X86_) || defined(x86) || defined(__i386__) || \
    // defined(__i386) || defined(_M_IX86) || defined(__I86__) || \
    // defined (PJ_M_X86_64) || defined(__amd64__) || defined(__amd64) || \
    // defined(__x86_64__) || defined(__x86_64) || \
    // defined(PJ_M_IA64) || defined(__ia64__) || defined(_IA64) || \
    // defined(__IA64__) || defined(_M_IA64)
// #   define CPU_CISC	    1
// /* #   define HAVE_X86	    1   use X86 inlined assembly code */
// #else
// /*#   define CPU_RISC	    1*/
// #   define CPU_CISC	    1
// #endif

// /* Define to compile in dynamic debugging system. */
// #define ENABLE_DEBUGGING    PJ_DEBUG

// /* Define to 1 if you have the <arpa/inet.h> header file. */
// #if defined(PJ_HAS_ARPA_INET_H) && PJ_HAS_ARPA_INET_H!=0
// #   define HAVE_ARPA_INET_H 1
// #endif

// /* Define to 1 if you have the <byteswap.h> header file. */
// /* #undef HAVE_BYTESWAP_H */

// /* Define to 1 if you have the `inet_aton' function. */
// #if defined(PJ_SOCK_HAS_INET_PTON) && PJ_SOCK_HAS_INET_PTON
// #   define HAVE_INET_ATON   1
// #endif


// /* Define to 1 if you have the <netinet/in.h> header file. */
// #if defined(PJ_HAS_NETINET_IN_H) && PJ_HAS_NETINET_IN_H!=0
// #   define HAVE_NETINET_IN_H	1
// #endif

// /* Define to 1 if you have the <stdlib.h> header file. */
// #if defined(PJ_HAS_STDLIB_H) && PJ_HAS_STDLIB_H!=0
// #   define HAVE_STDLIB_H    1
// #endif

// /* Define to 1 if you have the <string.h> header file. */
// #if defined(PJ_HAS_STRING_H) && PJ_HAS_STRING_H!=0
// #   define HAVE_STRING_H    1
// #endif

// /* Define to 1 if you have the <sys/socket.h> header file. */
// #if defined(PJ_HAS_SYS_SOCKET_H) && PJ_HAS_SYS_SOCKET_H!=0
// #   define HAVE_SYS_SOCKET_H	1
// #endif

// /* Define to 1 if you have the <sys/types.h> header file. */
// #if defined(PJ_HAS_SYS_TYPES_H) && PJ_HAS_SYS_TYPES_H!=0
// #   define HAVE_SYS_TYPES_H 1
// #endif

// /* Define to 1 if you have the <unistd.h> header file. */
// /* Define to 1 if you have the `usleep' function. */
// #if defined(PJ_HAS_UNISTD_H) && PJ_HAS_UNISTD_H!=0
// #   define HAVE_UNISTD_H    1
// #   define HAVE_USLEEP	    1
// #endif


// /* Define to 1 if you have the <windows.h> header file. */
// #if (defined(PJ_WIN32) && PJ_WIN32!=0) || (defined(PJ_WIN64) && PJ_WIN64 != 0)
// #   define HAVE_WINDOWS_H   1
// #endif

// /* Define to 1 if you have the <winsock2.h> header file. */
// #if defined(PJ_HAS_WINSOCK2_H) && PJ_HAS_WINSOCK2_H!=0
// #   define HAVE_WINSOCK2_H  1
// #endif

// #define HAVE_INT16_T	    1
// #define HAVE_INT32_T	    1
// #define HAVE_INT8_T	    1
// #define HAVE_UINT8_T	    1
// #define HAVE_UINT16_T	    1
// #define HAVE_UINT32_T	    1
// #define HAVE_UINT64_T	    1

// /* Define to 1 if you have the <stdint.h> header file. */
// #if defined(PJ_HAS_STDINT_H) && PJ_HAS_STDINT_H!=0
// #   define HAVE_STDINT_H    1
// #else
    // typedef pj_uint8_t	    uint8_t;
    // typedef pj_uint16_t	    uint16_t;
    // typedef pj_uint32_t	    uint32_t;
    // typedef pj_uint64_t	    uint64_t;
    // typedef pj_int8_t	    int8_t;
    // typedef pj_int16_t	    int16_t;
    // typedef pj_int32_t	    int32_t;
    // typedef pj_int64_t	    int64_t;
// #endif

// /* These shouldn't really matter as long as HAVE_UINT64_T is set */
// #define SIZEOF_UNSIGNED_LONG	    (sizeof(unsigned long))
// #define SIZEOF_UNSIGNED_LONG_LONG   8


// #if (_MSC_VER >= 1400) // VC8+
// #   ifndef _CRT_SECURE_NO_DEPRECATE
// #	define _CRT_SECURE_NO_DEPRECATE
// #   endif
// #   ifndef _CRT_NONSTDC_NO_DEPRECATE
// #	define _CRT_NONSTDC_NO_DEPRECATE
// #   endif
// #endif // VC8+

// #ifdef _MSC_VER
// #   ifndef __cplusplus
// #	define inline _inline
// #   endif

// #   pragma warning(disable:4311)
// #   pragma warning(disable:4761) // integral mismatch
// #   pragma warning(disable:4018) // signed/unsigned mismatch
// #   pragma warning(disable:4244) // conversion from int64 to int
// #   pragma warning(disable:4100) // unreferenced formal parameter
// #endif

// /* clock()  */
// #if defined(PJ_WIN32_WINCE) && PJ_WIN32_WINCE!=0
    // /* clock() causes unresolved symbol on linking */
// #   define _CLOCK_T_DEFINED
// #   define CLOCKS_PER_SEC   1000
// #   define clock_t	    unsigned

    // #include <windows.h>
    // static clock_t clock(void)
    // {
	// return GetTickCount();
    // }
// #endif


// /* Path to random device */
// /* #define DEV_URANDOM "/dev/urandom" */

// /* Only with PJSIP:
 // * Try to open PJ_DEV_URANDOM if present
 // */
// #if defined(PJ_HAS_FCNTL_H) && defined(PJ_HAS_UNISTD_H)
// #   define PJ_DEV_URANDOM	"/dev/urandom"
// #endif

// /* We have overridden libsrtp error mechanism, so these are not used. */
// /* #undef ERR_REPORTING_FILE */
// /* #undef ERR_REPORTING_STDOUT */
// /* #undef USE_ERR_REPORTING_FILE */
// /* #undef USE_SYSLOG */
// /* #undef HAVE_SYSLOG_H */


// /* Define this to use ISMAcryp code. */
// /* #undef GENERIC_AESICM */

// /* Define to 1 if you have the <inttypes.h> header file. */
// /* #undef HAVE_INTTYPES_H */

// /* Define to 1 if you have the `socket' function. */
// /* #undef HAVE_SOCKET */

// /* Define to 1 if you have the `socket' library (-lsocket). */
// /* #undef HAVE_LIBSOCKET */

// /* Define to 1 if you have the <machine/types.h> header file. */
// /* #undef HAVE_MACHINE_TYPES_H */


// /* Define to 1 if you have the <strings.h> header file. */
// //#define HAVE_STRINGS_H 1

// /* Define to 1 if you have the <sys/int_types.h> header file. */
// /* #undef HAVE_SYS_INT_TYPES_H */

// /* Define to use GDOI. */
// /* #undef SRTP_GDOI */

// /* Define to compile for kernel contexts. */
// /* #undef SRTP_KERNEL */

// /* Define to compile for Linux kernel context. */
// /* #undef SRTP_KERNEL_LINUX */

// /* Define to 1 if you have the ANSI C header files. */
// //#define STDC_HEADERS 1

// /* Endianness would have been set by pjlib. */
// /* #undef WORDS_BIGENDIAN */

// /* Define to empty if `const' does not conform to ANSI C. */
// /* #undef const */

// /* Define to `unsigned' if <sys/types.h> does not define. */
// /* #undef size_t */


// #endif	/* __SRTP_CONFIG_H__ */












/* config.h for Win32/MSVC */

/* Define if building for a CISC machine (e.g. Intel). */
#define CPU_CISC 1

/* Define if building for a RISC machine (assume slow byte access). */
/* #undef CPU_RISC */

/* Path to random device */
/* #define DEV_URANDOM "/dev/urandom" */

/* Define to compile in dynamic debugging system. */
#define ENABLE_DEBUGGING 1

/* Report errors to this file. */
#undef ERR_REPORTING_FILE

/* Define to use logging to stdout. */
#define ERR_REPORTING_STDOUT 1

/* Define this to use ISMAcryp code. */
/* #undef GENERIC_AESICM */

/* Define to 1 if you have the <arpa/inet.h> header file. */
/* #undef HAVE_ARPA_INET_H */

/* Define to 1 if you have the <byteswap.h> header file. */
/* #undef HAVE_BYTESWAP_H */

/* Define to 1 if you have the `inet_aton' function. */
/* #undef HAVE_INET_ATON */

/* Define to 1 if the system has the type `int16_t'. */
#define HAVE_INT16_T 1

/* Define to 1 if the system has the type `int32_t'. */
#define HAVE_INT32_T 1

/* Define to 1 if the system has the type `int8_t'. */
#define HAVE_INT8_T 1

/* Define to 1 if you have the <inttypes.h> header file. */
/* #undef HAVE_INTTYPES_H */

/* Define to 1 if you have the `socket' library (-lsocket). */
/* #undef HAVE_LIBSOCKET */

/* Define to 1 if you have the <machine/types.h> header file. */
/* #undef HAVE_MACHINE_TYPES_H */

/* Define to 1 if you have the <memory.h> header file. */
#define HAVE_MEMORY_H 1

/* Define to 1 if you have the <netinet/in.h> header file. */
#define HAVE_NETINET_IN_H 1

/* Define to 1 if you have the `socket' function. */
/* #undef HAVE_SOCKET */

/* Define to 1 if you have the <stdint.h> header file. */
/* #undef HAVE_STDINT_H */

/* Define to 1 if you have the <stdlib.h> header file. */
#define HAVE_STDLIB_H 1

/* Define to 1 if you have the <strings.h> header file. */
#define HAVE_STRINGS_H 1

/* Define to 1 if you have the <string.h> header file. */
#define HAVE_STRING_H 1

/* Define to 1 if you have the <syslog.h> header file. */
/* #undef HAVE_SYSLOG_H */

/* Define to 1 if you have the <sys/int_types.h> header file. */
/* #undef HAVE_SYS_INT_TYPES_H */

/* Define to 1 if you have the <sys/socket.h> header file. */
/* #undef HAVE_SYS_SOCKET_H */

/* Define to 1 if you have the <sys/stat.h> header file. */
#define HAVE_SYS_STAT_H 1

/* Define to 1 if you have the <sys/types.h> header file. */
#define HAVE_SYS_TYPES_H 1

/* Define to 1 if you have the <sys/uio.h> header file. */
/* #undef HAVE_SYS_UIO_H */

/* Define to 1 if the system has the type `uint16_t'. */
#define HAVE_UINT16_T 1

/* Define to 1 if the system has the type `uint32_t'. */
#define HAVE_UINT32_T 1

/* Define to 1 if the system has the type `uint64_t'. */
#define HAVE_UINT64_T 1

/* Define to 1 if the system has the type `uint8_t'. */
#define HAVE_UINT8_T 1

/* Define to 1 if you have the <unistd.h> header file. */
/* #undef HAVE_UNISTD_H */

/* Define to 1 if you have the `usleep' function. */
/* #undef HAVE_USLEEP */

/* Define to 1 if you have the <windows.h> header file. */
#define HAVE_WINDOWS_H 1

/* Define to 1 if you have the <winsock2.h> header file. */
/* #define HAVE_WINSOCK2_H 1 */

/* Define to use X86 inlined assembly code */
/* #undef HAVE_X86 */

/* Define to the address where bug reports for this package should be sent. */
#define PACKAGE_BUGREPORT ""

/* Define to the full name of this package. */
#define PACKAGE_NAME ""

/* Define to the full name and version of this package. */
#define PACKAGE_STRING ""

/* Define to the one symbol short name of this package. */
#define PACKAGE_TARNAME ""

/* Define to the version of this package. */
#define PACKAGE_VERSION ""

/* The size of a `unsigned long', as computed by sizeof. */
#define SIZEOF_UNSIGNED_LONG 4

/* The size of a `unsigned long long', as computed by sizeof. */
#define SIZEOF_UNSIGNED_LONG_LONG 8

/* Define to use GDOI. */
/* #undef SRTP_GDOI */

/* Define to compile for kernel contexts. */
/* #undef SRTP_KERNEL */

/* Define to compile for Linux kernel context. */
/* #undef SRTP_KERNEL_LINUX */

/* Define to 1 if you have the ANSI C header files. */
#define STDC_HEADERS 1

/* Write errors to this file */
/* #undef USE_ERR_REPORTING_FILE */

/* Define to use syslog logging. */
#undef USE_SYSLOG

/* Define to 1 if your processor stores words with the most significant byte
   first (like Motorola and SPARC, unlike Intel and VAX). */
/* #undef WORDS_BIGENDIAN */

/* Define to empty if `const' does not conform to ANSI C. */
/* #undef const */

/* Define 'inline' to nothing, since the MSVC compiler doesn't support it.  */
#define inline 

/* Define to `unsigned' if <sys/types.h> does not define. */
/* #undef size_t */

#if (_MSC_VER >= 1400) // VC8+
#   ifndef _CRT_SECURE_NO_DEPRECATE
#	define _CRT_SECURE_NO_DEPRECATE
#   endif
#   ifndef _CRT_NONSTDC_NO_DEPRECATE
#	define _CRT_NONSTDC_NO_DEPRECATE
#   endif
#endif // VC8+

#ifndef uint32_t
    typedef unsigned char	uint8_t;
    typedef unsigned short	uint16_t;
    typedef unsigned int	uint32_t;
//    typedef unsigned __int64    uint64_t;
    typedef signed char		int8_t;
    typedef short		int16_t;
    typedef int			int32_t;
//    typedef __int64		int64_t;
#endif

#ifdef _MSC_VER
    #pragma warning(disable:4311)
    #pragma warning(disable:4761) // integral mismatch
    #pragma warning(disable:4018) // signed/unsigned mismatch
    #pragma warning(disable:4244) // conversion from int64 to int
#endif
