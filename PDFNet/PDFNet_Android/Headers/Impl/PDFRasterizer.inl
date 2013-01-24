
inline PDFRasterizer::PDFRasterizer(PDFRasterizer::Type type)
{
	REX(TRN_PDFRasterizerCreate((enum TRN_PDFRasterizerType)type,&mp_rast));
}

inline PDFRasterizer::~PDFRasterizer()
{
	REX(TRN_PDFRasterizerDestroy(mp_rast));
	mp_rast=0;
}

inline void PDFRasterizer::Destroy()
{
	REX(TRN_PDFRasterizerDestroy(mp_rast));
	mp_rast=0;
}

inline std::vector<unsigned char> PDFRasterizer::Rasterize(Page& page, 
			int width, int height, int stride, 
			int num_comps, bool demult,
			const Common::Matrix2D& device_mtx,
			const Rect* clip,
			const Rect* scrl_clp_regions,
			volatile bool* cancel)
{
	UChar* in_out_image_buffer = 0;
	REX(TRN_PDFRasterizerRasterizeToMemory(mp_rast,page.mp_page,in_out_image_buffer,
		width,height,stride, num_comps, BToTB(demult),
		(const TRN_Matrix2D*)&device_mtx,(const TRN_Rect*)clip,
		(const TRN_Rect*)scrl_clp_regions,BToTBP(cancel)));
	std::vector<unsigned char> result(height * stride);
	memcpy(&result[0], in_out_image_buffer, height * stride);
	return result;
}

#ifndef SWIG
inline void PDFRasterizer::Rasterize(Page& page, UChar* in_out_image_buffer, 
			int width, int height, int stride, 
			int num_comps, bool demult,
			const Common::Matrix2D& device_mtx,
			const Rect* clip,
			const Rect* scrl_clp_regions,
			volatile bool* cancel)
{
	REX(TRN_PDFRasterizerRasterizeToMemory(mp_rast,page.mp_page,in_out_image_buffer,
		width,height,stride, num_comps, BToTB(demult),
		(const TRN_Matrix2D*)&device_mtx,(const TRN_Rect*)clip,
		(const TRN_Rect*)scrl_clp_regions,BToTBP(cancel)));
}
#endif

#if defined(_WIN32) && !defined(__WINRT__)
inline void PDFRasterizer::Rasterize(Page& page, void* hdc, 
	const Common::Matrix2D& device_mtx, 
	const Rect* clip,
	int dpi,
	volatile bool* cancel)
{
	REX(TRN_PDFRasterizerRasterizeToDevice(mp_rast,page.mp_page,hdc,
		(const TRN_Matrix2D*)&device_mtx,(const TRN_Rect*)clip,dpi,BToTBP(cancel)));
}
#endif

inline void PDFRasterizer::SetDrawAnnotations(bool render_annots)
{
	REX(TRN_PDFRasterizerSetDrawAnnotations(mp_rast, BToTB(render_annots)));
}

inline void PDFRasterizer::SetHighlightFields(bool highlight)
{
	REX(TRN_PDFRasterizerSetHighlightFields(mp_rast, BToTB(highlight)));
}

inline void PDFRasterizer::SetGamma(double exp)
{
	REX(TRN_PDFRasterizerSetGamma(mp_rast, exp));
}

inline void PDFRasterizer::SetOCGContext(OCG::Context* ctx)
{
	REX(TRN_PDFRasterizerSetOCGContext(mp_rast, ctx->mp_obj));
}

inline void PDFRasterizer::SetPrintMode(bool is_printing)
{
	REX(TRN_PDFRasterizerSetPrintMode(mp_rast,BToTB(is_printing)));	
}

inline void PDFRasterizer::SetOverprint(OverprintPreviewMode op)
{
	REX(TRN_PDFRasterizerSetOverprint(mp_rast, (enum TRN_OverprintPreviewMode)op));	
}

inline void PDFRasterizer::SetAntiAliasing(bool enable_aa)
{
	REX(TRN_PDFRasterizerSetAntiAliasing(mp_rast, BToTB(enable_aa)));
}

inline void PDFRasterizer::SetPathHinting(bool enable_ph)
{
	REX(TRN_PDFRasterizerSetPathHinting(mp_rast, BToTB(enable_ph)));
}

inline void PDFRasterizer::SetThinLineAdjustment(bool pixel_grid_fit, bool stroke_adjust)
{
	REX(TRN_PDFRasterizerSetThinLineAdjustment(mp_rast,BToTB(pixel_grid_fit), BToTB(stroke_adjust)));
}

inline void PDFRasterizer::SetImageSmoothing(bool smoothing_enabled)
{
	REX(TRN_PDFRasterizerSetImageSmoothing(mp_rast, BToTB(smoothing_enabled)));
}

inline void PDFRasterizer::SetCaching(bool enabled)
{
	REX(TRN_PDFRasterizerSetCaching(mp_rast, BToTB(enabled)));
}

#ifdef SWIG
inline void PDFRasterizer::SetErrorReportProc(Callback* instance)
{
	REX(TRN_PDFRasterizerSetErrorReportProc(mp_rast, (TRN_RasterizerErrorReportProc)&(Callback::StaticErrorReportProc),instance));
}
#else
inline void PDFRasterizer::SetErrorReportProc(PDFRasterizer::ErrorReportProc error_proc, void* data)
{
	REX(TRN_PDFRasterizerSetErrorReportProc(mp_rast, (TRN_RasterizerErrorReportProc)error_proc,data));
}
#endif

inline void PDFRasterizer::SetRasterizerType (PDFRasterizer::Type type)
{
	REX(TRN_PDFRasterizerSetRasterizerType(mp_rast,(enum TRN_PDFRasterizerType)type));
}

inline PDFRasterizer::Type PDFRasterizer::GetRasterizerType ()
{
	enum TRN_PDFRasterizerType result;
	REX(TRN_PDFRasterizerGetRasterizerType(mp_rast,&result));
	return (PDFRasterizer::Type)result;
}

