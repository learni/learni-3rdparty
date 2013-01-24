//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef   H_CALLBACK
#define   H_CALLBACK

#ifdef SWIG

#include <C/Common/TRN_Types.h>

namespace pdftron { 
	namespace PDF {

/**
 *	SWIG director's base class
 *
 *	Contains virtual functions which match the signature of the PDFNet function pointers.
 *  Each virtual function has a matching static function calling it.
 *	User extends this class in the target language and overrides the function of interest.
 *	The static function is assigned to the function pointer.
 */
class Callback
{
public:
	Callback() {}
	virtual ~Callback() {
	}

	/**
	 * Declaration for the callback function that will be called just 
	 * before PDFView starts rendering.
	 * 
	 * @param data Custom data to be passed as a parameter to 'proc'.
	 */
	virtual void RenderBeginEventProc(){}

	static void StaticRenderBeginEventProc(void* data) {
		Callback* f = (Callback*)data;
		f->RenderBeginEventProc();
	}

	/**
	 * Declaration for the callback function that will be called after 
	 * PDFView is done with rendering.
	 *
	 * @param data Custom data to be passed as a parameter to 'proc'.
	 * @param canceled - this parameter is false if PDFView successfully 
	 * completed the rendering, or is true if the rendering was canceled.
	 *
	 * @note this callback is available only in the C++ SDK and not 
	 * available in pre-packaged PDF viewing controls (.NET/Java/ActiveX).
	 */
	virtual void RenderFinishEventProc(bool cancelled){}

	static void StaticRenderFinishEventProc(void* data, bool cancelled) {
		Callback* f = (Callback*)data;
		f->RenderFinishEventProc(cancelled);
	}

	/** 
	 * Error handling.
	 * A type of callback function (or a delegate in .NET terminology) that is called in case
	 * an error is encountered during rendering.
	 */
	virtual void ErrorReportProc (const char* message) {}

	static void StaticErrorReportProc (const char* message, void* data) {
		Callback* f = (Callback*)data;
		f->ErrorReportProc(message);
	}

	/** 
	 * A prototype for a callback function (or a delegate in .NET terminology) 
	 * that will be called whenever current page number changes.
	 * 
	 * @param current_page the current page.
	 * @param num_pages total number of pages in the document.
	 * @param data Custom data to be passed as a second parameter to 'curr_pagenum_proc'.
	 */
	virtual void CurrentPageProc(int current_page, int num_pages) {}

	static void StaticCurrentPageProc(int current_page, int num_pages, void* data) {
		Callback* f = (Callback*)data;
		f->CurrentPageProc(current_page, num_pages);
	}

	/** 
	 * A prototype for a callback function (or a delegate in .NET terminology) 
	 * that will be called whenever current zoom (magnification) number changes.
	 * 
	 * @param current_zoom the current zoom.
	 * @param data Custom data to be passed as a second parameter to 'curr_zoom_proc'.
	 */
	virtual void CurrentZoomProc(double curr_zoom_proc) {}

	static void StaticCurrentZoomProc(double curr_zoom_proc, void* data) {
		Callback* f = (Callback*)data;
		f->CurrentZoomProc(curr_zoom_proc);
	}
	
	virtual void CreateTileProc(char* buffer, int originX, int originY, int width, int height, int cellNumber, bool finalRender, bool predictionRender, int tiles_remaining,
		bool first_tile, int canvas_width, int canvas_height, int cell_side_length, int cell_per_row, int cell_per_col, int thumb_nail_id) 
	{}
	
	static void StaticCreateTileProc(void* callingObject, char* buffer, int originX, int originY, int width, int height, int canvasNumber, int cellNumber, bool finalRender, bool predictionRender, int tiles_remaining,
		bool first_tile, int canvas_width, int canvas_height, int cell_side_length, int cell_per_row, int cell_per_col, int thumb_nail_id )
	{
		Callback* f = (Callback*)callingObject;
		f->CreateTileProc(buffer, originX, originY, width, height, cellNumber, finalRender, predictionRender, tiles_remaining,
			first_tile, canvas_width, canvas_height, cell_side_length, cell_per_row, cell_per_col, thumb_nail_id);
	}
	
	virtual void RemoveTileProc(int canvasNumber, int cellNumber, int thumb_nail_id) {}
	
	static void StaticRemoveTileProc(void* callingObject, int canvasNumber, int cellNumber, int thumb_nail_id) {
		Callback* f = (Callback*)callingObject;
		f->RemoveTileProc(canvasNumber, cellNumber, thumb_nail_id);
	}
};

	};	// namespace PDF
};	// namespace pdftron

#endif  // SWIG
#endif  // H_CALLBACK

