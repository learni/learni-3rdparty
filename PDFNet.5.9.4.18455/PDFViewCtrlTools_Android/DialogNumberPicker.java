//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

class DialogNumberPicker extends AlertDialog implements NumberPicker.OnValueChangeListener {
	private NumberPicker mInteger;
	private NumberPicker mDecimal;
	public DialogNumberPicker(Context context, float val) {
		super(context);

		setTitle("Thickness in Point");
		
		TableLayout layout = new TableLayout(context);
        layout.setPadding(6, 6, 6, 6);

        mInteger = new NumberPicker(context);
		mDecimal = new NumberPicker(context);
		mInteger.setMinValue(0);
		mInteger.setMaxValue(100);
		mInteger.setOnLongPressUpdateInterval(50);
		mDecimal.setMinValue(0);
		mDecimal.setMaxValue(10);
		mInteger.setValue((int)val);
		val -= (int)val;
		val = (float)Math.floor(val*10+0.5f);
		mDecimal.setValue((int)val);
		
		mInteger.setOnValueChangedListener(this);
		mDecimal.setOnValueChangedListener(this);
		
		//mTime = new TimePicker(context);
		
		/*
		mInteger.setOnKeyListener(
				new View.OnKeyListener() {
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						int k = 0;
						return false;
					}
				});
		*/
		//mDecimal.setOnKeyListener(this);
		
		 TextView dot = new TextView(context);
         dot.setText(".");
         dot.setTextSize(32);
         
         TableRow row_one = new TableRow(context);
         row_one.setGravity(Gravity.CENTER);
         row_one.addView(mInteger);
         row_one.addView(dot);
         row_one.addView(mDecimal);
         
         //row_one.addView(mTime);
  
         TableLayout table_main = new TableLayout(context);
         table_main.addView(row_one);

         TableRow row_main = new TableRow(context);
         row_main.setGravity(Gravity.CENTER_HORIZONTAL);
         row_main.addView(table_main);

         layout.addView(row_main);

         setView(layout, 8, 8, 8, 8);
	}
	
	protected void onStop () {
		//float v = mInteger.getValue();
		//Log.v("PDFNet", "on stop");
	}

	public float getNumber() {
		//int t = mTime.getCurrentHour();
		float v = mInteger.getValue() + ((float)mDecimal.getValue())/10;
		return v;
	}


	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub
	}
}
