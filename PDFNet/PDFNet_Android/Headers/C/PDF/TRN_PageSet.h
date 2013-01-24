
//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.	 
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFPageSet
#define   H_CPDFPageSet

#define PageSetCCast (PageSet*)

#include <C/Common/TRN_Types.h>
#include <C/PDF/TRN_PageSet.h>

#ifdef __cplusplus
extern "C" {
#endif


enum TRN_PagesFilter
{
	e_PagesFilter_all,
	e_PagesFilter_even,
	e_PagesFilter_odd
};

//Constructor and destructor
TRN_API TRN_PageSetCreate(TRN_PageSet* result);

TRN_API TRN_PageSetCreateSinglePage(TRN_PageSet* result, int one_page);

TRN_API TRN_PageSetCreateRange(TRN_PageSet* result, int range_start, int range_end);

TRN_API TRN_PageSetCreateFilteredRange(TRN_PageSet* result, int range_start, int range_end, TRN_PagesFilter filter);

TRN_API TRN_PageSetDestroy(TRN_PageSet page_set);

TRN_API TRN_PageSetAddPage(TRN_PageSet page_set, int one_page);

TRN_API TRN_PageSetAddRange(TRN_PageSet page_set, int range_start, int range_end);

TRN_API TRN_PageSetAddFilteredRange(TRN_PageSet page_set, int range_start, int range_end, TRN_PagesFilter filter);


#ifdef __cplusplus
}
#endif

#endif

