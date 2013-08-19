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
 * This class is for creating a rectangle annotation.
 *
 */
class RectCreate extends SimpleShapeCreate {
	public RectCreate(PDFViewCtrl ctrl) {
		super(ctrl);
		mNextToolMode = ToolManager.e_rect_create;
	}
	
	public int getMode() {
		return ToolManager.e_rect_create;
	}
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		mNextToolMode = ToolManager.e_annot_edit;
		try {
			mPDFView.lockDoc(true);
			pdftron.PDF.Rect rect = getShapeBBox();
			if ( rect != null ) {
				pdftron.PDF.Annots.Square square = pdftron.PDF.Annots.Square.create(mPDFView.getDoc(), rect);
				BorderStyle bs = square.getBorderStyle(); 
				bs.setWidth(mThickness);
				square.setBorderStyle(bs);
				
				double r = (double)Color.red(mStrokeColor)/255;
				double g = (double)Color.green(mStrokeColor)/255;
				double b = (double)Color.blue(mStrokeColor)/255;
				double a = (double)Color.alpha(mStrokeColor)/255;
				ColorPt color = new ColorPt(r, g, b);
				square.setColor(color, 3);
				square.setOpacity(a);
				
				square.refreshAppearance();
				
				Page page = mPDFView.getDoc().getPage(mDownPageNum);
				page.annotPushBack(square);
				
				mAnnot = square;
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
		
		return false;
	}
	
	public void onDraw (Canvas canvas, Matrix tfm) {
		float min_x = Math.min(mPt1.x, mPt2.x);
		float max_x = Math.max(mPt1.x, mPt2.x);
		float min_y = Math.min(mPt1.y, mPt2.y);
		float max_y = Math.max(mPt1.y, mPt2.y);
		
		//Android aligns in the middle of the line, while PDFNet aligns along the outer boundary;
		//so need to adjust the temporary shape drawn.
		float adjust = mThicknessDraw/2;
		
		canvas.drawRect(min_x+adjust, min_y+adjust, max_x-adjust, max_y-adjust, mPaint);
	}
}