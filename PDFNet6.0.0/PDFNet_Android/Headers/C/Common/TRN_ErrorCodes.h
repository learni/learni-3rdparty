//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef TRN_ERROR_CODE_H
#define TRN_ERROR_CODE_H

 #ifdef __cplusplus
 extern "C" {
 #endif 

#include <C/Common/TRN_Types.h>

	enum TRN_ErrorCodes
	{
		e_error_general,
		e_error_network,
		e_error_credentials,
		e_error_num
	};

 #ifdef __cplusplus
 } // extern C
 #endif 


#endif /* TRN_ERROR_CODE_H */

