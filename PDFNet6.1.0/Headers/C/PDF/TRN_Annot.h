//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFAnnot
#define   H_CPDFAnnot

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>


//1. General Annot
enum TRN_AnnotType
{
	e_Text,           ///< Text annotation
	e_Link,           ///< Link annotation
	e_FreeText,       ///< Free text annotation
	e_Line,           ///< Line annotation
	e_Square,         ///< Square annotation
	e_Circle,         ///< Circle annotation
	e_Polygon,        ///< Polygon annotation
	e_Polyline,       ///< Polyline annotation
	e_Highlight,      ///< Highlight annotation
	e_Underline,      ///< Underline annotation
	e_Squiggly,       ///< Squiggly-underline annotation
	e_StrikeOut,      ///< Strikeout annotation
	e_Stamp,          ///< Rubber stamp annotation
	e_Caret,          ///< Caret annotation
	e_Ink,            ///< Ink annotation
	e_Popup,          ///< Pop-up annotation
	e_FileAttachment, ///< File attachment annotation
	e_Sound,          ///< Sound annotation
	e_Movie,          ///< Movie annotation
	e_Widget,         ///< Widget annotation
	e_Screen,         ///< Screen annotation
	e_PrinterMark,    ///< Printer's mark annotation
	e_TrapNet,        ///< Trap network annotation
	e_Watermark,      ///< Watermark annotation
	e_3D,             ///< 3D annotation
	e_Redact,         ///< Redact annotation
	e_Projection,	  ///< Projection annotation, Adobe supplement to ISO 32000 
	e_RichMedia,      ///< Rich Media annotation, Adobe supplement to ISO 32000 
	e_Unknown         ///< Any other annotation type, not listed in PDF spec and unrecognized by PDFTron software
};	



enum TRN_AnnotFlag
{
	e_invisible,         // PDF 1.2
	e_hidden,            // PDF 1.2
	e_print,             // PDF 1.2
	e_no_zoom,           // PDF 1.3
	e_no_rotate,         // PDF 1.3
	e_no_view,           // PDF 1.3
	e_annot_read_only,   // PDF 1.3
	e_locked,            // PDF 1.4
	e_toggle_no_view,     // PDF 1.5
	e_locked_contents    // PDF 1.7
};

enum TRN_AnnotBorderStyleStyle {
	e_solid,   ///< A solid rectangle surrounding the annotation.
	e_dashed,  ///< A dashed rectangle surrounding the annotation.
	e_beveled, ///< A simulated embossed rectangle that appears to be raised above the surface of the page.
	e_inset,   ///< A simulated engraved rectangle that appears to be recessed below the surface of the page. 
	e_underline  ///< A single line along the bottom of the annotation rectangle.
};

	
enum TRN_AnnotState
{
	e_normal,
	e_rollover,
	e_down
};

TRN_API TRN_AnnotCreate(TRN_SDFDoc doc, enum TRN_AnnotType type, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_AnnotCreateFromObj (TRN_Obj d, TRN_Annot* result);
//TRN_API TRN_AnnotAssign(TRN_Annot* left, TRN_Annot* right);
TRN_API TRN_AnnotCopy (const TRN_Annot d, TRN_Annot* result);
TRN_API TRN_AnnotCompare(const TRN_Annot annot, const TRN_Annot d, TRN_Bool* result);
TRN_API TRN_AnnotIsValid(TRN_Annot annot, TRN_Bool* result);
TRN_API TRN_AnnotGetSDFObj (TRN_Annot annot, TRN_Obj* result);
TRN_API TRN_AnnotGetType(TRN_Annot annot, enum TRN_AnnotType* result);
TRN_API TRN_AnnotIsMarkup( TRN_Annot annot, TRN_Bool* result );
TRN_API TRN_AnnotGetRect(TRN_Annot annot, TRN_Rect* result) ;
TRN_API TRN_AnnotSetRect(TRN_Annot annot, const TRN_Rect* pos); 
TRN_API TRN_AnnotResize(TRN_Annot annot, const TRN_Rect* rect) ;
TRN_API TRN_AnnotSetContents(TRN_Annot annot, const TRN_UString cont );
TRN_API TRN_AnnotGetContents(TRN_Annot annot, TRN_UString* result );
//---------------------------------------------------------------------------------------
//TRN_API TRN_AnnotUpdateRect(TRN_Annot annot, const TRN_Rect* pos);
//---------------------------------------------------------------------------------------
TRN_API TRN_AnnotGetPage(TRN_Annot annot, TRN_Page* result);
TRN_API TRN_AnnotSetPage(TRN_Annot annot, const TRN_Page pg);
TRN_API TRN_AnnotGetUniqueID(TRN_Annot annot,TRN_Obj* result);
TRN_API TRN_AnnotSetUniqueID(TRN_Annot annot, const char* id, int id_buf_sz);
TRN_API TRN_AnnotGetDate(TRN_Annot annot, TRN_Date* result);
TRN_API TRN_AnnotSetDate(TRN_Annot annot, const TRN_Date* date);
TRN_API TRN_AnnotGetFlag(TRN_Annot annot, enum TRN_AnnotFlag flag, TRN_Bool* result);
TRN_API TRN_AnnotSetFlag(TRN_Annot annot, enum TRN_AnnotFlag flag, TRN_Bool value);
TRN_API TRN_AnnotBorderStyleCreate(enum TRN_AnnotBorderStyleStyle s, double b_width, double b_hr, double b_vr, TRN_AnnotBorderStyle* result);
TRN_API	TRN_AnnotBorderStyleCreateWithDashPattern(enum TRN_AnnotBorderStyleStyle s, double b_width, double b_hr, double b_vr, int buf_length, double* buffer, TRN_AnnotBorderStyle* result);
TRN_API TRN_AnnotBorderStyleCopy(const TRN_AnnotBorderStyle bs,TRN_AnnotBorderStyle* result);
TRN_API TRN_AnnotBorderStyleGetStyle(TRN_AnnotBorderStyle bs,enum TRN_AnnotBorderStyleStyle* result);
TRN_API TRN_AnnotBorderStyleSetStyle(TRN_AnnotBorderStyle bs,enum TRN_AnnotBorderStyleStyle style);
TRN_API TRN_AnnotBorderStyleDestroy(TRN_AnnotBorderStyle bs);
TRN_API TRN_AnnotGetAppearance(TRN_Annot annot, enum TRN_AnnotState annot_state, const char* app_state, TRN_Obj* result);
TRN_API TRN_AnnotSetAppearance(TRN_Annot annot, TRN_Obj app_stream, enum TRN_AnnotState annot_state, const char* app_state);
TRN_API TRN_AnnotRemoveAppearance(TRN_Annot annot, enum TRN_AnnotState annot_state, const char* app_state);
TRN_API TRN_AnnotFlatten(TRN_Annot annot, TRN_Page page);
TRN_API TRN_AnnotGetActiveAppearanceState(TRN_Annot annot, const char** result);
TRN_API TRN_AnnotSetActiveAppearanceState(TRN_Annot annot, const char* astate);
TRN_API TRN_AnnotGetColor(TRN_Annot annot, TRN_ColorPt* result);
TRN_API TRN_AnnotGetColorAsRGB(TRN_Annot annot, TRN_ColorPt* result);
TRN_API TRN_AnnotGetColorAsCMYK(TRN_Annot annot, TRN_ColorPt* result);
TRN_API TRN_AnnotGetColorAsGray(TRN_Annot annot, TRN_ColorPt* result);
TRN_API TRN_AnnotGetColorCompNum(TRN_Annot annot, int* result);
TRN_API TRN_AnnotSetColorDefault(TRN_Annot annot, const TRN_ColorPt* col);
TRN_API TRN_AnnotSetColor(TRN_Annot annot, const TRN_ColorPt* col, int numcomp);
TRN_API TRN_AnnotGetStructParent(TRN_Annot annot, int* result);
TRN_API TRN_AnnotSetStructParent(TRN_Annot annot, int pakeyval);
TRN_API TRN_AnnotGetOptionalContent(TRN_Annot annot, TRN_Obj* result);
TRN_API TRN_AnnotSetOptionalContent(TRN_Annot annot, const TRN_Obj* content);
TRN_API TRN_AnnotRefreshAppearance(TRN_Annot annot);
TRN_API TRN_AnnotBorderStyleCopy(const TRN_AnnotBorderStyle bs,TRN_AnnotBorderStyle* result);
TRN_API TRN_AnnotBorderStyleGetWidth(TRN_AnnotBorderStyle bs, double* result);
TRN_API TRN_AnnotBorderStyleSetWidth(TRN_AnnotBorderStyle bs, double width);
TRN_API TRN_AnnotBorderStyleGetHR(TRN_AnnotBorderStyle bs, double* result);
TRN_API TRN_AnnotBorderStyleSetHR(TRN_AnnotBorderStyle bs,double horizontal_radius);
TRN_API TRN_AnnotBorderStyleGetVR(TRN_AnnotBorderStyle bs, double* result);
TRN_API TRN_AnnotBorderStyleSetVR(TRN_AnnotBorderStyle bs, double vertical_radius);
TRN_API TRN_AnnotBorderStyleGetDashPattern(TRN_AnnotBorderStyle bs,int* buf_length, double** result);
TRN_API TRN_AnnotBorderStyleSetDashPattern(TRN_AnnotBorderStyle bs,int buf_length, const double* const buffer);
TRN_API TRN_AnnotGetBorderStyle(TRN_Annot annot,TRN_AnnotBorderStyle* result);
TRN_API TRN_AnnotSetBorderStyle(TRN_Annot annot, const TRN_AnnotBorderStyle bs, TRN_Bool oldStyleOnly );
TRN_API TRN_AnnotGetBorderStyleStyle( const TRN_AnnotBorderStyle bs, enum TRN_AnnotBorderStyleStyle* result);
TRN_API TRN_AnnotSetBorderStyleStyle( TRN_AnnotBorderStyle bs, const enum TRN_AnnotBorderStyleStyle bst );
TRN_API TRN_AnnotBorderStyleAssign( const TRN_AnnotBorderStyle from, TRN_AnnotBorderStyle to );
TRN_API TRN_AnnotBorderStyleCompare( const TRN_AnnotBorderStyle a, const TRN_AnnotBorderStyle b, TRN_Bool *result );

//Caret
TRN_API TRN_CaretAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_CaretAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_CaretAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_CaretAnnotGetSymbol(TRN_Annot caret, const char** result);
TRN_API TRN_CaretAnnotSetSymbol(TRN_Annot caret, const char* content);


enum TRN_LineAnnotEndingStyle
{
	e_Line_Square,
	e_Line_Circle,
	e_Line_Diamond,
	e_Line_OpenArrow,
	e_Line_ClosedArrow,						
	e_Line_Butt,
	e_Line_ROpenArrow,
	e_Line_RClosedArrow,
	e_Line_Slash,
	e_Line_None,
	e_Line_Unknown
};

enum TRN_LineAnnotIntentType
{
	e_Line_LineArrow,
	e_Line_LineDimension,
	e_Line_null
};
					
enum TRN_LineAnnotCapPos
{
	e_Line_Inline,
	e_Line_Top
};

TRN_API TRN_LineAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_LineAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);		
TRN_API TRN_LineAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_LineAnnotGetStartPoint(TRN_Annot line, TRN_Point* result);
TRN_API TRN_LineAnnotSetStartPoint(TRN_Annot line, const TRN_Point* stp);
TRN_API TRN_LineAnnotGetEndPoint(TRN_Annot line, TRN_Point* result);
TRN_API TRN_LineAnnotSetEndPoint(TRN_Annot line, const TRN_Point* etp);
TRN_API TRN_LineAnnotGetStartStyle(TRN_Annot line, enum TRN_LineAnnotEndingStyle* result);
TRN_API TRN_LineAnnotSetStartStyle(TRN_Annot line, enum TRN_LineAnnotEndingStyle ss);
TRN_API TRN_LineAnnotGetEndStyle(TRN_Annot line, enum TRN_LineAnnotEndingStyle* result);
TRN_API TRN_LineAnnotSetEndStyle(TRN_Annot line, enum TRN_LineAnnotEndingStyle es);
TRN_API TRN_LineAnnotGetLeaderLineLength(TRN_Annot line, double* result);
TRN_API TRN_LineAnnotSetLeaderLineLength(TRN_Annot line, double length);
TRN_API TRN_LineAnnotGetLeaderLineExtensionLength(TRN_Annot line, double* result);
TRN_API TRN_LineAnnotSetLeaderLineExtensionLength(TRN_Annot line, double length);
TRN_API TRN_LineAnnotGetShowCaption(TRN_Annot line, TRN_Bool* result);
TRN_API TRN_LineAnnotSetShowCaption(TRN_Annot line, TRN_Bool sorn);
TRN_API TRN_LineAnnotGetIntentType(TRN_Annot line, enum TRN_LineAnnotIntentType* result);
TRN_API TRN_LineAnnotSetIntentType(TRN_Annot line, enum TRN_LineAnnotIntentType it);
TRN_API TRN_LineAnnotGetCapPos(TRN_Annot line, enum TRN_LineAnnotCapPos* result);
TRN_API TRN_LineAnnotSetCapPos(TRN_Annot line, enum TRN_LineAnnotCapPos it);
TRN_API TRN_LineAnnotGetLeaderLineOffset(TRN_Annot line, double* result);
TRN_API TRN_LineAnnotSetLeaderLineOffset(TRN_Annot line, double length);
TRN_API TRN_LineAnnotGetTextHOffset(TRN_Annot line, double* result);
TRN_API TRN_LineAnnotSetTextHOffset(TRN_Annot line, double offset);
TRN_API TRN_LineAnnotGetTextVOffset(TRN_Annot line, double* result);
TRN_API TRN_LineAnnotSetTextVOffset(TRN_Annot line, double offset);



//Circle
TRN_API TRN_CircleAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_CircleAnnotCreateFromAnnot(TRN_Annot circle, TRN_Annot* result);
TRN_API TRN_CircleAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_CircleAnnotGetInteriorColor(TRN_Annot circle, TRN_ColorPt* result);
TRN_API TRN_CircleAnnotGetInteriorColorCompNum(TRN_Annot circle, int* result);
TRN_API TRN_CircleAnnotSetInteriorColorDefault(TRN_Annot circle, const TRN_ColorPt* col);
TRN_API TRN_CircleAnnotSetInteriorColor(TRN_Annot circle, const TRN_ColorPt* col, int numcomp);
TRN_API TRN_CircleAnnotGetContentRect(TRN_Annot circle, TRN_Rect* result);
TRN_API TRN_CircleAnnotSetContentRect(TRN_Annot circle, const TRN_Rect* cr);
TRN_API TRN_CircleAnnotGetPadding(TRN_Annot circle, TRN_Rect* result);
TRN_API TRN_CircleAnnotSetPadding(TRN_Annot circle, const TRN_Rect* cr);


//FileAttachment
enum TRN_FileAttachmentAnnotIcon
{
	e_FileAttachment_Graph,
	e_FileAttachment_PushPin,
	e_FileAttachment_Paperclip,
	e_FileAttachment_Tag,
	e_FileAttachment_Unknown
};

TRN_API TRN_FileAttachmentAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_FileAttachmentAnnotExport(TRN_Annot fileatt, const TRN_UString save_as, TRN_Bool* result);
TRN_API TRN_FileAttachmentAnnotCreateFromAnnot(TRN_Annot fileatt, TRN_Annot* result);
TRN_API TRN_FileAttachmentAnnotCreateWithFileSpec( TRN_SDFDoc doc, const TRN_Rect* pos, TRN_FileSpec fs, enum TRN_FileAttachmentAnnotIcon ic, TRN_Annot* result );
TRN_API TRN_FileAttachmentAnnotCreateWithIcon(TRN_SDFDoc doc, const TRN_Rect* pos, const TRN_UString path, enum TRN_FileAttachmentAnnotIcon ic, TRN_Annot* result);
TRN_API TRN_FileAttachmentAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, const TRN_UString path, const char* iconname, TRN_Annot* result);
TRN_API TRN_FileAttachmentAnnotCreateDefault(TRN_SDFDoc doc, const TRN_Rect* pos, const TRN_UString path, TRN_Annot* result);
TRN_API TRN_FileAttachmentAnnotGetFileSpec(TRN_Annot fileatt, TRN_FileSpec* result);
TRN_API TRN_FileAttachmentAnnotSetFileSpec(TRN_Annot fileatt, TRN_FileSpec fspec);
TRN_API TRN_FileAttachmentAnnotGetIcon(TRN_Annot fileatt, enum TRN_FileAttachmentAnnotIcon* result);
TRN_API TRN_FileAttachmentAnnotSetIcon(TRN_Annot fileatt, enum TRN_FileAttachmentAnnotIcon icon);
TRN_API TRN_FileAttachmentAnnotGetIconName(TRN_Annot fileatt, const char** result);
TRN_API TRN_FileAttachmentAnnotSetIconName(TRN_Annot fileatt, const char* iname);


enum TRN_FreeTextAnnotIntentName
{
	e_FreeText_FreeText,
	e_FreeText_FreeTextCallout,
	e_FreeText_FreeTextTypeWriter,
	e_FreeText_Unknown
};


TRN_API TRN_FreeTextAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_FreeTextAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_FreeTextAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_FreeTextAnnotGetDefaultAppearance(TRN_Annot ft, TRN_UString* result );
TRN_API TRN_FreeTextAnnotSetDefaultAppearance(TRN_Annot ft, const char* defApp);
TRN_API TRN_FreeTextAnnotGetQuaddingFormat(TRN_Annot ft, int* result);
TRN_API TRN_FreeTextAnnotSetQuaddingFormat(TRN_Annot ft, int format);
TRN_API TRN_FreeTextAnnotGetCalloutLinePoints(TRN_Annot ft, const TRN_Point* p1, const TRN_Point* p2, const TRN_Point* p3);
TRN_API TRN_FreeTextAnnotSetCalloutLinePoints(TRN_Annot ft, const TRN_Point* p1, const TRN_Point* p2, const TRN_Point* p3);
TRN_API TRN_FreeTextAnnotSetCalloutLinePointsTwo(TRN_Annot ft, const TRN_Point* p1, const TRN_Point* p2);
TRN_API TRN_FreeTextAnnotGetIntentName(TRN_Annot ft, enum TRN_FreeTextAnnotIntentName* result);
TRN_API TRN_FreeTextAnnotSetIntentName(TRN_Annot ft, enum TRN_FreeTextAnnotIntentName mode);
TRN_API TRN_FreeTextAnnotSetIntentNameDefault(TRN_Annot ft);
TRN_API TRN_FreeTextAnnotGetEndingStyle(TRN_Annot ft, enum TRN_LineAnnotEndingStyle* result);
TRN_API TRN_FreeTextAnnotSetEndingStyle(TRN_Annot ft, enum TRN_LineAnnotEndingStyle style);
TRN_API TRN_FreeTextAnnotSetEndingStyleName(TRN_Annot ft, const char* est);
TRN_API TRN_FreeTextAnnotSetTextColor( TRN_Annot ft, const TRN_ColorPt* color, int col_components );
TRN_API TRN_FreeTextAnnotGetTextColor( TRN_Annot ft, TRN_ColorPt* out_color, int* col_components );
TRN_API TRN_FreeTextAnnotSetLineColor( TRN_Annot ft, const TRN_ColorPt* color, int col_components );
TRN_API TRN_FreeTextAnnotGetLineColor( TRN_Annot ft, TRN_ColorPt* out_color, int* col_components );
TRN_API TRN_FreeTextAnnotSetFontSize( TRN_Annot ft, double font_size );
TRN_API TRN_FreeTextAnnotGetFontSize( TRN_Annot ft, double* result );

TRN_API TRN_HighlightAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_HighlightAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_HighlightAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);


TRN_API TRN_InkAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_InkAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_InkAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_InkAnnotGetPathCount(TRN_Annot ink, int* result);
TRN_API TRN_InkAnnotGetPointCount(TRN_Annot ink, int pathindex, int* result);
TRN_API TRN_InkAnnotGetPoint(TRN_Annot ink, unsigned int pathindex, unsigned int pointindex, TRN_Point* result);
TRN_API TRN_InkAnnotSetPoint(TRN_Annot ink, unsigned int pathindex, unsigned int pointindex, const TRN_Point* point);



 enum TRN_LinkAnnotHighlightingMode 
 {
	 e_Link_none,     
	 e_Link_invert,   
	 e_Link_outline,  
	 e_Link_push      
 };

TRN_API TRN_LinkAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_LinkAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_LinkAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
//TRN_API TRN_LinkAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Action action, TRN_Annot* result);
TRN_API TRN_LinkAnnotRemoveAction(TRN_Annot link);
TRN_API TRN_LinkAnnotGetAction(TRN_Annot link, TRN_Action* result);
TRN_API TRN_LinkAnnotSetAction(TRN_Annot link, TRN_Action action);
TRN_API TRN_LinkAnnotGetHighlightingMode(TRN_Annot link, enum TRN_LinkAnnotHighlightingMode* result);
TRN_API TRN_LinkAnnotSetHighlightingMode(TRN_Annot link, enum TRN_LinkAnnotHighlightingMode value);
TRN_API TRN_LinkAnnotGetQuadPointCount(TRN_Annot link, int* result);
TRN_API TRN_LinkAnnotGetQuadPoint(TRN_Annot link, int idx, TRN_QuadPoint* result);
TRN_API TRN_LinkAnnotSetQuadPoint(TRN_Annot link, int idx, const TRN_QuadPoint* qp);
TRN_API TRN_GetNormalizedUrl(const TRN_UString url, TRN_UString* result);

enum TRN_MarkupAnnotBorderEffect 
{
	e_Markup_None,
	e_Markup_Cloudy
};

TRN_API TRN_MarkupAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_MarkupAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_MarkupAnnotGetTitle(TRN_Annot markup, TRN_UString* result);
TRN_API TRN_MarkupAnnotSetTitle(TRN_Annot markup, const char* title);
TRN_API TRN_MarkupAnnotSetTitleUString(TRN_Annot markup, TRN_UString title);
TRN_API TRN_MarkupAnnotGetPopup(TRN_Annot markup, TRN_Annot* result);
TRN_API TRN_MarkupAnnotSetPopup(TRN_Annot markup, TRN_Annot ppup);
TRN_API TRN_MarkupAnnotGetOpacity(TRN_Annot markup, double* result);
TRN_API TRN_MarkupAnnotSetOpacity(TRN_Annot markup, double op);
TRN_API TRN_MarkupAnnotGetSubject(TRN_Annot markup, TRN_UString* result);
TRN_API TRN_MarkupAnnotSetSubject(TRN_Annot markup, TRN_UString contents);
TRN_API TRN_MarkupAnnotGetCreationDates(TRN_Annot markup, TRN_Date* result);
TRN_API TRN_MarkupAnnotGetCreationDates(TRN_Annot markup, TRN_Date* date);
TRN_API TRN_MarkupAnnotGetBorderEffect(TRN_Annot markup, enum TRN_MarkupAnnotBorderEffect* result);
TRN_API TRN_MarkupAnnotSetBorderEffect(TRN_Annot markup, enum TRN_MarkupAnnotBorderEffect effect);
TRN_API TRN_MarkupAnnotGetBorderEffectIntensity(TRN_Annot markup, double* result);
TRN_API TRN_MarkupAnnotSetBorderEffectIntensity(TRN_Annot markup, double intensity);
TRN_API TRN_MarkupAnnotGetCreationDates(TRN_Annot markup, TRN_Date* result);
TRN_API TRN_MarkupAnnotSetCreationDates(TRN_Annot markup, const TRN_Date* date);
TRN_API TRN_MarkupAnnotGetInteriorColor(TRN_Annot markup, TRN_ColorPt* result);
TRN_API TRN_MarkupAnnotGetInteriorColorCompNum(TRN_Annot markup, int* result);
TRN_API TRN_MarkupAnnotSetInteriorColorRGB(TRN_Annot markup, const TRN_ColorPt* col);
TRN_API TRN_MarkupAnnotSetInteriorColor(TRN_Annot markup, const TRN_ColorPt* col, int numcomp);
TRN_API TRN_MarkupAnnotGetContentRect(TRN_Annot markup, TRN_Rect* result);
TRN_API TRN_MarkupAnnotSetContentRect(TRN_Annot markup, const TRN_Rect* cr);
TRN_API TRN_MarkupAnnotGetPadding(TRN_Annot markup, TRN_Rect* result);
TRN_API TRN_MarkupAnnotSetPadding(TRN_Annot markup, const TRN_Rect* cr);

TRN_API TRN_MovieAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_MovieAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_MovieAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_MovieAnnotGetTitle(TRN_Annot movie, TRN_UString* result);
TRN_API TRN_MovieAnnotSetTitle(TRN_Annot movie, TRN_UString contents);
TRN_API TRN_MovieAnnotIsToBePlayed(TRN_Annot movie, TRN_Bool* result);
TRN_API TRN_MovieAnnotSetToBePlayed(TRN_Annot movie, TRN_Bool playono);

enum TRN_PolyLineAnnotIntentType
{
	e_PolyLine_PolygonCloud,
	e_PolyLine_PolyLineDimension,
	e_PolyLine_PolygonDimension,
	e_PolyLine_Unknown
};

TRN_API TRN_PolyLineAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_PolyLineAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_PolyLineAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_PolyLineAnnotGetVertexCount(TRN_Annot polyline, int* result);
TRN_API TRN_PolyLineAnnotGetVertex(TRN_Annot polyline, int idx, TRN_Point* result);
TRN_API TRN_PolyLineAnnotSetVertex(TRN_Annot polyline, int idx, const TRN_Point* pt);
TRN_API TRN_PolyLineAnnotGetStartStyle(TRN_Annot polyline, enum TRN_LineAnnotEndingStyle* result);
TRN_API TRN_PolyLineAnnotSetStartStyle(TRN_Annot polyline, enum TRN_LineAnnotEndingStyle style);
TRN_API TRN_PolyLineAnnotGetEndStyle(TRN_Annot polyline, enum TRN_LineAnnotEndingStyle* result);
TRN_API TRN_PolyLineAnnotSetEndStyle(TRN_Annot polyline, enum TRN_LineAnnotEndingStyle style);
TRN_API TRN_PolyLineAnnotGetIntentName(TRN_Annot polyline, enum TRN_PolyLineAnnotIntentType* result);
TRN_API TRN_PolyLineAnnotSetIntentName(TRN_Annot polyline, enum TRN_PolyLineAnnotIntentType style);


TRN_API TRN_PolygonAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_PolygonAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_PolygonAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);



TRN_API TRN_PopupAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_PopupAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_PopupAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_PopupAnnotGetParent(TRN_Annot popup, TRN_Annot* result);
TRN_API TRN_PopupAnnotSetParent(TRN_Annot popup, TRN_Annot parent);
TRN_API TRN_PopupAnnotIsOpen(TRN_Annot popup, TRN_Bool* result);
TRN_API TRN_PopupAnnotSetOpen(TRN_Annot popup, TRN_Bool closeono);


enum TRN_RedactionAnnotQuadForm
{
	e_Redaction_LeftJustified,
	e_Redaction_Centered,
	e_Redaction_RightJustified,
	e_Redaction_None
};

TRN_API TRN_RedactionAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_RedactionAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_RedactionAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_RedactionAnnotGetQuadPointCount(TRN_Annot redaction, int* result);
TRN_API TRN_RedactionAnnotGetQuadPoint(TRN_Annot redaction, int idx, TRN_QuadPoint* result); 
TRN_API TRN_RedactionAnnotSetQuadPoint(TRN_Annot redaction, int idx, const TRN_QuadPoint* qp);


TRN_API TRN_RedactionAnnotSetAppFormXO(TRN_Annot redaction, TRN_Obj formxo);
TRN_API TRN_RedactionAnnotGetOverlayText(TRN_Annot redaction, TRN_UString* result);
TRN_API TRN_RedactionAnnotSetOverlayText(TRN_Annot redaction, TRN_UString contents);
TRN_API TRN_RedactionAnnotGetUseRepeat(TRN_Annot redaction, TRN_Bool* result);
TRN_API TRN_RedactionAnnotSetUseRepeat(TRN_Annot redaction, TRN_Bool closeono);
TRN_API TRN_RedactionAnnotGetOverlayTextAppearance(TRN_Annot redaction, TRN_UString* result);
TRN_API TRN_RedactionAnnotSetOverlayTextAppearance(TRN_Annot redaction, TRN_UString contents);
TRN_API TRN_RedactionAnnotGetQuadForm(TRN_Annot redaction, enum TRN_RedactionAnnotQuadForm* result);
TRN_API TRN_RedactionAnnotSetQuadForm(TRN_Annot redaction, enum TRN_RedactionAnnotQuadForm style);
TRN_API TRN_RedactionAnnotGetAppFormXO(TRN_Annot redaction, TRN_Obj* result);
TRN_API TRN_RedactionAnnotSetAppFormXO(TRN_Annot redaction, TRN_Obj formxo);

enum TRN_RubberStampAnnotIcon
{
	e_RubberStamp_Approved, 
	e_RubberStamp_Experimental, 
	e_RubberStamp_NotApproved, 
	e_RubberStamp_AsIs, 
	e_RubberStamp_Expired , 
	e_RubberStamp_NotForPublicRelease, 
	e_RubberStamp_Confidential, 
	e_RubberStamp_Final, 
	e_RubberStamp_Sold, 
	e_RubberStamp_Departmental, 
	e_RubberStamp_ForComment, 
	e_RubberStamp_TopSecret, 						 
	e_RubberStamp_ForPublicRelease,
	e_RubberStamp_Draft,
	e_RubberStamp_Unknown
};

TRN_API TRN_RubberStampAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_RubberStampAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
//TRN_API TRN_RubberStampAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, enum TRN_RubberStamp stp, TRN_Annot* result);
TRN_API TRN_RubberStampAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_RubberStampAnnotGetIcon(TRN_Annot stamp, enum TRN_RubberStampAnnotIcon* result);
TRN_API TRN_RubberStampAnnotSetIcon(TRN_Annot stamp, enum TRN_RubberStampAnnotIcon style); 
TRN_API TRN_RubberStampAnnotSetIconDefault(TRN_Annot stamp);
TRN_API TRN_RubberStampAnnotGetIconName(TRN_Annot stamp, const char** result);
TRN_API TRN_RubberStampAnnotSetIconName(TRN_Annot stamp, const char* style);

enum TRN_ScreenAnnotScaleType
{
	e_Screen_Anamorphic,
	e_Screen_Proportional
};

enum TRN_ScreenAnnotScaleCondition
{
	e_Screen_Always,
	e_Screen_WhenBigger,
	e_Screen_WhenSmaller,
	e_Screen_Never
};


enum TRN_ScreenAnnotIconCaptionRelation
{
	e_Screen_NoIcon,
	e_Screen_NoCaption,
	e_Screen_CBelowI,
	e_Screen_CAboveI,
	e_Screen_CRightILeft,
	e_Screen_CLeftIRight,
	e_Screen_COverlayI
};


TRN_API TRN_ScreenAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_ScreenAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_ScreenAnnotGetTitle(TRN_Annot s, TRN_UString* result);
TRN_API TRN_ScreenAnnotSetTitle(TRN_Annot s, TRN_UString contents);
TRN_API TRN_ScreenAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_ScreenAnnotGetAction(TRN_Annot s, TRN_Action* result);
TRN_API TRN_ScreenAnnotSetAction(TRN_Annot s, TRN_Action action);
TRN_API TRN_ScreenAnnotGetTriggerAction(TRN_Annot s,TRN_Obj* result);
TRN_API TRN_ScreenAnnotSetTriggerAction(TRN_Annot s, TRN_Obj action);
TRN_API TRN_ScreenAnnotGetBorderColor(TRN_Annot s, TRN_ColorPt* result);
TRN_API TRN_ScreenAnnotSetBorderColor(TRN_Annot s, const TRN_ColorPt* col, int numcomp);					
TRN_API TRN_ScreenAnnotGetBorderColorCompNum(TRN_Annot s, int* result);
TRN_API TRN_ScreenAnnotGetBackgroundColorCompNum(TRN_Annot s, int* result);
TRN_API TRN_ScreenAnnotGetBackgroundColor(TRN_Annot s, TRN_ColorPt* result); 
TRN_API TRN_ScreenAnnotSetBackgroundColor(TRN_Annot s, const TRN_ColorPt* col, int numcomp);
TRN_API TRN_ScreenAnnotGetStaticCaptionText(TRN_Annot s, TRN_UString* result);
TRN_API TRN_ScreenAnnotSetStaticCaptionText(TRN_Annot s, TRN_UString contents);
TRN_API TRN_ScreenAnnotGetRolloverCaptionText(TRN_Annot s, TRN_UString* result);
TRN_API TRN_ScreenAnnotSetRolloverCaptionText(TRN_Annot s, TRN_UString contents);
TRN_API TRN_ScreenAnnotGetMouseDownCaptionText(TRN_Annot s, TRN_UString* result);
TRN_API TRN_ScreenAnnotSetMouseDownCaptionText(TRN_Annot s, TRN_UString contents);
TRN_API TRN_ScreenAnnotGetStaticIcon(TRN_Annot s,TRN_Obj* result);
TRN_API TRN_ScreenAnnotSetStaticIcon(TRN_Annot s, TRN_Obj icon);
TRN_API TRN_ScreenAnnotGetRolloverIcon(TRN_Annot s,TRN_Obj* result);
TRN_API TRN_ScreenAnnotSetRolloverIcon(TRN_Annot s, TRN_Obj icon);
TRN_API TRN_ScreenAnnotGetMouseDownIcon(TRN_Annot s,TRN_Obj* result);
TRN_API TRN_ScreenAnnotSetMouseDownIcon(TRN_Annot s, TRN_Obj icon);
TRN_API TRN_ScreenAnnotGetScaleType(TRN_Annot s, enum TRN_ScreenAnnotScaleType* result);
TRN_API TRN_ScreenAnnotSetScaleType(TRN_Annot s, enum TRN_ScreenAnnotScaleType style);
TRN_API TRN_ScreenAnnotGetIconCaptionRelation(TRN_Annot s, enum TRN_ScreenAnnotIconCaptionRelation* result);
TRN_API TRN_ScreenAnnotSetIconCaptionRelation(TRN_Annot s, enum TRN_ScreenAnnotIconCaptionRelation style);
TRN_API TRN_ScreenAnnotGetScaleCondition(TRN_Annot s, enum TRN_ScreenAnnotScaleCondition* result);
TRN_API TRN_ScreenAnnotSetScaleCondition(TRN_Annot s, enum TRN_ScreenAnnotScaleCondition style);
TRN_API TRN_ScreenAnnotGetRotation(TRN_Annot s, int* result);
TRN_API TRN_ScreenAnnotSetRotation(TRN_Annot s, int rot);
TRN_API TRN_ScreenAnnotGetFitFull(const TRN_Annot s, TRN_Bool* result);
TRN_API TRN_ScreenAnnotSetFitFull(TRN_Annot s, TRN_Bool ff);
TRN_API TRN_ScreenAnnotGetHIconLeftOver(TRN_Annot s, double* result);
TRN_API TRN_ScreenAnnotSetHIconLeftOver(TRN_Annot s, double hl);
TRN_API TRN_ScreenAnnotGetVIconLeftOver(TRN_Annot s, double* result);
TRN_API TRN_ScreenAnnotSetVIconLeftOver(TRN_Annot s, double vl);



enum TRN_SoundAnnotIcon
{
	e_Sound_Speaker,
	e_Sound_Mic,
	e_Sound_Unknown
};

TRN_API TRN_SoundAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_SoundAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_SoundAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_SoundAnnotCreateAtPoint(TRN_SDFDoc doc, const TRN_Point* pos, TRN_Annot* result);
TRN_API TRN_SoundAnnotGetSoundStream(TRN_Annot sound,TRN_Obj* result);
TRN_API TRN_SoundAnnotSetSoundStream(TRN_Annot sound, TRN_Obj icon);
TRN_API TRN_SoundAnnotGetIcon(TRN_Annot sound, enum TRN_SoundAnnotIcon* result);
TRN_API TRN_SoundAnnotSetIcon(TRN_Annot sound, enum TRN_SoundAnnotIcon style);
TRN_API TRN_SoundAnnotGetIconName(TRN_Annot sound, const char** result);
TRN_API TRN_SoundAnnotSetIconName(TRN_Annot sound, const char* style);

TRN_API TRN_SquareAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_SquareAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_SquareAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_SquareAnnotGetInteriorColor(TRN_Annot square, TRN_ColorPt* result);
TRN_API TRN_SquareAnnotGetInteriorColorCompNum(TRN_Annot square, int* result);
TRN_API TRN_SquareAnnotSetInteriorColorDefault(TRN_Annot square, const TRN_ColorPt* col);
TRN_API TRN_SquareAnnotSetInteriorColor(TRN_Annot square, const TRN_ColorPt* col, int numcomp);
TRN_API TRN_SquareAnnotGetContentRect(TRN_Annot square, TRN_Rect* result);
TRN_API TRN_SquareAnnotSetContentRect(TRN_Annot square, const TRN_Rect* cr);
TRN_API TRN_SquareAnnotGetPadding(TRN_Annot square, TRN_Rect* result);
TRN_API TRN_SquareAnnotSetPadding(TRN_Annot square, const TRN_Rect* cr);

TRN_API TRN_SquigglyAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_SquigglyAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_SquigglyAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);

TRN_API TRN_StrikeOutAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_StrikeOutAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_StrikeOutAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);


enum TRN_TextAnnotIcon //Corresponding to the "name" attribute.
{
	e_Text_Comment,
	e_Text_Key,							
	e_Text_Help,
	e_Text_NewParagraph,
	e_Text_Paragraph,
	e_Text_Insert,
	e_Text_Note,
	e_Text_Unknown //This corrsponds to user defined names.							 
};


TRN_API TRN_TextAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_TextAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_TextAnnotCreateAtPoint(TRN_SDFDoc doc, const TRN_Point* pos, TRN_Annot* result);
TRN_API TRN_TextAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);
TRN_API TRN_TextAnnotIsOpen(TRN_Annot text, TRN_Bool* result);
TRN_API TRN_TextAnnotSetOpen(TRN_Annot text, TRN_Bool closeono);
TRN_API TRN_TextAnnotGetIcon(TRN_Annot text, enum TRN_TextAnnotIcon* result);
TRN_API TRN_TextAnnotSetIcon(TRN_Annot text, enum TRN_TextAnnotIcon icon);
TRN_API TRN_TextAnnotSetIconDefault(TRN_Annot text);
TRN_API TRN_TextAnnotGetIconName(TRN_Annot text, const char** result);
TRN_API TRN_TextAnnotSetIconName(TRN_Annot text, const char* style);
TRN_API TRN_TextAnnotGetState(TRN_Annot text, TRN_UString* result);
TRN_API TRN_TextAnnotSetState(TRN_Annot text, TRN_UString contents);
TRN_API TRN_TextAnnotGetStateModel(TRN_Annot text, TRN_UString* result);
TRN_API TRN_TextAnnotSetStateModel(TRN_Annot text, TRN_UString contents);

TRN_API TRN_UnderlineAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result); 
TRN_API TRN_UnderlineAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_UnderlineAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);

TRN_API TRN_WatermarkAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_WatermarkAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_WatermarkAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Annot* result);

TRN_API TRN_TextMarkupAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_TextMarkupAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_TextMarkupAnnotGetQuadPointCount(TRN_Annot textmarkup, int* result);
TRN_API TRN_TextMarkupAnnotGetQuadPoint(TRN_Annot textmarkup, int idx, TRN_QuadPoint* result); 
TRN_API TRN_TextMarkupAnnotSetQuadPoint(TRN_Annot textmarkup, int idx, const TRN_QuadPoint* qp);

enum TRN_WidgetAnnotHighlightingMode
{
	e_Widget_none,
	e_Widget_invert,
	e_Widget_outline,
	e_Widget_push,
	e_Widget_toggle
};

enum TRN_WidgetAnnotScaleType
{
	e_Widget_Anamorphic,
	e_Widget_Proportional
};

enum TRN_WidgetAnnotIconCaptionRelation
{
	e_Widget_NoIcon,
	e_Widget_NoCaption,
	e_Widget_CBelowI,
	e_Widget_CAboveI,
	e_Widget_CRightILeft,
	e_Widget_CLeftIRight,
	e_Widget_COverlayI
};

enum TRN_WidgetAnnotScaleCondition
{
	e_Widget_Always,
	e_Widget_WhenBigger,
	e_Widget_WhenSmaller,
	e_Widget_Never
};
	
TRN_API TRN_WidgetAnnotCreate(TRN_SDFDoc doc, const TRN_Rect* pos, TRN_Field* fd, TRN_Annot* result);
TRN_API TRN_WidgetAnnotCreateFromObj(TRN_Obj d, TRN_Annot* result);
TRN_API TRN_WidgetAnnotCreateFromAnnot(TRN_Annot annot, TRN_Annot* result);
TRN_API TRN_WidgetAnnotGetField(TRN_Annot widget, TRN_Field* result);
TRN_API TRN_WidgetAnnotGetHighlightingMode(TRN_Annot widget, enum TRN_WidgetAnnotHighlightingMode* result);
TRN_API TRN_WidgetAnnotSetHighlightingMode(TRN_Annot widget, enum TRN_WidgetAnnotHighlightingMode value);
TRN_API TRN_WidgetAnnotGetAction(TRN_Annot widget, TRN_Action* result);
TRN_API TRN_WidgetAnnotSetAction(TRN_Annot widget, TRN_Action action);
TRN_API TRN_WidgetAnnotGetTriggerAction(TRN_Annot widget,TRN_Obj* result);
TRN_API TRN_WidgetAnnotSetTriggerAction(TRN_Annot widget, TRN_Obj action);
TRN_API TRN_WidgetAnnotGetBorderColor(TRN_Annot widget, TRN_ColorPt* result);
TRN_API TRN_WidgetAnnotSetBorderColor(TRN_Annot widget, const TRN_ColorPt* col, int numcomp);					
TRN_API TRN_WidgetAnnotGetBorderColorCompNum(TRN_Annot widget, int* result);
TRN_API TRN_WidgetAnnotGetBackgroundColorCompNum(TRN_Annot widget, int* result);
TRN_API TRN_WidgetAnnotGetBackgroundColor(TRN_Annot widget, TRN_ColorPt* result); 
TRN_API TRN_WidgetAnnotSetBackgroundColor(TRN_Annot widget, const TRN_ColorPt* col, int numcomp);
TRN_API TRN_WidgetAnnotGetStaticCaptionText(TRN_Annot widget, TRN_UString* result);
TRN_API TRN_WidgetAnnotSetStaticCaptionText(TRN_Annot widget, TRN_UString contents);
TRN_API TRN_WidgetAnnotGetRolloverCaptionText(TRN_Annot widget, TRN_UString* result);
TRN_API TRN_WidgetAnnotSetRolloverCaptionText(TRN_Annot widget, TRN_UString contents);
TRN_API TRN_WidgetAnnotGetMouseDownCaptionText(TRN_Annot widget, TRN_UString* result);
TRN_API TRN_WidgetAnnotSetMouseDownCaptionText(TRN_Annot widget, TRN_UString contents);
TRN_API TRN_WidgetAnnotGetStaticIcon(TRN_Annot widget,TRN_Obj* result);
TRN_API TRN_WidgetAnnotSetStaticIcon(TRN_Annot widget, TRN_Obj icon);
TRN_API TRN_WidgetAnnotGetRolloverIcon(TRN_Annot widget,TRN_Obj* result);
TRN_API TRN_WidgetAnnotSetRolloverIcon(TRN_Annot widget, TRN_Obj icon);
TRN_API TRN_WidgetAnnotGetMouseDownIcon(TRN_Annot widget,TRN_Obj* result);
TRN_API TRN_WidgetAnnotSetMouseDownIcon(TRN_Annot widget, TRN_Obj icon);
TRN_API TRN_WidgetAnnotGetScaleType(TRN_Annot widget, enum TRN_WidgetAnnotScaleType* result);
TRN_API TRN_WidgetAnnotSetScaleType(TRN_Annot widget, enum TRN_WidgetAnnotScaleType style);
TRN_API TRN_WidgetAnnotGetIconCaptionRelation(TRN_Annot widget, enum TRN_WidgetAnnotIconCaptionRelation* result);
TRN_API TRN_WidgetAnnotSetIconCaptionRelation(TRN_Annot widget, enum TRN_WidgetAnnotIconCaptionRelation style);
TRN_API TRN_WidgetAnnotGetScaleCondition(TRN_Annot widget, enum TRN_WidgetAnnotScaleCondition* result);
TRN_API TRN_WidgetAnnotSetScaleCondition(TRN_Annot widget, enum TRN_WidgetAnnotScaleCondition style);
TRN_API TRN_WidgetAnnotGetRotation(TRN_Annot widget, int* result);
TRN_API TRN_WidgetAnnotSetRotation(TRN_Annot widget, int rot);
TRN_API TRN_WidgetAnnotGetFitFull(const TRN_Annot widget, TRN_Bool* result);
TRN_API TRN_WidgetAnnotSetFitFull(TRN_Annot widget, TRN_Bool ff);
TRN_API TRN_WidgetAnnotGetHIconLeftOver(TRN_Annot widget, double* result);
TRN_API TRN_WidgetAnnotSetHIconLeftOver(TRN_Annot widget, double hl);
TRN_API TRN_WidgetAnnotGetVIconLeftOver(TRN_Annot widget, double* result);
TRN_API TRN_WidgetAnnotSetVIconLeftOver(TRN_Annot widget, double vl);
TRN_API TRN_WidgetAnnotCompare(const TRN_Annot widget, TRN_Bool* result);
TRN_API TRN_WidgetAnnotIsValid(TRN_Annot widget, TRN_Bool ff);

#ifdef __cplusplus
}
#endif


#endif


