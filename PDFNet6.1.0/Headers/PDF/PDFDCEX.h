//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------
#if defined(_WIN32)

#ifndef   H_CPPPDFDCEX
#define   H_CPPPDFDCEX

#include <PDF/PDFDoc.h>
#include <C/PDF/TRN_PDFDCEX.h>
#include <SDF/Obj.h>
#include <Windows.h>

namespace pdftron{ 
	namespace PDF {

/** 
* PDFDCEX is a utility class used to represent a PDF Device Context (DC).
* 
* Windows developers can use standard GDI or GDI+ API-s to write on PDFDCEX 
* and to generate PDF documents based on their existing drawing functions.
* PDFDCEX can also be used to implement file conversion from any printable 
* file format to PDF. 
*
* PDFDCEX class can be used in many ways to translate from GDI to PDF:
*  - To translate a single GDI drawing into a single page PDF document.
*  - To translate a single GDI drawing into an object which can be reused
*    many times throughout a PDF document (i.e. as a Form XObject).
*  - To translate many GDI drawings into single page or multipage PDF document.
*  ...
*
* Very few code changes are required to perform the translation from GDI to 
* PDF as PDFDCEX provides a GDI Device Context handle which can be passed to 
* all GDI function requiring an HDC.  PDFDCEX does use a "Virtual Printer" 
* approach so the translation should be of both high quality and speed.
*
* For more advanced translations or creations of PDF documents, such as security
* handling, the use of other PDFNet classes will be required.
*
* An example use of PDFDCEX can be found in PDFDCTest.cpp:
*
* @code
* // Start with a PDFDoc to put the picture into, and a PDFDCEX to translate GDI to PDF
* PDFDoc pdfdoc;
* PDFDCEX pdfdcex;
*
* // Begin the translation from GDI to PDF, provide the PDFDoc to append the translated
* // GDI drawing to and get back a GDI Device Context
* HDC hDC = pdfdcex.Begin(pdfdoc);
* ::StartPage(hDC);
*
* ... perform GDI drawing ...
*
* ::EndPage(hDC);
* // Complete the translation
* pdfdcex.EndDoc();
*
* // Save the PDF document
* pdfdoc.Save("PDFDCEX_is_cool.pdf", SDF::SDFDoc::e_remove_unused, NULL);
* @endcode
*/
class PDFDCEX
{
public:

	/**
	* Default constructor. Creates an empty new GDI to PDF translator.
	*/
	PDFDCEX ();

	/**
	*	Destructor
	*/
	~PDFDCEX ();

	/**
	* Begin the process of translating GDI drawing into a PDF, starting with
	* the creation of a GDI Device Context.
	*
	* @param in_pdfdoc the document which the converted GDI pages will be appended to.
	* 
	* @return a GDI Handle to Display Context.
	*/
	HDC Begin( PDFDoc & in_pdfdoc );

	/**
	* Closes the GDI Device Context, translating the GDI instruction to PDF, and adds
	* the PDF objects to the page in the location specified by PDFDCEX::Begin( page, box, ...).
	*
	* @exception An exception is thrown if there are any fatal errors in the 
	* the translation process.
	*/
	void End();

	UInt32 GetDPI();

	 /**
	 * Frees the native memory of the object.
	 */
	 void Destroy();

/// @cond PRIVATE_DOC
private:
	TRN_PDFDCEX m_pdfDcEx;
	PDFDCEX(const PDFDCEX&);
	PDFDCEX& operator= (const PDFDCEX&);
/// @endcond
};

#include <Impl/PDFDCEX.inl>

	}; // namespace PDF
}; // namespace pdftron

#endif // H_CPPPDFDCEX
#endif // defined(_WIN32)
