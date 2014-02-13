//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.pdftron.pdf.tools.R;

class DialogTextSearchOption extends AlertDialog {

    private CheckBox mWholeWord;
    private CheckBox mCaseSensitive;
    private CheckBox mUseReg;

    public DialogTextSearchOption(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tools_dialog_textsearch, null);

        mCaseSensitive = (CheckBox) layout.findViewById(R.id.tools_dialog_textsearch_case_sensitive);
        mWholeWord = (CheckBox) layout.findViewById(R.id.tools_dialog_textsearch_wholeword);
        mUseReg = (CheckBox) layout.findViewById(R.id.tools_dialog_textsearch_regex);

        setTitle(context.getString(R.string.tools_dialog_textsearch_title));
        setView(layout);
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
