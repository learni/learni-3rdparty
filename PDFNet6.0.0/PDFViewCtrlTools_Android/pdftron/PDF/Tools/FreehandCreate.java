//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import java.util.LinkedList;
import java.util.ListIterator;

import pdftron.PDF.ColorPt;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import pdftron.PDF.Annot.BorderStyle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;


/**
 * 
 * This class is for creating a free hand annotation.
 *
 */
class FreehandCreate extends SimpleShapeCreate {
	private Path mPath;
	private LinkedList<PointF> mPathPoints;
	public FreehandCreate(PDFViewCtrl ctrl) {
		super(ctrl);
		mNextToolMode = ToolManager.e_ink_create;
		mPath = new Path();
		mPathPoints = new LinkedList<PointF>();
	}
	
	public int getMode() {
		return ToolManager.e_ink_create;
	}
	
	public boolean onDown(MotionEvent e) {
		super.onDown(e);
		mPath.moveTo(mPt1.x, mPt1.y);
		mPathPoints.add(new PointF(mPt1.x, mPt1.y));
		return false;
	}
	
	public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
		//during moving, update the free hand path and the bounding box. note that for the
		//bounding box, we need to include the previous bounding box in the new bounding box
		//so that the previously drawn free hand will go away.
		float x = e2.getX() + mPDFView.getScrollX();
		float y = e2.getY() + mPDFView.getScrollY();
		
		mPath.lineTo(x, y);
		mPathPoints.add(new PointF(x, y));
		
		mPt1.x = Math.min(Math.min(x, mPt1.x), mPt1.x);
		mPt1.y = Math.min(Math.min(y, mPt1.y), mPt1.y);
		mPt2.x = Math.max(Math.max(x, mPt2.x), mPt2.x);
		mPt2.y = Math.max(Math.max(y, mPt2.y), mPt2.y);
		
		float min_x = mPt1.x - mThicknessDraw;
		float max_x = mPt2.x + mThicknessDraw;
		float min_y = mPt1.y - mThicknessDraw;
		float max_y = mPt2.y + mThicknessDraw;
		mPDFView.invalidate((int)min_x, (int)min_y, (int)Math.ceil(max_x), (int)Math.ceil(max_y));
		
		return true;
	}
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		mNextToolMode = ToolManager.e_annot_edit;
		try {
			mPDFView.lockDoc(true);
			pdftron.PDF.Rect rect = getShapeBBox();
			if ( rect != null ) {
				float sx = mPDFView.getScrollX();
				float sy = mPDFView.getScrollY();
				pdftron.PDF.Annots.Ink ink = pdftron.PDF.Annots.Ink.create(mPDFView.getDoc(), rect);
				
				int i = 0;
				ListIterator<PointF> itr = mPathPoints.listIterator(0);
				pdftron.PDF.Point p = new pdftron.PDF.Point();
				double [] pts = new double[2];
				while (itr.hasNext()) {
					PointF p_ = itr.next();
					pts = mPDFView.convClientPtToPagePt(p_.x - sx, p_.y - sy, mDownPageNum);
					p.x = pts[0];
					p.y = pts[1];
					//currently, the free hand annotation has only one path with all the points. 
					ink.setPoint(0, i++, p);	
				}
				
				BorderStyle bs = ink.getBorderStyle(); 
				bs.setWidth(mThickness);
				ink.setBorderStyle(bs);
				
				double r = (double)Color.red(mStrokeColor)/255;
				double g = (double)Color.green(mStrokeColor)/255;
				double b = (double)Color.blue(mStrokeColor)/255;
				double a = (double)Color.alpha(mStrokeColor)/255;
				ColorPt color = new ColorPt(r, g, b);
				ink.setColor(color, 3);
				ink.setOpacity(a);
				
				ink.refreshAppearance();
				
				Page page = mPDFView.getDoc().getPage(mDownPageNum);
				page.annotPushBack(ink);
				
				mAnnot = ink;
				mAnnotPageNum = mDownPageNum;
				buildAnnotBBox();

				mPDFView.update(mAnnot, mAnnotPageNum); //update the region where the annotation occupies.
			}
			mNextToolMode = ToolManager.e_annot_edit;
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
		canvas.drawPath(mPath, mPaint);
	}
}