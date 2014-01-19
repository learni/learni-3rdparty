//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import pdftron.Common.PDFNetException;
import pdftron.PDF.Annot.BorderStyle;
import pdftron.PDF.ColorPt;
import pdftron.PDF.Element;
import pdftron.PDF.ElementReader;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import pdftron.PDF.Rect;
import pdftron.SDF.Obj;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.MotionEvent;
import com.pdftron.pdf.tools.R;

class FreeTextCreate extends Tool {

    private PointF mPoint;
    private int mPageNum;
    private int mTextColor;
    private int mTextSize;

    public FreeTextCreate(PDFViewCtrl ctrl) {
        super(ctrl);
        mNextToolMode = ToolManager.e_text_create;
        this.mPoint = new PointF(0, 0);
    }

    @Override
    public int getMode() {
        return ToolManager.e_text_create;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        SharedPreferences settings = mPDFView.getContext().getSharedPreferences(Tool.PREFS_FILE_NAME, 0);
        mTextColor = settings.getInt("annotation_freetext_creation_color", 0xFFFF0000);
        mTextSize = settings.getInt("annotation_freetext_creation_size", 16);
        return super.onDown(e);
    }

    @Override
    public boolean onUp(MotionEvent e, int prior_event_type) {
        mNextToolMode = ToolManager.e_pan;

        mPoint.x = e.getX() + mPDFView.getScrollX();
        mPoint.y = e.getY() + mPDFView.getScrollY();

        mPageNum = mPDFView.getPageNumberFromScreenPt(e.getX(), e.getY());
        if (mPageNum < 1) {
            mPageNum = mPDFView.getCurrentPage();
        }

        final DialogAnnotNote d = new DialogAnnotNote(mPDFView.getContext(), "");
        d.setTitle(mPDFView.getResources().getString(R.string.tools_qm_text));
        d.setButton(AlertDialog.BUTTON_POSITIVE, mPDFView.getResources().getString(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    mPDFView.docLock(true);
                    if (!TextUtils.isEmpty(d.getNote())) {
                        pdftron.PDF.Rect bbox = getTextBBoxOnPage();
                        pdftron.PDF.Annots.FreeText freeText = pdftron.PDF.Annots.FreeText.create(mPDFView.getDoc(), bbox);
                        freeText.setContents(d.getNote());
                        freeText.setFontSize(mTextSize);
                        
                        // Set border style and color
                        BorderStyle border = freeText.getBorderStyle();
                        border.setWidth(0);
                        freeText.setBorderStyle(border);
                        double r = (double)Color.red(mTextColor) / 255;
                        double g = (double)Color.green(mTextColor) / 255;
                        double b = (double)Color.blue(mTextColor) / 255;
                        ColorPt color = new ColorPt(r, g, b);
                        freeText.setTextColor(color, 3);
                        freeText.refreshAppearance();
                        
                        // Get the annotation's content stream and iterate through elements to union
                        // their bounding boxes
                        Obj contentStream = freeText.getSDFObj().findObj("AP").findObj("N");
                        ElementReader er = new ElementReader();
                        Rect unionRect = null;
                        Element element;
                        
                        er.begin(contentStream);
                        for (element = er.next(); element != null; element = er.next()) {
                            Rect rect = element.getBBox();
                            if (rect != null && element.getType() == Element.e_text) {
                                if (unionRect == null) {
                                    unionRect = rect;
                                }
                                unionRect = getRectUnion(rect, unionRect);
                            }
                        }
                        
                        unionRect.setY1(unionRect.getY1() - 25);
                        unionRect.setX2(unionRect.getX2() + 25);
                        
                        // Move annotation back into position
                        double x1, y1, x2, y2;
                        x1 = unionRect.getX1();
                        y1 = unionRect.getY1();
                        x2 = unionRect.getX2();
                        y2 = unionRect.getY2();
                        double [] pt1;
                        double [] pt2;
                        
                        pt1 = mPDFView.convPagePtToScreenPt(x1, y1, mPageNum);
                        pt2 = mPDFView.convPagePtToScreenPt(x2, y2, mPageNum);

                        int pageRotation = mPDFView.getDoc().getPage(mPageNum).getRotation();
                        double xDist, yDist;
                        if (pageRotation == Page.e_90 || pageRotation == Page.e_270)
                        {
                            xDist = Math.abs(pt1[1] - pt2[1]);
                            yDist = Math.abs(pt1[0] - pt2[0]);
                        } else {
                            xDist = Math.abs(pt1[0] - pt2[0]);
                            yDist = Math.abs(pt1[1] - pt2[1]);
                        }
                        x1 = mPoint.x - mPDFView.getScrollX();
                        y1 = mPoint.y - mPDFView.getScrollY();
                        x2 = mPoint.x + xDist - mPDFView.getScrollX();
                        y2 = mPoint.y + yDist - mPDFView.getScrollY();
                        
                        // Let's make sure we do not go beyond page borders
                        RectF pageCropOnClientF = buildPageBoundBoxOnClient(mPageNum);
                        if (y2 > pageCropOnClientF.bottom) {
                            y2 = pageCropOnClientF.bottom;
                        }
                        if (x2 > pageCropOnClientF.right) {
                            x2 = pageCropOnClientF.right;
                        }
                        // and that we have a visible bounding box when
                        // inserting the free text close to the border
                        if ((pageCropOnClientF.bottom - y1) < 150) {
                            y1 = pageCropOnClientF.bottom - 150;
                        }
                        if ((pageCropOnClientF.right - x1) < 150) {
                            x1 = pageCropOnClientF.right - 150;
                        }
                        
                        pt1 = mPDFView.convScreenPtToPagePt(x1, y1, mPageNum);
                        pt2 = mPDFView.convScreenPtToPagePt(x2, y2, mPageNum);
                        
                        freeText.resize(new pdftron.PDF.Rect(pt1[0], pt1[1], pt2[0], pt2[1]));
                        freeText.refreshAppearance();
                        
                        Page page = mPDFView.getDoc().getPage(mPageNum);
                        page.annotPushBack(freeText);
                        
                        freeText.refreshAppearance();
                        
                        mAnnot = freeText;
                        mAnnotPageNum = mPageNum;
                        buildAnnotBBox();
                        
                        mPDFView.update(mAnnot, mAnnotPageNum);
                    }

                } catch (Exception e) {

                } finally {
                    mPDFView.docUnlock();
                }
                
                mPDFView.waitForRendering();
            }
        });
        
        d.setButton(AlertDialog.BUTTON_NEGATIVE, mPDFView.getResources().getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });
        
        d.show();

        return false;
    }

    private pdftron.PDF.Rect getRectUnion(pdftron.PDF.Rect rect1, pdftron.PDF.Rect rect2){
        pdftron.PDF.Rect rectUnion = null;
        try {
            rectUnion = new pdftron.PDF.Rect();
            rectUnion.setX1(Math.min(rect1.getX1(), rect2.getX1()));
            rectUnion.setY1(Math.min(rect1.getY1(), rect2.getY1()));
            rectUnion.setX2(Math.max(rect1.getX2(), rect2.getX2()));
            rectUnion.setY2(Math.max(rect1.getY2(), rect2.getY2()));
        } catch (PDFNetException e) {
        }
        return rectUnion;
    }

    private pdftron.PDF.Rect getTextBBox() {
        double [] pts1;
        double [] pts2;
        pts1 = mPDFView.convScreenPtToPagePt(mPoint.x - mPDFView.getScrollX(), mPoint.y - mPDFView.getScrollY(), mPageNum);
        pts2 = mPDFView.convScreenPtToPagePt(mPoint.x + 100 - mPDFView.getScrollX(), mPoint.y + 100 - mPDFView.getScrollY(), mPageNum);
        pdftron.PDF.Rect rect;
        try {
            rect = new pdftron.PDF.Rect(pts1[0], pts1[1], pts2[0], pts2[1]);
            return rect;
        } catch (Exception e) {
            return null;
        }
    }

    private pdftron.PDF.Rect getTextBBoxOnPage() {
        double [] pt1c = new double[2];
        double [] pt2c = new double[2];
        RectF pageCropOnClientF = buildPageBoundBoxOnClient(mPageNum);
        
        pt1c[0] = mPoint.x;
        pt1c[1] = mPoint.y;
        pt2c[0] = pageCropOnClientF.right;
        pt2c[1] = pageCropOnClientF.bottom;
        
        int threshold = 250;
        if ((pt2c[0] - pt1c[0]) < threshold) {
            pt1c[0] = pt2c[0] - threshold;
        }
        if ((pt2c[1] - pt1c[1]) < threshold) {
            pt1c[1] = pt2c[1] - threshold;
        }
        
        double [] pt1;
        double [] pt2;
        pt1 = mPDFView.convScreenPtToPagePt(pt1c[0], pt1c[1], mPageNum);
        pt2 = mPDFView.convScreenPtToPagePt(pt2c[0], pt2c[1], mPageNum);

        pdftron.PDF.Rect rect;
        try {
            rect = new pdftron.PDF.Rect(pt1[0], pt1[1], pt2[0], pt2[1]);
            return rect;
        } catch (Exception e) {
            return null;
        }
    }
}
