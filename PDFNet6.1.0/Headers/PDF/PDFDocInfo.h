//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPPPDFDocInfo
#define   H_CPPPDFDocInfo

#include <C/PDF/TRN_PDFDocInfo.h>
#include <PDF/Date.h>

namespace pdftron { 
	namespace PDF {

/** 
 * PDFDocInfo is a high-level utility class that can be used 
 * to read and modify document's metadata.
 */
class PDFDocInfo
{
public:

	/**
	 * @return The documentís title.
	 */
	 UString GetTitle();

	/**
	 * @return SDF/Cos string object representing document's title.
	 */
	 SDF::Obj GetTitleObj();

	/**
	 * Set documentís title.
	 * @param title New title of the document.
	 */
	 void SetTitle(const UString& title);

	/**
	 * @return The name of the person who created the document.
	 */
	 UString GetAuthor();

	/**
	 * @return SDF/Cos string object representing document's author.
	 */
	 SDF::Obj GetAuthorObj();

	/**
	 * Set the author of the document.
	 * @param The name of the person who created the document.
	 */
	 void SetAuthor(const UString& author);

	/**
	 * @return The subject of the document.
	 */
	 UString GetSubject();

	/**
	 * @return SDF/Cos string object representing document's subject.
	 */
	 SDF::Obj GetSubjectObj();

	/**
	 * Set the subject of the document
	 * @param subject The subject of the document.
	 */
	 void SetSubject(const UString& subject);

	/**
	 * @return Keywords associated with the document.
	 */
	 UString GetKeywords();

	/**
	 * @return SDF/Cos string object representing document's keywords.
	 */
	 SDF::Obj GetKeywordsObj();

	/**
	 * Set keywords associated with the document.
	 * @param Keywords Keywords associated with the document.
	 */
	 void SetKeywords(const UString& keywords);
	
	/**
	 * @return If the document was converted to PDF from another 
	 * format, the name of the application that created the original 
	 * document from which it was converted.
	 */
	 UString GetCreator();

	/**
	 * @return SDF/Cos string object representing document's creator.
	 */
	 SDF::Obj GetCreatorObj();

	/**
	 * Set documentís creator.
	 * @param creator The name of the application that created 
	 * the original document.
	 */
	 void SetCreator(const UString& creator);

	/**
	 * @return If the document was converted to PDF from another format,
	 * the name of the application (for example, Distiller) that 
	 * converted it to PDF.
	 */
	 UString GetProducer();

	/**
	 * @return SDF/Cos string object representing document's producer.
	 */
	 SDF::Obj GetProducerObj();

	/**
	 * Set documentís producer.
	 * @param producer The name of the application that generated PDF.
	 */
	 void SetProducer(const UString& producer);

	/**
	 * @return The date and time the document was created, 
	 * in human-readable form.
	 */
	 Date GetCreationDate();

	/**
	 * Set documentís creation date.
	 * @param creation_date The date and time the document was created.
	 */
	 void SetCreationDate(const Date& creation_date);

	/**
	 * @return The date and time the document was most recently 
	 * modified, in human-readable form.
	 */
	 Date GetModDate();

	/**
	 * Set documentís modification date.
	 * @param mod_date The date and time the document was most 
	 * recently modified.
	 */
	 void SetModDate(const Date& mod_date);

	/**
	 * @return documentís SDF/Cos 'Info' dictionary or NULL if 
	 * the info dictionary is not available.
	 */
	 SDF::Obj GetSDFObj();

	/**
	 * PDFDocInfo constructor. Typically this constructor is 
	 * never used since it is easier to obtain DocInfo using 
	 * PDFDoc.GetDocInfo()
	 */
	 PDFDocInfo (SDF::Obj tr);
	 PDFDocInfo (const PDFDocInfo&);
	 PDFDocInfo& operator= (const PDFDocInfo&);

	 //added default constructor for swig
	 PDFDocInfo ();


/// @cond PRIVATE_DOC
#ifndef SWIGHIDDEN
	TRN_PDFDocInfo mp_info;
	PDFDocInfo (TRN_PDFDocInfo impl);
#endif
/// @endcond
};



#include <Impl/PDFDocInfo.inl>

	};	// namespace PDF
};	// namespace pdftron

#endif
