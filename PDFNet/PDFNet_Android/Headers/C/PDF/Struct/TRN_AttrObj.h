//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFAttrObj
#define   H_CPDFAttrObj

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>

TRN_API TRN_AttrObjCreate (TRN_Obj dict, TRN_AttrObj* result);
TRN_API TRN_AttrObjCopy (const TRN_AttrObj a, TRN_AttrObj* result);
TRN_API TRN_AttrObjGetOwner(const TRN_AttrObj obj, const char** result);
TRN_API TRN_AttrObjGetSDFObj (const TRN_AttrObj obj, TRN_Obj* result);

#ifdef __cplusplus
}
#endif

#endif
