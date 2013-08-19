//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#if defined(_WIN32) && !defined(WINCE) && !defined(__WINRT__)

#ifndef   H_CPDFDCEX
#define   H_CPDFDCEX

#include <C/Common/TRN_Types.h>
#include <Windows.h>

#ifdef __cplusplus
extern "C" {
#endif

TRN_API TRN_PDFDCEXCreate (TRN_PDFDCEX *result);
TRN_API TRN_PDFDCEXBegin (TRN_PDFDCEX this_pdfdcex, TRN_PDFDoc in_pdfdoc, HDC *result );
TRN_API TRN_PDFDCEXEnd (TRN_PDFDCEX this_pdfDcEx);
TRN_API TRN_PDFDCEXGetDPI(TRN_PDFDCEX this_pdfDcEx, TRN_UInt32* out_dpi);
TRN_API TRN_PDFDCEXDestroy(TRN_PDFDCEX this_pdfDcEx);



#ifdef __cplusplus
}
#endif

#endif // H_CPDFDCEX
#endif // defined(_WIN32) && !defined(WINCE)
