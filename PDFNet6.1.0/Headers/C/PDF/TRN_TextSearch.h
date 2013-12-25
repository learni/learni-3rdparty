//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   HPP_CTextSearch
#define   HPP_CTextSearch


#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>

enum TRN_TextSearchResultCode
{
    e_done = 0,
    e_page = 1,
    e_found = 2
};

enum TRN_TextSearchMode
{
	e_reg_expression = 0x0001, 
	e_case_sensitive = e_reg_expression << 1,
	e_whole_word	 = e_case_sensitive << 1,
	e_search_up		 = e_whole_word << 1,
	e_page_stop		 = e_search_up << 1,
	e_highlight		 = e_page_stop << 1,
	e_ambient_string = e_highlight << 1
};

TRN_API TRN_TextSearchCreate(TRN_TextSearch* result);
TRN_API TRN_TextSearchDestroy(TRN_TextSearch ts);

TRN_API TRN_TextSearchBegin(TRN_TextSearch ts, TRN_PDFDoc doc, TRN_UString pattern, TRN_UInt32 mode, int start_page, int end_page, TRN_Bool* result);
TRN_API TRN_TextSearchRun(TRN_TextSearch ts, int* page_num, TRN_UString result_str, TRN_UString ambient_str, TRN_Highlights hlts, enum TRN_TextSearchResultCode* result);
TRN_API TRN_TextSearchSetPattern(TRN_TextSearch ts, TRN_UString pattern, TRN_Bool* result);
TRN_API TRN_TextSearchGetMode(TRN_TextSearch ts, TRN_UInt32* result);
TRN_API TRN_TextSearchSetMode(TRN_TextSearch ts, TRN_UInt32 mode);
TRN_API TRN_TextSearchGetCurrentPage(TRN_TextSearch ts, int* page);

#ifdef __cplusplus
}
#endif

#endif
