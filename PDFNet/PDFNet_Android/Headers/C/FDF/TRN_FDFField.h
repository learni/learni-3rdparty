//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CFDFField
#define   H_CFDFField

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>
	
TRN_API TRN_FDFFieldCreate (TRN_Obj field_dict, TRN_Obj fdf_dict, TRN_FDFField* result);

TRN_API TRN_FDFFieldAssign (TRN_FDFField* left, const TRN_FDFField* right);

TRN_API TRN_FDFFieldGetValue(TRN_FDFField* field, TRN_Obj* result);

TRN_API TRN_FDFFieldSetValue(TRN_FDFField* field, TRN_Obj value);

TRN_API TRN_FDFFieldGetName(TRN_FDFField* field, TRN_UString* result);

TRN_API TRN_FDFFieldGetPartialName(TRN_FDFField* field, TRN_UString* result);

TRN_API TRN_FDFFieldGetSDFObj(const TRN_FDFField* field, TRN_Obj* result);

TRN_API TRN_FDFFieldFindAttribute (const TRN_FDFField* field, const char* attrib, TRN_Obj* result);

#ifdef __cplusplus
}
#endif

#endif


