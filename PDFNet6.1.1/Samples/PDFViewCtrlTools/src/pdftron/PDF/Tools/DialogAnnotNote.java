//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import android.app.AlertDialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.EditText;

class DialogAnnotNote extends AlertDialog {

    private EditText mTextBox;

    public DialogAnnotNote(Context context, String note) {
        super(context);

        mTextBox = new EditText(context);
        ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(-1, -1);
        mTextBox.setLayoutParams(layout);
        if (note != null) {
            mTextBox.setText(note);
            // Set the caret position to the end of the text
            mTextBox.setSelection(mTextBox.getText().length());
        }
        setView(mTextBox, 8, 8, 8, 8);
    }

    public String getNote() {
        return mTextBox.getText().toString();
    }
}
