//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef TRN_EXCEPTIONS_H
#define TRN_EXCEPTIONS_H

 #ifdef __cplusplus
 extern "C" {
 #endif 

#include <C/Common/TRN_Types.h>

TRN_API_T(TRN_Int32) TRN_GetLineNum(TRN_Exception e);
TRN_API_T(const char*) TRN_GetCondExpr(TRN_Exception e);
TRN_API_T(const char*) TRN_GetFileName(TRN_Exception e);
TRN_API_T(const char*) TRN_GetFunction(TRN_Exception e);
TRN_API_T(const char*) TRN_GetMessage(TRN_Exception e);
TRN_API TRN_CreateException( const char* cond_expr, 
			const char* filename, TRN_Int32 linenumber, 
			const char* function, const char* message);


 #ifdef __cplusplus
 } // extern C
 #endif 


#endif /* TRN_EXCEPTIONS_H */

