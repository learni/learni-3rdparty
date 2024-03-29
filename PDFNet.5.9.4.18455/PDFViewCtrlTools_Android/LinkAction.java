//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import pdftron.PDF.Action;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.QuadPoint;
import pdftron.PDF.Annots.Link;
import pdftron.SDF.Obj;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.view.MotionEvent;

class LinkAction extends Tool {
	private Link mLink;
	private Paint mPaint;
	
	public LinkAction(PDFViewCtrl ctrl) {
		super(ctrl);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.rgb(227, 112, 20));
	}
	
	
	public int getMode() {
		return ToolManager.e_link_action;
	}
	
	public boolean onSingleTapConfirmed(MotionEvent e) {
		if ( mAnnot != null ) {
			mNextToolMode = ToolManager.e_link_action;
			try {
				mPDFView.lockDoc(true);
				mLink = new Link(mAnnot);
				mPDFView.invalidate();
			} 
			catch (Exception e1) {
			}
			finally {
				mPDFView.unlockDoc();
			}
		}
		else {
			mNextToolMode = ToolManager.e_pan;
		}
		return false;
	}
	
	public void onPostSingleTapConfirmed() {
		mNextToolMode = ToolManager.e_pan;
		if ( mLink != null ) {
			Action a;
			try {
				mPDFView.lockDoc(true);
				a = mLink.getAction();
				if ( a != null ) {
					int at = a.getType();
					if ( at == Action.e_URI ) {
						Obj o = a.getSDFObj();
						o = o.findObj("URI");
						if ( o != null ) {
							String uri = o.getAsPDFText();
							Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
							mPDFView.getContext().startActivity(i);
						}
					}
					else if ( at == Action.e_GoTo ) {
						mPDFView.executeAction(a);
					}
					mPDFView.invalidate(); //draw away the highlight.
				}
			}
			catch (Exception e1) {
			}
			finally {
				mPDFView.unlockDoc();
			}
		}		
	}
	
	
	public boolean onLongPress(MotionEvent e) {
		if ( mAnnot != null ) {
			mNextToolMode = ToolManager.e_link_action;
			try {
				mPDFView.lockDoc(true);
				mLink = new Link(mAnnot);
				mPDFView.invalidate();
			} 
			catch (Exception e1) {
			}
			finally {
				mPDFView.unlockDoc();
			}
		}
		else {
			mNextToolMode = ToolManager.e_pan;
		}
		return false;
	}
	
	public void onDraw (Canvas canvas, Matrix tfm) {
		try {
			int qn = mLink.getQuadPointCount();
			float sx = mPDFView.getScrollX();
			float sy = mPDFView.getScrollY();
			for ( int i = 0; i < qn; ++i ) {
				QuadPoint qp = mLink.getQuadPoint(i);
				float top, left, bottom, right;
				float x1, y1, x2, y2;
				x1 = (float)Math.min(Math.min(Math.min(qp.p1.x, qp.p2.x), qp.p3.x), qp.p4.x);
				y2 = (float)Math.min(Math.min(Math.min(qp.p1.y, qp.p2.y), qp.p3.y), qp.p4.y);
				x2 = (float)Math.max(Math.max(Math.max(qp.p1.x, qp.p2.x), qp.p3.x), qp.p4.x);
				y1 = (float)Math.max(Math.max(Math.max(qp.p1.y, qp.p2.y), qp.p3.y), qp.p4.y);
				double[] pts = new double[2];
				pts = mPDFView.convPagePtToClientPt(x1, y1, mAnnotPageNum);
				left = (float)pts[0] + sx;
				top = (float)pts[1] + sy;
				pts = mPDFView.convPagePtToClientPt(x2, y2, mAnnotPageNum);
				right = (float)pts[0] + sx;
				bottom = (float)pts[1] + sy;
				
				mPaint.setStyle(Paint.Style.FILL);
				mPaint.setAlpha(128);
				canvas.drawRect(left, top, right, bottom, mPaint);
				
				float len = Math.min(right-left, bottom-top);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setStrokeWidth(Math.max(len/15, 2));
				mPaint.setAlpha(255);
				canvas.drawRect(left, top, right, bottom, mPaint);
			}
		} catch (Exception e) {}
	}
	
	public boolean onUp(MotionEvent e, int prior_event_type) {
		mNextToolMode = ToolManager.e_pan;
		if ( mLink != null ) {
			Action a;
			try {
				mPDFView.lockDoc(true);
				int x = (int)(e.getX()+0.5);
				int y = (int)(e.getY()+0.5);
				if (isInsideAnnot(x, y)) {
					a = mLink.getAction();
					int at = a.getType();
					if ( at == Action.e_URI ) {
						Obj o = a.getSDFObj();
						o = o.findObj("URI");
						if ( o != null ) {
							String uri = o.getAsPDFText();
							Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
							mPDFView.getContext().startActivity(i);
						}
					}
					else if ( at == Action.e_GoTo ) {
						mPDFView.executeAction(a);
					}
				}
				mPDFView.invalidate(); //draw away the highlight.
			}
			catch (Exception e1) {
			}
			finally {
				mPDFView.unlockDoc();
			}
		}
		return false;
	}
}