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
import android.graphics.RectF;
import android.view.MotionEvent;


/**
 * 
 * This class is for creating an oval annotation.
 *
 */
class OvalCreate extends SimpleShapeCreate {
	private RectF mOval;
	public OvalCreate(PDFViewCtrl ctrl) {
		super(ctrl);
		mNextToolMode = ToolManager.e_oval_create;
		mOval = new RectF();
	}
	
	public int getMode() {
		return ToolManager.e_oval_create;
	}
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		mNextToolMode = ToolManager.e_annot_edit;
		try {
			mPDFView.lockDoc(true);
			pdftron.PDF.Rect rect = getShapeBBox();
			if ( rect != null ) {
				pdftron.PDF.Annots.Circle circle = pdftron.PDF.Annots.Circle.create(mPDFView.getDoc(), rect);
				BorderStyle bs = circle.getBorderStyle(); 
				bs.setWidth(mThickness);
				circle.setBorderStyle(bs);
				
				double r = (double)Color.red(mStrokeColor)/255;
				double g = (double)Color.green(mStrokeColor)/255;
				double b = (double)Color.blue(mStrokeColor)/255;
				double a = (double)Color.alpha(mStrokeColor)/255;
				ColorPt color = new ColorPt(r, g, b);
				circle.setColor(color, 3);
				circle.setOpacity(a);
				
				circle.refreshAppearance();
				
				Page page = mPDFView.getDoc().getPage(mDownPageNum);
				page.annotPushBack(circle);
				
				mAnnot = circle;
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
		
		//Android aligns in the middle of a line, while PDFNet aligns along the outer boundary;
		//so need to adjust the temporary shape drawn.
		float adjust = mThicknessDraw/2;
		min_x += adjust;
		max_x -= adjust;
		min_y += adjust;
		max_y -= adjust;
		
		mOval.set(min_x, min_y, max_x, max_y);
		canvas.drawArc(mOval, 0, 360, false, mPaint);
	}
}
