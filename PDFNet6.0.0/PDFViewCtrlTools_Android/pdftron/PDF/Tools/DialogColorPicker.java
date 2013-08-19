/*
 * Copyright (C) 2010 Daniel Nilsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pdftron.PDF.Tools;

import pdftron.PDF.Tools.ColorPickerView.OnColorChangedListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

class DialogColorPicker extends AlertDialog implements
		ColorPickerView.OnColorChangedListener {

	private ColorPickerView mColorPicker;
	private ColorPanelView mOldColor;
	private ColorPanelView mNewColor;
	private OnColorChangedListener mListener;
	private Context mContext;
	private TextView mText;

	public DialogColorPicker(Context context, int initialColor) {
		super(context);
		mContext = context;

		init(initialColor);
	}

	private void init(int color) {
		// To fight color branding.
		getWindow().setFormat(PixelFormat.RGBA_8888);

		setUp(color);

	}

    private void setUp(int color) {
		try {
			setTitle("Pick a Color");
			
			int margin = 10;
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			margin = (int)Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, margin, metrics));
			
			
			//This is the layout xml that the following code tries to implement.
			/*
			<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
			    android:layout_width="fill_parent"
			    android:layout_height="fill_parent">
					
				<pdftron.colorpicker.ColorPickerView
					android:id="@+id/color_picker_view"
					android:layout_width="fill_parent"
					android:layout_height="wrap_content"
					android:layout_centerHorizontal="true"
					android:layout_marginLeft="10dp"
					android:layout_marginRight="10dp"
				/>
				
				<LinearLayout
				android:layout_width="fill_parent"
				android:layout_height="40dp"
				android:orientation="horizontal"
				android:layout_below="@id/color_picker_view"
				android:layout_alignLeft="@id/color_picker_view"
				android:layout_alignRight="@id/color_picker_view"
				android:layout_marginTop="10dp">

				
					<pdftron.colorpicker.ColorPanelView
						android:id="@+id/old_color_panel"
						android:layout_width="0px"
						android:layout_height="fill_parent"
						android:layout_weight="0.5"
					/>
				
					<TextView
						android:layout_width="wrap_content"
						android:layout_height="fill_parent"	
						android:text=\u2192
						android:textSize="20sp"
						android:gravity="center"
						android:textColor="#ffffff"
						android:layout_marginLeft="10dp"
						android:layout_marginRight="10dp"
					/>
				
					<pdftron.colorpicker.ColorPanelView
						android:id="@+id/new_color_panel"
						android:layout_width="0px"
						android:layout_height="fill_parent"
						android:layout_weight="0.5"
					/>
				
				</LinearLayout>
				
			</RelativeLayout>
			*/
			
			RelativeLayout rlayout = new RelativeLayout(mContext);
			rlayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			setView(rlayout);
			
			mColorPicker = new ColorPickerView(mContext);
			mColorPicker.setId(1);
			RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			rp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			rp.setMargins(margin, 0, margin, 0);
			mColorPicker.setLayoutParams(rp);
			// Workaround for bug in color picker: https://code.google.com/p/color-picker-view/issues/detail?id=4
			if (android.os.Build.VERSION.SDK_INT >= 11) {    //Build.VERSION_CODES.HONEYCOMB)
			    mColorPicker.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			}
			
			LinearLayout llayout = new LinearLayout(mContext);
			llayout.setOrientation(LinearLayout.HORIZONTAL);
			rp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, margin*4);
			rp.addRule(RelativeLayout.BELOW, mColorPicker.getId());
			rp.addRule(RelativeLayout.ALIGN_LEFT, mColorPicker.getId());
			rp.addRule(RelativeLayout.ALIGN_RIGHT, mColorPicker.getId());
			rp.setMargins(0, margin, 0, 0);
			llayout.setLayoutParams(rp);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 0.5f);
			mOldColor = new ColorPanelView(mContext);
			mNewColor = new ColorPanelView(mContext);
			mOldColor.setLayoutParams(lp);
			mNewColor.setLayoutParams(lp);
			
			mText = new TextView(mContext);
			String str = new String(" " + "\u2192" + " "); //rightwards arrow
			mText.setText(str);
			mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			mText.setTextColor(0xFFFFFFFF);
			RelativeLayout.LayoutParams rp4 = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			rp4.setMargins(margin, 0, margin, 0);
			mText.setLayoutParams(rp4);
			
			llayout.addView(mOldColor);
			llayout.addView(mText);
			llayout.addView(mNewColor);
			rlayout.addView(mColorPicker);
			rlayout.addView(llayout);
	
			((LinearLayout) mOldColor.getParent()).setPadding(Math
					.round(mColorPicker.getDrawingOffset()), 0, Math
					.round(mColorPicker.getDrawingOffset()), 0);
	
			mColorPicker.setOnColorChangedListener(this);
	
			mOldColor.setColor(color);
			mColorPicker.setColor(color, true);
		}
		catch (Exception e) {
		}

	}

	public void onColorChanged(int color) {
		mNewColor.setColor(color);

		if (mListener != null) {
			mListener.onColorChanged(color);
		}

	}

	public void setAlphaSliderVisible(boolean visible) {
		mColorPicker.setAlphaSliderVisible(visible);
	}

	public int getColor() {
		return mColorPicker.getColor();
	}
}


/**
 * This class draws a panel which which will be filled with a color which can be set.
 * It can be used to show the currently selected color which you will get from
 * the {@link ColorPickerView}.
 * @author Daniel Nilsson
 *
 */
class ColorPanelView extends android.view.View{

	/**
	 * The width in pixels of the border 
	 * surrounding the color panel.
	 */
	private final static float	BORDER_WIDTH_PX = 1;
	
	private static float mDensity = 1f;
	
	private int 		mBorderColor = 0xff6E6E6E;
	private int 		mColor = 0xff000000;
	
	private Paint		mBorderPaint;
	private Paint		mColorPaint;
	
	private RectF		mDrawingRect;
	private RectF		mColorRect;

	private AlphaPatternDrawable mAlphaPattern;
	
	
	public ColorPanelView(Context context){
		this(context, null);
	}
	
	public ColorPanelView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public ColorPanelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}
	
	private void init(){
		mBorderPaint = new Paint();
		mColorPaint = new Paint();
		mDensity = getContext().getResources().getDisplayMetrics().density;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		final RectF	rect = mColorRect;
				
		if(BORDER_WIDTH_PX > 0){
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(mDrawingRect, mBorderPaint);		
		}
		
		if(mAlphaPattern != null){
			mAlphaPattern.draw(canvas);
		}
					
		mColorPaint.setColor(mColor);
		
		canvas.drawRect(rect, mColorPaint);
	}
		
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mDrawingRect = new RectF();		
		mDrawingRect.left =  getPaddingLeft();
		mDrawingRect.right  = w - getPaddingRight();
		mDrawingRect.top = getPaddingTop();
		mDrawingRect.bottom = h - getPaddingBottom();
		
		setUpColorRect();
		
	}
	
	private void setUpColorRect(){
		final RectF	dRect = mDrawingRect;		
		
		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX;
		float right = dRect.right - BORDER_WIDTH_PX;
		
		mColorRect = new RectF(left,top, right, bottom);
		
		mAlphaPattern = new AlphaPatternDrawable((int)(5 * mDensity));
		
		mAlphaPattern.setBounds(Math.round(mColorRect.left), 
				Math.round(mColorRect.top), 
				Math.round(mColorRect.right), 
				Math.round(mColorRect.bottom));
		
	}
	
	/**
	 * Set the color that should be shown by this view.
	 * @param color
	 */
	public void setColor(int color){
		mColor = color;
		invalidate();
	}
	
	/**
	 * Get the color currently show by this view.
	 * @return
	 */
	public int getColor(){
		return mColor;
	}
	
	/**
	 * Set the color of the border surrounding the panel.
	 * @param color
	 */
	public void setBorderColor(int color){
		mBorderColor = color;
		invalidate();
	}

	/**
	 * Get the color of the border surrounding the panel.
	 */
	public int getBorderColor(){
		return mBorderColor;
	}
	
}


/**
 * Displays a color picker to the user and allow them
 * to select a color. A slider for the alpha channel is
 * also available. Enable it by setting 
 * setAlphaSliderVisible(boolean) to true.
 * @author Daniel Nilsson
 */
class ColorPickerView extends android.view.View{

	public interface OnColorChangedListener{
		public void onColorChanged(int color);		
	}
		
	private final static int	PANEL_SAT_VAL = 0;
	private final static int	PANEL_HUE = 1;
	private final static int	PANEL_ALPHA = 2;
	
	/**
	 * The width in pixels of the border 
	 * surrounding all color panels.
	 */
	private final static float	BORDER_WIDTH_PX = 1;
	
	/**
	 * The width in dp of the hue panel.
	 */
	private float 		HUE_PANEL_WIDTH = 30f;	
	/**
	 * The height in dp of the alpha panel 
	 */
	private float		ALPHA_PANEL_HEIGHT = 20f;
	/**
	 * The distance in dp between the different
	 * color panels.
	 */
	private float 		PANEL_SPACING = 10f;	
	/**
	 * The radius in dp of the color palette tracker circle.
	 */
	private float 		PALETTE_CIRCLE_TRACKER_RADIUS = 5f;
	/**
	 * The dp which the tracker of the hue or alpha panel
	 * will extend outside of its bounds.
	 */
	private float		RECTANGLE_TRACKER_OFFSET = 2f;
	
	
	private static float mDensity = 1f;
	
	private OnColorChangedListener	mListener;
	
	private Paint 		mSatValPaint;
	private Paint		mSatValTrackerPaint;
	
	private Paint		mHuePaint;
	private Paint		mHueTrackerPaint;
	
	private Paint		mAlphaPaint;
	private Paint		mAlphaTextPaint;
	
	private Paint		mBorderPaint;
		
	private Shader		mValShader;
	private Shader		mSatShader;
	private Shader		mHueShader;
	private Shader		mAlphaShader;
	
	private int			mAlpha = 0xff;
	private float		mHue = 360f;
	private float 		mSat = 0f;
	private float 		mVal = 0f;
	
	private String		mAlphaSliderText = "Alpha";	
	private int 		mSliderTrackerColor = 0xff1c1c1c;
	private int 		mBorderColor = 0xff6E6E6E;
	private boolean		mShowAlphaPanel = false;
	
	/*
	 * To remember which panel that has the "focus" when 
	 * processing hardware button data.
	 */
	private int			mLastTouchedPanel = PANEL_SAT_VAL;
	
	/**
	 * Offset from the edge we must have or else
	 * the finger tracker will get clipped when
	 * it is drawn outside of the view.
	 */
	private float 		mDrawingOffset;
	

	/*
	 * Distance form the edges of the view 
	 * of where we are allowed to draw.
	 */	
	private RectF	mDrawingRect;
			
	private RectF	mSatValRect;
	private RectF 	mHueRect;
	private RectF	mAlphaRect;
	
	private AlphaPatternDrawable	mAlphaPattern;
	
	private Point	mStartTouchPoint = null;
	
	
	public ColorPickerView(Context context){
		this(context, null);
	}
	
	public ColorPickerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
		
	private void init(){
		mDensity = getContext().getResources().getDisplayMetrics().density;
		PALETTE_CIRCLE_TRACKER_RADIUS *= mDensity;		
		RECTANGLE_TRACKER_OFFSET *= mDensity;
		HUE_PANEL_WIDTH *= mDensity;
		ALPHA_PANEL_HEIGHT *= mDensity;
		PANEL_SPACING = PANEL_SPACING * mDensity;
		
		mDrawingOffset = calculateRequiredOffset();
		
		initPaintTools();
		
		//Needed for receiving trackball motion events.
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	private void initPaintTools(){
		
		mSatValPaint = new Paint();
		mSatValTrackerPaint = new Paint();
		mHuePaint = new Paint();
		mHueTrackerPaint = new Paint();
		mAlphaPaint = new Paint();
		mAlphaTextPaint = new Paint();
		mBorderPaint = new Paint();
		
		
		mSatValTrackerPaint.setStyle(Style.STROKE);
		mSatValTrackerPaint.setStrokeWidth(2f * mDensity);
		mSatValTrackerPaint.setAntiAlias(true);
		
		mHueTrackerPaint.setColor(mSliderTrackerColor);
		mHueTrackerPaint.setStyle(Style.STROKE);
		mHueTrackerPaint.setStrokeWidth(2f * mDensity);
		mHueTrackerPaint.setAntiAlias(true);
		
		mAlphaTextPaint.setColor(0xff1c1c1c);
		mAlphaTextPaint.setTextSize(14f * mDensity);
		mAlphaTextPaint.setAntiAlias(true);
		mAlphaTextPaint.setTextAlign(Align.CENTER);
		mAlphaTextPaint.setFakeBoldText(true);
	
		
	}
	
	private float calculateRequiredOffset(){		
		float offset = Math.max(PALETTE_CIRCLE_TRACKER_RADIUS, RECTANGLE_TRACKER_OFFSET);
		offset = Math.max(offset, BORDER_WIDTH_PX * mDensity);
		
		return offset * 1.5f;	
	}
	
	private int[] buildHueColorArray(){
		
		int[] hue = new int[361];
		
		int count = 0;
		for(int i = hue.length -1; i >= 0; i--, count++){
			hue[count] = Color.HSVToColor(new float[]{i, 1f, 1f});
		}
		
		return hue;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(mDrawingRect.width() <= 0 || mDrawingRect.height() <= 0) return;
		
		drawSatValPanel(canvas);	
		drawHuePanel(canvas);
		drawAlphaPanel(canvas);
		
	}
	
	private void drawSatValPanel(Canvas canvas){

		final RectF	rect = mSatValRect;
				
		if(BORDER_WIDTH_PX > 0){			
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(mDrawingRect.left, mDrawingRect.top, rect.right + BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX, mBorderPaint);		
		}
			
		if (mValShader == null) {
			mValShader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, 
					0xffffffff, 0xff000000, TileMode.CLAMP);
		}
		
		int rgb = Color.HSVToColor(new float[]{mHue,1f,1f});
	
		mSatShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, 
				0xffffffff, rgb, TileMode.CLAMP);
		ComposeShader mShader = new ComposeShader(mValShader, mSatShader, PorterDuff.Mode.MULTIPLY);
		mSatValPaint.setShader(mShader);
		
		canvas.drawRect(rect, mSatValPaint);
	
		Point p = satValToPoint(mSat, mVal);
			
		mSatValTrackerPaint.setColor(0xff000000);
		canvas.drawCircle(p.x, p.y, PALETTE_CIRCLE_TRACKER_RADIUS - 1f * mDensity, mSatValTrackerPaint);
				
		mSatValTrackerPaint.setColor(0xffdddddd);
		canvas.drawCircle(p.x, p.y, PALETTE_CIRCLE_TRACKER_RADIUS, mSatValTrackerPaint);
			
	}
	
	private void drawHuePanel(Canvas canvas){
	
		final RectF rect = mHueRect;
		
		if(BORDER_WIDTH_PX > 0){
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(rect.left - BORDER_WIDTH_PX, 
					rect.top - BORDER_WIDTH_PX, 
					rect.right + BORDER_WIDTH_PX, 
					rect.bottom + BORDER_WIDTH_PX, 
					mBorderPaint);		
		}

		if (mHueShader == null) {
			mHueShader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, buildHueColorArray(), null, TileMode.CLAMP);
			mHuePaint.setShader(mHueShader);
		}
	
		canvas.drawRect(rect, mHuePaint);
		
		float rectHeight = 4 * mDensity / 2;
				
		Point p = hueToPoint(mHue);
				
		RectF r = new RectF();
		r.left = rect.left - RECTANGLE_TRACKER_OFFSET;
		r.right = rect.right + RECTANGLE_TRACKER_OFFSET;
		r.top = p.y - rectHeight;
		r.bottom = p.y + rectHeight;
		
		
		canvas.drawRoundRect(r, 2, 2, mHueTrackerPaint);
		
	}
	
	private void drawAlphaPanel(Canvas canvas){
		
		if(!mShowAlphaPanel || mAlphaRect == null || mAlphaPattern == null) return;
		
		final RectF rect = mAlphaRect;
		
		if(BORDER_WIDTH_PX > 0){
			mBorderPaint.setColor(mBorderColor);
			canvas.drawRect(rect.left - BORDER_WIDTH_PX, 
					rect.top - BORDER_WIDTH_PX, 
					rect.right + BORDER_WIDTH_PX, 
					rect.bottom + BORDER_WIDTH_PX, 
					mBorderPaint);		
		}
		
		
		mAlphaPattern.draw(canvas);
		
		float[] hsv = new float[]{mHue,mSat,mVal};
		int color = Color.HSVToColor(hsv);
		int acolor = Color.HSVToColor(0, hsv);
		
		mAlphaShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, 
				color, acolor, TileMode.CLAMP);
		
		
		mAlphaPaint.setShader(mAlphaShader);
		
		canvas.drawRect(rect, mAlphaPaint);
		
		if(mAlphaSliderText != null && mAlphaSliderText!= ""){
			canvas.drawText(mAlphaSliderText, rect.centerX(), rect.centerY() + 4 * mDensity, mAlphaTextPaint);
		}
		
		float rectWidth = 4 * mDensity / 2;
		
		Point p = alphaToPoint(mAlpha);
				
		RectF r = new RectF();
		r.left = p.x - rectWidth;
		r.right = p.x + rectWidth;
		r.top = rect.top - RECTANGLE_TRACKER_OFFSET;
		r.bottom = rect.bottom + RECTANGLE_TRACKER_OFFSET;
		
		canvas.drawRoundRect(r, 2, 2, mHueTrackerPaint);
		
	}
	
	
	private Point hueToPoint(float hue){
		
		final RectF rect = mHueRect;
		final float height = rect.height();
		
		Point p = new Point();
			
		p.y = (int) (height - (hue * height / 360f) + rect.top);
		p.x = (int) rect.left;
		
		return p;		
	}
	
	private Point satValToPoint(float sat, float val){
		
		final RectF rect = mSatValRect;
		final float height = rect.height();
		final float width = rect.width();
		
		Point p = new Point();
		
		p.x = (int) (sat * width + rect.left);
		p.y = (int) ((1f - val) * height + rect.top);
		
		return p;
	}
	
	private Point alphaToPoint(int alpha){
		
		final RectF rect = mAlphaRect;
		final float width = rect.width();
		
		Point p = new Point();
		
		p.x = (int) (width - (alpha * width / 0xff) + rect.left);
		p.y = (int) rect.top;
		
		return p;
	
	}
	
	private float[] pointToSatVal(float x, float y){
	
		final RectF rect = mSatValRect;
		float[] result = new float[2];
		
		float width = rect.width();
		float height = rect.height();
		
		if (x < rect.left){
			x = 0f;
		}
		else if(x > rect.right){
			x = width;
		}
		else{
			x = x - rect.left;
		}
				
		if (y < rect.top){
			y = 0f;
		}
		else if(y > rect.bottom){
			y = height;
		}
		else{
			y = y - rect.top;
		}
		
			
		result[0] = 1.f / width * x;
		result[1] = 1.f - (1.f / height * y);
		
		return result;	
	}
	
	private float pointToHue(float y){		
		
		final RectF rect = mHueRect;
		
		float height = rect.height();
		
		if (y < rect.top){
			y = 0f;
		}
		else if(y > rect.bottom){
			y = height;
		}
		else{
			y = y - rect.top;
		}
		
		return 360f - (y * 360f / height);
	}
	
	private int pointToAlpha(int x){
		
		final RectF rect = mAlphaRect;
		final int width = (int) rect.width();
		
		if(x < rect.left){
			x = 0;
		}
		else if(x > rect.right){
			x = width;
		}
		else{
			x = x - (int)rect.left;
		}
		
		return 0xff - (x * 0xff / width);
		
	}
	
			
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		
		float x = event.getX();
		float y = event.getY();
		
		boolean update = false;
		
		
		if(event.getAction() == MotionEvent.ACTION_MOVE){
			
			switch(mLastTouchedPanel){
			
			case PANEL_SAT_VAL:
				
				float sat, val;
				
				sat = mSat + x/50f;
				val = mVal - y/50f; 
				
				if(sat < 0f){
					sat = 0f;
				}
				else if(sat > 1f){
					sat = 1f;
				}
				
				if(val < 0f){
					val = 0f;
				}
				else if(val > 1f){
					val = 1f;
				}
				
				mSat = sat;
				mVal = val;
				
				update = true;
				
				break;
				
			case PANEL_HUE:
				
				float hue = mHue - y * 10f;
				
				if(hue < 0f){
					hue = 0f;
				}
				else if(hue > 360f){
					hue = 360f;
				}
				
				mHue = hue;
						
				update = true;
				
				break;
				
			case PANEL_ALPHA:
				
				if(!mShowAlphaPanel || mAlphaRect == null){
					update = false;
				}
				else{
					
					int alpha = (int) (mAlpha - x*10);
					
					if(alpha < 0){
						alpha = 0;
					}
					else if(alpha > 0xff){
						alpha = 0xff;
					}
					
					mAlpha = alpha;
					
					
					update = true;
				}
				
				break;
			}
			
			
		}
		
		
		if(update){
			
			if(mListener != null){
				mListener.onColorChanged(Color.HSVToColor(mAlpha, new float[]{mHue, mSat, mVal}));
			}
			
			invalidate();
			return true;
		}
		
	
		return super.onTrackballEvent(event);
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		boolean update = false;
				
		switch(event.getAction()){
		
		case MotionEvent.ACTION_DOWN:
			
			mStartTouchPoint = new Point((int)event.getX(), (int)event.getY());
			
			update = moveTrackersIfNeeded(event);
		
			break;
						
		case MotionEvent.ACTION_MOVE:
			
			update = moveTrackersIfNeeded(event);
		
			break;
			
		case MotionEvent.ACTION_UP:
			
			mStartTouchPoint = null;
					
			update = moveTrackersIfNeeded(event);
			
			break;
	
		}
		
		if(update){
			
			if(mListener != null){
				mListener.onColorChanged(Color.HSVToColor(mAlpha, new float[]{mHue, mSat, mVal}));
			}
			
			invalidate();
			return true;
		}
		
	
		return super.onTouchEvent(event);
	}
		
	private boolean moveTrackersIfNeeded(MotionEvent event){
		
		if(mStartTouchPoint == null) return false;
		
		boolean update = false;
		
		int startX = mStartTouchPoint.x;
		int startY = mStartTouchPoint.y;
		
		
		if(mHueRect.contains(startX, startY)){
			mLastTouchedPanel = PANEL_HUE;
			
			mHue = pointToHue(event.getY());
						
			update = true;
		}
		else if(mSatValRect.contains(startX, startY)){
							
			mLastTouchedPanel = PANEL_SAT_VAL;
			
			float[] result = pointToSatVal(event.getX(), event.getY());
			
			mSat = result[0];
			mVal = result[1];

			update = true;
		}
		else if(mAlphaRect != null && mAlphaRect.contains(startX, startY)){
			
			mLastTouchedPanel = PANEL_ALPHA;
			
			mAlpha = pointToAlpha((int)event.getX());
			
			update = true;
		}
		
		
		return update;
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int width = 0;
		int height = 0;
			
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		int widthAllowed = MeasureSpec.getSize(widthMeasureSpec);
		int heightAllowed = MeasureSpec.getSize(heightMeasureSpec);
		
		
		widthAllowed = chooseWidth(widthMode, widthAllowed);
		heightAllowed = chooseHeight(heightMode, heightAllowed);
		
		
		if(!mShowAlphaPanel){
			height = (int) (widthAllowed - PANEL_SPACING - HUE_PANEL_WIDTH);
			
			//If calculated height (based on the width) is more than the allowed height.
			if(height > heightAllowed){
				height = heightAllowed;
				width = (int) (height + PANEL_SPACING + HUE_PANEL_WIDTH);
			}
			else{
				width = widthAllowed;
			}
		}
		else{
			
			width = (int) (heightAllowed - ALPHA_PANEL_HEIGHT + HUE_PANEL_WIDTH);
			
			if(width > widthAllowed){
				width = widthAllowed;
				height = (int) (widthAllowed - HUE_PANEL_WIDTH + ALPHA_PANEL_HEIGHT);
			}
			else{
				height = heightAllowed;
			}
			
			
		}
		
	
		setMeasuredDimension(width, height);
	}
	
	private int chooseWidth(int mode, int size){
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPrefferedWidth();
		} 
	}
	
	private int chooseHeight(int mode, int size){
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else { // (mode == MeasureSpec.UNSPECIFIED)
			return getPrefferedHeight();
		} 
	}
	
	private int getPrefferedWidth(){
		
		int width = getPrefferedHeight();
		
		if(mShowAlphaPanel){
			width -= (PANEL_SPACING + ALPHA_PANEL_HEIGHT);
		}
		
		
		return (int) (width + HUE_PANEL_WIDTH + PANEL_SPACING);
		
	}
	
	private int getPrefferedHeight(){
		
		int height = (int)(200 * mDensity);
		
		if(mShowAlphaPanel){
			height += PANEL_SPACING + ALPHA_PANEL_HEIGHT;
		}
		
		return height;
	}
	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mDrawingRect = new RectF();		
		mDrawingRect.left = mDrawingOffset + getPaddingLeft();
		mDrawingRect.right  = w - mDrawingOffset - getPaddingRight();
		mDrawingRect.top = mDrawingOffset + getPaddingTop();
		mDrawingRect.bottom = h - mDrawingOffset - getPaddingBottom();
		
		setUpSatValRect();
		setUpHueRect();
		setUpAlphaRect();
	}
	
	private void setUpSatValRect(){
		
		final RectF	dRect = mDrawingRect;		
		float panelSide = dRect.height() - BORDER_WIDTH_PX * 2;
		
		if(mShowAlphaPanel){
			panelSide -= PANEL_SPACING + ALPHA_PANEL_HEIGHT;
		}
		
		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = top + panelSide;
		float right = left + panelSide;
		
		mSatValRect = new RectF(left,top, right, bottom);
	}
	
	private void setUpHueRect(){
		final RectF	dRect = mDrawingRect;		
		
		float left = dRect.right - HUE_PANEL_WIDTH + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX - (mShowAlphaPanel ? (PANEL_SPACING + ALPHA_PANEL_HEIGHT) : 0);
		float right = dRect.right - BORDER_WIDTH_PX;
		
		mHueRect = new RectF(left, top, right, bottom);
	}

	private void setUpAlphaRect(){
		
		if(!mShowAlphaPanel) return;
		
		final RectF	dRect = mDrawingRect;		
		
		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.bottom - ALPHA_PANEL_HEIGHT + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX;
		float right = dRect.right - BORDER_WIDTH_PX;
		
		mAlphaRect = new RectF(left, top, right, bottom);	
		
	
		mAlphaPattern = new AlphaPatternDrawable((int) (5 * mDensity));
		mAlphaPattern.setBounds(Math.round(mAlphaRect.left), Math
				.round(mAlphaRect.top), Math.round(mAlphaRect.right), Math
				.round(mAlphaRect.bottom));
		
		
		
	}
	
	
	/**
	 * Set a OnColorChangedListener to get notified when the color
	 * selected by the user has changed.
	 * @param listener
	 */
	public void setOnColorChangedListener(OnColorChangedListener listener){
		mListener = listener;
	}
	
	/**
	 * Set the color of the border surrounding all panels.
	 * @param color
	 */
	public void setBorderColor(int color){
		mBorderColor = color;
		invalidate();
	}
	
	/**
	 * Get the color of the border surrounding all panels.
	 */
	public int getBorderColor(){
		return mBorderColor;
	}
	
	/**
	 * Get the current color this view is showing.
	 * @return the current color.
	 */
	public int getColor(){
		return Color.HSVToColor(mAlpha, new float[]{mHue,mSat,mVal});
	}
	
	/**
	 * Set the color the view should show.
	 * @param color The color that should be selected.
	 */
	public void setColor(int color){
		setColor(color, false);
	}
	
	/**
	 * Set the color this view should show.
	 * @param color The color that should be selected.
	 * @param callback If you want to get a callback to
	 * your OnColorChangedListener.
	 */
	public void setColor(int color, boolean callback){
	
		int alpha = Color.alpha(color);
		int red = Color.red(color);
		int blue = Color.blue(color);
		int green = Color.green(color);
		
		float[] hsv = new float[3];
		
		Color.RGBToHSV(red, green, blue, hsv);

		mAlpha = alpha;
		mHue = hsv[0];
		mSat = hsv[1];
		mVal = hsv[2];
		
		if(callback && mListener != null){			
			mListener.onColorChanged(Color.HSVToColor(mAlpha, new float[]{mHue, mSat, mVal}));				
		}
		
		invalidate();
	}
	
	/**
	 * Get the drawing offset of the color picker view.
	 * The drawing offset is the distance from the side of
	 * a panel to the side of the view minus the padding.
	 * Useful if you want to have your own panel below showing
	 * the currently selected color and want to align it perfectly.
	 * @return The offset in pixels.
	 */
	public float getDrawingOffset(){
		return mDrawingOffset;
	}
	
	/**
	 * Set if the user is allowed to adjust the alpha panel. Default is false.
	 * If it is set to false no alpha will be set.
	 * @param visible
	 */
	public void setAlphaSliderVisible(boolean visible){
		
		if(mShowAlphaPanel != visible){
			mShowAlphaPanel = visible;
			
			/*
			 * Reset all shader to force a recreation. 
			 * Otherwise they will not look right after 
			 * the size of the view has changed.
			 */
			mValShader = null;
			mSatShader = null;
			mHueShader = null;
			mAlphaShader = null;;
			
			requestLayout();
		}
		
	}
	
	public void setSliderTrackerColor(int color){
		mSliderTrackerColor = color;

		mHueTrackerPaint.setColor(mSliderTrackerColor);		
		
		invalidate();
	}
	
	public int getSliderTrackerColor(){
		return mSliderTrackerColor;
	}
	
	/**
	 * Set the text that should be shown in the 
	 * alpha slider. Set to null to disable text.
	 * @param res string resource id.
	 */
	public void setAlphaSliderText(int res){		
		String text = getContext().getString(res);
		setAlphaSliderText(text);
	}
	
	/**
	 * Set the text that should be shown in the 
	 * alpha slider. Set to null to disable text.
	 * @param text Text that should be shown.
	 */
	public void setAlphaSliderText(String text){
		mAlphaSliderText = text;
		invalidate();
	}

	/**
	 * Get the current value of the text
	 * that will be shown in the alpha
	 * slider.
	 * @return
	 */
	public String getAlphaSliderText(){
		return mAlphaSliderText;
	}
}



/**
 * This drawable that draws a simple white and gray chessboard pattern.
 * It's pattern you will often see as a background behind a 
 * partly transparent image in many applications.
 * @author Daniel Nilsson
 */
class AlphaPatternDrawable extends Drawable {
	
	private int mRectangleSize = 10;

	private Paint mPaint = new Paint();
	private Paint mPaintWhite = new Paint();
	private Paint mPaintGray = new Paint();

	private int numRectanglesHorizontal;
	private int numRectanglesVertical;

	/**
	 * Bitmap in which the pattern will be cahched.
	 */
	private Bitmap		mBitmap;
	
	public AlphaPatternDrawable(int rectangleSize) {
		mRectangleSize = rectangleSize;
		mPaintWhite.setColor(0xffffffff);
		mPaintGray.setColor(0xffcbcbcb);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, null, getBounds(), mPaint);
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		throw new UnsupportedOperationException("Alpha is not supported by this drawwable.");
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		throw new UnsupportedOperationException("ColorFilter is not supported by this drawwable.");
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);

		int height = bounds.height();
		int width = bounds.width();

		numRectanglesHorizontal = (int) Math.ceil((width / mRectangleSize));
		numRectanglesVertical = (int) Math.ceil(height / mRectangleSize);

		generatePatternBitmap();

	}
	
	/**
	 * This will generate a bitmap with the pattern 
	 * as big as the rectangle we were allow to draw on.
	 * We do this to chache the bitmap so we don't need to
	 * recreate it each time draw() is called since it 
	 * takes a few milliseconds.
	 */
	private void generatePatternBitmap(){
		
		if(getBounds().width() <= 0 || getBounds().height() <= 0){
			return;
		}
		
		mBitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Config.ARGB_8888);			
		Canvas canvas = new Canvas(mBitmap);
		
		Rect r = new Rect();
		boolean verticalStartWhite = true;
		for (int i = 0; i <= numRectanglesVertical; i++) {

			boolean isWhite = verticalStartWhite;
			for (int j = 0; j <= numRectanglesHorizontal; j++) {

				r.top = i * mRectangleSize;
				r.left = j * mRectangleSize;
				r.bottom = r.top + mRectangleSize;
				r.right = r.left + mRectangleSize;
				
				canvas.drawRect(r, isWhite ? mPaintWhite : mPaintGray);

				isWhite = !isWhite;
			}

			verticalStartWhite = !verticalStartWhite;

		}
		
	}
	
}

