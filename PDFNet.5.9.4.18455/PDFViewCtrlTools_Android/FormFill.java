//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import pdftron.Common.PDFNetException;
import pdftron.PDF.Action;
import pdftron.PDF.Annot;
import pdftron.PDF.ColorPt;
import pdftron.PDF.ColorSpace;
import pdftron.PDF.Field;
import pdftron.PDF.Font;
import pdftron.PDF.GState;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Annots.Link;
import pdftron.PDF.Annots.Widget;
import pdftron.SDF.Obj;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 
 * This class is responsible for editing a selected annotation, e.g., moving and
 * resizing.
 * 
 */
class FormFill extends Tool {
	
	private pdftron.PDF.Field mField;
	private EditText mInlineEditBox;
	private boolean mIsMultiLine;
	private double mBorderWidth;
	
	
	public FormFill(PDFViewCtrl ctrl) {
		super(ctrl);
		mInlineEditBox = null;
		mBorderWidth = 0;
	}
	
	public int getMode() {
		return ToolManager.e_form_fill;
	}
	
	
	public void onClose() {
		super.onClose();
		mPDFView.removeView(mInlineEditBox);
	}
	
	
	public boolean onSingleTapConfirmed(MotionEvent e) {
		handleForm(e);
		return false;
	}
	
	
	public void onPostSingleTapConfirmed() {
		if ( mInlineEditBox == null ) {
			//inline edit box is a widget added to PDFViewCtrl for edit text inline.
			//if it is null, it implies the user has single-tapped on other forms, such
			//as choice, button, etc. in such cases, we return to the pan mode immediately.
			mNextToolMode = ToolManager.e_pan;
		}
	}
	
	
	public void onPageTurning(int old_page, int cur_page) {
		mNextToolMode = ToolManager.e_pan;
		if ( mAnnot != null && mInlineEditBox != null ) {
			//in non-continuous mode and switched to a different page, apply the
			//necessary changes to forms.
			applyEditBoxAndQuit();
		}
	}
	

	public void onLayout (boolean changed, int l, int t, int r, int b) {
		if ( mAnnot != null ) {
			if ( !mPDFView.isContinuousPagePresentationMode(mPDFView.getPagePresentationMode())) {
				if ( mAnnotPageNum != mPDFView.getCurrentPage() ) {
					//now in single page mode, and the annotation is not on this page, quit this tool mode.
					if ( mInlineEditBox != null ) {
						applyEditBoxAndQuit();
					}
					mAnnot = null;
					mNextToolMode = ToolManager.e_pan;
					return;
				}
				else {
					if ( mInlineEditBox != null ) {
						adjustTextEditLocation();
					}
				}
			}
			else if ( mInlineEditBox != null ) {
				adjustTextEditLocation();
			}
		}
	}
	
	
	public boolean onLongPress(MotionEvent e) {
		handleForm(e);
		return false;
	}
	
	
	private void handleForm(MotionEvent e) {
		int x = (int)(e.getX()+0.5);
		int y = (int)(e.getY()+0.5);
		
		if ( mAnnot != null ) {
			boolean wait = false;
			mNextToolMode = ToolManager.e_form_fill;
			try {
				mPDFView.lockDoc(true);
				if (isInsideAnnot(x, y)) {
					Widget w = new Widget(mAnnot);
					mField = w.getField();
					if ( mField.isValid() && !mField.getFlag(pdftron.PDF.Field.e_read_only) ) {
						int field_type = mField.getType();
						
						if ( field_type == pdftron.PDF.Field.e_check ) {
							mField.setValue( !mField.getValueAsBool() );
							pdftron.PDF.Rect update_bbox = mField.getUpdateRect();
							double [] pts1, pts2;
							pts1 = mPDFView.convPagePtToClientPt(update_bbox.getX1(), update_bbox.getY1(), mAnnotPageNum);
							pts2 = mPDFView.convPagePtToClientPt(update_bbox.getX2(), update_bbox.getY2(), mAnnotPageNum);
							update_bbox = new pdftron.PDF.Rect(pts1[0], pts1[1], pts2[0], pts2[1]);
							update_bbox.normalize();
							mPDFView.update(update_bbox);
							wait = true;
						}
						
						else if ( field_type == pdftron.PDF.Field.e_radio ) {
							mField.setValue( true );
							pdftron.PDF.Rect update_bbox = mField.getUpdateRect();
							double [] pts1, pts2;
							pts1 = mPDFView.convPagePtToClientPt(update_bbox.getX1(), update_bbox.getY1(), mAnnotPageNum);
							pts2 = mPDFView.convPagePtToClientPt(update_bbox.getX2(), update_bbox.getY2(), mAnnotPageNum);
							update_bbox = new pdftron.PDF.Rect(pts1[0], pts1[1], pts2[0], pts2[1]);
							update_bbox.normalize();
							mPDFView.update(update_bbox);
							wait = true;
						}
						
						else if ( field_type == pdftron.PDF.Field.e_button ) {
							Link link = new Link(mAnnot);
							Action action = link.getAction();
							if ( action != null ) {
								int at = action.getType();
								if ( at == Action.e_URI ) {
									Obj o = action.getSDFObj();
									o = o.findObj("URI");
									if ( o != null ) {
										String uri = o.getAsPDFText();
										Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
										mPDFView.getContext().startActivity(i);
									}
								}
								else {
									mPDFView.executeAction(action);
								}
								mPDFView.invalidate();
								wait = true;
							}
						}
						
						else if ( field_type == pdftron.PDF.Field.e_choice ) {
							final DialogFormFillChoice d = new DialogFormFillChoice(mPDFView, mAnnot, mAnnotPageNum);
							d.show();
						}
						
						else if ( field_type == pdftron.PDF.Field.e_text ) {
							boolean inline_edit = canUseInlineEditing();
							if ( !inline_edit ) {
								//pop up a dialog for inputting text
								final DialogFormFillText d = new DialogFormFillText(mPDFView, mAnnot, mAnnotPageNum);
								d.show();
							}
							else {
								//inline editing
								handleTextInline();
							}
						}
						
						else if ( field_type == pdftron.PDF.Field.e_signature ) {
							//TODO: has to deal with encryption
						}
					}
				}
				else {
					//otherwise goes back to the pan mode.
					if ( mInlineEditBox != null ) {
						applyEditBoxAndQuit();
						wait = true;
					}
					mAnnot = null;
					mNextToolMode = ToolManager.e_pan;
				}
			}
			catch (Exception ex) {
			}
			finally {
				mPDFView.unlockDoc();
			}
			
			//wait a bit to avoid flickering. has to be called after document is unlocked.
			if ( wait ) {
				mPDFView.waitForRendering();
			}
		}
	}
	
	
	private boolean canUseInlineEditing() {
		try {
			float font_sz = 10 * (float)mPDFView.getZoom();
			GState gs = mField.getDefaultAppearance();
			if ( gs != null ) {
				font_sz = (float)gs.getFontSize();
				if ( font_sz <= 0 ) {
					//auto size; so examine the annoation's bbox
					double x1 = mAnnotBBox.left + mBorderWidth;
					double y1 = mAnnotBBox.bottom - mBorderWidth; //note mAnnotBBox is in PDF page space, so have to reverse it
					double x2 = mAnnotBBox.right - mBorderWidth;
					double y2 = mAnnotBBox.top + mBorderWidth;
					double pts1[] = mPDFView.convPagePtToClientPt(x1, y1, mAnnotPageNum);
					double pts2[] = mPDFView.convPagePtToClientPt(x2, y2, mAnnotPageNum);
					double height = Math.abs(pts1[1] - pts2[1]);
					font_sz = (float)(height/2.5);
				}
				else {
					font_sz *= (float)mPDFView.getZoom();
				}
			}
			font_sz = this.convDp2Pix(font_sz);
			if ( font_sz > 12 ) {
				//the font size is large enough, so use inline editing
				return true;
			}
		}
		catch (Exception e) {
			return false;
		}
		
		return false;
	}
	
		
	private void ajustFontSize() {
		try {
			float font_sz = 10 * (float)mPDFView.getZoom();
			GState gs = mField.getDefaultAppearance();
			if ( gs != null ) {
				font_sz = (float)gs.getFontSize();
				if ( font_sz <= 0 ) {
					//auto size
					font_sz = (float)(mInlineEditBox.getHeight()/2.5);
				}
				else {
					font_sz *= (float)mPDFView.getZoom();
				}
			}
			font_sz = this.convDp2Pix(font_sz);
			mInlineEditBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, font_sz);
			
			if ( !mIsMultiLine ) {
				int height = mInlineEditBox.getHeight();
				int line_height = mInlineEditBox.getLineHeight(); //typical line height
				int lp = mInlineEditBox.getPaddingLeft();
				int rp = mInlineEditBox.getPaddingRight();
				int tp = mInlineEditBox.getPaddingTop();
				int bp = mInlineEditBox.getPaddingBottom();
				//extra top padding is: (empty space below the line) * (proportional original top padding)
				double extra_tp = (height - line_height - tp - bp) * ((double)(tp)/(tp+bp));
				mInlineEditBox.setPadding(lp/3, (int)(tp+extra_tp), rp, bp);
			}
		} catch (PDFNetException e) {
		}
	}
	
	
	private void mapColorFont() {
		try {
			GState gs = mField.getDefaultAppearance();
			if ( gs != null ) {
				//set text color
				ColorPt color = gs.getFillColor();
				color = gs.getFillColorSpace().convert2RGB(color);
				int r = (int)Math.floor(color.get(0)*255+0.5);
				int g = (int)Math.floor(color.get(1)*255+0.5);
				int b = (int)Math.floor(color.get(2)*255+0.5);
				int color_int = Color.argb(255, r, g, b);
				mInlineEditBox.setTextColor(color_int);
				
				//set background color
				color = getFieldBkColor();
				if ( color == null ) {
					r = 255;
					g = 255;
					b = 255;
				}
				else {
					r = (int)Math.floor(color.get(0)*255+0.5);
					g = (int)Math.floor(color.get(1)*255+0.5);
					b = (int)Math.floor(color.get(2)*255+0.5);
					color_int = Color.argb(255, r, g, b);
					mInlineEditBox.setBackgroundColor(color_int);
				}
				
				//set the font of the EditBox to match the PDF form field's. in order to do this,
				//you need to bundle with you App the fonts, such as "Times", "Arial", "Courier", "Helvetica", etc.
				//the following is just a place holder.
				Font font = gs.getFont();
				if ( font != null ) {
					String family_name = font.getFamilyName();
					if ( family_name == null || family_name.length() == 0 ) {
						family_name = "Times";
					}
					String name = font.getName();
					if ( name == null || name.length() == 0 ) {
						name = "Times New Roman";
					}
					if ( family_name.contains("Times") || name.contains("Times") ) {
						//NOTE: you need to bundle the font file in you App and use it here.
						//TypeFace tf == Typeface.create(...);
						//mInlineEditBox.setTypeface(tf);
					}
				}
			}
		}
		catch (PDFNetException e) {
		}
	}
	
	private ColorPt getFieldBkColor() {
		try {
			Obj o = mAnnot.getSDFObj().findObj("MK");
			if( o != null )
			{
				Obj bgc = o.findObj("BG");
				if( bgc != null && bgc.isArray() )
				{
					int sz = (int)bgc.size();
					switch(sz)
					{
					case 1:
						{
							Obj n = bgc.getAt(0);
							if( n.isNumber() ) {
								return new ColorPt(n.getNumber(), n.getNumber(), n.getNumber());
							}
							break;
						}
					case 3:
						{
							Obj r = bgc.getAt(0), g = bgc.getAt(1), b = bgc.getAt(2);
							if(r.isNumber() && g.isNumber() && b.isNumber())
							{
								return new ColorPt(r.getNumber(),g.getNumber(),b.getNumber());
							}
							break;
						}
					case 4:
						{
							Obj c = bgc.getAt(0), m = bgc.getAt(1), y = bgc.getAt(2), k = bgc.getAt(3);
							if(c.isNumber() && m.isNumber() && y.isNumber() && k.isNumber())
							{
								ColorPt cp = new ColorPt(c.getNumber(),m.getNumber(),y.getNumber(),k.getNumber());
								ColorSpace cs = ColorSpace.createDeviceCMYK();
								return cs.convert2RGB(cp);
							}
						}
						break;
					}
				}
			}
		}
		catch (Exception e) {
		}	
		return null;
	}
	
	
	private void handleTextInline() {
		try {
			if (mField.isValid()) {
				//boolean is_comb = mField.getFlag(Field.e_comb);
				int max_len = mField.getMaxLen();

				mInlineEditBox = new EditText(mPDFView.getContext());

				// compute border width
				Annot.BorderStyle bs = mAnnot.getBorderStyle();
				Obj aso = mAnnot.getSDFObj();
				if (aso.findObj("BS") == null && aso.findObj("Border") == null) {
					bs.setWidth(0);
				}
				if (bs.getStyle() == Annot.BorderStyle.e_beveled
						|| bs.getStyle() == Annot.BorderStyle.e_inset) {
					bs.setWidth(bs.getWidth() * 2);
				}
				mBorderWidth = bs.getWidth();

				// comb and max length
				if (max_len >= 0) {
					LengthFilter filters[] = new LengthFilter[1];
					filters[0] = new InputFilter.LengthFilter(max_len);
					mInlineEditBox.setFilters(filters);
				}

				// compute alignment
				mIsMultiLine = mField.getFlag(Field.e_multiline);
				mInlineEditBox.setSingleLine(!mIsMultiLine);
				int just = mField.getJustification();
				if (just == Field.e_left_justified) {
					mInlineEditBox.setGravity(Gravity.LEFT
							| Gravity.CENTER_VERTICAL);
				} else if (just == Field.e_centered) {
					mInlineEditBox.setGravity(Gravity.CENTER
							| Gravity.CENTER_VERTICAL);
				} else if (just == Field.e_right_justified) {
					mInlineEditBox.setGravity(Gravity.RIGHT
							| Gravity.CENTER_VERTICAL);
				}
				
				// password format
				if (mField.getFlag(Field.e_password)) {
					mInlineEditBox.setTransformationMethod(new PasswordTransformationMethod());
				}

				// set initial text
				String init_str = mField.getValueAsString();
				mInlineEditBox.setText(init_str);

				// set position in the parent view
				adjustTextEditLocation();
				
				// compute font size
				ajustFontSize();
				
				// set color and font
				mapColorFont();

				// bring it up
				mPDFView.addView(mInlineEditBox);
				mInlineEditBox.requestFocus();
                
                // bring up soft keyboard in case it is not shown automatically
                InputMethodManager imm = (InputMethodManager)mPDFView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mInlineEditBox.getWindowToken(), 0);
                imm.showSoftInput(mInlineEditBox, 0);
			}
		} catch (PDFNetException e) {
		}
	}
	
	
	public boolean onScaleBegin(float x, float y) {
		if ( mInlineEditBox != null ) {
		}
		return false;
	}
	
	
	public boolean onScale(float x, float y) {
		if ( mInlineEditBox != null ) {
			mInlineEditBox.setVisibility(View.INVISIBLE);
		}
		return false;
	}
	
	
	public boolean onScaleEnd(float x, float y) {
		if ( mInlineEditBox != null ) {
			adjustTextEditLocation();
			ajustFontSize();
			mInlineEditBox.setVisibility(View.VISIBLE);
			mInlineEditBox.requestFocus();
		}
		return false;
	}
	
	
	public void onDoubleTapEnd(MotionEvent e) {
		if ( mInlineEditBox != null ) {
			adjustTextEditLocation();
			ajustFontSize();
			mInlineEditBox.requestFocus();
		}
	}

	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		if ( mInlineEditBox != null && prior_event_type == PDFViewCtrl.PRIOR_EVENT_DBLTAP ) {
			adjustTextEditLocation();
		}
		return false;
	}
	
	
	private void adjustTextEditLocation() {
		if ( mInlineEditBox != null ) {
			double x1 = mAnnotBBox.left + mBorderWidth;
			double y1 = mAnnotBBox.bottom - mBorderWidth; //note mAnnotBBox is in PDF page space, so have to reverse it
			double x2 = mAnnotBBox.right - mBorderWidth;
			double y2 = mAnnotBBox.top + mBorderWidth;
			double pts1[] = mPDFView.convPagePtToClientPt(x1, y1, mAnnotPageNum);
			double pts2[] = mPDFView.convPagePtToClientPt(x2, y2, mAnnotPageNum);
			x1 = pts1[0];
			y1 = pts1[1];
			x2 = pts2[0];
			y2 = pts2[1];
			
			int sx = mPDFView.getScrollX();
			int sy = mPDFView.getScrollY();
			int anchor_x = (int)(x1 + sx + 0.5);
			int anchor_y = (int)(y1 + sy + 0.5);
			mInlineEditBox.layout( anchor_x, anchor_y, (int)(anchor_x + x2 - x1 + 0.5), (int)(anchor_y + y2 - y1 + 0.5) );
		}
	}

	
	private void applyEditBoxAndQuit() {
		try {
			mPDFView.lockDoc(true);
				
			String str = mInlineEditBox.getText().toString();
			mField.setValue(str);
			mField.eraseAppearance();
			mField.refreshAppearance();

			mPDFView.update(mAnnot, mAnnotPageNum);
				
			//hide soft keyboard
			InputMethodManager imm = (InputMethodManager)mPDFView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mInlineEditBox.getWindowToken(), 0);

			mPDFView.removeView(mInlineEditBox);
		}
		catch (Exception e) {
		}
		finally {
			mAnnot = null;
			mNextToolMode = ToolManager.e_pan;
			mPDFView.unlockDoc();
		}
	}
}
