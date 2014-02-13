
//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.	 
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFStamper
#define   H_CPDFStamper

#define StampCCast (Stamper*)

#include <C/Common/TRN_Types.h>
#include <C/PDF/TRN_Stamper.h>

#ifdef __cplusplus
extern "C" {
#endif

enum TRN_SizeType
{
	e_SizeType_relative_scale = 1,
	e_SizeType_absolute_size = 2,
	e_SizeType_font_size = 3
};

enum TRN_TextAlignment
{
	e_TextAlignment_align_left = -1,
	e_TextAlignment_align_center = 0,
	e_TextAlignment_align_right = 1
};
enum TRN_HorizontalAlignment
{
	e_HorizontalAlignment_horizontal_left = -1,
	e_HorizontalAlignment_horizontal_center = 0,
	e_HorizontalAlignment_horizontal_right = 1
};

enum TRN_VerticalAlignment
{
	e_VerticalAlignment_vertical_bottom = -1,
	e_VerticalAlignment_vertical_center = 0,
	e_VerticalAlignment_vertical_top = 1
};

//Constructor and destructor
TRN_API TRN_StamperCreate(TRN_Stamper* result, enum TRN_SizeType size_type, double a, double b);

TRN_API TRN_StamperDestroy(TRN_Stamper stamp);

//Stamps
TRN_API TRN_StamperStampImage(TRN_Stamper stamp, TRN_PDFDoc dest_doc, TRN_Image img, TRN_PageSet page_set);

TRN_API TRN_StamperStampPage(TRN_Stamper stamp, TRN_PDFDoc dest_doc, TRN_Page page, TRN_PageSet page_set);

TRN_API TRN_StamperStampText(TRN_Stamper stamp, TRN_PDFDoc dest_doc, TRN_UString txt, TRN_PageSet page_set);


//Text-only methods
TRN_API TRN_StamperSetFont(TRN_Stamper stamp, TRN_Font font);

TRN_API TRN_StamperSetFontColor(TRN_Stamper stamp, const TRN_ColorPt* font_color);

TRN_API TRN_StamperSetTextAlignment(TRN_Stamper stamp, enum TRN_TextAlignment text_alignment);

//Appearance
TRN_API TRN_StamperSetOpacity(TRN_Stamper stamp, double opacity);

TRN_API TRN_StamperSetRotation(TRN_Stamper stamp, double rotation);

TRN_API TRN_StamperSetAsBackground(TRN_Stamper stamp, int background);

TRN_API TRN_StamperSetAsAnnotation(TRN_Stamper stamp, int annotation);

TRN_API TRN_StamperShowsOnScreen(TRN_Stamper stamp, int on_screen);

TRN_API TRN_StamperShowsOnPrint(TRN_Stamper stamp, int on_print);

//Position
TRN_API TRN_StamperSetAlignment(TRN_Stamper stamp, enum TRN_HorizontalAlignment horizontal_alignment, enum TRN_VerticalAlignment vertical_alignment);

TRN_API TRN_StamperSetPosition(TRN_Stamper stamp, double x, double y, int percentage);

//Size
TRN_API TRN_StamperSetSize(TRN_Stamper stamp, enum TRN_SizeType size_type, double a, double b);

//Static Methods
TRN_API TRN_StamperDeleteStamps(TRN_PDFDoc doc, TRN_PageSet page_set);

TRN_API TRN_StamperHasStamps(TRN_PDFDoc doc, TRN_PageSet page_set, int* result);

#ifdef __cplusplus
}
#endif

#endif
