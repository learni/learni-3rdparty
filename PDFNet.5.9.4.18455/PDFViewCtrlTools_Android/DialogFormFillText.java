//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import pdftron.PDF.Annot;
import pdftron.PDF.Field;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Annots.Widget;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.widget.EditText;


class DialogFormFillText extends AlertDialog {
	private PDFViewCtrl mCtrl;
	private Annot mAnnot;
	private int mAnnotPageNum;
	private pdftron.PDF.Field mField;
	private EditText mTextBox;
	
	
	public DialogFormFillText(PDFViewCtrl ctrl, Annot annot, int annot_page_num) {
		//initialization
		super(ctrl.getContext());
		mCtrl = ctrl;
		mAnnot = annot;
		mAnnotPageNum = annot_page_num;
		mField = null;
		try {
			Widget w = new Widget(mAnnot);
			mField = w.getField();
			if ( ctrl == null || annot == null || !mField.isValid() ) {
				dismiss();
				return;
			}
		}
		catch (Exception e) {
			dismiss();
			return;
		}
		
		//setup view
		setTitle("Text Field Content");
		mTextBox = new EditText(mCtrl.getContext());
		setView(mTextBox);
		
		try {
			// compute alignment
			boolean multiple_line = mField.getFlag(Field.e_multiline);
			mTextBox.setSingleLine(!multiple_line);
			int just = mField.getJustification();
			if (just == Field.e_left_justified) {
				mTextBox.setGravity(Gravity.LEFT
						| Gravity.CENTER_VERTICAL);
			} else if (just == Field.e_centered) {
				mTextBox.setGravity(Gravity.CENTER
						| Gravity.CENTER_VERTICAL);
			} else if (just == Field.e_right_justified) {
				mTextBox.setGravity(Gravity.RIGHT
						| Gravity.CENTER_VERTICAL);
			}
			
			// password format
			if (mField.getFlag(Field.e_password)) {
				mTextBox.setTransformationMethod(new PasswordTransformationMethod());
			}
			
			// set initial text
			String str = mField.getValueAsString();
			mTextBox.setText(str);

			//max length
			int max_len = mField.getMaxLen();
			if (max_len >= 0) {
				LengthFilter filters[] = new LengthFilter[1];
				filters[0] = new InputFilter.LengthFilter(max_len);
				mTextBox.setFilters(filters);
			}
		}
		catch (Exception e) {
			dismiss();
			return;
		}
		
		
		//add two button
		setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String str = mTextBox.getText().toString();
				try {
					mCtrl.lockDoc(true);
					mField.setValue(str);
					mField.eraseAppearance();
					mField.refreshAppearance();
					mCtrl.update(mAnnot, mAnnotPageNum);
				}
				catch (Exception e) {
				}
				finally {
					mCtrl.unlockDoc();
				}
				
				//wait a bit to avoid flickering.
				long start_time = SystemClock.uptimeMillis();
				while ( !mCtrl.isFinishedRendering() ) {
					if ( SystemClock.uptimeMillis() - start_time >= 1500 ) {
						//wait at most 1.5 seconds
						break;
					}
				}
			}
		});
		
		setButton2("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//do nothing
			}
		});
	}
}
