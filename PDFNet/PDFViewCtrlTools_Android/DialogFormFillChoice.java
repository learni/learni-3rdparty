//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
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
import android.content.DialogInterface;
import android.os.SystemClock;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;


class DialogFormFillChoice extends AlertDialog implements android.view.View.OnClickListener, 
	android.content.DialogInterface.OnDismissListener, android.content.DialogInterface.OnShowListener{
	private PDFViewCtrl mCtrl;
	private Annot mAnnot;
	private int mAnnotPageNum;
	private pdftron.PDF.Field mField;
	private boolean mSingleChoice;
	private CompoundButton mFocusButton;
	ArrayList<CompoundButton> mBtnList;
	RadioButton mClickedRadioBtn;
	private ScrollView mScrollView;
	
	
	public DialogFormFillChoice(PDFViewCtrl ctrl, Annot annot, int annot_page_num) {
		//initialization
		super(ctrl.getContext());
		mCtrl = ctrl;
		mAnnot = annot;
		mAnnotPageNum = annot_page_num;
		mFocusButton = null;
		mSingleChoice = true;
		mBtnList = null;
		mClickedRadioBtn = null;
		try {
			Widget w = new Widget(mAnnot);
			mField = w.getField();
			if ( ctrl == null || annot == null || !mField.isValid() ) {
				dismiss();
				return;
			}
			mSingleChoice = mField.getFlag(Field.e_combo);
		}
		catch (Exception e) {
			dismiss();
			return;
		}
		
		//setup view
		mScrollView = new ScrollView(mCtrl.getContext());
		LinearLayout layout = new LinearLayout(mCtrl.getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(20, layout.getPaddingTop(), 20, layout.getPaddingRight());
		setView(mScrollView);
		setOnDismissListener(this);
		setOnShowListener(this);
		
		//populate content
		try {
			mBtnList = getOptionList();
			if ( mSingleChoice ) {
				String selected_str = mField.getValueAsString();
				RadioGroup group = new RadioGroup(mCtrl.getContext());
				Iterator<CompoundButton> itr = mBtnList.iterator();
				while ( itr.hasNext() ) {
					CompoundButton btn = itr.next();
					btn.setOnClickListener(this);
					group.addView(btn);
					if ( btn.getText().toString().substring(2).equals(selected_str) ) {
						//when the text is set, added two spaces at the beginning; so has to remove it when comparing.
						btn.setChecked(true);
						mFocusButton = btn;
					}
				}
				
				layout.addView(group);
				mScrollView.addView(layout);
			}
			
			else {
				HashSet<Integer> selected = getSelectedPositions();
				Iterator<CompoundButton> itr = mBtnList.iterator();
				int pos = 0;
				while ( itr.hasNext() ) {
					CompoundButton btn = itr.next();
					btn.setOnClickListener(this);
					boolean checked = selected.contains(pos++);
					btn.setChecked(checked);
					layout.addView(btn);
					if ( mFocusButton == null && checked ) {
						mFocusButton = btn;
					}
				}
				mScrollView.addView(layout);
			}
		}
		catch (Exception e) {
		}	
	}
	

	//@Override
	public void onShow(DialogInterface dialog) {
		if ( mFocusButton != null ) {
			//scroll to show the selected button
			int y = mFocusButton.getTop();
			int x = mFocusButton.getLeft();
			mScrollView.scrollTo(x, y);
		}
		
	}
	
	
	//@Override
	public void onClick(View v) {
		if ( v instanceof RadioButton  ) {
			mClickedRadioBtn = (RadioButton) v;
			dismiss();
		}
	}
	
	
	//@Override
	public void onDismiss(DialogInterface dialog) {
		boolean wait = false;
		if ( mSingleChoice && mClickedRadioBtn != null ) {
			try {
				mCtrl.lockDoc(true);
				//when the text is set, added two spaces at the beginning; so has to remove it when comparing.
				String str = mClickedRadioBtn.getText().toString().substring(2);
				if ( mField.getValueAsString() != str ) {
					mField.setValue(str);
					mField.refreshAppearance();
					mCtrl.update(mAnnot, mAnnotPageNum);
					wait = true;
				}
			}
			catch(Exception e) {
			}
			finally {
				mCtrl.unlockDoc();
				this.dismiss();
			}		
		}
		
		else if ( !mSingleChoice && mBtnList != null ) {
			try {
				mCtrl.lockDoc(true);
				PDFDoc doc = mCtrl.getDoc();
				Obj arr = doc.createIndirectArray();
				Iterator<CompoundButton> itr = mBtnList.iterator();
				while ( itr.hasNext() ) {
					CompoundButton btn = itr.next();
					if ( btn.isChecked() ) {
						//when the text is set, added two spaces at the beginning; so has to remove it when comparing.
						String str = btn.getText().toString().substring(2);
						arr.pushBackText(str);
					}
				}
				mField.setValue(arr);
				mField.eraseAppearance();
				mField.refreshAppearance();
				mCtrl.update(mAnnot, mAnnotPageNum);
				wait = true;
			}
			catch(Exception e) {
			}
			finally {
				mCtrl.unlockDoc();
				this.dismiss();
			}	
		}
		
		if ( wait ) {
			//wait a bit to avoid flickering.
			long start_time = SystemClock.uptimeMillis();
			while ( !mCtrl.isFinishedRendering() ) {
				if ( SystemClock.uptimeMillis() - start_time >= 1500 ) {
					//wait at most 1.5 seconds
					break;
				}
			}
		}
	}
	

	//populate list from the choice annotation
	private ArrayList<CompoundButton> getOptionList() {
		try {
			ArrayList<CompoundButton> al = new ArrayList<CompoundButton>();
			Obj obj = mAnnot.getSDFObj().findObj("Opt");
			if ( obj != null && obj.isArray() ) {
				int sz = (int)obj.size();
				for ( int i = 0; i < sz; ++i ) {
					Obj o = obj.getAt(i);
					if ( o != null ) {
						if ( o.isString() ) {
							String str = o.getAsPDFText();
							CompoundButton btn = null;
							if ( mSingleChoice ) {
								btn = new RadioButton(mCtrl.getContext());
							}
							else {
								btn = new CheckBox(mCtrl.getContext());
							}
							btn.setText("  " + str);
							al.add(btn);
						}
						else if ( o.isArray() && o.size() == 2 ) {
							Obj s = o.getAt(1);
							if ( s != null && s.isString() ) {
								String str = s.getAsPDFText();
								CompoundButton btn = null;
								if ( mSingleChoice ) {
									btn = new RadioButton(mCtrl.getContext());
								}
								else {
									btn = new CheckBox(mCtrl.getContext());
								}
								btn.setText("  " + str);
								al.add(btn);
							}
						}
					}
				}
				return al;
			}
		}
		catch (Exception e) {
		}
		return null;
	}
	
	
	//find the selected items from a multiple choice list
	private HashSet<Integer> getSelectedPositions() {
		try {
			HashSet<Integer> al = new HashSet<Integer>();
			Obj val = mField.getValue();
			if ( val != null ) {
				if ( val.isString() ) {
					Obj o = mAnnot.getSDFObj().findObj("Opt");
					if ( o != null ) {
						int id =  GetOptIdx(val, o);
						if ( id >= 0 ) {
							al.add(id);
						}
					}
				}
				else if ( val.isArray() ) {
					int sz = (int)val.size();
					for ( int i = 0; i < sz; ++i ) {
						Obj entry = val.getAt(i);
						if ( entry.isString() ) {
							Obj o = mAnnot.getSDFObj().findObj("Opt");
							if ( o != null ) {
								int id = GetOptIdx(entry, o);
								if ( id >= 0 ) {
									al.add(id);
								}
							}
						}
					}
				}
			}
			
			return al;
		}
		catch (Exception e) {
			return null;
		}
		
	}
	
	
	private Integer GetOptIdx(Obj str_val, Obj opt) {
		try {
			int sz = (int)opt.size();
			String str_val_string = new String(str_val.getBuffer());
			for ( int i = 0; i < sz; ++i ) {
				Obj v = opt.getAt(i);
				if ( v.isString() && str_val.size() == v.size() ) {
					String v_string = new String(v.getBuffer());
					if ( str_val_string.equals(v_string) ) {
						return i;
					}
				}
				else if ( v.isArray() && v.size() >= 2 && v.getAt(1).isString() && str_val.size() == v.getAt(1).size() ) {
					v = v.getAt(1);
					String v_string = new String(v.getBuffer());
					if ( str_val_string.equals(v_string) ) {
						return i;
					}
				}
			}
		}
		catch ( Exception e ) {
		}
		
		return -1;
	}
}
