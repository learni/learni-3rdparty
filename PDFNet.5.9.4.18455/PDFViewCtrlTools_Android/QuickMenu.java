//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;


/**
 * This class implements the quick menu for tools. A quick menu is a popup window 
 * that contains a set of buttons. A quick menu is able to adjust its position and
 * size to fit in the screen.
 */
class QuickMenu extends PopupWindow{
	
	//class for the ensure menu bar
	class MenuView extends View
	{
		//class for a menu button
		class MenuBtn {
			private RectF mRectF;
			private String mLabel;
			private boolean mPressed;
			private float mTextWidth;
			private boolean mHasULCorner;
			private boolean mHasURCorner;
			private boolean mHasLRCorner;
			private boolean mHasLLCorner;
			
			public MenuBtn() {
				mPressed = false;
			}
			
			public void setLabel(String str) {
				mLabel = str;
			}
			
			public void setRectF(RectF r) {
				mRectF = r;
			}
			
			public void setPressed(boolean pressed) {
				mPressed = pressed;
			}
			
			public boolean getPressed() {
				return mPressed;
			}
			
			public String getLabel() {
				return mLabel;
			}
			
			public RectF getRectF() {
				return mRectF;
			}
			
			public boolean pressed(float x, float y) {
				return mRectF.contains(x, y);
			}
			
			public void setTextWidth(float w) {
				mTextWidth = w;
			}
			
			public float getTextWidth() {
				return mTextWidth;
			}
			
			public boolean hasULCorner() {
				return mHasULCorner;
			}
			
			public boolean hasURCorner() {
				return mHasURCorner;
			}
			
			public boolean hasLRCorner() {
				return mHasLRCorner;
			}
			
			public boolean hasLLCorner() {
				return mHasLLCorner;
			}
			
			public void setCornerInfo(boolean ul, boolean ur, boolean lr, boolean ll) {
				mHasULCorner = ul;
				mHasURCorner = ur;
				mHasLRCorner = lr;
				mHasLLCorner = ll;
			}
		}
		
		private Context mContext;
		private boolean mArrowAtBottom;
		private float mArrowX;				//x-coordinate of arrow	
		private MenuBtn[] mBtns;
		private float mHPadding;			//horizontal padding at the two ends of the buttons, reserve space for shadows 
		private float mVPadding;			//vertical padding at the two ends of the buttons, reserve space arrow 
		private float mBtnHPadding;			
		private float mBtnVPadding;
		private float mArrowHeight;
		private float mCornerRadius;	
		private float mBtnSepOffset;		//offset for drawing the separator between buttons.
		private float mStrokeWidth;
		private float mShadowFactor;
		private Paint mPaint;
		private RectF mBtnBBox;				//bounding box that enclose all the buttons.
		private float mTextSize;
		private int mWidth;					//width of the entire menu bar: all buttons + horizontal paddings
		private int mHeight;				//height of the entire menu bar: all buttons + vertical paddings 
		  
		public MenuView(Context context, ArrayList<String> titles) {
			super(context);
			mContext = context;
			
			//initialize some settings
			mWidth = 0;
			mHeight = 0;
			
			float adjust = 1.0f;
			DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
			int sd = Math.min(dm.widthPixels, dm.heightPixels);
			if ( sd <= 500 ) {
				adjust = 0.75f;
			}
			mTextSize = cmptInvariantSize(mContext, 15*adjust);
			mCornerRadius = cmptInvariantSize(mContext, 10*adjust);
			mHPadding = cmptInvariantSize(mContext, 16*adjust);
			mVPadding = cmptInvariantSize(mContext, 22*adjust);
			mArrowHeight = cmptInvariantSize(mContext, 12*adjust);
			mBtnHPadding = cmptInvariantSize(mContext, 12*adjust);
			mBtnVPadding = cmptInvariantSize(mContext, 4*adjust);
			mShadowFactor = cmptInvariantSize(mContext, 5*adjust);
			mBtnSepOffset = cmptInvariantSize(mContext, 1);
			mStrokeWidth = cmptInvariantSize(mContext, 1);
						
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setTextSize(mTextSize);
			
			//construct the menu buttons
			int sz = titles.size();
			if ( sz > 0 ) {
				mBtns = new MenuBtn[sz];
				float left = mHPadding, right;
				float top = mVPadding;
				float bottom = top + 3f * mTextSize;
				mBtnBBox = new RectF(left, top, left, bottom);
				
				float width = 0;
				for ( int i = 0; i < sz; ++i) {
					MenuBtn btn = new MenuBtn();
					btn.setLabel(titles.get(i));
					float text_width = mPaint.measureText(titles.get(i));
					float btn_width = text_width + mBtnHPadding * 2;
					//each button cannot be smaller than 6 times corner radius; so if there is only one button,
					//arrow won't be placed at the corners.
					btn_width = (btn_width < 6*mCornerRadius) ? (6*mCornerRadius) : btn_width;  
					right = left + btn_width;
					RectF r = new RectF(left, top, right, bottom);
					btn.setRectF(r); 
					btn.setTextWidth(text_width);
					boolean has_ul = false, has_ur = false, has_lr = false, has_ll = false;
					if ( i == 0 ) {
						has_ul = true;
						has_ll = true;
					}
					if ( i == sz - 1 ) {
						has_ur = true;
						has_lr = true;
					}
					btn.setCornerInfo(has_ul, has_ur, has_lr, has_ll);
					mBtns[i] = btn;
					
					width += r.width();
					mBtnBBox.right = right;
					left = right;
				}
				mWidth = (int)(2*mHPadding + width + 0.5f);
				mHeight = (int)(2*mVPadding + mBtns[0].getRectF().height() + 0.5f);
			}
		}
		
		public void setArrowInfo(float x, boolean arrow_at_bottom) {
			//ensure the arrow's x position should go beyond the rounded corners.
			if ( x < 4 * mCornerRadius )
			{
				x = 4 * mCornerRadius;
			}
			if ( x > mWidth - 4 * mCornerRadius) {
				x = mWidth - 4 * mCornerRadius; 
			}
			mArrowX = x;
			mArrowAtBottom = arrow_at_bottom;
		}
		
		private float cmptInvariantSize(Context context, float size) {
			float density = mContext.getResources().getDisplayMetrics().density;
			return size * density;
		}
		  
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(mWidth, mHeight);
		}
		
		public int getSelectedMenuId() {
			int sz = mBtns.length;
			for ( int i = 0; i < sz; ++i ) {
				if ( mBtns[i].getPressed() ) {
					return i;
				}
			}
			return -1;
		}
		
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();
			
			//find the pressed button, if any
			int sz = mBtns.length;
			int hit_btn = -1;
			for ( int i = 0; i < sz; ++i ) {
				if ( mBtns[i].pressed(x, y) ) {
					hit_btn = i;
					break;
				}
			}
			
			//handle events
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//a menu button is pressed
				if ( hit_btn >= 0 ) {
					mFirstPressedId = hit_btn;
					mBtns[mFirstPressedId].setPressed(true);
				}
				invalidate();
				break;
				
			case MotionEvent.ACTION_UP:
				//if the same button, dismiss
				if ( mFirstPressedId >= 0 && hit_btn == mFirstPressedId ) {
					dismiss();
				}
				else {
					invalidate();
				}
				break;
				
			case MotionEvent.ACTION_CANCEL:
				// JB delivers ACTION_CANCEL before executing dismiss(), which breaks
				// our logic if we treat it in the default case. If this happens,
				// we just ignore this event. See
				// http://developer.android.com/reference/android/view/MotionEvent.html
				break;				

			default:
				if ( mFirstPressedId >= 0 && hit_btn == mFirstPressedId ) {
					mBtns[hit_btn].setPressed(true);
				}
				else if (mFirstPressedId >= 0 ) {
					mBtns[mFirstPressedId].setPressed(false);
				}
				
				invalidate();
				break;
			}
			
			return true;
		}
		
		private Path makeRoundRect(RectF r, float co, boolean has_ul, boolean has_ur, boolean has_lr, boolean has_ll,
				boolean has_arrow, boolean arrow_at_bottom, float arrow_x, float arrow_height) {
			float top = r.top;
			float left = r.left;
			float width = r.width();
			float height = r.height();
			
			Path path = new Path();
			
			//1: move to upper-left corner after the round corner
			path.moveTo(left + co, top);	

			//2: draw the upper part
			if ( has_arrow && !arrow_at_bottom ) {
				path.rLineTo(arrow_x-left-co-arrow_height, 0);		//draw to lower-left corner of upper arrow
				path.rLineTo(arrow_height, -arrow_height);			//to the tip of upper arrow
				path.rLineTo(arrow_height, arrow_height);			//to the lower-right corner of upper arrow
				path.rLineTo(left+width-co-arrow_x-arrow_height, 0);//to the upper-right corner before the round corner
			}
			else {
				path.rLineTo(width - 2*co, 0);		//to the upper-right corner before the round corner
			}
			
			//3: upper-right corner
			if ( has_ur ) {
				//if rounded
				path.rQuadTo(co, 0, co, co);			//to the upper-right corner after the round corner
			}
			else {
				//if sharp
				path.rLineTo(co, 0);
				path.rLineTo(0, co);
			}
			
			//4: right part
			path.rLineTo(0, height - 2*co);			//to the lower-right corner before the round corner
			
			//5: lower-right corner
			if ( has_lr ) {
				//if rounded
				path.rQuadTo(0, co, -co, co);			//to the lower-right corner after the round corner
			}
			else {
				//if sharp
				path.rLineTo(0, co);
				path.rLineTo(-co, 0);
			}
			
			//6: draw the lower part
			if ( has_arrow && arrow_at_bottom ) {
				float dx = arrow_x - (left+width) + co + arrow_height;
				path.rLineTo(dx, 0);								//draw to lower-right corner of lower arrow
				path.rLineTo(-arrow_height, arrow_height);			//to the tip of lower arrow
				path.rLineTo(-arrow_height, -arrow_height);			//to the upper-left corner of lower arrow
				path.rLineTo(left+co-arrow_x+arrow_height, 0);		//to the upper-right corner before the round corner
			}
			else {
				path.rLineTo(-(width - 2*co), 0);	//to the lower-left corner before the round corner
			}
			
			//7: lower-left corner
			if ( has_ll) {
				//if rounded
				path.rQuadTo(-co, 0, -co, -co);
			}
			else {
				//if sharp
				path.rLineTo(-co, 0);
				path.rLineTo(0, -co);
				
			}
			
			//8: left part
			path.rLineTo(0, -(height - 2*co));
			
			//9: upper-left corner
			if ( has_ul ) {
				//rounded
				path.rQuadTo(0, -co, co, -co);
			}
			else {
				//sharp
				path.rLineTo(0, -co);
				path.rLineTo(co, 0);
			}
			
			path.close();
			return path;
		}
		  
		protected void onDraw(Canvas canvas) {
			
			//draw the surrounding path of all the buttons + arrow
			Path view_path = makeRoundRect(mBtnBBox, mCornerRadius, true, true, true, true, true, mArrowAtBottom, mArrowX, mArrowHeight);
			LinearGradient localLinearGradient = new LinearGradient(0.0F, mBtnBBox.top,
					0.0F, mBtnBBox.bottom, 0xFF808080, 0xFF000000, Shader.TileMode.CLAMP);
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setShader(localLinearGradient);
			
			if ( !mNoShadowLayer ) {
				mPaint.setShadowLayer(mShadowFactor-1, 0.0F, mShadowFactor/2, 0xFF000000);
			}
			
			canvas.drawPath(view_path, mPaint);
			mPaint.setShader(null);
			mPaint.clearShadowLayer();

			mPaint.setFakeBoldText(true);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(0.5f);
			mPaint.setColor(0xFF000000);
			if ( mNoShadowLayer ) {
				Path p = new Path();
				p.addPath(view_path);
				canvas.drawPath(p, mPaint);
			}
			else {
				canvas.drawPath(view_path, mPaint);
			}

			// draw text and button separators and pressed button background
			mPaint.setStrokeWidth(mStrokeWidth);
			int sz = mBtns.length;
			float x;
			float y = mHeight/2 + mPaint.descent();
			for (int i = 0; i < sz; ++i) {
				MenuBtn btn = mBtns[i];
				RectF r = btn.getRectF();
				mPaint.setStyle(Paint.Style.FILL);
				
				if ( btn.getPressed() ) {
					//draw background of selected button
					//shrink the button a bit
					RectF pr = new RectF(r.left+mBtnSepOffset, r.top+mBtnSepOffset, r.right-mBtnSepOffset, r.bottom-mBtnSepOffset);
					mPaint.setColor(Color.rgb(255, 175, 0));
					boolean ul = btn.hasULCorner();
					boolean ur = btn.hasURCorner();
					boolean lr = btn.hasLRCorner();
					boolean ll = btn.hasLLCorner();
					if ( ul || ur || lr || ll ) {
						Path btn_path = makeRoundRect(pr, mCornerRadius, ul, ur, lr, ll, false, false, 0, 0);
						canvas.drawPath(btn_path, mPaint);
					}
					else {
						canvas.drawRect(pr, mPaint);
					}
				}
				
				//draw buttons' text
				mPaint.setColor(Color.WHITE);
				x = r.left + (r.width()-btn.getTextWidth())/2;
				canvas.drawText(btn.getLabel(), x, y, mPaint);

				//draw button separator
				if (i != sz - 1) {
					mPaint.setStyle(Paint.Style.STROKE);
					x = r.right;
					mPaint.setColor(Color.BLACK);
					canvas.drawLine(x-mBtnSepOffset/2, r.top+mBtnVPadding, x-mBtnSepOffset/2, r.bottom - mBtnVPadding, mPaint);
					mPaint.setColor(Color.rgb(200, 200, 200));
					canvas.drawLine(x+mBtnSepOffset/2, r.top+mBtnVPadding, x+mBtnSepOffset/2, r.bottom - mBtnVPadding, mPaint);
				}
			}
		}
	}
	
	protected final View mAnchor;
	protected int mAnchorLeft;
	protected int mAnchorTop;
	protected int mAnchorRight;
	protected int mAnchorBottom;
	private MenuView mMenuView;
	protected final WindowManager mWndMgr;
	private ArrayList<String> mMenuTitles;
	private int mFirstPressedId = -1;
	private boolean mNoShadowLayer;
	
	//explicitly pass in anchor locations because it seems that ICS has a bug when mAnchor.getLocationOnScreen() is called.
	public QuickMenu(View anchor, int anchor_top, int anchor_left, int anchor_bottom, int anchor_right, 
			List<String> menu_titles, PopupWindow.OnDismissListener listener, boolean ha) {
		super(anchor.getContext());
		mAnchor = anchor;
		mAnchorLeft = anchor_left;
		mAnchorTop = anchor_top;
		mAnchorRight = anchor_right;
		mAnchorBottom = anchor_bottom;
		mMenuTitles = new ArrayList<String>(menu_titles);
		mNoShadowLayer = false;
		
		if ( android.os.Build.VERSION.SDK_INT < 14 && ha ) {
			//it seems that the shadow layer doesn't work properly when hardware acceleration is on before ICS.
			//http://android-developers.blogspot.com/2011_03_01_archive.html
			mNoShadowLayer = true;
		}
				
		setBackgroundDrawable(new BitmapDrawable());		//needed so when touched outside, it closes automatically. 
		setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		//setFocusable(true);	//cannot be set in order to be able to focus on the parent view directly.
	    setTouchable(true);
	    setOutsideTouchable(true);
	    setAnimationStyle(R.style.Animation_Dialog);
	    setOnDismissListener(listener);

		mWndMgr = (WindowManager) anchor.getContext().getSystemService(Context.WINDOW_SERVICE);
		
		mMenuView = new MenuView(anchor.getContext(), mMenuTitles);
		setContentView(mMenuView);
		mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mMenuView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	public void show () {
		int[] screen_location = new int[2];
		//mAnchor.getLocationOnScreen(screen_location); //this seems to be wrong in ICS, don't use it.
		screen_location[0] = mAnchorLeft;
		screen_location[1] = mAnchorTop;
		
		Rect arect = new Rect(screen_location[0], screen_location[1], 
				screen_location[0] + (mAnchorRight-mAnchorLeft), 
				screen_location[1] + (mAnchorBottom-mAnchorTop));

		int sw = mWndMgr.getDefaultDisplay().getWidth();		//screen width
		int sh = mWndMgr.getDefaultDisplay().getHeight();		//screen height
		int vw = mMenuView.getMeasuredWidth();					//menu view width
		int vh = mMenuView.getMeasuredHeight();					//menu view height
		int al = arect.left < 0 ? 0 : arect.left;				//anchor view left
		int ar = arect.right > sw ? sw : arect.right;			//anchor view right
		int at = arect.top < 0 ? 0 : arect.top;					//anchor view top
		int ab = arect.bottom > sh ? sh : arect.bottom;			//anchor view bottom
		int aw = ar - al;										//anchor view width
		//int ah = ab - at;										//anchor view height
		
		//adjust the horizontal position
		int left = 0, right = vw;
		if ( sw > vw ) {
			//if screen width is greater than the view width
			left = al + aw/2 - vw/2;
			right = left + vw;
			if ( left >= 0 && right <= sw ) {
				//can center the menu at the center of the annotation
			}
			else if ( left < 0 ) {
				left = 0;
				right = vw;
			} else if ( right > sw ) {
				right = sw;
				left = sw - vw;
			}
		}
		
		//adjust the vertical position
		int top = 0, bottom = vh;
		boolean arrow_at_bottom = true;
		if ( sh > vh ) {
			top = at - vh;
			bottom = top + vh;
			if ( top >= 0 ) {
				//can be placed at the top of the annotation
			}
			else {
				top = ab;
				bottom = top + vh;
				arrow_at_bottom = false;
			}
			
			if ( bottom > sh ) {
				arrow_at_bottom = true;
				top = sh/2;
				bottom = top + vh;
			}
		}
		
		//compute the arrow anchor position
		float ax = (al + ar) / 2 - left;	//arrow tries to point to the center of the annotation
		mMenuView.setArrowInfo(ax, arrow_at_bottom);
		
		//show the menu
		showAtLocation(mAnchor, Gravity.NO_GRAVITY, left, top);
	}
	
	public int getSelectedId() {
		return mMenuView.getSelectedMenuId();
		
	}
	
	public String getSelectedTitle() {
		if ( mMenuTitles != null ) {
			int id = mMenuView.getSelectedMenuId();
			return mMenuTitles.get(id);
		}
		else {
			return null;
		}
	}
}
