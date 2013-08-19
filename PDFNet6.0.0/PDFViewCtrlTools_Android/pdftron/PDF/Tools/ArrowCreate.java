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
import android.graphics.PointF;
import android.view.MotionEvent;


/**
 * 
 * This class is for creating an arrow.
 *
 */
class ArrowCreate extends SimpleShapeCreate {
	private double mCos, mSin;
	private PointF mPt3, mPt4;
	private final float mArrowLength;
	
	
	public ArrowCreate(PDFViewCtrl ctrl) {
		super(ctrl);
		
		mArrowLength = convDp2Pix(20);
		
		mNextToolMode = ToolManager.e_arrow_create;
		mCos = Math.cos(3.14159265/6); //30 degree
		mSin = Math.sin(3.14159265/6);
		mPt3 = new PointF(0, 0);
		mPt4 = new PointF(0, 0);
	}
	
	
	public int getMode() {
		return ToolManager.e_arrow_create;
	}
	
	
	public boolean onDown(MotionEvent e) {
		super.onDown(e);
		mPt3.set(mPt1);
		mPt4.set(mPt1);
		calcArrow();
		return false;
	}
	
	public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
		//during moving, update the arrow shape and the bounding box. note that for the
		//bounding box, we need to include the previous bounding box in the new bounding box
		//so that the previously drawn arrow will go away.
		float min_x = Math.min(Math.min(Math.min(mPt1.x, mPt2.x), mPt3.x), mPt4.x);
		float max_x = Math.max(Math.max(Math.max(mPt1.x, mPt2.x), mPt3.x), mPt4.x);
		float min_y = Math.min(Math.min(Math.min(mPt1.y, mPt2.y), mPt3.y), mPt4.y);
		float max_y = Math.max(Math.max(Math.max(mPt1.y, mPt2.y), mPt3.y), mPt4.y);
		
		mPt2.x = e2.getX() + mPDFView.getScrollX();
		mPt2.y = e2.getY() + mPDFView.getScrollY();
		
		//don't allow the annotation to go beyond the page
		if ( mPageCropOnClientF != null ) {
			if ( mPt2.x < mPageCropOnClientF.left ) {
				mPt2.x = mPageCropOnClientF.left;
			}
			else if ( mPt2.x > mPageCropOnClientF.right ) {
				mPt2.x = mPageCropOnClientF.right;
			}
			if ( mPt2.y < mPageCropOnClientF.top ) {
				mPt2.y = mPageCropOnClientF.top;
			}
			else if ( mPt2.y > mPageCropOnClientF.bottom ) {
				mPt2.y = mPageCropOnClientF.bottom;
			}
		}
		
		calcArrow();
		
		min_x = Math.min(Math.min(min_x, mPt3.x), mPt4.x) - mThicknessDraw;
		max_x = Math.max(Math.max(max_x, mPt3.x), mPt4.x) + mThicknessDraw;
		min_y = Math.min(Math.min(min_y, mPt3.y), mPt4.y) - mThicknessDraw;
		max_y = Math.max(Math.max(max_y, mPt3.y), mPt4.y) + mThicknessDraw;
		mPDFView.invalidate((int)min_x, (int)min_y, (int)Math.ceil(max_x), (int)Math.ceil(max_y));
		return true;
	}
	
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		//when an up gesture is detected, create the arrow and go to 
		//the annotation edit tool.
		mNextToolMode = ToolManager.e_annot_edit_line;
		try {
			mPDFView.lockDoc(true);
			pdftron.PDF.Rect bbox = getShapeBBox();
			if ( bbox != null ) {
				pdftron.PDF.Annots.Line line = pdftron.PDF.Annots.Line.create(mPDFView.getDoc(), bbox);
				line.setStartStyle(pdftron.PDF.Annots.Line.e_OpenArrow);
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
				
				mPDFView.update(mAnnot, mAnnotPageNum); //update the region where the annotation occupies.
			}
		} 
		catch (Exception e1) {
			float min_x = Math.min(Math.min(Math.min(mPt1.x, mPt2.x), mPt3.x), mPt4.x) - mThicknessDraw;
			float max_x = Math.max(Math.max(Math.max(mPt1.x, mPt2.x), mPt3.x), mPt4.x) + mThicknessDraw;
			float min_y = Math.min(Math.min(Math.min(mPt1.y, mPt2.y), mPt3.y), mPt4.y) - mThicknessDraw;
			float max_y = Math.max(Math.max(Math.max(mPt1.y, mPt2.y), mPt3.y), mPt4.y) + mThicknessDraw;
			mPDFView.postInvalidate((int)min_x, (int)min_y, (int)Math.ceil(max_x), (int)Math.ceil(max_y));
		}
		finally {
			mPDFView.unlockDoc();
		}
		
		mPDFView.waitForRendering();
		
		return false;
	}
	
	
	private void calcArrow() {
		//mPt1 and mPt2 are the first touch-down point and the moving point,
		//and mPt3 and mPt4 are the end points of the arrow's two shorter lines. 
		mPt3.set(mPt2);
		mPt4.set(mPt2);
		double dx = mPt2.x - mPt1.x;
		double dy = mPt2.y - mPt1.y;
		double len = dx*dx + dy*dy;
		
		if ( len != 0 ) {
			len = Math.sqrt(len);
			dx /= len;
			dy /= len;
			
			double dx1 = dx * mCos - dy * mSin;
			double dy1 = dy * mCos + dx * mSin;
			mPt3.x = (float)(mPt1.x + mArrowLength * dx1);
			mPt3.y = (float)(mPt1.y + mArrowLength * dy1);
			
			double dx2 = dx * mCos + dy * mSin;
			double dy2 = dy * mCos - dx * mSin;
			mPt4.x = (float)(mPt1.x + mArrowLength * dx2);
			mPt4.y = (float)(mPt1.y + mArrowLength * dy2);
		}
	}
	
	
	//draw the temporary arrow.
	public void onDraw (Canvas canvas, Matrix tfm) {
		canvas.drawLine(mPt1.x, mPt1.y, mPt2.x, mPt2.y, mPaint);
		canvas.drawLine(mPt1.x, mPt1.y, mPt3.x, mPt3.y, mPaint);
		canvas.drawLine(mPt1.x, mPt1.y, mPt4.x, mPt4.y, mPaint);
	}
}