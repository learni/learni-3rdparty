//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdfnet.demo.pdfviewctrl;

import java.io.File;
import java.io.InputStream;

import pdftron.Common.PDFNetException;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFNet;
import pdftron.PDF.PDFViewCtrl;
import pdftron.SDF.SDFDoc;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;


public class PDFViewCtrlDemo extends Activity implements PDFViewCtrl.RenderingListener {
	
	private PDFViewCtrl mPDFView; //derived from anroid.view.ViewGroup
	private RenderSpinner mSpinner = null; //used to indicate if a page is being rendered
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	/*
    	 * initialize PDFNet 
    	 */
        try {
        	PDFNet.initialize(this);								//no license key, will produce water-marks
        	//Log.v("PDFNet", "Version: " + PDFNet.getVersion());	//check the version number
        	//PDFNet.initialize(this, "your license key");			//full version
        	//PDFNet.setColorManagement(PDFNet.e_lcms);				//sets color management (more accurate, but more expensive)
        	
        	//PDFNet.setDefaultDiskCachingEnabled(false);			//should disallow disk caching if write-external-storage is not permitted.
        															//to add the permission: add <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        	//PDFNet.setViewerCache(500*1024*1024, 1000);			//set viewer cache to 500MB, and 1000% zoom factor.
    	}
        catch (Exception e) {
        	return;
        }
  
        
		/*
    	 * create PDFViewCtrl
    	 */
        boolean inflate = false;
        if ( !inflate ) {
        	//not through inflation; need to call initScrollbars to setup the scroll bars.
        	mPDFView = new PDFViewCtrl(this, null);
			TypedArray a = obtainStyledAttributes(R.styleable.View);
        	mPDFView.initScrollbars(a);
        	setContentView(mPDFView);
        }
        else {
        	//through inflation; scroll bars are setup in main.xml
        	setContentView(R.layout.main);
			mPDFView = (PDFViewCtrl)findViewById(R.id.pdfviewctrl);
        }
        
		/*
		 * Hardware acceleration: if the target device runs Android API 11 or
		 * higher, you can turn on hardware acceleration by using
		 * android:hardwareAccelerated="true" in AndroidManifest.xml file.
		 */
        
		
		/*
		 * use the tool manager to add additional UI modules to PDFViewCtrl.
		 * PDFNet SDK ships with a Tools.jar that contains various modules,
		 * including annotation editing, text search, text selection, and etc.
		 * if you are looking for a bare bone viewer with only basic viewing
		 * features, such as scrolling and zooming, simply comment out the following
		 * two lines. 
		 */
		pdftron.PDF.Tools.ToolManager tm = new pdftron.PDF.Tools.ToolManager();
		mPDFView.setToolManager(tm);
		
		
		/*
		 * misc PDFViewCtrl settings
		 */
		mPDFView.setPagePresentationMode(PDFViewCtrl.PAGE_PRESENTATION_SINGLE);	//set to single page mode; default is single continuous .
		//mPDFView.setUseThumbView(true, false);								//use the thumbs from the input PDF file (more efficient).
		//mPDFView.setProgressiveRendering(false);								//turn off progressive rendering.
		//mPDFView.setImageSmoothing(true);										//turn on image smoothing (better quality, but more expensive).
		mPDFView.setHighlightFields(true); 										//turn on form fields highlighting.
		//mPDFView.setCaching(true);											//turn on caching (consume more memory, but faster).
		//mPDFView.setOverprint(PDFViewCtrl.OVERPRINT_PDFX);					//turn on overprint for PDF/X files (more accurate, but more expensive).
		//mPDFView.setPageRefViewMode(PDFViewCtrl.PAGE_VIEW_FIT_WIDTH);			//set the reference page view mode.
		
		
		/*
		 * if you want to set the background of PDFViewCtrl to a Drawable, you
		 * can first set its background to be transparent and then set the
		 * drawable.
		 */
		//mPDFView.setClientBackgroundColor(255, 255, 255, true);
		//Drawable draw = ...
		//mPDFView.setBackgroundDrawable(draw);
		
		
		/*
		 * set zoom limits
		 */
		//mPDFView.setZoomLimits(PDFViewCtrl.ZOOM_LIMIT_RELATIVE, 1.0, 4);

		
		/*
		 * set content memory usage
		 */
		//mPDFView.setRenderedContentCacheSize( (long)(Runtime.getRuntime().maxMemory() / (1024*1024) * 0.5));
		
		
		/*
		 * load a PDF file.
		 */
		boolean load_from_url = false; //make it 'true' to try PDFViewCtrl's incremental downloading capability.
		
		if ( !load_from_url ) {
			PDFDoc doc = null;
	        try {
	        	//load from resource
	        	Resources rs = getResources();
				InputStream fis = rs.openRawResource(R.raw.sample);
				doc = new PDFDoc(fis);
				
	        	//load from file name
	        	//doc = new PDFDoc("/mnt/sdcard/Download/sample.pdf"); 
	
	        	//load from standard file filter
				//StdFile std_filter = new StdFile("/mnt/sdcard/Download/sample.pdf", StdFile.e_read_mode);
				//doc = new PDFDoc(std_filter);
	        	
				// load from custom filter in which you can customize the
				// reading and writing process,
				// for, e.g., custom encryption/decryption.
	        	//File file = new File("/mnt/sdcard/Download/zoo.pdf");
	        	//RandomAccessFile raf = new RandomAccessFile(file, "r");
				//UserCustomFilter custom_filter = new UserCustomFilter(CustomFilter.READ_MODE, raf);
				//doc = new PDFDoc(custom_filter);
			} 
	        catch (Exception e) {
	        	doc = null;
	        }
	        
	        /*
	         * If the document is password protected, you can "unlock" it now, or PDFViewCtrl will
	         * prompt for password when setDoc() is called later.
	         */
	        //doc.initStdSecurityHandler("the password");
	        
	       	mPDFView.setDoc(doc);
		}
		
		else {
			try {
				String cache_file = this.getCacheDir().getPath() + "/pdfref.pdf";
				mPDFView.openURL("http://www.pdftron.com/downloads/pdfref.pdf", cache_file, "");
			} catch (PDFNetException e) {
				Toast t = Toast.makeText(mPDFView.getContext(), e.getMessage(), Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
			}
		}
		
		
		/*
		 * Uncomment out the following code if you want to show the spinner
		 * while the PDF is being rendered.
		 */
		//mSpinner = new RenderSpinner(mPDFView);
		//mPDFView.setRenderingListener(this);
       	      
 	
       	/*
       	 * Note: after setting the doc to PDFViewCtrl, it will be constantly accessed by PDFViewCtrl for rendering.
       	 * However, simultaneous accessing to a PDFDoc is not allowed. So, if the same PDFDoc is to be accessed
       	 * from other places, it is necessary to call PDFDoc.lock(), and this could wait until the current rendering
       	 * task 
       	 */
    }
    

    @Override
    protected void onPause () {
    	super.onPause();
    	if ( mPDFView != null ) {
    		mPDFView.pause();
    	}
    }
    
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if ( mPDFView != null ) {
    		mPDFView.resume();
    	}
    }
    
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if ( mPDFView != null ) {
    		mPDFView.destroy();
    		mPDFView = null;
    	}    	
    }
     
    public void onLowMemory () {
    	super.onLowMemory();
    	if ( mPDFView != null ) {
    		mPDFView.purgeMemory();
    	}
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	MenuItem item = menu.findItem(R.id.save_file);
    	if ( item != null ) {
	    	boolean modified = false;
	    	if ( mPDFView != null &&  mPDFView.getDoc() != null ) {
	    		try {
					modified = mPDFView.getDoc().isModified();
				} catch (Exception e) {}
	    	}
	    	item.setEnabled(modified);
    	}
    	return true;
    }
    
    
    /**
     * handles the "Open", "Save", and "Settings" option menu items.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menu_item)
    {
    	switch (menu_item.getItemId()) {
    	case R.id.open_file:
    		Intent file_open_intent = new Intent(this, FileOpenDlg.class);
        	startActivityForResult(file_open_intent, 1);
    		break;
    		
    	case R.id.open_url:
    		DialogURL dlg = new DialogURL(this, mPDFView);
    		dlg.show();
    		break;
    		
    	case R.id.save_file:
    		PDFDoc doc = mPDFView.getDoc();
    		if ( doc != null ) {
	    		try {
	    			doc.lock();	//note: document needs to be locked first before it can be saved.
	    			if ( doc.isModified() ) {
	    				String file_name = doc.getFileName();
	    				if ( file_name.length() == 0 ) {
	    					Toast.makeText(this, "no file path; cannot save", Toast.LENGTH_SHORT).show();
	    				}
	    				else {
							File file = new File(file_name);
							boolean exist = file.exists();
							if ( !exist || file.canWrite() ) {
								//use file name to output file
								doc.save(file_name, SDFDoc.e_compatibility, null);
								
								//use custom filter to output file
								//File f = new File("/mnt/sdcard/Download/custom_filter_test.pdf");
					        	//RandomAccessFile raf = new RandomAccessFile(f, "rw");
								//UserCustomFilter custom_filter = new UserCustomFilter(CustomFilter.WRITE_MODE, raf);
								//doc.save(custom_filter, 0);
								
								Toast.makeText(this, "saved \"" + file_name + "\"", Toast.LENGTH_SHORT).show();
							}
							else {
								Toast.makeText(this, "failed to save \"" + file_name + "\"", Toast.LENGTH_SHORT).show();
							}
	    				}
					}
				} catch (Exception e) {
					Log.v("PDFNet", e.toString());
				} 
				finally {
					try {
						doc.unlock();	//note: unlock the document is necessary.
					} catch (Exception e) {}
				}
    		}
			break;
			
    	case R.id.settings:
			final SettingsDlg d = new SettingsDlg(mPDFView.getContext(), mPDFView);
			
			d.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mPDFView.setProgressiveRendering(d.getProgressive());
					mPDFView.setPagePresentationMode(d.getPagePresentationMode());
					mPDFView.setPageViewMode(d.getPageViewMode());
				}
			});

			d.setButton2("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});

			d.show();
    		
    	default:
    		break;
    	}
    	return true;
    }

    
    /**
     * handles file opening.
     */
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	if ( resultCode == RESULT_OK) {
    		String str = data.getStringExtra("com.pdftron.pdfnet.demo.pdfviewctrl.FileOpenData");
    		if ( str.length() > 0 ) {
	    		try {
	    			PDFDoc doc = new PDFDoc(str);
	    			mPDFView.setDoc(doc);
	    		} catch (Exception e) {
	    			mPDFView.closeDoc();
	    		}
    		}
    	}
    }
    
    
	/**
	 * When rotating the device, android restarts this activity by default. To
	 * maintain the current activity, it is necessary to add
	 * android:configChanges="keyboardHidden|orientation" to the Activity's
	 * manifest node in AndroidManifest.xml, and then override this function.
	 */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	  super.onConfigurationChanged(newConfig);
    }
    
    
    private class DialogURL extends AlertDialog {
		private EditText mTextBox;
		private PDFViewCtrl mCtrl;
		
		public DialogURL(Context context, PDFViewCtrl ctrl) {
			super(context);
			setTitle("URL");
			mCtrl = ctrl;
			mTextBox = new EditText(context);
			ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(-1, -1);
			mTextBox.setLayoutParams(layout);
			setView(mTextBox, 8, 8, 8, 8);
			
			setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String url = mTextBox.getText().toString();
					try {
						if ( url.length() > 0 ) {
							String fn = url.substring( url.lastIndexOf('/'), url.length());
							String cache_file = mCtrl.getContext().getCacheDir().getPath() + fn;
							mCtrl.openURL(url, cache_file, "");
						}
					} catch (Exception e) {
						Log.v("PDFNet", e.toString());
					}
				}
			});

			setButton2("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			
		}
	
		public void show() {
			mTextBox.setText("http://www.pdftron.com/downloads/pdfref.pdf");
			super.show();
		}
	}

    
	//rendering spinner
	class RenderSpinner extends PopupWindow{
		PDFViewCtrl mCtrl;
		ProgressBar mProg;
		public RenderSpinner(PDFViewCtrl ctrl) {
			super(ctrl.getContext());
			mCtrl = ctrl;
			mProg = new ProgressBar(mCtrl.getContext(), null,  android.R.attr.progressBarStyle);
			setBackgroundDrawable(new BitmapDrawable());		//this is needed for setTouchInterceptor to work. Strange!
			setWidth(WindowManager.LayoutParams.FILL_PARENT);
			setHeight(WindowManager.LayoutParams.FILL_PARENT);	
			
			setFocusable(false);
		    setTouchable(false);
		    setOutsideTouchable(false);
		    setAnimationStyle(-1);
			setContentView(mProg);
		}
		
		
		public void show () {
			int[] sc = new int[2];
			mCtrl.getLocationOnScreen(sc);
			setWidth(100);
			setHeight(100);
			showAtLocation(mCtrl, Gravity.CENTER, sc[0], sc[1]);
		}
	}

	//callback if the client region of PDFViewCtrl has started to render
	@Override
	public void onRenderingStarted() {
		if ( mSpinner != null && !mSpinner.isShowing() ) {
			mSpinner.show();
		}
	}


	//callback if the client region of PDFViewCtrl has finished rendering
	@Override
	public void onRenderingFinished() {
		if ( mSpinner != null && mSpinner.isShowing() ) {
			mSpinner.dismiss();
		}
	}
}
