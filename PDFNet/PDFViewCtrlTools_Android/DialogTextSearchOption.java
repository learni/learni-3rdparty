//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;


class DialogTextSearchOption extends AlertDialog {

	private Context mContext;
	private CheckBox mWholeWord;
	private CheckBox mCaseSensitive;
	private CheckBox mUseReg;
	
	public DialogTextSearchOption(Context context) {
		super(context);
		mContext = context;
		
		setTitle("Text Search Options");
		LinearLayout main_layout = new LinearLayout(mContext);
		main_layout.setOrientation(LinearLayout.VERTICAL);
		main_layout.setPadding(5, main_layout.getPaddingTop(), main_layout.getPaddingRight(), main_layout.getPaddingRight());
		
		mWholeWord = new CheckBox(mContext);
		mWholeWord.setText("Whole word");
		
		mCaseSensitive = new CheckBox(mContext);
		mCaseSensitive.setText("Case sensitive");
		
		mUseReg = new CheckBox(mContext);
		mUseReg.setText("Regular expressions");
		
		main_layout.addView(mCaseSensitive);
		main_layout.addView(mWholeWord);
		main_layout.addView(mUseReg);
		
		setView(main_layout);
	}
	
	public boolean getWholeWord() {
		return mWholeWord.isChecked();
	}
	
	public boolean getCaseSensitive() {
		return mCaseSensitive.isChecked();
	}
	

	public boolean getRegExps() {
		return mUseReg.isChecked();
	}
	
	public void setWholeWord(boolean flag) {
		mWholeWord.setChecked(flag);
	}
	
	public void setCaseSensitive(boolean flag) {
		mCaseSensitive.setChecked(flag);
	}
	
	public void setRegExps(boolean flag) {
		mUseReg.setChecked(flag);
	}
}
