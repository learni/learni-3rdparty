//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFFlattener
#define   H_CPDFFlattener

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>

TRN_API TRN_FlattenerCreate (TRN_Flattener* result);
TRN_API TRN_FlattenerDestroy(TRN_Flattener flattener);

enum TRN_FlattenMode
{
	flatten_mode_simple,
	flatten_mode_fast
};

TRN_API TRN_FlattenerSetDPI(TRN_Flattener flattener, TRN_UInt32 dpi);
TRN_API TRN_FlattenerSetMaximumImagePixels(TRN_Flattener flattener, TRN_UInt32 max_pixels);
TRN_API TRN_FlattenerSetPreferJpg(TRN_Flattener flattener, bool jpg);
TRN_API TRN_FlattenerProcess(TRN_Flattener flattener, TRN_PDFDoc doc, enum TRN_FlattenMode mode);

#ifdef __cplusplus
}
#endif

#endif
