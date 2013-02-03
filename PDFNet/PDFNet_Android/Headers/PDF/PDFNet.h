//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPPPDFNet
#define   H_CPPPDFNet

#include <C/PDF/TRN_PDFNet.h>
#include <Common/UString.h>
#include <Common/Iterator.h>

namespace pdftron { 

/** 
 * PDFNet contains global library initialization, registration, configuration,
 * and termination methods. 
 * 
 * @note there is only a single, static instance of PDFNet class. Initialization
 * and termination methods need to be called only once per application session. 
 */
class PDFNet
{
public:

	/** 
	* Initializes PDFNet library. 
	* Initialize() is usually called once, during process initialization.
	*
	* @note it is unsafe to call any other PDFNet API without first initializing 
	* the library
	* 
	* @param license_key Optional license key used to activate the product.
	 * If the license_key is not specified or is null, the product will work in the 
	 * demo mode.
	 *
	 * @exception If the license_key is invalid, the function will throw an exception.
	*/
	static void Initialize(const char* license_key = 0);

#ifndef SWIG
	/** 
	 * Terminates PDFNet library.
	 * Terminate() is usually called once, when the process is terminated. 
	 * 
	 * @note it is unsafe to call any other PDFNet API after you terminate 
	 * the library.
	 */
	 static void Terminate();
#endif

	/**
	 * Sets the location of PDFNet resource file. 
	 * 
	 * @note Starting with v.4.5 PDFNet does not require a separate resource 
	 * file (pdfnet.res) on all desktop/server platforms. As a result, this function 
	 * is not longer required for proper PDFNet initialization. The function is still 
	 * available on embedded systems and for backwards compatibility. The function can 
	 * be also used to specify a default search path for ICC profiles, fonts, and other 
	 * user defined resources.
	 * 
	 * @param path - The default resource directory path.
	 * @return true if path is found, false otherwise.
	 */
	 static bool SetResourcesPath(const UString& path);

	/** 
	* @return the location of PDFNet resources folder. Empty string means 
	* that resources are located in your application folder.
	*/
	 static UString GetResourcesPath();

	 /** 
	  * @return PDFNet version number. 
	  */
	 static double GetVersion();

	 enum CMSType
	 {
		e_lcms,		///< Use LittleCMS (available on all supported platforms).
		e_icm,		///< Use Windows ICM2 (available only on Windows platforms).
		e_no_cms    ///< No ICC color management.
	 };

	/** 
	 * Used to set a specific Color Management System (CMS) for 
	 * use during color conversion operators, image rendering, etc.
	 * 
	 * @param t identifies the type of color management to use.
	 */
	 static void SetColorManagement(CMSType t = e_lcms);

	/** 
	 * Sets the default ICC color profile for DeviceCMYK color space. 
	 * 
	 * @note You can use this method to override default PDFNet settings.
	 * For more information on default color spaces please refer to 
	 * section 'Default Color Spaces' in Chapter 4.5.4 of PDF Reference Manual.
	 * 
	 * @exception the function will throw Exception if the ICC profile 
	 * can't be found or if it fails to open.
	 */
	 static void SetDefaultDeviceCMYKProfile(const UString& icc_filename);

	/** 
	 * Sets the default ICC color profile for DeviceRGB color space. 
	 * 
	 * @note You can use this method to override default PDFNet settings.
	 * For more information on default color spaces please refer to 
	 * section 'Default Color Spaces' in Chapter 4.5.4 of PDF Reference Manual.
	 * 
	 * @exception the function will throw Exception if the ICC profile 
	 * can't be found or if it fails to open.
	 */
	 static void SetDefaultDeviceRGBProfile(const UString& icc_filename);

	/** 
	 * Sets the default policy on using temporary files.
	 * 
	 * @use_disk if parameter is true then new documents are allowed to create
	 * temporary files; otherwise all document contents will be stored in memory.
	 */
	static void SetDefaultDiskCachingEnabled( bool use_disk );

	/**
	 *
	 *  Sets the default parameters for the viewer cache.  Any subsequently created documents
	 *	will use these parameters.
	 *
	 *	@param max_cache_size - The maximum size, in bytes, of the entire document's page cache.
	 *	@param max_zoom_factor - The maximum zoom factor (in percent) supported by the cache.  (value is clamped to a minimum of 100)
	 *
	 *	@default By default, maximum cache size is 512 MB and maximum zoom factor is 1000%
	 */
	static void SetViewerCache(size_t max_cache_size, size_t max_zoom_factor);

	/**
	 * Standard character orderings. PDFNet.AddFontSubst() can be used 
	 * to associate a specific font with a given character ordering 
	 * and to override default font mapping algorithm.
	 */
	enum CharacterOrdering {
		e_Identity = 0,    ///< Generic/Unicode
		e_Japan1   = 1,    ///< Japanese
		e_Japan2   = 2,    ///< Japanese
		e_GB1      = 3,    ///< Chinese; Simplified
		e_CNS1     = 4,    ///< Chinese; Traditional
		e_Korea1   = 5     ///< Korean
	};

	/** 
	 * AddFontSubst functions can be used to create font substitutes
	 * that can override default PDFNet font selection algorithm.
	 * 
	 * These functions are useful in situations where referenced fonts 
	 * are not present in the document and PDFNet font substitution
	 * algorithm is not producing desired results.
	 * 
	 * AddFontSubst(fontname, fontpath) maps the given font name (i.e. 'BaseFont' 
	 * entry from the font dictionary) to a font file.
	 * 
	 * AddFontSubst(ordering, fontpath) maps the given character ordering (see 
	 * Ordering entry in CIDSystemInfo dictionary; Section 5.6.2 in PDF Reference)
	 * to a font file. This method is less specific that the former variant of 
	 * AddFontSubst, and can be used to override a range of missing fonts (or 
	 * any missing font) with a predefined substitute.
	 * 
 	 * The following is an example of using these functions to provide user 
	 * defined font substitutes:
	 *
	 * @code
	 * PDFNet::Initialize();
	 * PDFNet::SetResourcesPath("c:/myapp/resources");
	 * // Specify specific font mappings...
	 * PDFNet::AddFontSubst("MinionPro-Regular", "c:/myfonts/MinionPro-Regular.otf");
	 * PDFNet::AddFontSubst("Times-Roman", "c:/windows/fonts/times.ttf");
	 * PDFNet::AddFontSubst("Times-Italic", "c:/windows/fonts/timesi.ttf");
	 * 	 
	 * // Specify more general font mappings...
	 * PDFNet::AddFontSubst(PDFNet::e_Identity, "c:/myfonts/arialuni.ttf");  // Arial Unicode MS
	 * PDFNet::AddFontSubst(PDFNet::e_Japan1, "c:/myfonts/KozMinProVI-Regular.otf");
	 * PDFNet::AddFontSubst(PDFNet::e_Japan2, "c:/myfonts/KozMinProVI-Regular.otf");
	 * PDFNet::AddFontSubst(PDFNet::e_Korea1, "c:/myfonts/AdobeSongStd-Light.otf");
	 * PDFNet::AddFontSubst(PDFNet::e_CNS1, "c:/myfonts/AdobeMingStd-Light.otf");
	 * PDFNet::AddFontSubst(PDFNet::e_GB1, "c:/myfonts/AdobeMyungjoStd-Medium.otf");
	 * ... 
	 * PDFDoc doc("c:/my.pdf");
	 * ...
	 * @endcode 
	 */
	 static bool AddFontSubst(const char* fontname, const UString& fontpath);
	 static bool AddFontSubst(CharacterOrdering ordering, const UString& fontpath);

	/**
	 * Set the location of temporary folder. 
	 * 
	 * This method is provided for applications that require tight control of 
	 * the location where temporary files are created.
	 */ 
	 static bool SetTempPath(const UString& temp_path);

#ifndef SWIG
	/**
	 * CreateSecurityHandler is a function pointer and a factory method used to create 
	 * new instances of SecurityHandler. The function pointer is used to register a 
	 * new type of SecurityHandler with PDFNet.
	 */
	typedef TRN_PDFNetCreateSecurityHandler CreateSecurityHandler;

	/**
	 * Registers a new type of SecurityHandler. If a handler with the same name was 
	 * already registered, the new SecurityHandler will replace the old handler.
	 * 
	 * @param handler_name - SecurityHandler's document name (as it appears in Encrypt dictionary).
	 * @param factory_method -  a function pointer and a factory method used to create 
	 *    new instances of SecurityHandler.
	 */
	static void RegisterSecurityHandler(const char* handler_name, const char* gui_name, CreateSecurityHandler factory_method);

	/**
	 *	SecurityDescriptor is a class describing SecurityHandler. 
	 */
	class SecurityDescriptor : public TRN_SecHdlrInfo {
	public:
		const char* GetName() { return m_name; }
		const char* GetGuiName() { return m_gui_name; }
		CreateSecurityHandler GetCreateFunct() { return m_factory_method; }
	};

	typedef Common::Iterator<SecurityDescriptor> SecurityDescriptorIterator;

	/**
	 * @return iterator to the first SecurityDescriptor in the list of currently 
	 * registered SecurityHandler types.
	 */ 
	static SecurityDescriptorIterator GetSecHdlrInfoIterator();

	/**
	* Sets a callback function used to override the default PDFNet number serialization 
	* routine. Applications that require advanced control over PDF number processing 
	* can provide a custom handler.
	* 
	* @param WriteProc A pointer to a function that will serialize a floating-point number 
	* in PDF number format. The following parameters describe the callback function:
	*   @param num  The number to be converted to decimal.
	*   @param in_buf the buffer used to serialize the number.
	*   @param in_buf_size the size of input buffer.
	*   @return A pointer within in_buf array where the number string starts.
	*/
	static void SetNumberWriteProc(char* (*WriteProc) (double num, char *in_buf, int in_buf_size));

	/**
	* Sets a callback function used to override the default PDFNet number parsing function.
	* Applications that require advanced control over PDF number processing can
	* provide a custom handler.
	*
	* @param WriteProc A pointer to a function that will serialize a floating-point number 
	* in PDF number format. 
	*/
	static void SetNumberReadProc(TRN_Bool (*ReadProc) (const TRN_UChar *buf, double *output)); 
#endif
};


#include <Impl/PDFNet.inl>


};	// namespace pdftron

#endif

