inline void Convert::FromXps(PDFDoc & in_pdfdoc, const UString & in_filename)
{
	REX(TRN_ConvertFromXps(in_pdfdoc.mp_doc, in_filename.mp_impl));
}

inline void Convert::FromXps(PDFDoc & in_pdfdoc, const char* buf, size_t buf_sz)
{
	REX(TRN_ConvertFromXpsMem(in_pdfdoc.mp_doc, buf, buf_sz));
}

inline void Convert::FromEmf(PDFDoc & in_pdfdoc, const UString & in_filename)
{
	REX(TRN_ConvertFromEmf(in_pdfdoc.mp_doc, in_filename.mp_impl));
}

inline void Convert::ToEmf(PDFDoc & in_pdfdoc, const UString & in_filename)
{
	REX(TRN_ConvertDocToEmf(in_pdfdoc.mp_doc, in_filename.mp_impl));
}

inline void Convert::ToEmf(Page & in_page, const UString & in_filename)
{
	REX(TRN_ConvertPageToEmf(in_page.mp_page, in_filename.mp_impl));
}

inline Convert::SVGOutputOptions::SVGOutputOptions()
{
	m_obj = m_objset.CreateDict().mp_obj;
}

inline void Convert::SVGOutputOptions::SetEmbedImages(bool embed_images)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj, "EMBEDIMAGES", BToTB(embed_images), &result));
}

inline void Convert::SVGOutputOptions::SetNoFonts(bool no_fonts)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj, "NOFONTS", BToTB(no_fonts), &result));
}

inline void Convert::SVGOutputOptions::SetNoUnicode(bool no_unicode)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj, "NOUNICODE", BToTB(no_unicode), &result));
}

inline void Convert::SVGOutputOptions::SetIndividualCharPlacement(bool individual_char_placement)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj, "INDIVIDUALCHARPLACEMENT", BToTB(individual_char_placement), &result));
}

inline void Convert::SVGOutputOptions::SetRemoveCharPlacement(bool remove_char_placement)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj, "REMOVECHARPLACEMENT", BToTB(remove_char_placement), &result));
}

inline void Convert::ToSvg(PDFDoc & in_pdfdoc, const UString & in_filename, const Convert::SVGOutputOptions& in_options)
{
	REX(TRN_ConvertDocToSvgWithOptions(in_pdfdoc.mp_doc, in_filename.mp_impl, in_options.m_obj));
}

inline void Convert::ToSvg(Page & in_page, const UString & in_filename, const Convert::SVGOutputOptions& in_options)
{
	REX(TRN_ConvertPageToSvgWithOptions(in_page.mp_page, in_filename.mp_impl, in_options.m_obj));
}

inline Convert::XPSOutputCommonOptions::XPSOutputCommonOptions()
{
	m_obj=m_objset.CreateDict().mp_obj;
}

inline void Convert::XPSOutputCommonOptions::SetPrintMode(bool print_mode)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"PRINTMODE",BToTB(print_mode),&result));
}

inline void Convert::XPSOutputCommonOptions::SetDPI(UInt32 dpi)
{
	TRN_Obj result;
	REX(TRN_ObjPutNumber(m_obj,"DPI",dpi,&result));
}

inline void Convert::XPSOutputCommonOptions::SetRenderPages(bool render)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"RENDER",BToTB(render),&result));
}

inline void Convert::XPSOutputCommonOptions::SetThickenLines(bool thicken)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"THICKENLINES",BToTB(thicken),&result));
}

inline void Convert::XPSOutputCommonOptions::GenerateURLLinks(bool generate)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"URL_LINKS",BToTB(generate),&result));
}

inline void Convert::XPSOutputOptions::SetOpenXps(bool openxps)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"OPENXPS",BToTB(openxps),&result));
}

inline void Convert::XODOutputOptions::SetOutputThumbnails(bool include_thumbs)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"NOTHUMBS",BToTB(!include_thumbs),&result));
}

inline void Convert::XODOutputOptions::SetThumbnailSize(UInt32 size)
{
	TRN_Obj result;
	REX(TRN_ObjPutNumber(m_obj,"THUMB_SIZE",size,&result));
}

inline void Convert::XODOutputOptions::SetElementLimit(UInt32 element_limit)
{
	TRN_Obj result;
	REX(TRN_ObjPutNumber(m_obj,"ELEMENTLIMIT",element_limit,&result));
}

inline void Convert::XODOutputOptions::SetOpacityMaskWorkaround(bool opacity_render)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"MASKRENDER",opacity_render,&result));
}

inline void Convert::XODOutputOptions::SetMaximumImagePixels(UInt32 max_pixels)
{
	TRN_Obj result;
	REX(TRN_ObjPutNumber(m_obj,"MAX_IMAGE_PIXELS",max_pixels,&result));
}

inline void Convert::XODOutputOptions::SetFlattenContent(bool flatten)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"FLATTEN_CONTENT",flatten,&result));
}

inline void Convert::XODOutputOptions::SetPreferJPG(bool prefer_jpg)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"PREFER_JPEG",prefer_jpg,&result));
}

inline void Convert::XODOutputOptions::SetSilverlightTextWorkaround(bool workaround)
{
	TRN_Obj result;
	REX(TRN_ObjPutBool(m_obj,"REMOVE_ROTATED_TEXT",workaround,&result));
}

inline void Convert::ToXps( PDFDoc & in_pdfdoc, const UString & in_filename, const Convert::XPSOutputOptions& options)
{
	REX(TRN_ConvertToXps(in_pdfdoc.mp_doc,in_filename.mp_impl, options.m_obj));
}

inline void Convert::ToXps( const UString & in_inputFilename, const UString & in_outputFilename, const Convert::XPSOutputOptions& options)
{
	REX(TRN_ConvertFileToXps(in_inputFilename.mp_impl, in_outputFilename.mp_impl, options.m_obj));
}

inline void Convert::ToXod( const UString & in_filename, const UString & out_filename, const Convert::XODOutputOptions& options)
{
	REX(TRN_ConvertFileToXod(in_filename.mp_impl, out_filename.mp_impl, options.m_obj));
}

inline void Convert::ToXod(PDFDoc & in_pdfdoc, const UString & out_filename, const Convert::XODOutputOptions& options)
{
	REX(TRN_ConvertToXod(in_pdfdoc.mp_doc, out_filename.mp_impl, options.m_obj));
}

inline Filters::Filter Convert::ToXod( const UString & in_filename, const Convert::XODOutputOptions& options)
{
	TRN_Filter result;
	REX(TRN_ConvertFileToXodStream(in_filename.mp_impl, options.m_obj, &result));
	return Filters::Filter(result,true);
}

inline Filters::Filter Convert::ToXod(PDFDoc& in_pdfdoc, const Convert::XODOutputOptions& options)
{
	TRN_Filter result;
	REX(TRN_ConvertToXodStream(in_pdfdoc.mp_doc, options.m_obj, &result));
	return Filters::Filter(result,true);
}

inline void Convert::ToPdf(PDFDoc & in_pdfdoc, const UString & in_filename)
{
	REX(TRN_ConvertToPdf(in_pdfdoc.mp_doc, in_filename.mp_impl));
}

inline bool Convert::RequiresPrinter(const UString & in_filename)
{
	RetBool(TRN_ConvertRequiresPrinter(in_filename.mp_impl, &result));
}

inline void Convert::Printer::Install(const UString & in_printerName)
{
	REX(TRN_ConvertPrinterInstall(in_printerName.mp_impl));
}

inline void Convert::Printer::Uninstall()
{
	REX(TRN_ConvertPrinterUninstall());
}

inline const UString Convert::Printer::GetPrinterName()
{
	RetStr(TRN_ConvertPrinterGetPrinterName(&result));
}

inline void Convert::Printer::SetPrinterName(const UString & in_printerName)
{
	REX(TRN_ConvertPrinterSetPrinterName(in_printerName.mp_impl));
}

inline bool Convert::Printer::IsInstalled(const UString & in_printerName)
{
	RetBool(TRN_ConvertPrinterIsInstalled(in_printerName.mp_impl,&result));
}
