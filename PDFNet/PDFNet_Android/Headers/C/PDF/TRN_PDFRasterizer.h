//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPDFRasterizer
#define   H_CPDFRasterizer

#ifdef __cplusplus
extern "C" {
#endif

#include <C/Common/TRN_Types.h>


enum TRN_PDFRasterizerType {
	e_PDFRasterizer_BuiltIn,  
	e_PDFRasterizer_GDIPlus  
}; 

enum TRN_OverprintPreviewMode {
	e_op_off = 0,
	e_op_on,
	e_op_pdfx_on
};

TRN_API  TRN_PDFRasterizerCreate(enum TRN_PDFRasterizerType type, TRN_PDFRasterizer* result);
TRN_API  TRN_PDFRasterizerDestroy(TRN_PDFRasterizer r);

TRN_API TRN_PDFRasterizerRasterizeToMemory(TRN_PDFRasterizer r,TRN_Page page, TRN_UChar* in_out_image_buffer, 
			int width, int height, int stride, 
			int num_comps,
			TRN_Bool demult,
			const TRN_Matrix2D* device_mtx,
			const TRN_Rect* clip,
			const TRN_Rect* scrl_clp_regions,
			volatile TRN_Bool* cancel);
#ifdef _WIN32
TRN_API TRN_PDFRasterizerRasterizeToDevice(TRN_PDFRasterizer r,TRN_Page page, void* hdc, 
	const TRN_Matrix2D* device_mtx, 
	const TRN_Rect* clip,int dpi,
	volatile TRN_Bool* cancel);
#endif
TRN_API TRN_PDFRasterizerSetDrawAnnotations(TRN_PDFRasterizer r,TRN_Bool render_annots);
TRN_API TRN_PDFRasterizerSetHighlightFields(TRN_PDFRasterizer r,TRN_Bool highlight);
TRN_API TRN_PDFRasterizerSetAntiAliasing(TRN_PDFRasterizer r,TRN_Bool enable_aa);
TRN_API TRN_PDFRasterizerSetPathHinting(TRN_PDFRasterizer r,TRN_Bool enable_ph);
TRN_API TRN_PDFRasterizerSetThinLineAdjustment(TRN_PDFRasterizer r, TRN_Bool grid_fit, TRN_Bool stroke_adjust);
TRN_API TRN_PDFRasterizerSetGamma(TRN_PDFRasterizer r, double gamma);
TRN_API TRN_PDFRasterizerSetOCGContext(TRN_PDFRasterizer r, TRN_OCGContext ctx);
TRN_API TRN_PDFRasterizerSetPrintMode(TRN_PDFRasterizer r, TRN_Bool is_printing);
TRN_API TRN_PDFRasterizerSetImageSmoothing(TRN_PDFRasterizer r,TRN_Bool smoothing_enabled);
TRN_API TRN_PDFRasterizerSetOverprint(TRN_PDFRasterizer  r, enum TRN_OverprintPreviewMode op);
TRN_API TRN_PDFRasterizerSetCaching(TRN_PDFRasterizer r,TRN_Bool enabled);
TRN_API TRN_PDFDrawSetOCGContext(TRN_PDFDraw r, TRN_OCGContext ctx);

typedef void (*TRN_RasterizerErrorReportProc) (const char* message, void* data);

TRN_API TRN_PDFRasterizerSetErrorReportProc(TRN_PDFRasterizer r,TRN_RasterizerErrorReportProc error_proc, void* data);
TRN_API TRN_PDFRasterizerSetRasterizerType (TRN_PDFRasterizer r,enum TRN_PDFRasterizerType type);
TRN_API TRN_PDFRasterizerGetRasterizerType (TRN_PDFRasterizer r,enum TRN_PDFRasterizerType* result);

#ifdef __cplusplus
}
#endif


#endif 


