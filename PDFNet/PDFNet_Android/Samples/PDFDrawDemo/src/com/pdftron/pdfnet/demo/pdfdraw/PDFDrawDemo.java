//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------
package com.pdftron.pdfnet.demo.pdfdraw;

import java.io.InputStream;

import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFNet;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class PDFDrawDemo extends Activity {
    private PDFDrawView mPDFDrawView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	/*
    	 * initialize PDFNet 
    	 */
        try {
        	PDFNet.initialize(this);						//no license key, will produce water-marks
        	//PDFNet.initialize(this, "your license key");	//full version
    	}
        catch (Exception e) {
        	return;
        }
        
        /*
		 * load a PDF file.
		 */
		PDFDoc doc = null;
        try {
        	Resources rs = getResources();
			InputStream fis = rs.openRawResource(R.raw.pdf2xps);
			doc = new PDFDoc(fis);
        	//doc = new PDFDoc("/mnt/sdcard/Download/pdf2xps.pdf"); //load from disk via a file name.
		} 
        catch (Exception e) {
        }
        
        try {
        	setContentView(R.layout.main);
        }
        catch (Exception e) {
        	Log.v("PDFNet", e.toString());
        }
        
        
        mPDFDrawView = (PDFDrawView) findViewById(R.id.pdfdrawview);
        mPDFDrawView.setDoc(doc);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menu_item)
    {
    	switch (menu_item.getItemId()) {
    	case R.id.open_file:
    		Intent file_open_intent = new Intent(this, FileOpenDlg.class);
        	startActivityForResult(file_open_intent, 1);
    		break;
    	default:
    		break;
    	}
    	return true;
    }

    
    /**
     * open the selected document.
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	if ( resultCode == RESULT_OK) {
    		String str = data.getStringExtra("com.pdftron.pdfnet.demo.pdfdraw.FileOpenData");
    		if ( str.length() > 0 ) {
	    		try {
	    			PDFDoc doc = new PDFDoc(str);
	    			mPDFDrawView.setDoc(doc);
	    		} catch (Exception e) {
	    		}
    		}
    	}
    }
}
