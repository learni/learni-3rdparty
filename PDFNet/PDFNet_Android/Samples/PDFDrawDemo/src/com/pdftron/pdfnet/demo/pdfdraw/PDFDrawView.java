//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2012 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdfnet.demo.pdfdraw;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import pdftron.Common.PDFNetException;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFDraw;
import pdftron.PDF.Page;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;


public class PDFDrawView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private PDFDrawThread mRdrThread;
    private Lock mPageInfoLock; 
    private PDFDraw mPDFDraw;
    private PDFDoc mPDFDoc;
    private int mPageCount;
    private int mCurPageNum;
    private PageViews mPageViews;
    private int mPageToRender;
    
    
    public PDFDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    	  
        mHolder = getHolder();
        mHolder.addCallback(this);

        mPageInfoLock = new ReentrantLock();
        mCurPageNum = 0;
        mPageCount = 0;
        
        /**
         * create a PDFDraw instance
         */
        try {
			mPDFDraw = new PDFDraw();
		} catch (Exception e) {
		}
		
		 /**
         * cache the rendered pages
         */
		mPageViews = new PageViews(4);
		setFocusable(true);
		
		/**
         * create the drawing thread, which will be started in surfaceCreated()
         */
		mRdrThread = new PDFDrawThread(mHolder);
    }
    
    
    @Override
    protected void onLayout (boolean changed, int l, int t, int r, int b) {
    	if ( changed ) {
        	int width = r - l;
    		int height = b - t;
    		boolean preserve_aspect_ratio = true;
    		try {
				mPDFDraw.setImageSize(width, height, preserve_aspect_ratio);
			} catch (PDFNetException e) {
			}
    	}
	}
    
    
    /**
     * set the PDF file for drawing
     */
    public void setDoc(PDFDoc doc) {
    	mPageViews.clear();
    	mPDFDoc = doc;
    	try {
			mPDFDoc.initSecurityHandler();
			mCurPageNum = 1;
			mPageToRender = 1;
			mPageCount = mPDFDoc.getPageCount();
		} catch (PDFNetException e) {
		}
		
		/**
         * create a new drawing thread; old thread should
         * have been stopped in surfaceDestroyed().
         */
		mRdrThread = new PDFDrawThread(mHolder);
		
		Toast t = Toast.makeText(getContext(), "Touch top/bottom half of the view to turn pages.", Toast.LENGTH_LONG);
		t.setGravity(Gravity.CENTER, 0, 0);
		t.show();
    }
    
    
    public boolean onTouchEvent (MotionEvent event) {
		super.onTouchEvent(event);
		
		/**
         * touch down action turns pages.
         */
		if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
			int height = this.getHeight();
			int pos = (int)event.getY();
			boolean go_up = pos < height/2;
			
			mPageInfoLock.lock();
    		
			boolean flag = false;
			if ( !go_up ) {
				//touched the bottom half, go to the next page
	   			if (mCurPageNum < mPageCount) {
	           		mCurPageNum++;
	           		flag = true;
	   			}
			}
			else {
				//touched the top half, go to the previous page
				if (mCurPageNum > 1) {
            		mCurPageNum--;
            		flag = true;
          		}
			}
    		
    		if (flag) {
    			//check if drawn page is in cache; if not, draw it.
    			PageView pv = mPageViews.getPageView(mCurPageNum);
        		if (pv!=null) {
        			mPageToRender = -1;
        			Canvas c = null;
        			SurfaceHolder holder = getHolder();
        			try {
	        			c = holder.lockCanvas(null);
	                    synchronized (holder) {
	                    	c.drawBitmap(pv.mBmp, 0, 0, null);
	                    }
        			} finally {
	                    if (c != null) {
	                    	holder.unlockCanvasAndPost(c);
	                    }
	                }
        		}
        		else {
        			//set the page to be drawn, and the drawing thread
        			//will pick it up.
        			mPageToRender = mCurPageNum;
        		}
    		}
    		
    		mPageInfoLock.unlock();
    	}
		
		return true;
    }  
    
    
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
    }

    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	mPageViews.clear();
    	mPageToRender = mCurPageNum;
        mRdrThread = new PDFDrawThread(mHolder);
        
    	mRdrThread.setRunning(true);
    	mRdrThread.start();
    }

    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
            	//stop the running thread
            	mRdrThread.setRunning(false);
            	mRdrThread.join();
            	retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    
    
    /**
	 * this stores a page number and the drawn bitmap of that page.
	 */
	class PageView {
		public int mPageNum;
		public Bitmap mBmp;
		
		 public PageView() {
			 mPageNum = 0;
		 }
		 
		 public PageView(int pn) {
			 mPageNum = pn;
		 }
		 
		 public PageView(int pn, Bitmap bmp) {
			 mPageNum = pn;
			 mBmp = bmp;
		 }
	}
	
	class ViewCompare implements Comparator<PageView> {
		  public int compare(PageView p1, PageView p2) {
			  return p1.mPageNum-p2.mPageNum;
		  }
	}

	
	/**
	 * this class caches the drawn page bitmaps.
	 */
	class PageViews {
		int mCapacity;
		TreeSet<PageView> mSet;
		
		public PageViews(int capacity) {
			mCapacity = capacity;
			mSet = new TreeSet<PageView>(new ViewCompare());
		}
		
		public boolean hasPage(PageView pv) {
			return mSet.contains(pv);
		}
		
		public PageView getPageView(int pn) {
			if (mSet.isEmpty()) {
				return null;
			}
			
			//PageView pv = mSet.floor(new PageView(pn));	//Android 3.0 above
			PageView pv = findPageView(pn);
			if ( pv != null && pv.mPageNum == pn) {
				return pv;
			}
			
			return null;
		}
		
		public void clear() {
			mSet.clear();
		}
		
		private PageView findPageView(int pn) {
			PageView pv = null;
			Iterator<PageView> itr = mSet.iterator();
			while (itr.hasNext()) {
				PageView v = itr.next();
				if ( pv == null && v.mPageNum == pn ) {
					pv = v;
				}
				//just keep iterating so that next time the iterator is right.
			}
			return pv;
		}
		
		public void add(PageView pv) {
			if (!mSet.contains(pv))
			{
				
				if (mSet.size()==mCapacity) {
					int max_pn_dif = 0;
					PageView max_pv = null;
					for (Iterator<PageView> itr = mSet.iterator(); itr.hasNext(); ) {
					    PageView p = itr.next(); 
					    int pn_dif = pv.mPageNum - p.mPageNum;
					    if (pn_dif<0)
					    	pn_dif = -pn_dif;
					    if (pn_dif>max_pn_dif) {
					    	max_pn_dif = pn_dif;
					    	max_pv = p;
					    }
					}
					mSet.remove(max_pv);
					mSet.add(pv);
				}
				else {
					mSet.add(pv);
				}
			}
		}
	}
	

	 /**
     * this is the background thread that uses PDFDraw to render PDF pages
     */
    class PDFDrawThread extends Thread {
        private boolean mRun = false;
        private SurfaceHolder mSurfaceHolder;
        
        public PDFDrawThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;       
        }

        public void setRunning(boolean running) {
            mRun = running;
        }

        @Override
        public void run() {
        	while (mRun) {
	        	 /**
	             * check if there is a page to render
	             */
        		if ( mPageToRender > 0 ) {
		        	mPageInfoLock.lock();
		        	int page1 = mPageToRender;
		        	mPageInfoLock.unlock();
		        	
		        	if (page1>=1) {
		        		Bitmap bmp = doRender(page1);	//render the page
		        		if ( bmp != null ) {
			        		Canvas c = null;
			            	try {
			            		mPageInfoLock.lock();		//make it thread-safe
			            		
			            		int page2 = mPageToRender;
			            		mPageViews.add(new PageView(page1, bmp));	//cache the rendered page
			            		
			            		if (page1==page2) {
			            			//if page hasn't been turned, put in on canvas
			            			c = mSurfaceHolder.lockCanvas(null);
			                        synchronized (mSurfaceHolder) {
			                        	c.drawBitmap(bmp, 0, 0, null);
			                        }
			                    }
			            		
			            		mPageToRender = -1;		//indicate that no page to render
			                } finally {
			                	mPageInfoLock.unlock();
			                    if (c != null) {
			                        mSurfaceHolder.unlockCanvasAndPost(c);
			                    }
			                }
		        		}
		        	}
        		}
        	}
        }
        
        /**
         * use pdftron.PDF.PDFDraw to render a page
         */
        private Bitmap doRender(int page_num) {
			try {
				Page pg = mPDFDoc.getPage(page_num);
				return mPDFDraw.getBitmap(pg);
			}
	        catch (Exception e) {
			}
	        return null;
        }
    }
}
