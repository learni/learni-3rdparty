//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import java.util.LinkedList;

import pdftron.Common.PDFNetException;
import pdftron.PDF.Annot;
import pdftron.PDF.ColorPt;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import pdftron.PDF.Point;
import pdftron.PDF.Annots.Line;
import pdftron.PDF.Annots.Markup;
import pdftron.PDF.Annots.Popup;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import com.pdftron.pdf.tools.R;

/**
 * This class is responsible for editing a selected line or arrow, e.g., moving and resizing.
 */
class AnnotEditLine extends Tool {

    private Line mLine = null;
    private RectF mBBox;
    private RectF mTempRect;
    private RectF mPageCropOnClientF;
    private int mEffCtrlPtId;
    private boolean mModifiedAnnot;
    private boolean mCtrlPtsSet;
    private boolean mScaled;
    private Paint mPaint;
    private Path mPath;
    
    private final int e_start_point = 0;
    private final int e_end_point = 1;
    
    private PointF[] mCtrlPts;
    private PointF[] mCtrlPts_save;
    
    private final float mCtrlRadius;    // Radius of the control point

    public AnnotEditLine(PDFViewCtrl ctrl) {
        super(ctrl);
        
        // The radius size can be stored in the res/values folder.
        // This way we can pick up different sizes depending on the
        // device's size/resolution.
        mCtrlRadius = this.convDp2Pix(7.5f);
        
        mCtrlPts = new PointF[2];
        mCtrlPts_save = new PointF[2];
        for (int i = 0; i < 2; ++i) {
            mCtrlPts[i] = new PointF();
            mCtrlPts_save[i] = new PointF();
        }
        
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBBox = new RectF();
        mTempRect = new RectF();
        mPath = new Path();
        mModifiedAnnot = false;
        mCtrlPtsSet = false;
        mScaled = false;
    }

    public void onCreate() {
        super.onCreate();
        
        try {
            mLine = new Line(mAnnot);
        } catch (PDFNetException e) {
            e.printStackTrace();
        }
        
        mMenuTitles = new LinkedList<MenuEntry>();
        if (mAnnot != null) {
            // Create menu items based on the type of the selected annotation
            mMenuTitles.add(new MenuEntry("note",       getStringFromResId(R.string.tools_qm_note)));
            mMenuTitles.add(new MenuEntry("color",      getStringFromResId(R.string.tools_qm_color)));
            mMenuTitles.add(new MenuEntry("thickness",  getStringFromResId(R.string.tools_qm_thickness)));
            mMenuTitles.add(new MenuEntry("delete",     getStringFromResId(R.string.tools_qm_delete)));
            
            // Remember the page bounding box in client space; this is used to ensure while
            // moving/resizing, the widget doesn't go beyond the page boundary.
            mPageCropOnClientF = buildPageBoundBoxOnClient(this.mAnnotPageNum);
        }
    }

    public int getMode() {
        return ToolManager.e_annot_edit_line;
    }

    private void setCtrlPts() {
        mCtrlPtsSet = true;
        
        try {
            float x1 = (float) mLine.getStartPoint().x;
            float y1 = (float) mLine.getStartPoint().y;
            float x2 = (float) mLine.getEndPoint().x;
            float y2 = (float) mLine.getEndPoint().y;
            
            float sx = mPDFView.getScrollX();
            float sy = mPDFView.getScrollY();
            
            // Start point
            double[] pts = mPDFView.convPagePtToScreenPt(x1, y1, mAnnotPageNum);
            mCtrlPts[e_start_point].x = (float) pts[0] + sx;
            mCtrlPts[e_start_point].y = (float) pts[1] + sy;
            
            // End point
            pts = mPDFView.convPagePtToScreenPt(x2, y2, mAnnotPageNum);
            mCtrlPts[e_end_point].x = (float) pts[0] + sx;
            mCtrlPts[e_end_point].y = (float) pts[1] + sy;
            
            // Compute the bounding box
            mBBox.left = Math.min(mCtrlPts[e_start_point].x, mCtrlPts[e_end_point].x) - mCtrlRadius;
            mBBox.top = Math.min(mCtrlPts[e_start_point].y, mCtrlPts[e_end_point].y) - mCtrlRadius;
            mBBox.right = Math.max(mCtrlPts[e_start_point].x, mCtrlPts[e_end_point].x) + mCtrlRadius;
            mBBox.bottom = Math.max(mCtrlPts[e_start_point].y, mCtrlPts[e_end_point].y) + mCtrlRadius;
            
            for (int i = 0; i < 2; ++i) {
                mCtrlPts_save[i].set(mCtrlPts[i]);
            }
            
        } catch (PDFNetException e) {
            mCtrlPtsSet = false;
        }
    }

    /**
     * Draws the annotation widget.
     */
    public void onDraw(Canvas canvas, Matrix tfm) {
        super.onDraw(canvas, tfm);
        
        float left = mCtrlPts[e_start_point].x;
        float top = mCtrlPts[e_end_point].y;
        float right = mCtrlPts[e_end_point].x;
        float bottom = mCtrlPts[e_start_point].y;
        
        if (mAnnot != null) {
            
            if (mModifiedAnnot) {
                mPaint.setColor(mPDFView.getResources().getColor(R.color.tools_annot_edit_line_shadow));
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setPathEffect(new DashPathEffect(new float[] { 5, 2 }, 0));
                // Bug in drawLine: https://code.google.com/p/android/issues/detail?id=29944
                // Need to use draPath instead.
                // canvas.drawLine(right, top, left, bottom, mPaint);
                mPath.reset();
                mPath.moveTo(right, top);
                mPath.lineTo(left, bottom);
                canvas.drawPath(mPath, mPaint);
            }
            
            mPaint.setColor(mPDFView.getResources().getColor(R.color.tools_selection_control_point));
            mPaint.setPathEffect(null);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(left, bottom, mCtrlRadius, mPaint);
            canvas.drawCircle(right, top, mCtrlRadius, mPaint);
            
            mPaint.setColor(mPDFView.getResources().getColor(R.color.tools_selection_control_point_border));
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(left, bottom, mCtrlRadius, mPaint);
            canvas.drawCircle(right, top, mCtrlRadius, mPaint);
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        super.onSingleTapConfirmed(e);
        
        int x = (int) (e.getX() + 0.5);
        int y = (int) (e.getY() + 0.5);
        
        if (mAnnot != null) {
            
            Annot tempAnnot = mPDFView.getAnnotationAt(x, y);
            
            if (mAnnot.equals(tempAnnot)) {
                // Single click within the annotation: set the control points,
                // draw the widget and show the menu.
                mNextToolMode = ToolManager.e_annot_edit_line;
                setCtrlPts();
                mPDFView.invalidate((int) Math.floor(mBBox.left), (int) Math.floor(mBBox.top), (int) Math.ceil(mBBox.right), (int) Math.ceil(mBBox.bottom));
                showMenu(mMenuTitles, getAnnotRect());
            } else {
                // Otherwise goes back to the pan mode.
                mAnnot = null;
                mNextToolMode = ToolManager.e_pan;
                setCtrlPts();
                mPDFView.invalidate((int) Math.floor(mBBox.left), (int) Math.floor(mBBox.top), (int) Math.ceil(mBBox.right), (int) Math.ceil(mBBox.bottom));
            }
        }
        
        return false;
    }

    public void onPageTurning(int old_page, int cur_page) {
        super.onPageTurning(old_page, cur_page);
        mNextToolMode = ToolManager.e_pan;
    }

    /**
     * Responds to menu clicks.
     */
    protected void onQuickMenuClicked(int menu_id, String menu_type) {
        super.onQuickMenuClicked(menu_id, menu_type);
        
        if (mAnnot != null) {
            try {
                // Locks the document first as accessing annotation/doc information isn't thread
                // safe.
                mPDFView.docLock(true);
                
                String str = menu_type.toLowerCase();
                
                // Delete the annotation
                if (str.equals("delete")) {
                    mNextToolMode = ToolManager.e_pan;
                    Page page = mPDFView.getDoc().getPage(mAnnotPageNum);
                    page.annotRemove(mAnnot);
                    mPDFView.update(mAnnot, mAnnotPageNum);
                    mAnnot = null;
                }
                
                // Change the color of the annotation
                else if (str.equals("color")) {
                    boolean is_markup = mAnnot.isMarkup();
                    mNextToolMode = ToolManager.e_annot_edit_line;
                    int a = 255;
                    ColorPt color = mAnnot.getColorAsRGB();
                    int r = (int) Math.floor(color.get(0) * 255 + 0.5);
                    int g = (int) Math.floor(color.get(1) * 255 + 0.5);
                    int b = (int) Math.floor(color.get(2) * 255 + 0.5);
                    if (is_markup) {
                        Markup m = new Markup(mAnnot);
                        a = (int) Math.floor(m.getOpacity() * 255 + 0.5);
                    }
                    int color_int = Color.argb(a, r, g, b);
                    final DialogColorPicker d = new DialogColorPicker(mPDFView.getContext(), color_int);
                    d.setAlphaSliderVisible(is_markup);
                    
                    d.setButton(DialogInterface.BUTTON_POSITIVE, mPDFView.getResources().getString(R.string.tools_misc_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int c = d.getColor();
                                    double r = (double) Color.red(c) / 255;
                                    double g = (double) Color.green(c) / 255;
                                    double b = (double) Color.blue(c) / 255;
                                    double a = (double) Color.alpha(c) / 255;
                                    
                                    try {
                                        // Locks the document first as accessing annotation/doc
                                        // information isn't thread safe.
                                        mPDFView.docLock(true);
                                        ColorPt color = new ColorPt(r, g, b);
                                        mAnnot.setColor(color, 3);
                                        if (mAnnot.isMarkup()) {
                                            Markup m = new Markup(mAnnot);
                                            m.setOpacity(a);
                                        }
                                        mAnnot.refreshAppearance();
                                        mPDFView.update(mAnnot, mAnnotPageNum);

                                    } catch (Exception e) {

                                    } finally {
                                        mPDFView.docUnlock();
                                    }
                                    
                                    SharedPreferences settings = mPDFView.getContext().getSharedPreferences(Tool.PREFS_FILE_NAME, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    
                                    editor.putInt("annotation_creation_color", c);
                                    
                                    editor.commit();
                                    
                                    showMenu(mMenuTitles, getAnnotRect());
                                }
                            });
                    
                    d.setButton(DialogInterface.BUTTON_NEGATIVE, mPDFView.getResources().getString(R.string.tools_misc_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    showMenu(mMenuTitles, getAnnotRect());
                                }
                            });
                    
                    d.show();
                }
                
                // Add note to the annotation
                else if (str.equals("note")) {
                    Markup m = new Markup(mAnnot);
                    Popup tp = m.getPopup();
                    if (tp == null || !tp.isValid()) {
                        tp = Popup.create(mPDFView.getDoc(), mAnnot.getRect());
                        m.setPopup(tp);
                    }
                    final Popup p = tp;
                    final DialogAnnotNote d = new DialogAnnotNote(mPDFView.getContext(), p.getContents());
                    d.setTitle(mPDFView.getResources().getString(R.string.tools_qm_note));
                    d.setButton(DialogInterface.BUTTON_POSITIVE, mPDFView.getResources().getString(R.string.tools_misc_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        // Locks the document first as accessing annotation/doc
                                        // information isn't thread safe.
                                        mPDFView.docLock(true);
                                        p.setContents(d.getNote());

                                    } catch (Exception e) {

                                    } finally {
                                        mPDFView.docUnlock();
                                    }
                                    showMenu(mMenuTitles, getAnnotRect());
                                }
                            });
                    
                    d.setButton(DialogInterface.BUTTON_NEGATIVE, mPDFView.getResources().getString(R.string.tools_misc_cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    showMenu(mMenuTitles, getAnnotRect());
                                }
                            });
                    
                    d.show();
                }
                
                // Show options for the line thickness of the annotation
                else if (str.equals("thickness")) {
                    LinkedList<MenuEntry> pt_values = new LinkedList<MenuEntry>();
                    pt_values.add(new MenuEntry("0.5pt"));
                    pt_values.add(new MenuEntry("1pt"));
                    pt_values.add(new MenuEntry("3pt"));
                    pt_values.add(new MenuEntry("7pt"));
                    pt_values.add(new MenuEntry("12pt"));
                    showMenu(pt_values, getAnnotRect());

//                    // Use DialogNumberPicker instead. Note that this dialog needs NumberPicker
//                    // class available only after API 11.
//                    // if (android.os.Build.VERSION.SDK_INT >= 11) { ...
//                    final pdftron.PDF.Annot.BorderStyle bs = mAnnot.getBorderStyle();
//                    final DialogNumberPicker d = new DialogNumberPicker(mPDFView.getContext(), (float)bs.getWidth());
//                    d.setTitle(getStringFromResId(R.string.tools_thicknesspicker_title));
//                    d.setButton(DialogInterface.BUTTON_POSITIVE, mPDFView.getResources().getString(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            try {
//                                // Locks the document first as accessing annotation/doc
//                                // information isn't thread safe.
//                                mPDFView.docLock(true);
//                                float thickness = d.getNumber();
//                                bs.setWidth(thickness);
//                                mAnnot.setBorderStyle(bs);
//                                mAnnot.refreshAppearance();
//                                mPDFView.update();
//
//                                SharedPreferences settings = mPDFView.getContext().getSharedPreferences(Tool.PREFS_FILE_NAME, 0);
//                                SharedPreferences.Editor editor = settings.edit();
//                                editor.putFloat("annotation_creation_thickness", thickness);
//                                editor.commit();
//                            }
//                            catch (Exception e) {
//                            }
//                            finally {
//                                mPDFView.docUnlock();
//                            }
//                            showMenu(mMenuTitles, getAnnotRect());
//                        }
//                    });
//                    d.setButton(DialogInterface.BUTTON_NEGATIVE, mPDFView.getResources().getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            showMenu(mMenuTitles, getAnnotRect());
//                        }
//                    });
//                    d.show();
                }

                // Change the line thickness of the annotation
                else if (str.endsWith("pt")) {
                    String pt_str = str.replace("pt", "");
                    float thickness = Float.valueOf(pt_str);
                    pdftron.PDF.Annot.BorderStyle bs = mAnnot.getBorderStyle();
                    bs.setWidth(thickness);
                    mAnnot.setBorderStyle(bs);
                    mAnnot.refreshAppearance();
                    mPDFView.update();

                    SharedPreferences settings = mPDFView.getContext().getSharedPreferences(Tool.PREFS_FILE_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putFloat("annotation_creation_thickness", thickness);
                    editor.commit();

                    showMenu(mMenuTitles, getAnnotRect());
                }

            } catch (Exception e) {

            } finally {
                mPDFView.docUnlock();
            }
            
            mPDFView.waitForRendering();

        } else {
            mNextToolMode = ToolManager.e_pan;
        }
    }

    public boolean onUp(MotionEvent e, int prior_event_type) {
        super.onUp(e, prior_event_type);
        
        mNextToolMode = ToolManager.e_annot_edit_line;
        mScaled = false;
        
        if (mAnnot != null
                && (mModifiedAnnot
                || !mCtrlPtsSet
                || prior_event_type == PDFViewCtrl.PRIOR_EVENT_SCROLLING
                || prior_event_type == PDFViewCtrl.PRIOR_EVENT_PINCH
                || prior_event_type == PDFViewCtrl.PRIOR_EVENT_DBLTAP)) {
            
            if (!mCtrlPtsSet) {
                setCtrlPts();
            }
            
            try {
                mPDFView.docLock(true);
                if (mModifiedAnnot) {
                    mModifiedAnnot = false;
                    
                    // Compute the new annotation position
                    float x1 = mCtrlPts[e_start_point].x - mPDFView.getScrollX();
                    float y1 = mCtrlPts[e_start_point].y - mPDFView.getScrollY();
                    float x2 = mCtrlPts[e_end_point].x - mPDFView.getScrollX();
                    float y2 = mCtrlPts[e_end_point].y - mPDFView.getScrollY();
                    double[] pts1, pts2;
                    pts1 = mPDFView.convScreenPtToPagePt(x1, y1, mAnnotPageNum);
                    pts2 = mPDFView.convScreenPtToPagePt(x2, y2, mAnnotPageNum);
                    pdftron.PDF.Rect new_annot_rect = new pdftron.PDF.Rect(pts1[0], pts1[1], pts2[0], pts2[1]);
                    new_annot_rect.normalize();
                    
                    // Compute the old annotation position in screen space for update
                    double[] pts1_old, pts2_old;
                    pdftron.PDF.Rect r = mAnnot.getRect();
                    pts1_old = mPDFView.convPagePtToScreenPt(r.getX1(), r.getY1(), mAnnotPageNum);
                    pts2_old = mPDFView.convPagePtToScreenPt(r.getX2(), r.getY2(), mAnnotPageNum);
                    pdftron.PDF.Rect old_update_rect = new pdftron.PDF.Rect(pts1_old[0], pts1_old[1], pts2_old[0], pts2_old[1]);
                    old_update_rect.normalize();
                    
                    mAnnot.resize(new_annot_rect);
                    
                    Line line = new Line(mAnnot);
                    line.setStartPoint(new Point(pts1[0], pts1[1]));
                    line.setEndPoint(new Point(pts2[0], pts2[1]));
                    
                    mAnnot.refreshAppearance();
                    buildAnnotBBox();
                    mPDFView.update(old_update_rect);   // Update the old position
                    mPDFView.update(mAnnot, mAnnotPageNum);

                } else if (prior_event_type == PDFViewCtrl.PRIOR_EVENT_PINCH || prior_event_type == PDFViewCtrl.PRIOR_EVENT_DBLTAP) {
                    setCtrlPts();
                }

            } catch (Exception e1) {

            } finally {
                mPDFView.docUnlock();
            }
            
            if (prior_event_type == PDFViewCtrl.PRIOR_EVENT_SCROLLING
                    || prior_event_type == PDFViewCtrl.PRIOR_EVENT_PINCH
                    || prior_event_type == PDFViewCtrl.PRIOR_EVENT_DBLTAP
                    || prior_event_type == PDFViewCtrl.PRIOR_EVENT_FLING) {
                // Show menu item in certain cases
                showMenu(mMenuTitles, getAnnotRect());
            }
            
            mPDFView.waitForRendering();
            
            if (prior_event_type == PDFViewCtrl.PRIOR_EVENT_SCROLLING || prior_event_type == PDFViewCtrl.PRIOR_EVENT_FLING) {
                // Don't let the main view scroll
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public boolean onScaleEnd(float x, float y) {
        super.onScaleEnd(x, y);
        
        if (mAnnot != null) {
            // Scaled and if while moving, disable moving and set the
            // control points back to where the annotation is; this is
            // to avoid complications.
            mScaled = true;
            setCtrlPts();
        }
        return false;
    }

    public boolean onFlingStop() {
        super.onFlingStop();
        
        if (mAnnot != null) {
            if (!mCtrlPtsSet) {
                setCtrlPts(); // may be preceded by annotation creation touch up.
            }
            mPDFView.invalidate((int) Math.floor(mBBox.left), (int) Math.floor(mBBox.top), (int) Math.ceil(mBBox.right), (int) Math.ceil(mBBox.bottom));
        }
        return false;
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        
        if (mAnnot != null) {
            if (!mPDFView.isContinuousPagePresentationMode(mPDFView.getPagePresentationMode())) {
                if (mAnnotPageNum != mPDFView.getCurrentPage()) {
                    // Now in single page mode, and the annotation is not on this page, quit this
                    // tool mode.
                    mAnnot = null;
                    mNextToolMode = ToolManager.e_pan;
                    setCtrlPts();
                    mEffCtrlPtId = -1;
                    if (isMenuShown()) {
                        closeMenu();
                    }
                    return;
                }
            }
            
            setCtrlPts();
            if (isMenuShown() && changed) {
                closeMenu();
                showMenu(mMenuTitles, getAnnotRect());
            }
        }
    }

    public boolean onLongPress(MotionEvent e) {
        super.onLongPress(e);
        
        if (mAnnot != null) {
            if (mEffCtrlPtId >= 0) {
                mNextToolMode = ToolManager.e_annot_edit_line;
                setCtrlPts();
                mEffCtrlPtId = 2;
            } else {
                mAnnot = null;
                mNextToolMode = ToolManager.e_pan;
                setCtrlPts();
                mEffCtrlPtId = -1;
            }
            mPDFView.invalidate((int) Math.floor(mBBox.left), (int) Math.floor(mBBox.top), (int) Math.ceil(mBBox.right), (int) Math.ceil(mBBox.bottom));
        }
        
        return false;
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mAnnot != null && (Math.abs(t - oldt) <= 1) && !isMenuShown()) {
            showMenu(mMenuTitles, getAnnotRect());
        }
    }

    public boolean onDown(MotionEvent e) {
        super.onDown(e);
        
        float x = e.getX() + mPDFView.getScrollX();
        float y = e.getY() + mPDFView.getScrollY();
        
        // Check if any control point is hit
        mEffCtrlPtId = -1;
        float thresh = mCtrlRadius * 2.25f;
        float shortest_dist = -1;
        for (int i = 0; i < 2; ++i) {
            float s = mCtrlPts[i].x;
            float t = mCtrlPts[i].y;
            
            float dist = (x - s) * (x - s) + (y - t) * (y - t);
            dist = (float)Math.sqrt(dist);
            if (dist <= thresh && (dist < shortest_dist || shortest_dist < 0)) {
                mEffCtrlPtId = i;
                shortest_dist = dist;
            }
            
            mCtrlPts_save[i].set(mCtrlPts[i]);
        }
        
        // Check if hit within the line without hitting any control point.
        if (mEffCtrlPtId < 0) {
            if (pointToLineDistance(x, y)) {
                mEffCtrlPtId = 2; // Indicating moving mode;
            }
        }
        
        // Re-compute the page bounding box on screen, since the zoom
        // factor may have been changed.
        if (mAnnot != null) {
            mPageCropOnClientF = buildPageBoundBoxOnClient(this.mAnnotPageNum);
        }
        
        return false;
    }

    private boolean pointToLineDistance(double x, double y) {
        double lineXDist = mCtrlPts[e_end_point].x - mCtrlPts[e_start_point].x;
        double lineYDist = mCtrlPts[e_end_point].y - mCtrlPts[e_start_point].y;
        
        double squaredDist = (lineXDist * lineXDist) + (lineYDist * lineYDist);
        
        double distRatio = ((x - mCtrlPts[e_start_point].x) * lineXDist + (y - mCtrlPts[e_start_point].y) * lineYDist) / squaredDist;
        
        if (distRatio < 0) {
            distRatio = 0;  // This way, we will compare against e_start_point
        }
        if (distRatio > 1) {
            distRatio = 0;  // This way, we will compare against e_end_point
        }
        
        double dx = mCtrlPts[e_start_point].x - x + distRatio * lineXDist;
        double dy = mCtrlPts[e_start_point].y - y + distRatio * lineYDist;
        
        double dist = (dx * dx) + (dy * dy);
        
        if (dist < (mCtrlRadius * mCtrlRadius * 4)) {
            return true;
        }
        
        return false;
    }

    private boolean boundCornerCtrlPts(float ox, float oy, boolean translate) {
        boolean changed = false;
        
        if (mPageCropOnClientF != null) {
            
            if (translate) {
                float max_x = Math.max(mCtrlPts[e_start_point].x, mCtrlPts[e_end_point].x);
                float min_x = Math.min(mCtrlPts[e_start_point].x, mCtrlPts[e_end_point].x);
                float max_y = Math.max(mCtrlPts[e_start_point].y, mCtrlPts[e_end_point].y);
                float min_y = Math.min(mCtrlPts[e_start_point].y, mCtrlPts[e_end_point].y);
                
                float shift_x = 0, shift_y = 0;
                if (min_x < mPageCropOnClientF.left) {
                    shift_x = mPageCropOnClientF.left - min_x;
                }
                if (min_y < mPageCropOnClientF.top) {
                    shift_y = mPageCropOnClientF.top - min_y;
                }
                if (max_x > mPageCropOnClientF.right) {
                    shift_x = mPageCropOnClientF.right - max_x;
                }
                if (max_y > mPageCropOnClientF.bottom) {
                    shift_y = mPageCropOnClientF.bottom - max_y;
                }
                
                mCtrlPts[e_start_point].x += shift_x;
                mCtrlPts[e_start_point].y += shift_y;
                mCtrlPts[e_end_point].x += shift_x;
                mCtrlPts[e_end_point].y += shift_y;
                
                changed = true;
                
            } else {
                
                // Bounding along x-axis
                if (mCtrlPts[e_start_point].x < mPageCropOnClientF.left && ox < 0) {
                    mCtrlPts[e_start_point].x = mPageCropOnClientF.left;
                    changed = true;
                } else if (mCtrlPts[e_start_point].x > mPageCropOnClientF.right && ox > 0) {
                    mCtrlPts[e_start_point].x = mPageCropOnClientF.right;
                    changed = true;
                } else if (mCtrlPts[e_end_point].x < mPageCropOnClientF.left && ox < 0) {
                    mCtrlPts[e_end_point].x = mPageCropOnClientF.left;
                    changed = true;
                } else if (mCtrlPts[e_end_point].x > mPageCropOnClientF.right && ox > 0) {
                    mCtrlPts[e_end_point].x = mPageCropOnClientF.right;
                    changed = true;
                }
                
                // Bounding along y-axis
                if (mCtrlPts[e_start_point].y > mPageCropOnClientF.bottom && oy > 0) {
                    mCtrlPts[e_start_point].y = mPageCropOnClientF.bottom;
                    changed = true;
                } else if (mCtrlPts[e_start_point].y < mPageCropOnClientF.top && oy < 0) {
                    mCtrlPts[e_start_point].y = mPageCropOnClientF.top;
                    changed = true;
                } else if (mCtrlPts[e_end_point].y > mPageCropOnClientF.bottom && oy > 0) {
                    mCtrlPts[e_end_point].y = mPageCropOnClientF.bottom;
                    changed = true;
                } else if (mCtrlPts[e_end_point].y < mPageCropOnClientF.top && oy < 0) {
                    mCtrlPts[e_end_point].y = mPageCropOnClientF.top;
                    changed = true;
                }
            }
        }
        return changed;
    }

    public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
        if (mScaled) {
            // Scaled and if while moving, disable moving to avoid complications.
            return false;
        }
        
        if (mEffCtrlPtId >= 0) {
            float ox = e2.getX() - e1.getX();
            float oy = e2.getY() - e1.getY();
            
            mTempRect.set(mBBox);
            
            if (mEffCtrlPtId == 2) {
                for (int i = 0; i < 2; ++i) {
                    mCtrlPts[i].x = mCtrlPts_save[i].x + ox;
                    mCtrlPts[i].y = mCtrlPts_save[i].y + oy;
                }
                boundCornerCtrlPts(ox, oy, true);
                
                // Compute the bounding box
                mBBox.left = Math.min(mCtrlPts[e_start_point].x, mCtrlPts[e_end_point].x) - mCtrlRadius;
                mBBox.top = Math.min(mCtrlPts[e_start_point].y, mCtrlPts[e_end_point].y) - mCtrlRadius;
                mBBox.right = Math.max(mCtrlPts[e_start_point].x, mCtrlPts[e_end_point].x) + mCtrlRadius;
                mBBox.bottom = Math.max(mCtrlPts[e_start_point].y, mCtrlPts[e_end_point].y) + mCtrlRadius;
                
                mModifiedAnnot = true;

            } else {
                boolean valid = false;
                switch (mEffCtrlPtId) {
                case e_start_point:
                    mCtrlPts[e_start_point].x = mCtrlPts_save[e_start_point].x + ox;
                    mCtrlPts[e_start_point].y = mCtrlPts_save[e_start_point].y + oy;
                    valid = true;
                    break;
                case e_end_point:
                    mCtrlPts[e_end_point].x = mCtrlPts_save[e_end_point].x + ox;
                    mCtrlPts[e_end_point].y = mCtrlPts_save[e_end_point].y + oy;
                    valid = true;
                    break;
                }
                mModifiedAnnot = true;
                
                if (valid) {
                    boundCornerCtrlPts(ox, oy, false);
                    
                    // Compute the bounding box
                    mBBox.left = Math.min(mCtrlPts[e_start_point].x, mCtrlPts[e_end_point].x) - mCtrlRadius;
                    mBBox.top = Math.min(mCtrlPts[e_start_point].y, mCtrlPts[e_end_point].y) - mCtrlRadius;
                    mBBox.right = Math.max(mCtrlPts[e_start_point].x, mCtrlPts[e_end_point].x) + mCtrlRadius;
                    mBBox.bottom = Math.max(mCtrlPts[e_start_point].y, mCtrlPts[e_end_point].y) + mCtrlRadius;
                    
                    mModifiedAnnot = true;
                }
            }
            
            float min_x = Math.min(mTempRect.left, mBBox.left);
            float max_x = Math.max(mTempRect.right, mBBox.right);
            float min_y = Math.min(mTempRect.top, mBBox.top);
            float max_y = Math.max(mTempRect.bottom, mBBox.bottom);
            mPDFView.invalidate((int) min_x - 1, (int) min_y - 1, (int) Math.ceil(max_x) + 1, (int) Math.ceil(max_y) + 1);
            
            return true;
        }
        return false;
    }
}
