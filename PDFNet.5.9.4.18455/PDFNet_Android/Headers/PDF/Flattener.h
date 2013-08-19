//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CPPPDFFlattener
#define   H_CPPPDFFlattener

#include <Common/Common.h>
#include <C/PDF/TRN_Flattener.h>

namespace pdftron {
namespace PDF {

/**
 * Flattener is a utility class that can be used to create PDF’s that render 
 * faster on devices with lower memory and speeds.
 *
 * By using the FlattenMode::e_simple option each page in the PDF will be
 * reduced to a single background image, with the remaining text over top in vector
 * format. Some text may still get flattened, in particular any text that is clipped, 
 * or underneath, other content that will be flattened.
 *
 * On the other hand the FlattenMode::e_fast will not flatten simple content, such
 * as simple straight lines, nor will it flatten Type3 fonts.
 */
class Flattener
{
public:
	/**
	 * Flattener constructor
	 */
	Flattener();
	~Flattener();

	/**
	 * The output resolution, from 1 to 1000, in Dots Per Inch (DPI) at which to 
	 * render elements which cannot be directly converted. 
	 * the default value is 150 Dots Per Inch
	 * @param dpi the resolution in Dots Per Inch
	 */
	void SetDPI(UInt32 dpi);

	/**
	 * Specifies the maximum image size in pixels.
	 * @param max_pixels the maximum number of pixels an image can have.
	 */
	void SetMaximumImagePixels(UInt32 max_pixels);

	/**
	 * Specifies whether to leave images in existing compression, or as JPEG.
	 * @param jpg if true PDF will contain all JPEG images.
	 */
	void SetPreferJpg(bool jpg);

	enum FlattenMode
	{
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

	/**
	 * Process each page in the PDF, flattening content that matches the mode criteria.
	 * @param doc the document to flatten.
	 * @param mode indicates the criteria for which elements are flattened.
	 */
	void Process(class PDFDoc& doc, enum FlattenMode mode );

	 /**
	 * Frees the native memory of the object.
	 */
	 void Destroy();

	/// @cond PRIVATE_DOC
	#ifndef SWIGHIDDEN
	TRN_Flattener mp_impl;
	#endif

private:
	// ElementBuilder should not be copied
	Flattener(const Flattener&);
	Flattener& operator= (const Flattener&);
	/// @endcond
};

#include <Impl/Flattener.inl>

	};	// namespace PDF
};	// namespace pdftron

#endif
