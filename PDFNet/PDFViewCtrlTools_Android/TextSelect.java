//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import java.util.Iterator;
import java.util.LinkedList;

import pdftron.Common.PDFNetException;
import pdftron.PDF.ColorPt;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import pdftron.PDF.Point;
import pdftron.PDF.QuadPoint;
import pdftron.PDF.Annots.Highlight;
import pdftron.PDF.Annots.Squiggly;
import pdftron.PDF.Annots.StrikeOut;
import pdftron.PDF.Annots.TextMarkup;
import pdftron.PDF.Annots.Underline;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;


/**
 * This class selects text on pages.
 */
class TextSelect extends Tool {
	
	//the selection widget at the two ends of the selected text. 
	private class SelWidget {
		public PointF mStrPt;
		public PointF mEndPt;
		public SelWidget() {}
	}
	
	LinkedList<RectF> mSelRects;
	Path mSelPath;
	boolean mBeingLongPressed;
	boolean mBeingPressed;
	RectF mSelBBox;
	RectF mTempRect;
	Rect mInvalidateBBox;
	boolean mDrawingLoupe;
	boolean mScaled;
	int mPagePresModeWhileSelected;
	
	int mEffSelWidgetId;
	boolean mSelWidgetEnabled;
	SelWidget [] mSelWidgets;
	PointF mStationPt;
	
	int mLoupeWidth;
	int mLoupeHeight;
	int mLoupeMargin;
	float mLoupeThickness;
	float mLoupeShadowFactor;
	float mLoupeArrowHeight;
	Canvas mCanvas;
	Bitmap mBitmap;
	RectF mSrcRectF;
	RectF mDesRectF;
	Matrix mMatrix;
	RectF mLoupeBBox;
	PointF mPressedPoint;
	Path mLoupePath;
	Path mLoupeShadowPath;
	Paint mPaint;
	
	protected final float mTSWidgetThickness;
	protected final float mTSWidgetRadius;
	
	public TextSelect(PDFViewCtrl ctrl) {
		super(ctrl);
		
		mTSWidgetThickness = this.convDp2Pix(2);
		mTSWidgetRadius = this.convDp2Pix(7.5f);
		mLoupeWidth = (int)this.convDp2Pix(150);
		mLoupeMargin = (int)this.convDp2Pix(5);
		
		mSelRects = new LinkedList<RectF>();
		mSelPath = new Path();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mBeingLongPressed = false;
		mBeingPressed = false;
		mSelBBox = new RectF();
		mLoupeBBox = new RectF();
		mTempRect = new RectF();
		mInvalidateBBox = new Rect();
		mEffSelWidgetId = -1;
		mSelWidgetEnabled = false;
		mSelWidgets = new SelWidget[2];
		mSelWidgets[0] = new SelWidget();
		mSelWidgets[1] = new SelWidget();
		mSelWidgets[0].mStrPt = new PointF();
		mSelWidgets[0].mEndPt = new PointF();
		mSelWidgets[1].mStrPt = new PointF();
		mSelWidgets[1].mEndPt = new PointF();
		mStationPt = new PointF();
		mDrawingLoupe = false;
		mScaled = false;
		
		mLoupeHeight = mLoupeWidth/2;
		mLoupeArrowHeight = (float)mLoupeHeight/4;
		mLoupeThickness = (float)mLoupeMargin/4;
		mLoupeShadowFactor = mLoupeThickness * 4;
		mBitmap = Bitmap.createBitmap(mLoupeWidth - mLoupeMargin*2, mLoupeHeight - mLoupeMargin*2, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas();
		mCanvas.setBitmap(mBitmap);
		
		mSrcRectF = new RectF();
		mDesRectF = new RectF();
		mMatrix = new Matrix();
		mPressedPoint = new PointF();
		
		//create the loupe stroke path
		mLoupePath = new Path();
		mLoupeShadowPath = new Path();
		float co = mLoupeMargin;
		
		//outer loop with arrow
		mLoupePath.moveTo(0, -co-mLoupeArrowHeight); //lower-left after round corner
		mLoupePath.rLineTo(0, -(mLoupeHeight-2*co));
		mLoupePath.rQuadTo(0, -co, co, -co);
		mLoupePath.rLineTo(mLoupeWidth-2*co, 0);
		mLoupePath.rQuadTo(co, 0, co, co);
		mLoupePath.rLineTo(0, mLoupeHeight-2*co);
		mLoupePath.rQuadTo(0, co, -co, co);
		mLoupePath.rLineTo(-(mLoupeWidth-2*co-mLoupeArrowHeight)/2, 0);
		mLoupePath.rLineTo(-mLoupeArrowHeight/2, mLoupeArrowHeight/2);
		mLoupePath.rLineTo(-mLoupeArrowHeight/2, -mLoupeArrowHeight/2);
		mLoupePath.rLineTo(-(mLoupeWidth-2*co-mLoupeArrowHeight)/2, 0);
		mLoupePath.rQuadTo(-co, 0, -co, -co);
		mLoupePath.close();
		
		mLoupeShadowPath.set(mLoupePath);
		
		//inner loop
		mLoupePath.moveTo(mLoupeMargin, -mLoupeMargin-co-mLoupeArrowHeight);
		mLoupePath.rLineTo(0, -(mLoupeHeight-2*co-2*mLoupeMargin));
		mLoupePath.rQuadTo(0, -co, co, -co);
		mLoupePath.rLineTo(mLoupeWidth-2*co-2*mLoupeMargin, 0);
		mLoupePath.rQuadTo(co, 0, co, co);
		mLoupePath.rLineTo(0, mLoupeHeight-2*co-2*mLoupeMargin);
		mLoupePath.rQuadTo(0, co, -co, co);
		mLoupePath.rLineTo(-mLoupeWidth+2*co+2*mLoupeMargin, 0);
		mLoupePath.rQuadTo(-co, 0, -co, -co);
		mLoupePath.close();
		
		mLoupePath.setFillType(Path.FillType.EVEN_ODD);
	}
	
	public int getMode() {
		return ToolManager.e_text_select;
	}
	
	public void onCreate() {
		super.onCreate();
		mMenuTitles = new LinkedList<String>();
		mMenuTitles.add("Copy");
		mMenuTitles.add("Highlight");
		mMenuTitles.add("Underline");
		mMenuTitles.add("Crossout");
		mMenuTitles.add("Squiggly");
	}
	
	
	public void onCustom(Object o) {
		mNextToolMode = ToolManager.e_pan;
	}
	
	
	public boolean onDown(MotionEvent e) {
		float x = e.getX() + mPDFView.getScrollX();
		float y = e.getY() + mPDFView.getScrollY();
		mPressedPoint.x = x;
		mPressedPoint.y = y;
		mBeingPressed = true;
		
		//test if one of the two select widgets are hit
		mEffSelWidgetId = hitTest(x, y);
		
		if ( mEffSelWidgetId >= 0 ) {
			//update station point that is the starting selection point.
			x = (mSelWidgets[1-mEffSelWidgetId].mStrPt.x + mSelWidgets[1-mEffSelWidgetId].mEndPt.x)/2; 
			y = (mSelWidgets[1-mEffSelWidgetId].mStrPt.y + mSelWidgets[1-mEffSelWidgetId].mEndPt.y)/2;
			mStationPt.set(x, y);
			
			//show loupe
			setLoupeInfo(e.getX(), e.getY());
			mInvalidateBBox.left = (int)mLoupeBBox.left - (int)Math.ceil(mLoupeShadowFactor) - 1 ;
			mInvalidateBBox.top = (int)mLoupeBBox.top - 1;
			mInvalidateBBox.right = (int)Math.ceil(mLoupeBBox.right) + (int)Math.ceil(mLoupeShadowFactor) + 1;
			mInvalidateBBox.bottom = (int)Math.ceil(mLoupeBBox.bottom) + (int)Math.ceil(1.5f * mLoupeShadowFactor) + 1;
			mPDFView.invalidate(mInvalidateBBox);
		}
		
		return false;
	}
	
	public boolean onScaleEnd(float x, float y) {
		super.onScaleEnd(x, y);
		mScaled = true;
		return false;
	}
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		if ( mPDFView.hasSelection() ) {
			mPagePresModeWhileSelected = mPDFView.getPagePresentationMode();
			mSelWidgetEnabled = true;
			
			if ( mScaled
				 || prior_event_type == PDFViewCtrl.PRIOR_EVENT_SCROLLING 
				 || prior_event_type == PDFViewCtrl.PRIOR_EVENT_PINCH 
				 || prior_event_type == PDFViewCtrl.PRIOR_EVENT_DBLTAP
				 || (mBeingLongPressed && prior_event_type != PDFViewCtrl.PRIOR_EVENT_FLING)) {
				
				if ( mScaled || prior_event_type == PDFViewCtrl.PRIOR_EVENT_PINCH || 
					 prior_event_type == PDFViewCtrl.PRIOR_EVENT_DBLTAP) {
					//after zooming, re-populate the selection result
					mSelPath.reset();
					populateSelectionResult();
				}
					
				float sx = mPDFView.getScrollX();
				float sy = mPDFView.getScrollY();
				
				showMenu(mMenuTitles, new RectF(mSelBBox.left-sx, mSelBBox.top-sy, mSelBBox.right-sx, mSelBBox.bottom-sy));
			}
		}
		
		mScaled = false;
		mBeingLongPressed = false;
		mBeingPressed = false;
		mEffSelWidgetId = -1;
		mPDFView.invalidate(); //always needed to draw away the previous loupe even if there is not any selection.
		
		return false;
	}
	
	public boolean onFlingStop() {
		if ( mPDFView.hasSelection() ) {
			float sx = mPDFView.getScrollX();
			float sy = mPDFView.getScrollY();
			showMenu(mMenuTitles, new RectF(mSelBBox.left-sx, mSelBBox.top-sy, mSelBBox.right-sx, mSelBBox.bottom-sy));
		}
		return false;
	}
	
	private void setLoupeInfo(float touch_x, float touch_y) {
		float sx = mPDFView.getScrollX();
		float sy = mPDFView.getScrollY();
		
		//note that the bbox of the loupe has to take into account the following factors:
		//1. loupe arrow
		//2. boundary line thickness
		float left = touch_x + sx - (float)mLoupeWidth/2  - mLoupeThickness/2;
		float right = left + mLoupeWidth + mLoupeThickness;
		float top = touch_y + sy - mLoupeHeight * 1.45f - mLoupeArrowHeight - mLoupeThickness/2;
		float bottom = top + mLoupeHeight + mLoupeArrowHeight + mLoupeThickness;
		
		mLoupeBBox.set(left, top, right, bottom);
	}
	
	public void selectText(float x1, float y1, float x2, float y2, boolean by_rect) {
		if ( by_rect ) {
			float delta = 0.01f;
			x2 += delta;
			y2 += delta;
			delta *= 2;
			x1 = x2 - delta >= 0 ? x2 - delta : 0;
			y1 = y2 - delta >= 0 ? y2 - delta : 0;
		}
		
		//clear pre-selected content
		mPDFView.clearSelection();
		boolean had_sel = !mSelPath.isEmpty();
		mSelPath.reset();
		
		//select text
		try {
			//locks the document first as accessing annotation/doc information isn't thread safe.
			mPDFView.lockDoc(true);
			if ( by_rect ) {
				mPDFView.selectByRect(x1, y1, x2, y2);
			}
			else {
				mPDFView.selectByStruct(x1, y1, x2, y2);
			}
		}
		catch (Exception e) {
		}
		finally {
			mPDFView.unlockDoc();
		}
		
		//update the bounding box that should include:
		//(1)previous selection bbox and loupe bbox
		//(2)current selection bbox and loupe bbox
		if ( had_sel ) {
			mTempRect.set(mSelBBox);
		}
		populateSelectionResult();
		if ( !had_sel ) {
			mTempRect.set(mSelBBox);
		}
		else {
			mTempRect.union(mSelBBox);
		}
			
		mTempRect.union(mLoupeBBox);
		setLoupeInfo(x2, y2);
		mTempRect.union(mLoupeBBox);
						
		mInvalidateBBox.left = (int)mTempRect.left - (int)Math.ceil(mLoupeShadowFactor) - 1;
		mInvalidateBBox.top = (int)mTempRect.top - 1;
		mInvalidateBBox.right = (int)Math.ceil(mTempRect.right) + (int)Math.ceil(mLoupeShadowFactor) + 1;
		mInvalidateBBox.bottom = (int)Math.ceil(mTempRect.bottom) + (int)Math.ceil(1.5f*mLoupeShadowFactor) + 1;
	}
	
	public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
		float sx = mPDFView.getScrollX();
		float sy = mPDFView.getScrollY();
		mPressedPoint.x = e2.getX() + sx;
		mPressedPoint.y = e2.getY() + sy;
		
		if ( mEffSelWidgetId < 0 ) {
			if ( mBeingLongPressed ) {
				//select single word using rectangular selection
				selectText(0, 0, e2.getX(), e2.getY(), true);
				mPDFView.invalidate(mInvalidateBBox);
				return true;
			}
			else {
				showTransientPageNumber();
				return false; //just scroll
			}
		}
		else {
			//structural selection
			selectText(mStationPt.x-sx, mStationPt.y-sy, e2.getX(), e2.getY(), false);
			mPDFView.invalidate(mInvalidateBBox);
			return true;
		}
	}
	

	public void onLayout (boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		if ( mPDFView.hasSelection() ) {
			//after relayout, need to adjust the positions of the selection and menu;
			//but, if page presentation has changed from continuous to non-continuous, just
			//in case the selection crosses pages, remove all the text selection.
			if ( mPDFView.isContinuousPagePresentationMode(mPagePresModeWhileSelected) &&
				 !mPDFView.isContinuousPagePresentationMode(mPDFView.getPagePresentationMode())) {
				mPDFView.clearSelection();
				if ( isMenuShown() ) {
					closeMenu();
				}
				mSelPath.reset();
				mNextToolMode = ToolManager.e_pan;
				return;
			}
			
			mSelPath.reset();
			populateSelectionResult();
			mPDFView.invalidate();
			
			if ( isMenuShown() ) {
				closeMenu();
				float sx = mPDFView.getScrollX();
				float sy = mPDFView.getScrollY();
				showMenu(mMenuTitles, new RectF(mSelBBox.left-sx, mSelBBox.top-sy, mSelBBox.right-sx, mSelBBox.bottom-sy));
			}
		}
	}
	
	public boolean onLongPress(MotionEvent e) {
		mNextToolMode = ToolManager.e_text_select;
		mBeingLongPressed = true;
		mBeingPressed = true;
		
		float sx = mPDFView.getScrollX();
		float sy = mPDFView.getScrollY();
		mPressedPoint.x = e.getX() + sx;
		mPressedPoint.y = e.getY() + sy;
		
		if ( mEffSelWidgetId < 0 ) {
			//if there is no effective selection widget, select a single word. need to clear the existing selection info first.
			mEffSelWidgetId = -1;
			mSelWidgetEnabled = false;
			selectText(0, 0, e.getX(), e.getY(), true);
			mPDFView.invalidate(mInvalidateBBox);
			
			if (!mPDFView.hasSelection()) {
				//nothing selected, go back to pan mode.
				mNextToolMode = ToolManager.e_pan;
			}
			
		}
		else {
			//do nothing and wait for onMove event
		}
		
		return false;
	}
	
	public boolean onSingleTapConfirmed(MotionEvent e) {
		super.onSingleTapConfirmed(e);
		
		//dis-select text
		mNextToolMode = ToolManager.e_pan;
		mPDFView.clearSelection();
		mEffSelWidgetId = -1;
		mSelWidgetEnabled = false;
		if ( !mSelPath.isEmpty() ) {
			//clear the path data for highlighting
			mSelPath.reset();
			mPDFView.invalidate(mInvalidateBBox);
		}
		
		return false;
	}
	
	private int hitTest(float x, float y) {
		//test if one of the two selection widgets is hit; if 
		//so, return the selection widget id. during the onMove()
		//gesture afterwards, we can select properly.
		float dist = -1;
		int id = -1;
		for ( int i = 0; i < 2; ++i ) {
			float s = x - mSelWidgets[i].mEndPt.x; 
			float t = y - mSelWidgets[i].mEndPt.y;
			float d = (float)Math.sqrt(s*s + t*t);
			if ( d < mTSWidgetRadius * 4 ) {
				if ( dist < 0 || dist > d ) {
					dist = d;
					id = i;
				}
			}
		}
		return id;
	}
	
	private void populateSelectionResult() {
		float sx = mPDFView.getScrollX();
		float sy = mPDFView.getScrollY();
		int sel_pg_begin = mPDFView.getSelectionBeginPage();
		int sel_pg_end = mPDFView.getSelectionEndPage();
		float min_x = 1E10f, min_y = 1E10f, max_x = 0, max_y = 0;
		boolean has_sel = false;
		
		//loop through the pages that have text selection, and construct 'mSelPath' for highlighting.
		//NOTE: android has a bug that if hardware acceleration is turned on and the path is too big,
		//it may not get rendered. See http://code.google.com/p/android/issues/detail?id=24023
		for ( int pg = sel_pg_begin; pg <= sel_pg_end; ++pg )  {
			PDFViewCtrl.Selection sel = mPDFView.getSelection(pg); //each Selection may have multiple quads 
			double[] quads = sel.getQuads();
			double[] pts;
			int sz = quads.length / 8;	//each quad has eight numbers (x0, y0), ... (x3, y3)
			
			if ( sz == 0 ) {
				continue;
			}
			int k = 0;
			float x, y;
			for (int i = 0; i < sz; ++i, k+=8) {
				has_sel = true;
				
				pts = mPDFView.convPagePtToClientPt(quads[k], quads[k + 1], pg);
				x = (float) pts[0] + sx;
				y = (float) pts[1] + sy;
				mSelPath.moveTo(x, y);
				min_x = min_x > x ? x : min_x;
				max_x = max_x < x ? x : max_x;
				min_y = min_y > y ? y : min_y;
				max_y = max_y < y ? y : max_y;
			
				if ( pg == sel_pg_begin && i == 0 ) {
					//set the start point of the first selection widget that is based 
					//on the first quad point.
					mSelWidgets[0].mStrPt.set(x-mTSWidgetThickness/2, y); 
					x -= mTSWidgetThickness + mTSWidgetRadius;
					min_x = min_x > x ? x : min_x;
					max_x = max_x < x ? x : max_x;
				}  
			
				pts = mPDFView.convPagePtToClientPt(quads[k + 2], quads[k + 3], pg);
				x = (float) pts[0] + sx;
				y = (float) pts[1] + sy;
				mSelPath.lineTo(x, y);
				min_x = min_x > x ? x : min_x;
				max_x = max_x < x ? x : max_x;
				min_y = min_y > y ? y : min_y;
				max_y = max_y < y ? y : max_y;
			
				if ( pg == sel_pg_end && i == sz-1 ) {
					//set the end point of the second selection widget that is based 
					//on the last quad point.
					mSelWidgets[1].mEndPt.set(x+mTSWidgetThickness/2, y); 
					x += mTSWidgetThickness + mTSWidgetRadius;
					y += mTSWidgetRadius * 2;
					min_x = min_x > x ? x : min_x;
					max_x = max_x < x ? x : max_x;
					min_y = min_y > y ? y : min_y;
					max_y = max_y < y ? y : max_y;
				}  

				pts = mPDFView.convPagePtToClientPt(quads[k + 4], quads[k + 5],	pg);
				x = (float) pts[0] + sx;
				y = (float) pts[1] + sy;
				mSelPath.lineTo(x, y);
				min_x = min_x > x ? x : min_x;
				max_x = max_x < x ? x : max_x;
				min_y = min_y > y ? y : min_y;
				max_y = max_y < y ? y : max_y;
			
				if ( pg == sel_pg_end && i == sz-1 ) {
					//set the start point of the second selection widget that is based 
					//on the last quad point.
					mSelWidgets[1].mStrPt.set(x+mTSWidgetThickness/2, y);
					x += mTSWidgetThickness + mTSWidgetRadius;
					min_x = min_x > x ? x : min_x;
					max_x = max_x < x ? x : max_x;
				}

				pts = mPDFView.convPagePtToClientPt(quads[k + 6], quads[k + 7],	pg);
				x = (float) pts[0] + sx;
				y = (float) pts[1] + sy;
				mSelPath.lineTo(x, y);
				min_x = min_x > x ? x : min_x;
				max_x = max_x < x ? x : max_x;
				min_y = min_y > y ? y : min_y;
				max_y = max_y < y ? y : max_y;

				if ( pg == sel_pg_begin && i == 0 ) {
					//set the end point of the first selection widget that is based 
					//on the first quad point.
					mSelWidgets[0].mEndPt.set(x-mTSWidgetThickness/2, y); 
					x -= mTSWidgetThickness + mTSWidgetRadius;
					y -= mTSWidgetRadius * 2;
					min_x = min_x > x ? x : min_x;
					max_x = max_x < x ? x : max_x;
					min_y = min_y > y ? y : min_y;
					max_y = max_y < y ? y : max_y;
				}  
			
				mSelPath.close();			
			}
		}
		
		if ( has_sel ) {
			mSelBBox.set(min_x, min_y, max_x, max_y);
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void onQuickMenuClicked(int menu_id, String menu_title) {
		if ( mPDFView.hasSelection() ) {
			int sel_pg_begin = mPDFView.getSelectionBeginPage();
			int sel_pg_end = mPDFView.getSelectionEndPage();
			String str = new String(menu_title);
			str = str.toLowerCase();
			
			if ( str.equals("copy") ) {
				String text = new String();
				for ( int pg = sel_pg_begin; pg <= sel_pg_end; ++pg )  {
					PDFViewCtrl.Selection sel = mPDFView.getSelection(pg);
					String t = sel.getAsUnicode();
					text += t;
				}
				
				if ( android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
					android.text.ClipboardManager mgr = (android.text.ClipboardManager) mPDFView.getContext().getSystemService(Context.CLIPBOARD_SERVICE );
					if ( mgr != null ) {
						mgr.setText(text);
					}
				}
				else {
					android.content.ClipboardManager mgr = (android.content.ClipboardManager) mPDFView.getContext().getSystemService(Context.CLIPBOARD_SERVICE );
					if ( mgr != null ) {
						mgr.setText(text);
					}
				}
			}
			
			else if ( str.equals("highlight") || str.equals("underline") ||
					  str.equals("crossout") || str.equals("squiggly")) {
				LinkedList<pdftron.PDF.Rect> rect_list = new LinkedList<pdftron.PDF.Rect>();
				try {
					mPDFView.lockDoc(true);
					PDFDoc doc = mPDFView.getDoc();
					for ( int pg = sel_pg_begin; pg <= sel_pg_end; ++pg )  {
						PDFViewCtrl.Selection sel = mPDFView.getSelection(pg);
						double[] quads = sel.getQuads();
						int sz = quads.length / 8;
						if ( sz == 0 ) {
							continue;
						}
						
						Point p1 = new Point();
						Point p2 = new Point();
						Point p3 = new Point();
						Point p4 = new Point();
						QuadPoint qp = new QuadPoint(p1, p2, p3, p4);
						
						TextMarkup tm = new TextMarkup();
						pdftron.PDF.Rect bbox = new pdftron.PDF.Rect(quads[0], quads[1], quads[4], quads[5]); //just use the first quad to temporarily populate the bbox
						if (str.equals("highlight")) {
							tm = Highlight.create(doc, bbox);
						}
						else if (str.equals("underline")) {
							tm = Underline.create(doc, bbox);
						}
						else if (str.equals("crossout")) {
							tm = StrikeOut.create(doc, bbox);
						}
						else if (str.equals("squiggly")) {
							tm = Squiggly.create(doc, bbox);
						}
						
						int k = 0;
						for ( int i = 0; i < sz; ++i, k+=8 ) {
							p1.x = quads[k]; 
							p1.y = quads[k+1];
							
							p2.x = quads[k+2]; 
							p2.y = quads[k+3];
							
							p3.x = quads[k+4]; 
							p3.y = quads[k+5];
							
							p4.x = quads[k+6]; 
							p4.y = quads[k+7];
							
							qp.p1 = p1;
							qp.p2 = p2;
							qp.p3 = p3;
							qp.p4 = p4;
							
							tm.setQuadPoint(i, qp);
						}
						
						ColorPt color = new ColorPt(1.0, 1.0, 0);
						tm.setColor(color, 3);
						tm.refreshAppearance();
						Page page = mPDFView.getDoc().getPage(pg);
						page.annotPushBack(tm);
						
						//compute the bbox of the annotation in screen space
						pdftron.PDF.Rect r = tm.getRect();
						pdftron.PDF.Rect ur = new pdftron.PDF.Rect();
						r.normalize();
						double[] pts = new double[2];
						pts = mPDFView.convPagePtToClientPt(r.getX1(), r.getY2(), pg);
						ur.setX1(pts[0]);
						ur.setY1(pts[1]);
						pts = mPDFView.convPagePtToClientPt(r.getX2(), r.getY1(), pg);
						ur.setX2(pts[0]);
						ur.setY2(pts[1]);
						rect_list.add(ur);
					}
					
					//clear existing selections
					mEffSelWidgetId = -1;
					mSelWidgetEnabled = false;
					if ( !mSelPath.isEmpty() ) {
						mSelPath.reset();
					}
					mPDFView.clearSelection();
				}
				catch (PDFNetException e) {
				}
				finally {
					mPDFView.unlockDoc();
				}

				Iterator<pdftron.PDF.Rect> itr = rect_list.iterator();
				while(itr.hasNext()) {
					pdftron.PDF.Rect t = itr.next();
					mPDFView.update(t);
				}
				
				//after highlighting, register a custom callback, in which will
				//switch to pan tool.
				mPDFView.postToolOnCustomEvent(null);
				
				mPDFView.waitForRendering();
			}
		}
	} 
	
	
	public void onDraw (Canvas canvas, Matrix tfm) {
		if ( !mDrawingLoupe ) {
			super.onDraw(canvas, tfm);
		}
		
		//check if need to draw loupe
		boolean draw_loupe = false;
		
		if ( !mDrawingLoupe && 									//prevent recursive calling
			 (mEffSelWidgetId >= 0 || mBeingLongPressed ) ) { 	//show loupe either when being long pressed or a selection widget is effective
			mDrawingLoupe = true;	
			
			//make the drawn portion half the size of the loupe so as to achieve magnifying effect
			float left = mPressedPoint.x - mLoupeBBox.width()/4;
			float top = mPressedPoint.y - mLoupeBBox.height()/4;
			float right = left + mLoupeBBox.width()/2;
			float bottom = top + mLoupeBBox.height()/2;
			
			mSrcRectF.set(left, top, right, bottom);
			mDesRectF.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
			mMatrix.setRectToRect(mSrcRectF, mDesRectF, Matrix.ScaleToFit.CENTER);
			
			mCanvas.save();
			mCanvas.setMatrix(mMatrix);
			mPDFView.draw(mCanvas);
			mCanvas.restore();
			
			mDrawingLoupe = false;
			draw_loupe = true;
		}
		
		//draw the highlight quads
		if ( !mSelPath.isEmpty() ) {
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.rgb(0, 100, 175));
			mPaint.setAlpha(127);
			canvas.drawPath(mSelPath, mPaint);
			
			//draw the two selection widgets
			if ( mSelWidgetEnabled ) {
				mPaint.setColor(Color.rgb(255, 128, 0));
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setStrokeWidth(mTSWidgetThickness);
				float x1 = mSelWidgets[0].mStrPt.x;
				float y1 = mSelWidgets[0].mStrPt.y;
				float x2 = mSelWidgets[0].mEndPt.x;
				float y2 = mSelWidgets[0].mEndPt.y;
				canvas.drawLine(x1, y1, x2, y2, mPaint);
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(x2, y2-mTSWidgetRadius, mTSWidgetRadius, mPaint);
				
				x1 = mSelWidgets[1].mStrPt.x;
				y1 = mSelWidgets[1].mStrPt.y;
				x2 = mSelWidgets[1].mEndPt.x;
				y2 = mSelWidgets[1].mEndPt.y;
				mPaint.setStyle(Paint.Style.STROKE);
				canvas.drawLine(x1, y1, x2, y2, mPaint);
				mPaint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(x2, y2+mTSWidgetRadius, mTSWidgetRadius, mPaint);
			}
		}
		
		//draw loupe content
		if ( draw_loupe ) {
			//shadow
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.BLACK);
			mPaint.setStrokeWidth(0);
			mLoupeShadowPath.offset(mLoupeBBox.left, mLoupeBBox.bottom);
			mPaint.setShadowLayer(mLoupeShadowFactor - 1, 0.0F, mLoupeShadowFactor/2, 0x96000000);
			boolean ha = mPDFView.isHardwareAccelerated();
			if ( ha ) {
				Path p = new Path();
				p.addPath(mLoupeShadowPath);
				canvas.drawPath(p, mPaint);
			}
			else {
				canvas.drawPath(mLoupeShadowPath, mPaint);
			}
			mPaint.clearShadowLayer();
			mLoupeShadowPath.offset(-mLoupeBBox.left, -mLoupeBBox.bottom);
			
			//magnified bitmap
			canvas.drawBitmap(mBitmap, mLoupeBBox.left + mLoupeMargin, mLoupeBBox.top + mLoupeMargin, null);
			
			//outer and inner boundaries
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.WHITE);
			mLoupePath.offset(mLoupeBBox.left, mLoupeBBox.bottom);
			if ( ha ) {
				Path p = new Path();
				p.addPath(mLoupePath);
				p.setFillType(Path.FillType.EVEN_ODD);
				canvas.drawPath(p, mPaint);
			}
			else {
				canvas.drawPath(mLoupePath, mPaint);
			}
			
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.BLACK);
			mPaint.setStrokeWidth(mLoupeThickness);
			if ( ha ) {
				Path p = new Path();
				p.addPath(mLoupePath);
				p.setFillType(Path.FillType.EVEN_ODD);
				canvas.drawPath(p, mPaint);
			}
			else {
				canvas.drawPath(mLoupePath, mPaint);
			}
			mLoupePath.offset(-mLoupeBBox.left, -mLoupeBBox.bottom);	
		}
	}
}
