//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import java.util.LinkedList;
import java.util.List;

import pdftron.PDF.Annot;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

/**
 * The base class that implements the PDFViewCtrl.Tool interface and several
 * basic tool functionalities.
 */
abstract class Tool implements PDFViewCtrl.Tool{
	public static final String PREFS_FILE_NAME = "com_pdftron_pdfnet_pdfviewctrl_prefs_file";
	protected PDFViewCtrl mPDFView;
	protected int mNextToolMode;
	protected Annot mAnnot;
	protected int mAnnotPageNum;
	protected RectF mAnnotBBox;		//in page space
	protected QuickMenu mQuickMenu;
	protected LinkedList<String> mMenuTitles;
	protected Paint mPaint4PageNum;
	protected boolean mJustSwitchedFromAnotherTool;
	protected boolean mMenuShown;
	protected boolean mShowPageNum;
	protected float mPageNumPosAdjust;
	protected RectF mTempPageDrawingRectF;
	protected final float mTextSize;
	protected final float mTextVOffset;
	private Matrix mTempMtx1;
	private Matrix mTempMtx2;
	
	/*
	 * Used to remove the shown page number
	 */
	private Handler mPageNumberRemovalHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mShowPageNum = false;
			mPDFView.invalidate();
		}
	};	
	
	
	/**
	 * Constructor.
	 */
	public Tool(PDFViewCtrl ctrl){
		mPDFView = ctrl;
		mNextToolMode = ToolManager.e_pan;
		mAnnot = null;
		mAnnotBBox = new RectF();
		mJustSwitchedFromAnotherTool = false;
		mMenuShown = false;
		mPageNumPosAdjust = 0;
		mShowPageNum = false;
		mTempPageDrawingRectF = new RectF();
		
		mTextSize = convDp2Pix(15);
		mTextVOffset = convDp2Pix(50);
		mPaint4PageNum = new Paint();
		mPaint4PageNum.setAntiAlias(true);
		mPaint4PageNum.setTextSize(mTextSize);
		mPaint4PageNum.setStyle(Paint.Style.FILL);
		
		mTempMtx1 = new Matrix();
		mTempMtx2 = new Matrix();
		
		//disable page turning (in non-continuous page presentation mode);
		//it is only turned on in Pan tool.
		mPDFView.setBuiltInPageSlidingEnabled(false);
	}
	
	
	/**
	 * Implements the PDFViewCtrl.Tool interfaces
	 */
	public abstract int getMode();
	
	
	final public int getNextToolMode() {
		return mNextToolMode;
	}
	
	
	public boolean onDown(MotionEvent e) {
		return false;
	}
	
	
	public void onDocumentDownloadEvent(int type, int page_num, int page_downloaded, int page_count, String message) {
//		if ( type == PDFViewCtrl.DOWNLOAD_START ) {
//			Log.v("PDFNet", "start downloading");
//		}
//		else if (type == PDFViewCtrl.DOWNLOAD_PAGE ) {
//			Log.v("PDFNet", "downloaded page " + page_num + ", percentage: " + (float)page_downloaded/page_count);
//		}
//		else if (type == PDFViewCtrl.DOWNLOAD_FINISH ) {
//			Log.v("PDFNet", "finished downloading.");
//		}
//		else if (type == PDFViewCtrl.DOWNLOAD_FAIL) {
//		    Log.v("PDFNet", "download fail: " + message);
//		}
	}
	
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		mPageNumberRemovalHandler.sendEmptyMessageDelayed(1, 3000);
		return false;
	}
	
	
	public boolean onFlingStop() {
		return false;
	}
	
	
	public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
		showTransientPageNumber();
		return false;
	}
	
	
	public void onPageTurning(int old_page, int cur_page) {
	}
	
	
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}
	
	
	public void onPostSingleTapConfirmed() {
	}
	
	
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	
	public boolean onDoubleTap(MotionEvent e) {
		showTransientPageNumber();
		
		//the following code shows how to override the double tap behavior of PDFViewCtrl.
		boolean customize = false;
		if ( !customize ) {
			//let PDFViewCtrl handle how double tap zooms
			return false;
		}
		else {
			//I want to customize how double tap zooms
			boolean use_snapshot = true;
			mPDFView.setZoom(mPDFView.getZoom()/2, use_snapshot);
			return true;	//this tells PDFViewCtrl to skip its internal logic
		}
	}
	
	
	public void onDoubleTapEnd(MotionEvent e) {
	}
	
	
	public void onLayout (boolean changed, int l, int t, int r, int b) {
	}
	
	
	public boolean onLongPress(MotionEvent e) {
		return false;
	}
	
	
	public boolean onScaleBegin(float x, float y) {
		//(x, y) is the scaling focal point in client space
		return false;
	}
	
	
	public boolean onScale(float x, float y) {
		//(x, y) is the scaling focal point in client space
		return false;
	}
	
	
	public boolean onScaleEnd(float x, float y) {
		//(x, y) is the scaling focal point in client space
		showTransientPageNumber();
		return false;
	}
	
	
	public boolean onShowPress(MotionEvent e) {
		return false;
	}
	
	
	public void onClose() {
		closeMenu();
	}
	
	
	public void onCustom(Object o) {
	}
	
	
	public void onSetDoc() {
	}
	
	
	/**
	 * Called after the tool is created by PDFViewCtrlManager. 
	 */
	public void onCreate() 
	{
		if (mShowPageNum) {
			showTransientPageNumber();
		}
	}
	
	
	public void onDraw(Canvas canvas, android.graphics.Matrix tfm) {
		//draw page number
		if ( mShowPageNum ) {
			int width = mPDFView.getWidth();
			int height = mPDFView.getHeight();
			int page_num = mPDFView.getCurrentPage();
			boolean restore = false;
			int yOffset = 0;
			
			try {
				//during page sliding, PDFViewCtrl might apply extra transformation
				//matrix to Canvas for animation. However, page number should not
				//move; hence applying the inverse to offset it.
				if ( !tfm.isIdentity() ) {
					canvas.save();
					restore = true;
					tfm.invert(mTempMtx1);
					canvas.getMatrix(mTempMtx2);
					mTempMtx2.postConcat(mTempMtx1);
					canvas.setMatrix(mTempMtx2);
					
                    // Workaround for bug found in Android > ICS with hardware acceleration turned ON.
                    // See http://code.google.com/p/android/issues/detail?id=24517 for more info.
                    // Using canvas.translate helps to avoid the problem, but it is still possible to
                    // see a one or two pixels offset when drawing the page indicator during sliding.
                    // Using the height of the status bar as an offset resolves the issue without any
                    // differences between page sliding or just scrolling the page.
                    //float[] values = new float[9];
                    //tfm.getValues(values);
                    //canvas.translate(values[2] * -1f, 1f);
					if (Build.VERSION.SDK_INT >= 14 /*Build.VERSION_CODES.ICE_CREAM_SANDWICH*/ &&
                            mPDFView.isHardwareAccelerated()) {
                        Rect rectgle = new Rect();
                        ((android.app.Activity)mPDFView.getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(rectgle);
                        yOffset = rectgle.top;
                    }
				}
				
				String str = new String();
				int page_count = mPDFView.getDoc().getPageCount();
				str += page_num + " of " + page_count;
				
				Rect r = new Rect();
				mPaint4PageNum.getTextBounds(str, 0, str.length(), r);
				float str_width = r.width();
				float str_height = r.height();
				
				float margin = str_height/1.5f;
				float left = width - str_width * 1.5f - margin + mPDFView.getScrollX();
				
				float top = mPDFView.getScrollY() + height - mPageNumPosAdjust - str_height * 3.0f + yOffset;
				
				float right = left + str_width + margin * 2;
				float bottom = top + str_height + margin * 2;
				
				mTempPageDrawingRectF.set(left, top, right, bottom);
				mPaint4PageNum.setColor(Color.rgb(75, 75, 75));
				mPaint4PageNum.setAlpha(200);
				canvas.drawRoundRect(mTempPageDrawingRectF, margin, margin, mPaint4PageNum);
				
				mPaint4PageNum.setAlpha(255);
				mPaint4PageNum.setColor(Color.WHITE);
				left += margin;
				top += str_height/2 + margin + mPaint4PageNum.descent();
				
				canvas.drawText(str, left, top, mPaint4PageNum);
			}
			catch (Exception e) {
			}
			finally {
				if ( restore ) {
					canvas.restore();
				}
			}
		}
	}
	
	
	public void setJustCreatedFromAnotherTool() {
		mJustSwitchedFromAnotherTool = true;
	}
	
	
	public void closeMenu() {
		if ( mQuickMenu != null ) {
			mMenuShown = false;
			mQuickMenu.dismiss();
		}
	}
	
	protected void onQuickMenuClicked(int menu_id, String menu_title) {
	}
	
	
	protected boolean isMenuShown() {
		return mMenuShown;
	}
	
	
	protected void showTransientPageNumber() {
		mPageNumberRemovalHandler.removeMessages(1);
		mShowPageNum = true;
		mPageNumberRemovalHandler.sendEmptyMessageDelayed(1, 3000);
		mPDFView.invalidate();
	}
	
	
	/**
	 * Build the bounding box of the annotation.
	 */
	protected void buildAnnotBBox() {
		if ( mAnnot != null ) {
			mAnnotBBox.set(0, 0, 0, 0);
			try {
				pdftron.PDF.Rect r = mAnnot.getRect();;
				mAnnotBBox.set((float)r.getX1(), (float)r.getY1(), (float)r.getX2(), (float)r.getY2());
			} 
			catch (Exception e) {
			}
		}
	}
	
	protected boolean isInsideAnnot(float screen_x, float screen_y)  {
		if ( mAnnot != null ) {
			double [] pts = mPDFView.convClientPtToPagePt((double)screen_x, (double)screen_y, mAnnotPageNum);
			if ( mAnnotBBox.contains((float)pts[0], (float)pts[1]) ) {
				return true;
			}
		}
		return false;
	}
	
	protected RectF getAnnotRect() {
		if ( mAnnot != null ) {
			double [] pts1 = mPDFView.convPagePtToClientPt(mAnnotBBox.left, mAnnotBBox.bottom, mAnnotPageNum);
			double [] pts2 = mPDFView.convPagePtToClientPt(mAnnotBBox.right, mAnnotBBox.top, mAnnotPageNum);
			RectF r = new RectF((float)pts1[0], (float)pts1[1], (float)pts2[0], (float)pts2[1]);
			return r;
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Shows the quick menu.
	 */
	public boolean showMenu(List<String> menu_titles, RectF anchor_rect) {
		int menu_sz = menu_titles.size();
		if ( menu_sz > 0 && anchor_rect != null ) {
			if ( mQuickMenu != null ) {
				closeMenu();
				mQuickMenu = null;
			}
			
			RectF client_r = new RectF(0, 0, mPDFView.getWidth(), mPDFView.getHeight());
			if ( !client_r.intersect(anchor_rect)) {
				return false;
			}
			int[] location = new int[2];
			mPDFView.getLocationOnScreen(location);
			View anchor = new View(mPDFView.getContext());
	        anchor.setVisibility(View.INVISIBLE);
	        int atop = (int)anchor_rect.top+location[1];
	        int aleft = (int)anchor_rect.left+location[0];
	        int aright = (int)anchor_rect.right+location[0];
	        int abottom = (int)anchor_rect.bottom+location[1];
			anchor.layout(aleft, atop, aright, abottom);
			
			mQuickMenu = new QuickMenu(anchor, atop, aleft, abottom, aright, menu_titles, 
					new PopupWindow.OnDismissListener() {
							public void onDismiss() {
								//when dismissed, trigger the menu-clicked call-back function.
								mMenuShown = false;
								if ( mQuickMenu != null ) {
									int selected = mQuickMenu.getSelectedId();
									if ( selected >= 0 ) {
										onQuickMenuClicked(selected, mQuickMenu.getSelectedTitle());
									}
								}
							}
					},
					mPDFView.isHardwareAccelerated());
			mQuickMenu.show();
			mMenuShown = true;
			return true;
		}
		return false;
	}
	
	
	/**
	 * Computes the page bounding box in the client space.
	 */
	protected RectF buildPageBoundBoxOnClient(int page_num) {
		RectF rect = null;
		if ( page_num >= 1 ) {
			try {
				mPDFView.lockReadDoc();
				Page page = mPDFView.getDoc().getPage(page_num);
				if ( page != null ) {
					rect = new RectF();
					pdftron.PDF.Rect r = page.getCropBox();
					
					double x1 = r.getX1();
					double y1 = r.getY1();
					double x2 = r.getX2();
					double y2 = r.getY2();
					double [] pts1, pts2, pts3, pts4; 
					
					//need to compute the transformed coordinates for the four
					//corners of the page bounding box, since a page can be rotated.
					pts1 = mPDFView.convPagePtToClientPt(x1, y1, page_num);
					pts2 = mPDFView.convPagePtToClientPt(x2, y1, page_num);
					pts3 = mPDFView.convPagePtToClientPt(x2, y2, page_num);
					pts4 = mPDFView.convPagePtToClientPt(x1, y2, page_num);
					
					double min_x = Math.min(Math.min(Math.min(pts1[0], pts2[0]), pts3[0]), pts4[0]);
					double max_x = Math.max(Math.max(Math.max(pts1[0], pts2[0]), pts3[0]), pts4[0]);
					double min_y = Math.min(Math.min(Math.min(pts1[1], pts2[1]), pts3[1]), pts4[1]);
					double max_y = Math.max(Math.max(Math.max(pts1[1], pts2[1]), pts3[1]), pts4[1]);
					
					float sx = mPDFView.getScrollX();
					float sy = mPDFView.getScrollY();
					rect = new RectF();
					rect.set((float)min_x+sx, (float)min_y+sy, (float)max_x+sx, (float)max_y+sy);
				}
			}
			catch (Exception e) {
			}
			finally {
				mPDFView.unlockReadDoc();
			}
		}
		return rect;
	}
	
	
	/**
	 * Converts density independent pixels to physical pixels.
	 */
	protected float convDp2Pix(float dp) {
		float density = mPDFView.getContext().getResources().getDisplayMetrics().density;
		return dp * density;
	}

	
	/**
	 * Converts physical pixels to density independent pixels.
	 */
	protected float convPix2Dp(float pix) {
		float density = mPDFView.getContext().getResources().getDisplayMetrics().density;
		return pix / density;
	}
	
	/**
	 * Gets a rectangle to use when selecting text.
	 */
	protected RectF getTextSelectRect(float x, float y) {
		float delta = 0.5f;
		float x2 = x + delta;
		float y2 = y + delta;
		delta *= 2;
		float x1 = x2 - delta >= 0 ? x2 - delta : 0;
		float y1 = y2 - delta >= 0 ? y2 - delta : 0;
		
		RectF r = new RectF(x1, y1, x2, y2);
		return r;
	}
}
