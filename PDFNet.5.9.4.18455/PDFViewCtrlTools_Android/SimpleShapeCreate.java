//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import pdftron.PDF.PDFViewCtrl;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;


/**
 * This class is the base class for several shape creation classes,
 * e.g., LineCreate, OvalCreate, etc.
 */
abstract class SimpleShapeCreate extends Tool {
	protected PointF mPt1, mPt2;			//touch-down point and moving point
	protected Paint mPaint;
	protected int mDownPageNum;				
	protected RectF mPageCropOnClientF;
	protected float mThickness;
	protected float mThicknessDraw;
	protected int mStrokeColor;
	
	
	public SimpleShapeCreate(PDFViewCtrl ctrl) {
		super(ctrl);
		mPt1 = new PointF(0, 0);
		mPt2 = new PointF(0, 0);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mThickness = 1.0f;
		mThicknessDraw = 1.0f;
	}
	
	
	abstract public int getMode();
	
	
	public boolean onDown(MotionEvent e) {
		//the first touch-down point
		mPt1.x = e.getX() + mPDFView.getScrollX();
		mPt1.y = e.getY() + mPDFView.getScrollY();
		
		//the moving point that is the same with the touch-down point initially
		mPt2.set(mPt1);
		
		//remembers which page is touched initially and that is the page where
		//the annotation is going to reside on.
		mDownPageNum = mPDFView.getPageNumberFromClientPt(e.getX(), e.getY());
		if ( mDownPageNum < 1 ) {
			mDownPageNum = mPDFView.getCurrentPage();
		}
		if ( mDownPageNum >= 1 ) {
			mPageCropOnClientF = buildPageBoundBoxOnClient(this.mDownPageNum);
		}
		
		//query for the default thickness and color, which are to be used when the 
		//annotation is created in the derived classes.
		SharedPreferences settings = mPDFView.getContext().getSharedPreferences(Tool.PREFS_FILE_NAME, 0);
		mThickness = settings.getFloat("annotation_creation_thickness", 1.0f);
		float zoom = (float)mPDFView.getZoom();
		mThicknessDraw = mThickness * zoom;
		mPaint.setStrokeWidth(mThicknessDraw);
		
		mStrokeColor = settings.getInt("annotation_creation_color", 0xFFFF0000);
		mPaint.setColor(mStrokeColor);
		
		return false;
	}
	
	
	public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
		//while moving, update the moving point so that a rubber band can be shown to
		//indicate the bounding box of the resulting annotation. 
		float x = mPt2.x;
		float y = mPt2.y;
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
		
		float min_x = Math.min(Math.min(x, mPt2.x), mPt1.x) - mThicknessDraw;
		float max_x = Math.max(Math.max(x, mPt2.x), mPt1.x) + mThicknessDraw;
		float min_y = Math.min(Math.min(y, mPt2.y), mPt1.y) - mThicknessDraw;
		float max_y = Math.max(Math.max(y, mPt2.y), mPt1.y) + mThicknessDraw;
		
		mPDFView.invalidate((int)min_x, (int)min_y, (int)Math.ceil(max_x), (int)Math.ceil(max_y));
		return true;
	}
	
	
	public boolean onScaleBegin(float x, float y) {
		//don't allow scaling during annotation creation
		return true;
	}
	
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		//when an up gesture happens, the annotation is created (in one of the derived class),
		//and then it goes to the annotation edit tool.
		mNextToolMode = ToolManager.e_annot_edit;
		float min_x = Math.min(mPt1.x, mPt2.x) - mThicknessDraw;
		float max_x = Math.max(mPt1.x, mPt2.x) + mThicknessDraw;
		float min_y = Math.min(mPt1.y, mPt2.y) - mThicknessDraw;
		float max_y = Math.max(mPt1.y, mPt2.y) + mThicknessDraw;
		
		mPDFView.postInvalidate((int)min_x, (int)min_y, (int)Math.ceil(max_x), (int)Math.ceil(max_y));
		return false;
	}
	
	
	protected pdftron.PDF.Rect getShapeBBox() {
		//computes the bounding box of the rubber band in page space.
		double [] pts1 = new double[2];
		double [] pts2 = new double[2];
		pts1 = mPDFView.convClientPtToPagePt(mPt1.x-mPDFView.getScrollX(), mPt1.y-mPDFView.getScrollY(), mDownPageNum);
		pts2 = mPDFView.convClientPtToPagePt(mPt2.x-mPDFView.getScrollX(), mPt2.y-mPDFView.getScrollY(), mDownPageNum);
		pdftron.PDF.Rect rect;
		try {
			rect = new pdftron.PDF.Rect(pts1[0], pts1[1], pts2[0], pts2[1]);
			return rect;
		} catch (Exception e) {
			return null;
		}
	}
}