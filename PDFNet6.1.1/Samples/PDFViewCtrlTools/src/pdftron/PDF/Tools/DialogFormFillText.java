//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import pdftron.PDF.Annot;
import pdftron.PDF.Annots.Widget;
import pdftron.PDF.Field;
import pdftron.PDF.PDFViewCtrl;
import com.pdftron.pdf.tools.R;

class DialogFormFillText extends AlertDialog {

    private PDFViewCtrl mCtrl;
    private Annot mAnnot;
    private int mAnnotPageNum;
    private pdftron.PDF.Field mField;
    private EditText mTextBox;

    public DialogFormFillText(PDFViewCtrl ctrl, Annot annot, int annot_page_num) {
        // Initialization
        super(ctrl.getContext());
        mCtrl = ctrl;
        mAnnot = annot;
        mAnnotPageNum = annot_page_num;
        mField = null;
        try {
            Widget w = new Widget(mAnnot);
            mField = w.getField();
            if (ctrl == null || annot == null || !mField.isValid()) {
                dismiss();
                return;
            }
        } catch (Exception e) {
            dismiss();
            return;
        }

        // Setup view
        LayoutInflater inflater = (LayoutInflater) mCtrl.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tools_dialog_formfilltext, null);
        mTextBox = (EditText) view.findViewById(R.id.tools_dialog_formfilltext_edit_text);
        setTitle(mCtrl.getContext().getString(R.string.tools_dialog_formfilltext_title));
        setView(view);

        try {
            // Compute alignment
            boolean multiple_line = mField.getFlag(Field.e_multiline);
            mTextBox.setSingleLine(!multiple_line);
            int just = mField.getJustification();
            if (just == Field.e_left_justified) {
                mTextBox.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            } else if (just == Field.e_centered) {
                mTextBox.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            } else if (just == Field.e_right_justified) {
                mTextBox.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            }

            // Password format
            if (mField.getFlag(Field.e_password)) {
                mTextBox.setTransformationMethod(new PasswordTransformationMethod());
            }

            // Set initial text
            String str = mField.getValueAsString();
            mTextBox.setText(str);
            // Set the caret position to the end of the text
            mTextBox.setSelection(mTextBox.getText().length());

            // Max length
            int max_len = mField.getMaxLen();
            if (max_len >= 0) {
                LengthFilter filters[] = new LengthFilter[1];
                filters[0] = new InputFilter.LengthFilter(max_len);
                mTextBox.setFilters(filters);
            }

        } catch (Exception e) {
            dismiss();
            return;
        }

        // Add two button
        setButton(DialogInterface.BUTTON_POSITIVE, mCtrl.getContext().getString(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String str = mTextBox.getText().toString();
                try {
                    mCtrl.docLock(true);
                    mField.setValue(str);
                    mField.eraseAppearance();
                    mField.refreshAppearance();
                    mCtrl.update(mAnnot, mAnnotPageNum);
                } catch (Exception e) {

                } finally {
                    mCtrl.docUnlock();
                }

                // Wait a bit to avoid flickering.
                mCtrl.waitForRendering();
            }
        });

        setButton(DialogInterface.BUTTON_NEGATIVE, mCtrl.getContext().getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
    }
}
