//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdfnet.demo.pdfviewctrl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import android.os.Debug;
import pdftron.Common.PDFNetException;
import pdftron.Filters.CustomFilter;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFNet;
import pdftron.PDF.PDFViewCtrl;
import pdftron.SDF.SDFDoc;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PDFViewCtrlDemo extends FragmentActivity implements
        PDFViewCtrl.RenderingListener, PDFViewCtrl.DocumentDownloadListener,
        BookmarkDlg.OnBookmarkSelectedListener, TestingAutomationServer.TestingAutomationCallbackInterface {
    
    private PDFViewCtrl mPDFView; // Derived from android.view.ViewGroup
    private RenderSpinner mSpinner = null; // Used to indicate if a page is being rendered
    
    static final int OPEN_FILE_REQUEST = 0;
    static final int BOOKMARKS_NAV_REQUEST = 1;

	private TestingAutomationServer _taServer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		try {
			_taServer = new TestingAutomationServer(this);
			_taServer.start();
		}
		catch (Exception e) {
			_taServer = null;
		}

        /*
         * Initialize PDFNet
         */
        try {
			PDFNet.initialize(this, R.raw.pdfnet,
					"Bligear Ltd.(learni.net):OEM:Learni::IA:AMS(20140611):3E0AACCAE7665023C8DEB62F4E6F7FC700C5CAD504351B5AE528A60ABAB6F5C7");
            
            //PDFNet.initialize(this, R.raw.pdfnet, "your license key");  // Full version mode
            //Log.v("PDFNet", "Version: " + PDFNet.getVersion());   // Check the version number
            
            //PDFNet.setColorManagement(PDFNet.e_lcms);             // Sets color management (more accurate, but more expensive)
            
            // Disk caching should be disabled if write-external-storage is not permitted.
            // To add the permission, add <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
            // to the manifest file.
            //PDFNet.setDefaultDiskCachingEnabled(false);
            
            //PDFNet.setViewerCache(500*1024*1024, 1000);           // Set viewer cache to 500MB, and 1000% zoom factor.
        } catch (Exception e) {
            return;
        }
        
        /*
         * Create PDFViewCtrl
         */
        boolean inflate = false;
        if (!inflate) {
            // Not through inflation; need to call initScrollbars to setup the scroll bars.
            mPDFView = new PDFViewCtrl(this, null);
            TypedArray a = obtainStyledAttributes(R.styleable.View);
            mPDFView.initScrollbars(a);
            setContentView(mPDFView);
        } else {
            // Through inflation; scroll bars are setup in main.xml
            setContentView(R.layout.main);
            mPDFView = (PDFViewCtrl) findViewById(R.id.pdfviewctrl);
        }
        
        /*
         * Hardware acceleration: if the target device runs Android API 11 or higher,
         * you can turn on hardware acceleration by using
         * android:hardwareAccelerated="true" in AndroidManifest.xml file.
         */
        
        /*
         * Use the tool manager to add additional UI modules to PDFViewCtrl.
         * PDFNet SDK ships with a Tools.jar that contains various modules, including
         * annotation editing, text search, text selection, etc. If you are looking
         * for a bare bone viewer with only basic viewing features, such as
         * scrolling and zooming, simply comment out the following two lines.
         */
        pdftron.PDF.Tools.ToolManager tm = new pdftron.PDF.Tools.ToolManager();
        mPDFView.setToolManager(tm);
        
        /*
         * Miscellaneous PDFViewCtrl settings
         */
        // Set to single page mode; default is single continuous.
        mPDFView.setPagePresentationMode(PDFViewCtrl.PAGE_PRESENTATION_SINGLE_CONT);
        
        // Some examples of thumbnail usage:
        // Use the thumbs from the input PDF file or render new thumbs in case
        // there are no embedded thumbs (more efficient). Do not use persistent
        // cache.
        mPDFView.setupThumbnails(false, false, false, 0, "", 0);

        // Set to use runtime thumbnail generation and set the persistent disk cache
        // for PDF thumbs (improves rendering experience).
        //mPDFView.setupThumbnails(false,                         // Do not use embedded thumbs
        //        true,                                           // Generate thumbs at runtime
        //        true,                                           // Use persistent thumb cache
        //        0,                                              // 0 -> max thumb size is defined by the SDK
        //        getExternalFilesDir(null).getAbsolutePath(),    // Persistent thumb cache location 
        //        10 * 1024 * 1024);                              // Size of the data cache file (10 MB)
 
        mPDFView.setHighlightFields(true);                              // Turn on form fields highlighting.
        mPDFView.setProgressiveRendering(false);                      // Turn off progressive rendering.
        mPDFView.setImageSmoothing(true);                             // Turn on image smoothing (better quality, but more expensive)
        mPDFView.setCaching(true);                                    // Turn on caching (consume more memory, but faster)
        //mPDFView.setOverprint(PDFViewCtrl.OVERPRINT_PDFX);            // Turn on overprint for PDF/X files (more accurate, but more expensive)
        //mPDFView.setPageViewMode(PDFViewCtrl.PAGE_VIEW_FIT_WIDTH);    // Set the page view mode
        //mPDFView.setPageRefViewMode(PDFViewCtrl.PAGE_VIEW_FIT_WIDTH); // Set the reference page view mode (more meaningful for non-continuous modes)
        
		mPDFView.setRightToLeftLanguage(true);
		mPDFView.setZoomLimits(PDFViewCtrl.ZOOM_LIMIT_RELATIVE, 0.3, 4.0);
		mPDFView.setBuiltInPageSlidingEnabled(true);
		mPDFView.setPageViewMode(PDFViewCtrl.PAGE_VIEW_FIT_WIDTH);
		mPDFView.setPageRefViewMode(PDFViewCtrl.PAGE_VIEW_FIT_WIDTH);

		long allowed_max = Runtime.getRuntime().maxMemory() / (1024 * 1024);
		mPDFView.setRenderedContentCacheSize((long) (allowed_max * 0.5));
        
        /*
         * If you want to set the background of PDFViewCtrl to a Drawable,
         * you can first set its background to be transparent and then set
         * the Drawable.
         */
        //mPDFView.setClientBackgroundColor(255, 255, 255, true);
        //Drawable draw = ...
        //mPDFView.setBackgroundDrawable(draw);
        
        /*
         * Set zoom limits
         */
        //mPDFView.setZoomLimits(PDFViewCtrl.ZOOM_LIMIT_RELATIVE, 1.0, 4.0);
        
        /*
         * Set content memory usage
         */
        //mPDFView.setRenderedContentCacheSize((long)(Runtime.getRuntime().maxMemory() / (1024*1024) * 0.5));
        
        /*
         * Enables URL extraction
         */
        //mPDFView.setUrlExtraction(true);
        
        /*
         * Load a PDF file.
         */
        boolean load_from_url = false; // Make it 'true' to try PDFViewCtrl's incremental downloading capability.
        
        if (!load_from_url) {
            PDFDoc doc = null;
	        try {
	        	//load from resource
//	        	Resources rs = getResources();
//				InputStream fis = rs.openRawResource(R.raw.sample);
//				doc = new PDFDoc(fis);
				
	        	//load from file name
//	        	doc = new PDFDoc("/mnt/sdcard/Download/YM.pdf");
	
	        	//load from standard file filter
				//StdFile std_filter = new StdFile("/mnt/sdcard/Download/sample.pdf", StdFile.e_read_mode);
				//doc = new PDFDoc(std_filter);
	        	
				// load from custom filter in which you can customize the
				// reading and writing process,
				// for, e.g., custom encryption/decryption.
				boolean use_filter = true;
				String fileName = "/mnt/sdcard/Download/BM.pdf";
//				String fileName = "/mnt/sdcard/Download/BM_opt2.pdf";
//				String fileName = "/mnt/sdcard/Download/SM_A.pdf";
//				String tereName = "/mnt/sdcard/Download/SM_KS.pdf";
				if (use_filter) {
					File file = new File(fileName);
					RandomAccessFile raf = new RandomAccessFile(file, "r");
					UserCustomFilter custom_filter = new UserCustomFilter(CustomFilter.READ_MODE, raf);
					doc = new PDFDoc(custom_filter);
				}
				else {
	        		doc = new PDFDoc(fileName);
				}
			} 
	        catch (Exception e) {
	        	doc = null;
	        }
            
            /*
             * If the document is password protected, you can "unlock" it now,
             * or PDFViewCtrl will prompt for password when setDoc() is called later.
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
         * Uncomment the following code if you want to show the spinner while the PDF is being rendered.
         */
 		mSpinner = new RenderSpinner(mPDFView);
		mPDFView.setRenderingListener(this);
        
        /*
         * Use the DocumentDownloadListener to listen for download events (used with openURL()).
         */
        mPDFView.setDocumentDownloadListener(this);
        
        /*
         * Note: after setting the doc to PDFViewCtrl, it will be constantly accessed by
         * PDFViewCtrl for rendering. However, simultaneous accessing to a PDFDoc
         * is not allowed (unless for reading operations). So, if the same PDFDoc is to be
         * accessed from other places, it is necessary to call PDFDoc.lock(), and this
         * could wait until the current rendering task finishes. If you want access for
         * reading, you can use PDFViewCtrl.
         */
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mPDFView != null) {
            mPDFView.pause();
        }
		if (_taServer != null) {
			_taServer.stop();
		}

    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (mPDFView != null) {
            mPDFView.resume();
        }
		if (_taServer != null) {
			if (! _taServer.wasStarted()) {
				try {
					_taServer.start();
				} catch (IOException e) {
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}
		}

    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPDFView != null) {
            mPDFView.destroy();
            mPDFView = null;
        }
    }
    
    public void onLowMemory() {
        super.onLowMemory();
        if (mPDFView != null) {
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Let's enable/disable the menu options depending on
        // the document.
        MenuItem itemSave = menu.findItem(R.id.save_file);
        MenuItem itemBookmark = menu.findItem(R.id.bookmark);
        MenuItem itemGoToPage = menu.findItem(R.id.gotopage);

        if (mPDFView != null && mPDFView.getDoc() != null) {
            // Save item
            boolean modified = false;
            try {
                modified = mPDFView.getDoc().isModified();
            } catch (Exception e) {
            }
            itemSave.setEnabled(modified);
            itemBookmark.setEnabled(true);
            itemGoToPage.setEnabled(true);
        } else {
            itemSave.setEnabled(false);
            itemBookmark.setEnabled(false);
            itemGoToPage.setEnabled(false);
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menu_item) {
        switch (menu_item.getItemId()) {
        
        case R.id.open_file:
            Intent file_open_intent = new Intent(this, FileOpenDlg.class);
            startActivityForResult(file_open_intent, OPEN_FILE_REQUEST);
            break;
        
        case R.id.open_url:
            DialogURL dlg = new DialogURL(this, mPDFView);
            dlg.show();
            break;
        
        case R.id.save_file:
            PDFDoc doc = mPDFView.getDoc();
            boolean docLocked = false;
            if (doc != null) {
                try {
                    // Note: document needs to be locked first before it can be saved. Also,
                    // trying to acquire a write lock (doc.lock()) while we hold a read lock
                    // might throw an exception, so let's use tryLock() instead.
                    docLocked = doc.tryLock(); 
                    if (docLocked) {
                        if (doc.isModified()) {
                            String file_name = doc.getFileName();
                            if (file_name.length() == 0) {
                                Toast.makeText(this, "File has an invalid path; cannot save", Toast.LENGTH_SHORT).show();
                            } else {
                                File file = new File(file_name);
                                boolean exist = file.exists();
                                if (!exist || file.canWrite()) {
                                    // Use file name to output file
                                    doc.save(file_name, SDFDoc.e_compatibility, null);
                                    
                                    // Use custom filter to output file
                                    //File f = new File("/mnt/sdcard/Download/custom_filter_test.pdf");
                                    //RandomAccessFile raf = new RandomAccessFile(f, "rw");
                                    //UserCustomFilter custom_filter = new UserCustomFilter(CustomFilter.WRITE_MODE, raf);
                                    //doc.save(custom_filter, 0);
                                    
                                    Toast.makeText(this, "Saved \"" + file_name + "\"", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Failed to save \"" + file_name + "\"", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Failed to save: file is locked for writing", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.v("PDFNet", e.toString());
                } finally {
                    try {
                        // Note: it is necessary to unlock the document.
                        if (docLocked) {
                            doc.unlock();
                        }
                    } catch (Exception e) {
                        Log.v("PDFNet", e.toString());
                    }
                }
            }
            break;
        
        case R.id.settings:
            final SettingsDlg d = new SettingsDlg(mPDFView.getContext(), mPDFView);
            
            d.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mPDFView.setProgressiveRendering(d.getProgressive());
                    mPDFView.setPagePresentationMode(d.getPagePresentationMode());
                    mPDFView.setPageViewMode(d.getPageViewMode());
                }
            });
            
            d.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            
            d.show();
            break;
        
        case R.id.gotopage:
            DialogGoToPage dlgGotoPage = new DialogGoToPage(this, mPDFView);
            dlgGotoPage.show();
            break;
        
        case R.id.bookmark:
            BookmarkDlg bookmarkDlg = new BookmarkDlg(mPDFView.getDoc());
            bookmarkDlg.show(getSupportFragmentManager(), "bookmarks");
            break;
            
        default:
            break;
        }
        return true;
    }
    
    /*
     * Handles file opening.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String str = data.getStringExtra("com.pdftron.pdfnet.demo.pdfviewctrl.FileOpenData");
            if (str.length() > 0) {
                try {
                    PDFDoc doc = new PDFDoc(str);
                    mPDFView.setDoc(doc);
                } catch (Exception e) {
                    mPDFView.closeDoc();
                }
            }
        }
    }
    
    /*
     * When rotating the device, android restarts this activity by default.
     * To maintain the current activity, it is necessary to add
     * android:configChanges="keyboardHidden|orientation|screenSize" to the
     * Activity's manifest node in AndroidManifest.xml, and then override this function.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

	@Override
	public void TA_openDoc(String doc) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void TA_flipToPage(final int page) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				Debug.startMethodTracing("tron");
				mPDFView.setCurrentPage(page);
//				Debug.stopMethodTracing();
			}
		});
	}


    
    private class DialogURL extends AlertDialog {
        private EditText mTextBox;
        private PDFViewCtrl mCtrl;
        
        public DialogURL(Context context, PDFViewCtrl ctrl) {
            super(context);
            setTitle("Open URL");
            setIcon(0);
            mCtrl = ctrl;
            mTextBox = new EditText(context);
            mTextBox.setHint("Please, enter the URL");
            ViewGroup.LayoutParams layout = new ViewGroup.LayoutParams(-1, -1);
            mTextBox.setLayoutParams(layout);
            setView(mTextBox, 8, 8, 8, 8);
            
            setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String url = mTextBox.getText().toString();
                    try {
                        if (url.length() > 0) {
                            String fn = url.substring(url.lastIndexOf('/'), url.length());
                            String cache_file = mCtrl.getContext().getCacheDir().getPath() + fn;
                            mCtrl.openURL(url, cache_file, "");
                        }
                    } catch (Exception e) {
                        Log.v("PDFNet", e.toString());
                    }
                }
            });
            
            setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        
        public void show() {
            mTextBox.setText("http://www.pdftron.com/downloads/pdfref.pdf");
            super.show();
        }
    }
    
    private class DialogGoToPage {
        private EditText mTextBox;
        private PDFViewCtrl mCtrl;
        private Context mContext;
        private String mHint;
        private int mPageCount;
        
        public DialogGoToPage(Context context, PDFViewCtrl ctrl) {
            mCtrl = ctrl;
            mContext = context;
            mPageCount = 0;
            mHint = "";
            try {
                PDFDoc doc = mCtrl.getDoc();
                if (doc != null) {
                    mPageCount = doc.getPageCount();
                    if (mPageCount > 0) {
                        mHint = "Enter page number (1 - " + mPageCount + ")";
                    }
                }
            } catch (Exception e) {
                mPageCount = 0;
            }
        }
        
        public void show() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Go To Page");
            mTextBox = new EditText(mContext);
            mTextBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            if (mPageCount > 0) {
                mTextBox.setHint(mHint);
            }
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
            mTextBox.setLayoutParams(layoutParams);
            FrameLayout layout = new FrameLayout(mPDFView.getContext());
            layout.addView(mTextBox);
            layout.setPadding(12, 0, 12, 8);
            builder.setView(layout);
            
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing here because we override this button later
                    // to change the close behaviour. However, we still need
                    // this because on older versions of Android unless we pass
                    // a handler the button doesn't get instantiated.
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pageNum = 0;
                            try {
                                pageNum = Integer.parseInt(mTextBox.getText().toString());
                            } catch (NumberFormatException nfe) {
                                pageNum = 0;
                            }
                            if (pageNum > 0 && pageNum <= mPageCount) {
                                mCtrl.setCurrentPage(pageNum);
                                dialog.dismiss();
                            } else {
                                mTextBox.setText("");
                            }
                        }
                    });
        }
    }
    
    /*
     * Rendering spinner
     */
    class RenderSpinner extends PopupWindow {
        PDFViewCtrl mCtrl;
        ProgressBar mProg;
        
        public RenderSpinner(PDFViewCtrl ctrl) {
            super(ctrl.getContext());
            mCtrl = ctrl;
            mProg = new ProgressBar(mCtrl.getContext(), null, android.R.attr.progressBarStyle);
            setBackgroundDrawable(new BitmapDrawable()); // this is needed for setTouchInterceptor to work. Strange!
            setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            
            setFocusable(false);
            setTouchable(false);
            setOutsideTouchable(false);
            setAnimationStyle(-1);
            setContentView(mProg);
        }
        
        public void show() {
            int[] sc = new int[2];
            mCtrl.getLocationOnScreen(sc);
            setWidth(100);
            setHeight(100);
            showAtLocation(mCtrl, Gravity.CENTER, sc[0], sc[1]);
        }
    }
    
    // Callback if the client region of PDFViewCtrl has started to render
    @Override
    public void onRenderingStarted() {
        if (mSpinner != null && !mSpinner.isShowing()) {
            mSpinner.show();
        }
		if (_taServer != null) {
			_taServer.addMeasurement("renderingStarted");
		}

    }
    
    // Callback if the client region of PDFViewCtrl has finished rendering
    @Override
    public void onRenderingFinished() {
        if (mSpinner != null && mSpinner.isShowing()) {
            mSpinner.dismiss();
        }
		if (_taServer != null) {
			_taServer.addMeasurement("renderingDone");
		}

    }
    
    // Callback for download events (used with openUrl()).
    @Override
    public void onDownloadEvent(int type, int page_num, int page_downloaded, int page_count, String message) {
        switch (type) {
        case PDFViewCtrl.DOWNLOAD_FINISHED:
            Toast.makeText(this, "Download Finished", Toast.LENGTH_LONG).show();
            break;
        case PDFViewCtrl.DOWNLOAD_FAILED:
            Toast.makeText(this, "Download Failed", Toast.LENGTH_LONG).show();
            break;
        default:
            break;
        }
    }

    // Callback for the bookmark dialog
    @Override
    public void onBookmarkSelected(int pageNum) {
        mPDFView.setCurrentPage(pageNum);
    }
}