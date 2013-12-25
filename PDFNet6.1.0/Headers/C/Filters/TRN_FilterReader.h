
//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef	  H_CFilterReader
#define	  H_CFilterReader

#ifdef __cplusplus
extern "C" {
#endif 

#include <C/Common/TRN_Types.h>
#include <C/Filters/TRN_Filter.h>
#include <stdio.h>

TRN_API TRN_FilterReaderCreate(TRN_Filter filter, TRN_FilterReader* result);
TRN_API TRN_FilterReaderDestroy (TRN_FilterReader reader);
TRN_API TRN_FilterReaderAttachFilter(TRN_FilterReader reader, TRN_Filter filter);
TRN_API TRN_FilterReaderGetAttachedFilter(TRN_FilterReader reader, TRN_Filter* result);

// Object forwarding methods
TRN_API TRN_FilterReaderSeek(TRN_FilterReader reader, TRN_Ptrdiff offset, enum TRN_FilterReferencePos origin);
TRN_API TRN_FilterReaderTell (TRN_FilterReader reader, TRN_Ptrdiff* result);
TRN_API TRN_FilterReaderCount (TRN_FilterReader reader, TRN_Size* result);
TRN_API TRN_FilterReaderFlush (TRN_FilterReader reader);
TRN_API TRN_FilterReaderFlushAll (TRN_FilterReader reader);
TRN_API TRN_FilterReaderGet(TRN_FilterReader reader, int* result);
TRN_API TRN_FilterReaderPeek(TRN_FilterReader reader, int* result);
TRN_API TRN_FilterReaderRead(TRN_FilterReader reader, TRN_UChar* buf, TRN_Size buf_size, TRN_Size* result);

#ifdef __cplusplus
}
#endif 


#endif //H_CFilterReader
