//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdfnet.demo.pdfviewctrl;

import pdftron.PDF.PDFViewCtrl;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;


class SettingsDlg extends AlertDialog {

	private Context mContext;
	
	private CheckBox mProgressive;
	
	private RadioGroup mPageViewGrp;
	private RadioButton mPageViewFitPage;
	private RadioButton mPageViewFitWidth;
	private RadioButton mPageViewFitHeight;
	
	private RadioGroup mPagePresGrp;
	private RadioButton mPagePresSingle;
	private RadioButton mPagePresSingleCont;
	private RadioButton mPagePresFacing;
	private RadioButton mPagePresFacingCont;
	private RadioButton mPagePresFacingCover;
	private RadioButton mPagePresFacingCoverCont;
	
	public SettingsDlg(Context context, PDFViewCtrl ctrl) {
		super(context);
		mContext = context;
		
		setTitle("Settings");
		
		mProgressive = new CheckBox(mContext);
		mProgressive.setText("Progressive Rendering");
		mProgressive.setChecked(ctrl.getProgressiveRendering());
		
		//page view group
		mPageViewGrp = new RadioGroup(mContext);
		
		mPageViewFitPage = new RadioButton(mContext);
		mPageViewFitPage.setText("Fit Page");
		mPageViewFitPage.setId(PDFViewCtrl.PAGE_VIEW_FIT_PAGE);
		
		mPageViewFitWidth = new RadioButton(mContext);
		mPageViewFitWidth.setText("Fit Width");
		mPageViewFitWidth.setId(PDFViewCtrl.PAGE_VIEW_FIT_WIDTH);
		
		mPageViewFitHeight = new RadioButton(mContext);
		mPageViewFitHeight.setText("Fit Height");
		mPageViewFitHeight.setId(PDFViewCtrl.PAGE_VIEW_FIT_HEIGHT);
		
		mPageViewGrp.addView(mPageViewFitPage);
		mPageViewGrp.addView(mPageViewFitWidth);
		mPageViewGrp.addView(mPageViewFitHeight);
		
		mPageViewGrp.check(ctrl.getPageViewMode());
		
		//page presentation group
		mPagePresGrp = new RadioGroup(mContext);
		mPagePresGrp.setPadding(5, 0, 0, 0);
		
		mPagePresSingle = new RadioButton(mContext);
		mPagePresSingle.setText("Single");
		mPagePresSingle.setId(PDFViewCtrl.PAGE_PRESENTATION_SINGLE);
		
		mPagePresSingleCont = new RadioButton(mContext);
		mPagePresSingleCont.setText("Single Continuous");
		mPagePresSingleCont.setId(PDFViewCtrl.PAGE_PRESENTATION_SINGLE_CONT);
		
		mPagePresFacing = new RadioButton(mContext);
		mPagePresFacing.setText("Facing");
		mPagePresFacing.setId(PDFViewCtrl.PAGE_PRESENTATION_FACING);
		
		mPagePresFacingCont = new RadioButton(mContext);
		mPagePresFacingCont.setText("Facing Continuous");
		mPagePresFacingCont.setId(PDFViewCtrl.PAGE_PRESENTATION_FACING_CONT);
		
		mPagePresFacingCover = new RadioButton(mContext);
		mPagePresFacingCover.setText("Cover");
		mPagePresFacingCover.setId(PDFViewCtrl.PAGE_PRESENTATION_FACING_COVER);
		
		mPagePresFacingCoverCont = new RadioButton(mContext);
		mPagePresFacingCoverCont.setText("Cover Continuous");
		mPagePresFacingCoverCont.setId(PDFViewCtrl.PAGE_PRESENTATION_FACING_COVER_CONT);
		
		mPagePresGrp.addView(mPagePresSingle);
		mPagePresGrp.addView(mPagePresSingleCont);
		mPagePresGrp.addView(mPagePresFacing);
		mPagePresGrp.addView(mPagePresFacingCont);
		mPagePresGrp.addView(mPagePresFacingCover);
		mPagePresGrp.addView(mPagePresFacingCoverCont);
		
		mPagePresGrp.check(ctrl.getPagePresentationMode());
		
		//
		LinearLayout view_pres_layout = new LinearLayout(mContext);
		view_pres_layout.setOrientation(LinearLayout.HORIZONTAL);
		view_pres_layout.addView(mPageViewGrp);
		view_pres_layout.addView(mPagePresGrp);
		
		LinearLayout main_layout = new LinearLayout(mContext);
		main_layout.setPadding(5, 0, 0, 0);
		main_layout.setOrientation(LinearLayout.VERTICAL);
		main_layout.addView(mProgressive);
		main_layout.addView(view_pres_layout);
		
		setView(main_layout);
	}
	
	public boolean getProgressive() {
		return mProgressive.isChecked();
	}
	
	public int getPagePresentationMode() {
		return mPagePresGrp.getCheckedRadioButtonId();
	}
	
	public int getPageViewMode() {
		return mPageViewGrp.getCheckedRadioButtonId();
	}
}
