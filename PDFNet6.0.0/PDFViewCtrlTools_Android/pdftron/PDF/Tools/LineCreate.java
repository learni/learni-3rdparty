//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import pdftron.PDF.Annot.BorderStyle;
import pdftron.PDF.ColorPt;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.MotionEvent;


/**
 * 
 * This class is for creating a line annotation.
 *
 */
class LineCreate extends SimpleShapeCreate {
	public LineCreate(PDFViewCtrl ctrl) {
		super(ctrl);
		mNextToolMode = ToolManager.e_line_create;
	}
	
	public int getMode() {
		return ToolManager.e_line_create;
	}
	
	public void onDraw (Canvas canvas, Matrix tfm) {
		canvas.drawLine(mPt1.x, mPt1.y, mPt2.x, mPt2.y, mPaint);
	}
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
	    // If both start point and end point are the same, we
	    // don't push back the line and go back to pan tool.
	    mNextToolMode = ToolManager.e_pan;
	    if (!mPt1.equals(mPt2)) {
    		try {
    		    mNextToolMode = ToolManager.e_annot_edit_line;
    			mPDFView.lockDoc(true);
    			pdftron.PDF.Rect rect = getShapeBBox();
    			if ( rect != null ) {
    				pdftron.PDF.Annots.Line line = pdftron.PDF.Annots.Line.create(mPDFView.getDoc(), rect);
    				BorderStyle bs = line.getBorderStyle(); 
    				bs.setWidth(mThickness);
    				line.setBorderStyle(bs);
    				
    				double r = (double)Color.red(mStrokeColor)/255;
    				double g = (double)Color.green(mStrokeColor)/255;
    				double b = (double)Color.blue(mStrokeColor)/255;
    				double a = (double)Color.alpha(mStrokeColor)/255;
    				ColorPt color = new ColorPt(r, g, b);
    				line.setColor(color, 3);
    				line.setOpacity(a);
    				
    				line.refreshAppearance();
    				
    				Page page = mPDFView.getDoc().getPage(mDownPageNum);
    				page.annotPushBack(line);
    				
    				mAnnot = line;
    				mAnnotPageNum = mDownPageNum;
    				buildAnnotBBox();
    				mPDFView.update(mAnnot, mAnnotPageNum);
    			}
    		}
    		catch (Exception e1) {
    		}
    		finally {
    			mPDFView.unlockDoc();
    		}
    		
    		mPDFView.waitForRendering();
	    }
		return false;
	}
}
