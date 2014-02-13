//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import pdftron.PDF.ColorPt;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import pdftron.PDF.Point;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

/**
 * This class is for creating a sticky note annotation.
 */
class StickyNoteCreate extends SimpleShapeCreate {

    private Path mNotePath;
    private float mNoteHeight;
    private float mNoteWidth;
    private float mNoteLineWidth;
    private float mNoteArrowHeight;
    private float mCornerRadius;
    private float mStickyNoteHeight;
    private float mStrokeThickness;

    public StickyNoteCreate(PDFViewCtrl ctrl) {
        super(ctrl);

        mStickyNoteHeight = this.convDp2Pix(60);
        mStrokeThickness = this.convDp2Pix(2);

        mNextToolMode = ToolManager.e_text_annot_create;

        mNoteHeight = mStickyNoteHeight;
        mNoteWidth = mNoteHeight * 1.45f;
        mNoteLineWidth = mNoteHeight / 20;
        mNoteArrowHeight = mNoteHeight / 4;
        mCornerRadius = mNoteHeight / 4;

        // Create a path that resembles the sticky note for drawing while creating it.
        // note that the sticky note is originated at the lower-left corner of the big
        // rectangle.
        mPaint.setStrokeWidth(mNoteLineWidth);
        mNotePath = new Path();

        float co = mCornerRadius;
        mNotePath.moveTo(0, -co);   // Lower-left after round corner
        mNotePath.rLineTo(0, -(mNoteHeight-2*co));
        mNotePath.rQuadTo(0, -co, co, -co);
        mNotePath.rLineTo(mNoteWidth-2*co, 0);
        mNotePath.rQuadTo(co, 0, co, co);
        mNotePath.rLineTo(0, mNoteHeight-2*co);
        mNotePath.rQuadTo(0, co, -co, co);

        mNotePath.rLineTo(-mNoteWidth+4.0f*co, 0);
        mNotePath.rLineTo(-1.75f*co, mNoteArrowHeight);
        mNotePath.rLineTo(0.5f*co, -mNoteArrowHeight);

        mNotePath.lineTo(co, 0);
        mNotePath.rQuadTo(-co, 0, -co, -co);
    }

    public int getMode() {
        return ToolManager.e_text_annot_create;
    }

    public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
        float x = mPt1.x;
        float y = mPt1.y;
        mPt1.x = e2.getX() + mPDFView.getScrollX();
        mPt1.y = e2.getY() + mPDFView.getScrollY();

        // Don't allow the annotation to go beyond the page
        if (mPageCropOnClientF != null) {
            if (mPt1.x < mPageCropOnClientF.left) {
                mPt1.x = mPageCropOnClientF.left;
            } else if (mPt1.x + mNoteWidth > mPageCropOnClientF.right) {
                mPt1.x = mPageCropOnClientF.right - mNoteWidth;
            }
            if (mPt1.y - mNoteHeight < mPageCropOnClientF.top) {
                mPt1.y = mPageCropOnClientF.top + mNoteHeight;
            } else if (mPt1.y > mPageCropOnClientF.bottom) {
                mPt1.y = mPageCropOnClientF.bottom;
            }
        }

        float min_x = Math.min(x, mPt1.x) - mNoteLineWidth;
        float max_x = Math.max(x, mPt1.x) + mNoteWidth + mNoteLineWidth;
        float min_y = Math.min(y, mPt1.y) - mNoteHeight - mNoteLineWidth;
        float max_y = Math.max(y, mPt1.y) + mNoteArrowHeight + mNoteLineWidth;
        mPDFView.invalidate((int)min_x, (int)min_y, (int)Math.ceil(max_x), (int)Math.ceil(max_y));
        return true;
    }

    public boolean onUp(MotionEvent e, int prior_event_type) {
        mNextToolMode = ToolManager.e_annot_edit;
        try {
            mPDFView.docLock(true);
            double [] pts;
            pts = mPDFView.convScreenPtToPagePt(mPt1.x - mPDFView.getScrollX(), mPt1.y - mPDFView.getScrollY(), mDownPageNum);
            Point p = new Point(pts[0], pts[1]);
            pdftron.PDF.Rect rect = new pdftron.PDF.Rect();
            rect.set(pts[0], pts[1], pts[0] + 20, pts[1] + 20);
            pdftron.PDF.Annots.Text text = pdftron.PDF.Annots.Text.create(mPDFView.getDoc(), p);
            text.setIcon(pdftron.PDF.Annots.Text.e_Comment);
            text.setColor(new ColorPt(1, 1, 0), 3);

            rect.set(pts[0] + 20, pts[1] + 20, pts[0] + 90, pts[1] + 90);
            pdftron.PDF.Annots.Popup pop = pdftron.PDF.Annots.Popup.create(mPDFView.getDoc(), rect);
            pop.setParent(text);
            text.setPopup(pop);

            text.refreshAppearance();
            Page page = mPDFView.getDoc().getPage(mDownPageNum);
            page.annotPushBack(text);
            page.annotPushBack(pop);

            mAnnot = text;
            mAnnotPageNum = mDownPageNum;
            buildAnnotBBox();

            mNextToolMode = ToolManager.e_annot_edit;
            mPDFView.update(mAnnot, mAnnotPageNum);

        } catch (Exception e1) {

        } finally {
            mPDFView.docUnlock();
        }

        mPDFView.waitForRendering();

        return false;
    }

    public boolean onDown(MotionEvent e) {
        super.onDown(e);
        mPaint.setStrokeWidth(mStrokeThickness);

        return false;
    }

    public void onDraw (Canvas canvas, Matrix tfm) {
        // During creating, draw the blown-up stick note stored in mNotePath.
        mNotePath.offset(mPt1.x, mPt1.y);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.rgb(255, 255, 0));
        boolean ha = mPDFView.isHardwareAccelerated();
        if (ha) {
            Path p = new Path();
            p.addPath(mNotePath);
            canvas.drawPath(p, mPaint);
        } else {
            canvas.drawPath(mNotePath, mPaint);
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        if (ha) {
            Path p = new Path();
            p.addPath(mNotePath);
            canvas.drawPath(p, mPaint);
        } else {
            canvas.drawPath(mNotePath, mPaint);
        }

        float x = mPt1.x + mCornerRadius;
        float y = mPt1.y - mNoteHeight + mCornerRadius;
        canvas.drawLine(x, y, x + mNoteWidth - 2 * mCornerRadius, y, mPaint);
        float dy = (mNoteHeight - 2 * mCornerRadius) / 2;
        y += dy;
        canvas.drawLine(x, y, x + mNoteWidth - 2 * mCornerRadius, y, mPaint);
        y += dy;
        canvas.drawLine(x, y, x + mNoteWidth - 3 * mCornerRadius, y, mPaint);

        mNotePath.offset(-mPt1.x, -mPt1.y);
    }
}
