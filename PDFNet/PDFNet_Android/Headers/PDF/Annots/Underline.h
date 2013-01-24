//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_PDFTRON_PDF_CPPWRAP_UNDERLINE
#define   H_PDFTRON_PDF_CPPWRAP_UNDERLINE

#include <PDF/Annots/TextMarkup.h>

namespace pdftron { 
		namespace PDF {
			namespace Annots {
/** 
 * An Underline annotation shows as a line segment across the bottom 
 * of a word or a group of contiguous words.
 */
class Underline : public TextMarkup
{
public:
	/** 
	 * Creates an Underline annotation and initializes it using given Cos/SDF object.
	 * @note The constructor does not copy any data, but is instead the logical
	 * equivalent of a type cast.
	*/
	Underline(SDF::Obj d);

	/** 
	 * Creates an Underline annotation and initializes it using given annotation object.
	 * @note The constructor does not copy any data, but is instead the logical
	 * equivalent of a type cast.
	*/
	Underline(const Annot& ann) : TextMarkup(ann.GetSDFObj()) {} 

	/** 
	 * Creates a new Underline annotation in the specified document.
	 * 
	 * @param doc A document to which the Underline annotation is added.
	 * @param pos A rectangle specifying the Underline annotation's bounds in default user space units.
	 * 
	 * @return A newly created blank Underline annotation.
	 */
	static Underline Create(SDF::SDFDoc& doc, const Rect& pos);

	/// @cond PRIVATE_DOC
	#ifndef SWIGHIDDEN
	Underline(TRN_Annot underline);
	#endif
	/// @endcond

};//class Underline
			};//namespace Annot
		};//namespace PDF
};//namespace pdftron
#include <Impl/Page.inl>
#endif