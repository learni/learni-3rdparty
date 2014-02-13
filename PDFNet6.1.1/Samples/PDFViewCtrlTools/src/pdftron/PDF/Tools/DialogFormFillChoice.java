//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import pdftron.PDF.Annot;
import pdftron.PDF.Field;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Annots.Widget;
import pdftron.SDF.Obj;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import com.pdftron.pdf.tools.R;

class DialogFormFillChoice extends AlertDialog implements
        android.view.View.OnClickListener,
        android.content.DialogInterface.OnDismissListener,
        android.content.DialogInterface.OnShowListener {

    private PDFViewCtrl mCtrl;
    private Annot mAnnot;
    private int mAnnotPageNum;
    private pdftron.PDF.Field mField;
    private boolean mSingleChoice;
    private boolean mIsCombo;
    private CompoundButton mFocusButton;
    ArrayList<CompoundButton> mBtnList;
    RadioButton mClickedRadioBtn;
    private ScrollView mScrollView;

    public DialogFormFillChoice(PDFViewCtrl ctrl, Annot annot, int annot_page_num) {
        // Initialization
        super(ctrl.getContext());
        mCtrl = ctrl;
        mAnnot = annot;
        mAnnotPageNum = annot_page_num;
        mFocusButton = null;
        mSingleChoice = true;
        mIsCombo = false;
        mBtnList = null;
        mClickedRadioBtn = null;

        try {
            Widget w = new Widget(mAnnot);
            mField = w.getField();
            if (ctrl == null || annot == null || !mField.isValid()) {
                dismiss();
                return;
            }
            mIsCombo = mField.getFlag(Field.e_combo);
            mSingleChoice = mIsCombo || !mField.getFlag(Field.e_multiselect);
        } catch (Exception e) {
            dismiss();
            return;
        }

        // Setup view
        LayoutInflater inflater = (LayoutInflater) mCtrl.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tools_dialog_formfillchoice, null);
        mScrollView = (ScrollView) view.findViewById(R.id.tools_formfillchoice_scrollview);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.tools_dialog_formfillchoice_layout);
        setView(view);

        setOnDismissListener(this);
        setOnShowListener(this);

        // Populate content
        try {
            mBtnList = getOptionList();
            if (mSingleChoice) {
                String selected_str = mField.getValueAsString();
                RadioGroup group = new RadioGroup(mCtrl.getContext());
                Iterator<CompoundButton> itr = mBtnList.iterator();
                while (itr.hasNext()) {
                    CompoundButton btn = itr.next();
                    btn.setOnClickListener(this);
                    group.addView(btn);
                    if (btn.getText().toString().substring(2).equals(selected_str)) {
                        // When the text was set it was added two spaces at the beginning.
                        // So we have to remove them when comparing.
                        btn.setChecked(true);
                        mFocusButton = btn;
                    }
                }

                layout.addView(group);
                mScrollView.addView(layout);

            } else {
                HashSet<Integer> selected = getSelectedPositions();
                Iterator<CompoundButton> itr = mBtnList.iterator();
                int pos = 0;
                while (itr.hasNext()) {
                    CompoundButton btn = itr.next();
                    btn.setOnClickListener(this);
                    boolean checked = selected.contains(pos++);
                    btn.setChecked(checked);
                    layout.addView(btn);
                    if (mFocusButton == null && checked) {
                        mFocusButton = btn;
                    }
                }
                mScrollView.addView(layout);
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (mFocusButton != null) {
            // Scroll to show the selected button
            int y = mFocusButton.getTop();
            int x = mFocusButton.getLeft();
            mScrollView.scrollTo(x, y);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof RadioButton) {
            mClickedRadioBtn = (RadioButton) v;
            dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        boolean wait = false;
        if (mSingleChoice && mClickedRadioBtn != null) {
            try {
                mCtrl.docLock(true);

                // When the text was set it was added two spaces at the beginning.
                // So we have to remove them when comparing.
                String str = mClickedRadioBtn.getText().toString().substring(2);

                if (mIsCombo) {
                    if ( mField.getValueAsString() != str ) {
                        mField.setValue(str);
                        mField.refreshAppearance();
                        mCtrl.update(mAnnot, mAnnotPageNum);
                        wait = true;
                    }
                } else {
                    PDFDoc doc = mCtrl.getDoc();
                    Obj arr = doc.createIndirectArray();
                    arr.pushBackText(str);
                    mField.setValue(arr);
                    mField.eraseAppearance();
                    mField.refreshAppearance();
                    mCtrl.update(mAnnot, mAnnotPageNum);
                    wait = true;
                }
            } catch(Exception e) {

            } finally {
                mCtrl.docUnlock();
                this.dismiss();
            }
        }

        else if (!mSingleChoice && mBtnList != null) {
            try {
                mCtrl.docLock(true);
                PDFDoc doc = mCtrl.getDoc();
                Obj arr = doc.createIndirectArray();
                for (CompoundButton btn : mBtnList) {
                    if (btn.isChecked()) {
                        // When the text was set it was added two spaces at the beginning.
                        // So we have to remove them when comparing.
                        String str = btn.getText().toString().substring(2);
                        arr.pushBackText(str);
                    }
                }
                mField.setValue(arr);
                mField.eraseAppearance();
                mField.refreshAppearance();
                mCtrl.update(mAnnot, mAnnotPageNum);
                wait = true;

            } catch(Exception e) {

            } finally {
                mCtrl.docUnlock();
                this.dismiss();
            }
        }

        if (wait) {
            // Wait a bit to avoid flickering.
            mCtrl.waitForRendering();
        }
    }

    // Populate list from the choice annotation
    private ArrayList<CompoundButton> getOptionList() {
        try {
            ArrayList<CompoundButton> al = new ArrayList<CompoundButton>();

            Widget w = new Widget(mAnnot);
            Field f = w.getField();
            int numOpt = f.getOptCount();

            for (int i = 0; i < numOpt; i++) {
                CompoundButton btn;
                if (mSingleChoice) {
                    btn = new RadioButton(mCtrl.getContext());
                } else {
                    btn = new CheckBox(mCtrl.getContext());
                }
                btn.setText("  " + f.getOpt(i));
                al.add(btn);
            }

            return al;

        } catch (Exception e) {

        }

        return null;
    }

    // Find the selected items from a multiple choice list
    private HashSet<Integer> getSelectedPositions() {
        try {
            HashSet<Integer> al = new HashSet<Integer>();
            Obj val = mField.getValue();
            if (val != null) {
                if (val.isString()) {
                    Obj o = mAnnot.getSDFObj().findObj("Opt");
                    if (o != null) {
                        int id =  GetOptIdx(val, o);
                        if (id >= 0) {
                            al.add(id);
                        }
                    }
                } else if (val.isArray()) {
                    int sz = (int)val.size();
                    for (int i = 0; i < sz; ++i) {
                        Obj entry = val.getAt(i);
                        if (entry.isString()) {
                            Obj o = mAnnot.getSDFObj().findObj("Opt");
                            if (o != null) {
                                int id = GetOptIdx(entry, o);
                                if (id >= 0) {
                                    al.add(id);
                                }
                            }
                        }
                    }
                }
            }

            return al;

        } catch (Exception e) {
            return null;
        }
    }

    private Integer GetOptIdx(Obj str_val, Obj opt) {
        try {
            int sz = (int)opt.size();
            String str_val_string = new String(str_val.getBuffer());
            for (int i = 0; i < sz; ++i) {
                Obj v = opt.getAt(i);
                if (v.isString() && str_val.size() == v.size()) {
                    String v_string = new String(v.getBuffer());
                    if (str_val_string.equals(v_string)) {
                        return i;
                    }
                } else if (v.isArray() && v.size() >= 2 && v.getAt(1).isString() && str_val.size() == v.getAt(1).size()) {
                    v = v.getAt(1);
                    String v_string = new String(v.getBuffer());
                    if (str_val_string.equals(v_string)) {
                        return i;
                    }
                }
            }

        } catch (Exception e) {

        }

        return -1;
    }
}
