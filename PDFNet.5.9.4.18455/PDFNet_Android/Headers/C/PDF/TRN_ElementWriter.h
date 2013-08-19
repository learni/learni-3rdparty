//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.	 
//---------------------------------------------------------------------------------------

#ifndef	  H_CPDFElementWriter
#define	  H_CPDFElementWriter

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>

enum TRN_ElementWriterWriteMode {
	TRN_ElementWriter_e_underlay,    ///> element is put in the background layer of the page
	TRN_ElementWriter_e_overlay,     ///> element appears on top of the existing graphics
	TRN_ElementWriter_e_replacement  ///> element will replace current page contents
};

TRN_API TRN_ElementWriterCreate(TRN_ElementWriter* result);
TRN_API TRN_ElementWriterDestroy(TRN_ElementWriter w);
TRN_API TRN_ElementWriterBeginOnPage(TRN_ElementWriter w, TRN_Page page, enum TRN_ElementWriterWriteMode placement, TRN_Bool page_coord_sys, TRN_Bool compress);
TRN_API TRN_ElementWriterBegin(TRN_ElementWriter w, TRN_SDFDoc doc, TRN_Bool compress);
TRN_API TRN_ElementWriterBeginOnObj( TRN_ElementWriter w, TRN_Obj obj, TRN_Bool compress );
TRN_API TRN_ElementWriterEnd(TRN_ElementWriter w, TRN_Obj* result);
TRN_API TRN_ElementWriterWriteElement(TRN_ElementWriter w, TRN_Element element);
TRN_API TRN_ElementWriterWritePlacedElement(TRN_ElementWriter w, TRN_Element element);
TRN_API TRN_ElementWriterFlush(TRN_ElementWriter w); 
TRN_API TRN_ElementWriterWriteBuffer(TRN_ElementWriter w, const char* data, int data_sz);
TRN_API TRN_ElementWriterWriteString(TRN_ElementWriter w, const char* str);

#ifdef __cplusplus
}
#endif


#endif
