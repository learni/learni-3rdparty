//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef	  H_CPPPDFElementBuilder
#define	  H_CPPPDFElementBuilder

#include <PDF/Page.h>
#include <PDF/Element.h>
#include <Common/Matrix2D.h>
#include <PDF/Image.h>
#include <PDF/Shading.h>
#include <PDF/PDFDoc.h>
#include <C/PDF/TRN_ElementBuilder.h>

namespace pdftron { 
	namespace PDF {


/**
 * ElementBuilder is used to build new PDF::Elements (e.g. image, text, path, etc) 
 * from scratch. In conjunction with ElementWriter, ElementBuilder can be used to create
 * new page content.
 * 
 * @note Analogous to ElementReader, every call to ElementBuilder.Create? method destroys 
 * the Element currently associated with the builder and all previous Element pointers are 
 * invalidated. 
 * 
 * @note For C++ developers. Analogous to ElementReader, ElementBuilder is the owner of
 * all Element objects it creates. 
 */
class ElementBuilder
{
public:

	 ElementBuilder();
	 ~ElementBuilder();

	/**
	 * The function sets the graphics state of this Element to the given value. 
	 * If 'gs' parameter is not specified or is NULL the function resets the 
	 * graphics state of this Element to the default graphics state (i.e. the 
	 * graphics state at the beginning of the display list).
	 * 
	 * The function can be used in situations where the same ElementBuilder is used 
	 * to create content on several pages, XObjects, etc. If the graphics state is not
	 * Reset() when moving to a new display list, the new Element will have the same 
	 * graphics state as the last Element in the previous display list (and this may 
	 * or may not be your intent).
	 * 
	 * Another use of Reset(gs) is to make sure that two Elements have the graphics 
	 * state. 
	 */
	 void Reset(GState gs = 0);

	// Image Element ------------------------------------------------

	/**
	 * Create a content image Element out of a given document Image.
	 */
	 Element CreateImage(Image& img);

	/**
	 * Create a content image Element out of a given document Image.
	 * @param mtx the image transformation matrix.
	 */
	 Element CreateImage(Image& img, const Common::Matrix2D& mtx);

	/**
	 * Create a content image Element out of a given document Image with 
	 * the lower left corner at (x, y), and scale factors (hscale, vscale).
	 */
	 Element CreateImage(Image& img, double x, double y, double hscale, double vscale);

	/**
	 * Create e_group_begin Element (i.e. 'q' operator in PDF content stream). 
	 * The function saves the current graphics state.
	 */
	 Element CreateGroupBegin();

	/**
	 * Create e_group_end Element (i.e. 'Q' operator in PDF content stream). 
	 * The function restores the previous graphics state.
	 */
	 Element CreateGroupEnd();

	/**
	 * Create a shading Element.
	 */
	 Element CreateShading(Shading& sh);

	/**
	 * Create a Form XObject Element.
	 * @param form a Form XObject content stream
	 */
	 Element CreateForm(SDF::Obj form);

	/**
	 * Create a Form XObject Element using the content of the existing page. 
	 * This method assumes that the XObject will be used in the same 
	 * document as the given page. If you need to create the Form XObject 
	 * in a different document use CreateForm(Page, Doc) method.
	 *
	 * @param page A page used to create the Form XObject.
	 */
	 Element CreateForm(Page page);

	/**
	 * Create a Form XObject Element using the content of the existing page.
	 * Unlike CreateForm(Page) method, you can use this method to create form 
	 * in another document.
	 * 
	 * @param page A page used to create the Form XObject.
	 * @param doc Destination document for the Form XObject.
	 */
	 Element CreateForm(Page page, class PDFDoc& doc);

	/**
	 * Start a text block ('BT' operator in PDF content stream). 
	 * The function installs the given font in the current graphics state.
	 */
	 Element CreateTextBegin(Font font, double font_sz);

	/**
	 * Start a text block ('BT' operator in PDF content stream). 
	 */
	 Element CreateTextBegin();

	/**
	 * Ends a text block.
	 */
	 Element CreateTextEnd();

	/**
	 * Create a text run using the given font.
	 * @note a text run can be created only within a text block
	 */
	 Element CreateTextRun(const char* text_data, Font font, double font_sz);
#ifndef SWIG
	 Element CreateTextRun(const char* text_data, UInt32 text_data_sz, Font font, double font_sz);
	 Element CreateTextRun(const UChar* text_data, UInt32 text_data_sz, Font font, double font_sz);
#endif

	/**
	 * Create a new text run.
	 * @note a text run can be created only within a text block
	 * @note you must set the current Font and font size before calling this function.
	 */
	 Element CreateTextRun(const char* text_data);
#ifndef SWIG
	 Element CreateTextRun(const char* text_data, UInt32 text_data_sz);
	 Element CreateTextRun(const UChar* text_data, UInt32 text_data_sz);
#endif

	/**
	* Create a new Unicode text run.
	*
	* @param text_data pointer to Unicode text
	* @param text_data_sz number of characters (not bytes) in text_data
	*
	* @note you must set the current Font and font size before calling this function 
	* and the font must be created using Font::CreateCIDTrueTypeFont() method.
	* 
	* @note a text run can be created only within a text block
	*/
	 Element CreateUnicodeTextRun(const Unicode* text_data, UInt32 text_data_sz);

	/**
	 * Create e_text_new_line Element (i.e. a Td operator in PDF content stream).
	 * Move to the start of the next line, offset from the start of the current 
	 * line by (dx , dy). dx and dy are numbers expressed in unscaled text space 
	 * units.
	 */
	 Element CreateTextNewLine(double dx, double dy);

	/**
	 * Create e_text_new_line Element (i.e. a T* operator in PDF content stream).
	 */
	 Element CreateTextNewLine();

	// Path Element -------------------------------------------------

	/**
	 * Create a path Element using given path segment data
	 */
	 Element CreatePath(const std::vector<double>& points, const std::vector<unsigned char>& seg_types);

#ifndef SWIG
	 Element CreatePath(const double* points, int point_count, const char* seg_types, int seg_types_count);
#endif

	/**
	 * Create a rectangle path Element.
	 * 
	 * @param x, y The coordinates of the lower left corner of the rectangle.
	 * @param width, height - The width and height of the rectangle.
	 */
	 Element CreateRect(double x, double y, double width, double height);

	/**
	* Create an ellipse (or circle, if rx == ry) path Element.
	* 
	* @param cx, cy The coordinates of the ellipse center.
	* @param rx, ry - The width and height of the ellipse rectangle.
	*/
	Element CreateEllipse(double cx, double cy, double rx, double ry);

	/**
	 * Starts building a new path Element that can contain an arbitrary sequence 
	 * of lines, curves, and rectangles.
	 */
	 void PathBegin();

	/**
	 * Finishes building of the path Element. 
	 * @return the path Element
	 */
	Element PathEnd();

	/**
	 * Set the current point.
	 */
	 void MoveTo(double x, double y);

	/**
	 * Draw a line from the current point to the given point.
	 */
	 void LineTo(double x, double y);

	/**
	 * Draw a Bezier curve from the current point to the given point (x2, y2) using 
	 * (cx1, cy1) and (cx2, cy2) as control points.
	 */
	 void CurveTo(double cx1, double cy1, double cx2, double cy2, double x2, double y2);

	/**
	 * Draw an arc with the specified parameters (lower left corner, width, height and angles).
	 *
	 * @param	x, y			coordinates of the lower left corner of the ellipse encompassing rectangle
	 * @param	width, height	overall width and height of the full ellipse (not considering the angular extents).
	 * @param	start			starting angle of the arc in degrees
	 * @param	extent			angular extent of the arc in degrees
	 */
	void ArcTo(double x, double y, double width, double height, double start, double extent);

	/**
	 * Draw an arc from the current point to the end point.
	 *
	 * @param xr, yr				x and y radius for the arc
	 * @param rx					x-axis rotation in degrees
	 * @param isLargeArc			indicates if smaller or larger arc is chosen
	 *								1 - one of the two larger arc sweeps is chosen
	 *								0 - one of the two smaller arc sweeps is chosen
	 * @param sweep					direction in which arc is drawn (1 - clockwise, 0 - counterclockwise)
	 * @param endX, endY			end point
	 *
	 * @note The Arc is defined the same way as it is specified by SVG or XPS standards. For
	 *		further questions please refer to the XPS or SVG standards.
	 */
	void ArcTo(double xr, double yr,
			   double rx,
			   bool isLargeArc,
			   bool sweep,
			   double endX, double endY);

	/**
	* Add an ellipse (or circle, if rx == ry) to the current path as a complete subpath.
	* Setting the current point is not required before using this function.
	* 
	* @param cx, cy The coordinates of the ellipse center.
	* @param rx, ry - The radii of the ellipse.
	*/
	void Ellipse(double cx, double cy, double rx, double ry);

	/**
	 * Add a rectangle to the current path as a complete subpath. 
	 * Setting the current point is not required before using this function.
	 *
	 * @param x, y The coordinates of the lower left corner of the rectangle.
	 * @param width, height - The width and height of the rectangle.
	 */
	 void Rect(double x, double y, double width, double height);

	/**
	 * Closes the current subpath.
	 */
	 void ClosePath();

	 /**
	 * Frees the native memory of the object.
	 */
	 void Destroy();

/// @cond PRIVATE_DOC
#ifndef SWIGHIDDEN
	 TRN_ElementBuilder mp_builder;
#endif
private:
	// ElementBuilder should not be copied
	ElementBuilder(const ElementBuilder&);
	ElementBuilder& operator= (const ElementBuilder&);
/// @endcond
};


#include <Impl/ElementBuilder.inl>

	};	// namespace PDF
};	// namespace pdftron


#endif

