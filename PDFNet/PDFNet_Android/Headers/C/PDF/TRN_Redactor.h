//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CRedactor
#define   H_CRedactor

#include <C/Common/TRN_Types.h>

#ifdef __cplusplus
extern "C" {
#endif

struct TRN_redaction;
typedef struct TRN_redaction* TRN_Redaction;

TRN_API TRN_Redactor_RedactionCreate(TRN_Redaction* result, 
									 int page_num, 
									 const TRN_Rect* bbox, 
									 TRN_Bool negative, 
									 const TRN_UString text);
TRN_API TRN_Redactor_RedactionDestroy(TRN_Redaction redaction);
TRN_API TRN_Redactor_RedactionCopy(TRN_Redaction* result, TRN_Redaction other);

struct TRN_redaction_app;
typedef struct TRN_redaction_app* TRN_RedactionAppearance;

TRN_API TRN_Redactor_AppearanceCreate(TRN_RedactionAppearance* result, 
									  TRN_Bool redaction_overlay,
									  const TRN_ColorPt* positive_overlay_color,
									  const TRN_ColorPt* negative_overlay_color,
									  TRN_Bool border,
									  TRN_Bool use_overlay_text,
									  TRN_Font font,
									  double min_font_size,
									  double max_font_size,
									  const TRN_ColorPt* text_color,
									  int horiz_text_alignment,
									  int vert_text_alignment,
									  TRN_Bool show_redacted_content_regions,
									  const TRN_ColorPt* redacted_content_color);
TRN_API TRN_Redactor_AppearanceDestroy(TRN_RedactionAppearance app);

TRN_API TRN_Redactor_Redact(TRN_PDFDoc doc, const TRN_Redaction* red_arr, int buf_size, TRN_RedactionAppearance appearance,
							TRN_Bool ext_neg_mode, TRN_Bool page_coord_sys);

#ifdef __cplusplus
}
#endif

#endif // H_CRedactor
