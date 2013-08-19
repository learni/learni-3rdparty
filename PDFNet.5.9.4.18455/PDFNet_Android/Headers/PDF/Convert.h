//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPPCONVERTER
#define   H_CPPCONVERTER

#include <PDF/PDFDoc.h>
#include <C/PDF/TRN_Convert.h>
#include <SDF/Obj.h>
#include <SDF/ObjSet.h>

namespace pdftron{ 
	namespace PDF {

/** 
 * Converter is a utility class used to convert documents and files to PDF.
 * Conversion of XPS, EMF and image files to PDF documents is performed internally.
 * Other document formats are converted via native application and printing.
 *
 * @code
 * using namespace pdftron;
 * using namespace PDF;
 * PDFDoc pdfdoc;
 *
 * Convert::FromXps(pdfdoc, input_path + "simple-xps.xps" );
 * Convert::FromEmf(pdfdoc, input_path + "simple-emf.emf" );
 * Convert::ToPdf(pdfdoc, input_path + test docx file.docx );
 *
 * // Save the PDF document
 * UString outputFile = output_path + "ConverterTest.pdf";
 * pdfdoc.Save(outputFile, SDF::SDFDoc::e_remove_unused, NULL);
 * @endcode
 *
 * The PDFTron PDFNet printer needs to be installed to convert document formats.
 * On Windows installation of printer drivers requires administrator UAC, manifests
 * have been added to the Convert samples (C++, C# and Visual Basic).
 *
 * To install the printer the process must be running as administrator.  Execute:
 *
 * @code
 * Convert::Printer::Install();
 * @endcode
 *
 * Installation can take a few seconds, so it is recommended that you install the printer 
 * once as part of your deployment process.  Duplicated installations will be quick since
 * the presence of the printer is checked before installation is attempted.  The printer
 * is a virtual XPS printer supported on Vista and Windows 7, and on Windows XP with the 
 * XPS Essentials Pack.
 *
 * There is no need to uninstall the printer after conversions, it can be left installed 
 * for later access. To uninstall the printer the process must be running as administrator.
 * Execute:
 *
 * @code
 * Convert::Printer::Uninstall();
 * @endcode
 */
class Convert
{
public:
	/**
	 * Convert the specified XPS document to PDF and append converted pages
	 * to the specified PDF document.
	 *
	 * @param in_pdfdoc the PDFDoc to append to
	 *
	 * @param in_filename the path to the XPS document to convert
	 *
	 */
	static void FromXps(PDFDoc & in_pdfdoc, const UString & in_filename);

	/**
	 * Convert the specified XPS document contained in memory to PDF 
	 * and append converted pages to the specified PDF document.
	 *
	 * @param in_pdfdoc the PDFDoc to append to
	 *
	 * @param buf the buffer containing the xps document
	 *
	 * @param buf_sz the size of the buffer
	 *
	 */
	static void FromXps(PDFDoc & in_pdfdoc, const char* buf, size_t buf_sz);

	/**
	 * Convert the specified EMF to PDF and append converted pages to
	 * to the specified PDF document.  EMF will be fitted to the page.
	 *
	 * @param in_pdfdoc the PDFDoc to append to
	 *
	 * @param in_filename the path to the EMF document to convert
	 *
	 * @note This method is available only on Windows platforms.
	 */
	static void FromEmf(PDFDoc & in_pdfdoc, const UString & in_filename);

	/**
	 * Convert the PDFDoc to EMF and save to the specified path
	 *
	 * @param in_pdfdoc the PDFDoc to convert to EMF
	 *
	 * @param in_filename the path to the EMF files to create, one file per page
	 *
	 * @note This method is available only on Windows platforms.
	 */
	static void ToEmf(PDFDoc & in_pdfdoc, const UString & in_filename);

	/**
	 * Convert the Page to EMF and save to the specified path
	 *
	 * @param in_page the Page to convert to EMF
	 *
	 * @param in_filename the path to the EMF file to create
	 *
	 * @note This method is available only on Windows platforms.
	 */
	static void ToEmf(Page & in_page, const UString & in_filename);

	/**
	 * A class containing options for ToSvg functions
	 */
	class SVGOutputOptions
	{
	public:
		/**
		 * Creates an SVGOutputOptions object with default settings
		 */
		SVGOutputOptions();

		/**
		 * Sets whether to embed all images
		 * @param embed_images if true, images will be embeded
		 */
		void SetEmbedImages(bool embed_images);

		/**
		 * Sets whether to disable conversion of font data to SVG
		 * @param no_fonts if true, font data conversion is disabled
		 */
		void SetNoFonts(bool no_fonts);

		/**
		 * Sets whether to disable mapping of text to public Unicode region. Instead text will be converted using a custom encoding
		 * @param no_unicode if true, mapping of text to public Unicode region is disabled
		 */
		void SetNoUnicode(bool no_unicode);

		/**
		 * Some viewers do not support the default text positioning correctly. This option works around this issue to place text correctly, but produces verbose output. This option will override SetRemoveCharPlacement
		 * @param individual_char_placement if true, text will be positioned correctly
		 */
		void SetIndividualCharPlacement(bool individual_char_placement);

		/**
		 * Sets whether to disable the output of character positions.  This will produce slightly smaller output files than the default setting, but many viewers do not support the output correctly
		 * @param remove_char_placement if true, the output of character positions is disabled
		 */
		void SetRemoveCharPlacement(bool remove_char_placement);
	protected:
		TRN_Obj m_obj;
		friend class Convert;
		SDF::ObjSet m_objset;
	};

	/**
	 * Convert the PDFDoc to SVG and save to the specified path
	 *
	 * @param in_pdfdoc the PDFDoc to convert to SVG
	 *
	 * @param in_filename the path to the SVG files to create, one file per page
	 *
	 * @param in_options the conversion options
	 */
	static void ToSvg(PDFDoc & in_pdfdoc, const UString & in_filename, const SVGOutputOptions& in_options = SVGOutputOptions());

	/**
	 * Convert the Page to SVG and save to the specified path
	 *
	 * @param in_page the Page to convert to SVG
	 *
	 * @param in_filename the path to the SVG file to create
	 *
	 * @param in_options the conversion options
	 */
	static void ToSvg(Page & in_page, const UString & in_filename, const SVGOutputOptions& in_options = SVGOutputOptions());

	/**
	 * A class containing options common to ToXps and ToXod functions
	 */
	class XPSOutputCommonOptions
	{
	public:
		/**
		 * Creates an XPSConvertOptions object with default settings
		 */
		XPSOutputCommonOptions();
		/**
		 * Sets whether ToXps should be run in print mode
		 * print mode is disabled by default
		 * @param print_mode if true print mode is enabled
		 */
		void SetPrintMode(bool print_mode);

		/**
		 * The output resolution, from 1 to 1000, in Dots Per Inch (DPI) at which to render elements which cannot be directly converted. 
		 * the default value is 150 Dots Per Inch
		 * @param dpi the resolution in Dots Per Inch
		 */
		void SetDPI(UInt32 dpi);

		/**
		 * Sets whether rendering of pages should be permitted when necessary to guarantee output
		 * the default setting is to allow rendering in this case
		 * @param render if false rendering is not permitted under any circumstance
		 */
		void SetRenderPages(bool render);

		/**
		 * Sets whether thin lines should be thickened
		 * the default setting is to not thicken lines
		 * @param thicken if true then thin lines will be thickened
		 */
		void SetThickenLines(bool thicken);

		/**
		 * Sets whether links should be generated from urls
		 * found in the document. By default these links are generated.
		 * @param generate if true links will be generated from urls
		 */
		void GenerateURLLinks(bool generate);

		enum OverprintPreviewMode
		{
			e_op_off = 0,
			e_op_on,
			e_op_pdfx_on
		};

		/** 
		 * Enable or disable support for overprint and overprint simulation. 
		 * Overprint is a device dependent feature and the results will vary depending on 
		 * the output color space and supported colorants (i.e. CMYK, CMYK+spot, RGB, etc). 
		 * 
		 * @default By default overprint is only enabled for PDF/X files.
		 * 
		 * @param op e_op_on: always enabled; e_op_off: always disabled; e_op_pdfx_on: enabled for PDF/X files only.
		 */
		void SetOverprint(OverprintPreviewMode mode);
	protected:
		TRN_Obj m_obj;
		friend class Convert;
		SDF::ObjSet m_objset;
	};

	/**
	 * A class containing options for ToXps functions
	 */
	class XPSOutputOptions : public XPSOutputCommonOptions
	{
	public:
		/**
		 * Sets whether the output format should be open xps
		 * microsoft xps output is the default
		 * @param openxps if open xps output should be used
		 */
		void SetOpenXps(bool openxps);
	};

	/**
	 * A class containing options for ToXod functions
	 */
	class XODOutputOptions : public XPSOutputCommonOptions
	{
	public:

		enum FlattenFlag {
			/**
			 * Disable flattening and convert all content as is.
			 */
			e_off,
			/** 
			 * Feature reduce PDF to a simple two layer representation consisting 
			 * of a single background RGB image and a simple top text layer.
			 */
			e_simple,
			/** 
			 * Feature reduce PDF while trying to preserve some 
			 * complex PDF features (such as vector figures, transparency, shadings, 
			 * blend modes, Type3 fonts etc.) for pages that are already fast to render. 
			 * This option can also result in smaller & faster files compared to e_simple,
			 * but the pages may have more complex structure.
			 */
			e_fast
		};

		enum AnnotationOutputFlag {
			e_internal_xfdf,				// include the annotation file in the XOD output. This is the default option
			e_external_xfdf,				// output the annotation file externally to the same output path with extension .xfdf. 
											// This is not available when using streaming conversion
			e_flatten                      // flatten all annotations that are not link annotations
		};

		/**
		 * Sets whether per page thumbnails should be included in the file
		 * the default setting is to output thumbnails
		 * @param include_thumbs if true thumbnails will be included
		 */
		void SetOutputThumbnails(bool include_thumbs);

		/**
		 * The width and height of a square in which all thumbnails will 
		 * be contained.
		 * @param size the maximum dimension (width or height) that 
		 * thumbnails will have.
		 */
		void SetThumbnailSize(UInt32 size);

		/**
		 * If rendering is permitted, sets the maximum number of page elements before that page will be rendered.
		 * the default value is 10000 elements
		 * @param element_limit the maximum number of elements before a given page will be rendered
		 */
		void SetElementLimit(UInt32 element_limit);

		/**
		 * If rendering is permitted, sets whether pages containing opacity masks should be rendered.
		 * This option is used as a workaround to a bug in Silverlight where opacity masks are transformed incorrectly.
		 * the default setting is not to render pages with opacity masks 
		 * @param opacity_render if true pages with opacity masks will be rendered
		 */
		void SetOpacityMaskWorkaround(bool opacity_render);

		/**
		 * Specifies the maximum image size in pixels.
		 * @param max_pixels the maximum number of pixels an image can have.
		 */
		void SetMaximumImagePixels(UInt32 max_pixels);

		/**
		 * Flatten images and paths into a single background image overlaid with 
		 * vector text. This option can be used to improve speed on devices with 
		 * little processing power such as iPads.
		 * @param flatten select which flattening mode to use.
		 */
		void SetFlattenContent(enum FlattenFlag flatten);

		/**
		 * Where possible output JPG files rather than PNG. This will apply to both 
		 * thumbnails and document images.
		 * @param prefer_jpg if true JPG images will be used whenever possible.
		 */
		void SetPreferJPG(bool prefer_jpg);

		/**
		 * Outputs rotated text as paths. This option is used as a workaround to a bug in Silverlight 
		 * where pages with rotated text could cause the plugin to crash.
		 * @param workaround if true rotated text will be changed to paths
		 */
		void SetSilverlightTextWorkaround(bool workaround);
		
		/**
		 * Choose how to output annotations.
		 * @param annot_output the flag to specify the output option
		 */
		void SetAnnotationOutput(enum AnnotationOutputFlag annot_output);
		
		/**
		 * Output XOD as a collection of loose files rather than a zip archive. 
		 * This option should be used when using the external part retriever in Webviewer.
		 * @param generate if true XOD is output as a collection of loose files
		 */
		void SetExternalParts(bool generate);

		/**
		 * Encrypt XOD parts with AES 128 encryption using the supplied password.
		 * This option is not available when using SetExternalParts(true)
		 * @param pass the encryption password
		 */
		void SetEncryptPassword(const char* pass);
	};

	/**
	 * A class containing options common to ToHtml and ToEpub functions
	 */
	class HTMLOutputOptions
	{
	public:
		/**
		 * Creates an HTMLOutputCommonOptions object with default settings
		 */
		HTMLOutputOptions();

		/**
		 * Use JPG files rather than PNG. This will apply to all generated images.
		 * @param prefer_jpg if true JPG images will be used whenever possible.
		 */
		void SetPreferJPG(bool prefer_jpg);

		/**
		 * The output resolution, from 1 to 1000, in Dots Per Inch (DPI) at which to render elements which cannot be directly converted. 
		 * the default value is 150 Dots Per Inch
		 * @param dpi the resolution in Dots Per Inch
		 */
		void SetDPI(UInt32 dpi);

		/**
		 * Specifies the maximum image size in pixels
		 * @param max_pixels the maximum number of pixels an image can have
		 */
		void SetMaximumImagePixels(UInt32 max_pixels);

		/**
		 * Switch between fixed (pre-paginated) and reflowable HTML generation
		 * @param reflow if true, generated HTML will be reflowable, otherwise, fixed positioning will be used
		 */
		void SetReflow(bool reflow);

		/**
		 * Set an overall scaling of the generated HTML pages.
		 * @param scale A number greater than 0 which is used as a scale factor. For example, calling SetScale(0.5) will reduce the HTML body of the page to half its original size, whereas SetScale(2) will double the HTML body dimensions of the page and will rescale all page content appropriately.
		 */
		void SetScale(double scale);
	protected:
		TRN_Obj m_obj;
		friend class Convert;
		SDF::ObjSet m_objset;
	};

	/**
	 * A class containing options common to ToEpub functions
	 */
	class EPUBOutputOptions
	{
	public:
		/**
		 * Creates an EPUBOutputOptions object with default settings
		 */
		EPUBOutputOptions();

		/**
		 * Create the EPUB in expanded format.
		 * @param expanded if false a single EPUB file will be generated, otherwise, the generated EPUB will be in unzipped (expanded) format
		 */
		void SetExpanded(bool expanded);

		/**
		 * Set whether the first content page in the EPUB uses the cover image or not. If this
		 * is set to true, then the first content page will simply wrap the cover image in HTML.
		 * Otherwise, the page will be converted the same as all other pages in the EPUB.
		 * @param reuse if true the first page will simply be EPUB cover image, otherwise, the first page will be converted the same as the other pages
		 */
		void SetReuseCover(bool reuse);
	protected:
		TRN_Obj m_obj;
		friend class Convert;
		SDF::ObjSet m_objset;
	};

	/**
	 * Convert the PDFDoc to XPS and save to the specified path
	 *
	 * @param in_pdfdoc the PDFDoc to convert to XPS
	 *
	 * @param in_filename the path to the document to create
	 *
	 * @param options the conversion options
	 *
	 * @see XPSOutputOptions
	 *
	 */
	static void ToXps(PDFDoc & in_pdfdoc, const UString & in_filename, const XPSOutputOptions& options = XPSOutputOptions());

	/**
	 * Convert the input file to XPS format and save to the specified path
	 *
	 * @param in_inputFilename the file to convert to XPS
	 *
	 * @param in_outputFilename the path to the XPS file to create
	 * 
	 * @param options the conversion options
	 *
	 * @see XPSOutputOptions
	 *
	 * @see ToPdf()
	 *
	 * @note: Requires the Convert::Printer class for all file formats
	 * that ToPdf also requires.
	 */
	static void ToXps(const UString & in_inputFilename, const UString & in_outputFilename, const XPSOutputOptions& options = XPSOutputOptions());

	/**
	 * Convert the input file to XOD format and save to the specified path
	 *
	 * @param in_inputFilename the file to convert to XOD
	 *
	 * @param in_outputFilename the path to the XOD file to create
	 *
	 * @param options the conversion options 
	 *
	 * @see XODOutputOptions
	 *
	 * @see ToPdf()
	 *
	 * @note: Requires the Convert::Printer class for all file formats
	 * that ToPdf also requires.
	 */
	static void ToXod(const UString & in_filename, const UString & out_filename, const XODOutputOptions& options = XODOutputOptions());

	/**
	 * Convert the input file to XOD format and save to the specified path
	 *
	 * @param in_pdfdoc the PDFDoc to convert to XOD
	 *
	 * @param in_outputFilename the path to the XOD file to create
	 *
	 * @param options the conversion options 
	 *
	 * @see XODOutputOptions
	 *
	 * @see ToPdf()
	 *
	 */
	static void ToXod(PDFDoc & in_pdfdoc, const UString & out_filename, const XODOutputOptions& options = XODOutputOptions());

	/**
	 * Convert the PDF to HTML and save to the specified path
	 *
	 * @param in_pdfdoc the PDF doc to convert to HTML
	 *
	 * @param out_path the path to where generated content will be stored 
	 *
	 * @param options the conversion options 
	 *
	 * @see HTMLOutputOptions
	 *
	 * @see ToPdf()
	 *
	 */
	static void ToHtml(PDFDoc & in_pdfdoc, const UString & out_path, const HTMLOutputOptions& options = HTMLOutputOptions());

	/**
	 * Convert the PDFDoc to EPUB format and save to the specified path
	 *
	 * @param in_pdfdoc the PDFDoc to convert to EPUB
	 *
	 * @param out_path the path to where generated content will be stored
	 *
	 * @param options the conversion options 
	 *
	 * @see HTMLOutputOptions
	 *
	 * @see EPUBOutputOptions
	 *
	 * @see ToPdf()
	 *
	 */
	static void ToEpub(PDFDoc & in_pdfdoc, const UString & out_path, const HTMLOutputOptions& html_options = HTMLOutputOptions(), const EPUBOutputOptions& epub_options = EPUBOutputOptions());

	/**
	 * Generate a stream that incrementally converts the input file to XOD format.
	 *
	 * @param in_inputFilename the file to convert to XOD
	 *
	 * @param in_outputFilename the path to the XOD file to create
	 *
	 * @param options the conversion options 
	 *
	 * @return A filter from which the file can be read incrementally.
	 *
	 * @see XODOutputOptions
	 *
	 * @see ToPdf()
	 *
	 * @note: Requires the Convert::Printer class for all file formats
	 * that ToPdf also requires.
	 */
	static Filters::Filter ToXod(const UString & in_filename, const XODOutputOptions& options = XODOutputOptions());

	/**
	 * Generate a stream that incrementally converts the input file to XOD format.
	 *
	 * @param in_pdfdoc the PDFDoc to convert to XOD
	 *
	 * @param in_outputFilename the path to the XOD file to create
	 *
	 * @param options the conversion options 
	 *
	 * @return A filter from which the file can be read incrementally.
	 *
	 * @see XODOutputOptions
	 *
	 * @see ToPdf()
	 *
	 */
	static Filters::Filter ToXod(PDFDoc & in_pdfdoc, const XODOutputOptions& options = XODOutputOptions());



	/** 
	 * Convert the file or document to PDF and append to the specified PDF document
	 *
	 * @param in_pdfdoc the PDFDoc to append the converted document to. The
	 * PDFDoc can then be converted to XPS, EMF or SVG using the other functions
	 * in this class.
	 *
	 * @param in_filename the path to the document to be converted to pdf
	 *
	 * @note Internally formats include BMP, EMF, JPEG, PNG, TIF, XPS.
	 *
	 * @note Formats that require external applications for conversion use the
	 * Convert::Printer class and the PDFNet printer to be installed. This is 
	 * only supported on Windows platforms.  Document formats in this category 
	 * include RTF(MS Word or Wordpad), TXT (Notepad or Wordpad), DOC and DOCX 
	 * (MS Word), PPT and PPTX (MS PowerPoint), XLS and XLSX (MS Excel), 
	 * OpenOffice documents, HTML and MHT (Internet Explorer), PUB (MS Publisher),
	 * MSG (MS Outlook).
	 */
	static void ToPdf(PDFDoc & in_pdfdoc, const UString & in_filename);

	/**
	 * Utility function to determine if ToPdf or ToXps will require the PDFNet
	 * printer to convert a specific external file to PDF.
	 *
	 * @param in_filename the path to the document to be checked
	 *
	 * @return true if ToPdf requires the printer to convert the file, false 
	 * otherwise.
	 *
	 * @note Current implementation looks only at the file extension not
	 * file contents. If the file extension is missing, false will be returned
	 */
	static bool RequiresPrinter(const UString & in_filename);

	/** 
	 * Convert::Printer is a utility class to install the a printer for 
	 * print-based conversion of documents for Convert::ToPdf
	 */
	class Printer
	{
	public:
		/**
		 * Install the PDFNet printer. Installation can take a few seconds, 
		 * so it is recommended that you install the printer once as part of 
		 * your deployment process.  Duplicated installations will be quick since
		 * the presence of the printer is checked before installation is attempted.
		 * There is no need to uninstall the printer after conversions, it can be 
		 * left installed for later access.
		 *
		 * @param in_printerName the name of the printer to install and use for conversions.
		 * If in_printerName is not provided then the name "PDFTron PDFNet" is used.
		 *
		 * @note Installing and uninstalling printer drivers requires the process
		 * to be running as administrator.
		 */
		static void Install(const UString & in_printerName = "PDFTron PDFNet");

		/** 
		 * Uninstall all printers using the PDFNet printer driver.  
		 *
		 * @note Installing and uninstalling printer drivers requires the process
		 * to be running as administrator.  Only the "PDFTron PDFNet" printer can 
		 * be uninstalled with this function.
		 */
		static void Uninstall();

		/** 
		 * Get the name of the PDFNet printer installed in this process session.
		 *
		 * @return the Unicode name of the PDFNet printer 
		 *
		 * @note if no printer was installed in this process then the predefined string
		 * "PDFTron PDFNet" will be returned.
		 */
		static const UString GetPrinterName();

		/** 
		 * Set the name of the PDFNet printer installed in this process session.
		 *
		 * @return the Unicode name of the PDFNet printer 
		 *
		 * @note if no printer was installed in this process then the predefined string
		 * "PDFTron PDFNet" will be used.
		 */
		static void SetPrinterName(const UString & in_printerName = "PDFTron PDFNet");

		/**
		 * Determine if the PDFNet printer is installed
		 *
		 * @param in_printerName the name of the printer to install and use for conversions.
		 * If in_printerName is not provided then the name "PDFTron PDFNet" is used.
		 *
		 * @return true if the named printer is installed, false otherwise
		 *
		 * @note may or may not check if the printer with the given name is actually 
		 * a PDFNet printer.
		 */
		static bool IsInstalled(const UString & in_printerName = "PDFTron PDFNet");


	};

private:
	Convert ();
	~Convert ();
};

#include <Impl/Convert.inl>

	}; // namespace PDF
}; // namespace pdftron

#endif // H_CPPCONVERTER
