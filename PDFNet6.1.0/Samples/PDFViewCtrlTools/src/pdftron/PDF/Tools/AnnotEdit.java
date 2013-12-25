//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import java.util.LinkedList;

import pdftron.PDF.Annot;
import pdftron.PDF.ColorPt;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.Page;
import pdftron.PDF.Annots.FreeText;
import pdftron.PDF.Annots.Markup;
import pdftron.PDF.Annots.Popup;
import com.pdftron.pdf.tools.R;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * This class is responsible for editing a selected annotation, e.g., moving and resizing.
 */
class AnnotEdit extends Tool {

    private RectF mBBox;
    private RectF mTempRect;
    private RectF mPageCropOnClientF;
    private int mEffCtrlPtId;
    private boolean mModifiedAnnot;
    private boolean mCtrlPtsSet;
    private boolean mAnnotIsSticky;
    private boolean mAnnotIsTextMarkup;
    private boolean mAnnotIsFreeText;
    private boolean mScaled;
    private Paint mPaint;
    private boolean mUpFromStickyCreate;
    private boolean mUpFromStickyCreateDlgShown;

    private final int e_ll = 0;     // lower left control point
    private final int e_lm = 1;     // lower middle
    private final int e_lr = 2;     // lower right
    private final int e_mr = 3;     // middle right
    private final int e_ur = 4;     // upper left
    private final int e_um = 5;     // upper middle
    private final int e_ul = 6;     // upper left
    private final int e_ml = 7;     // middle left
    private PointF [] mCtrlPts;
    private PointF [] mCtrlPts_save;

    private final float mCtrlRadius;    // radius of the control point

    public AnnotEdit(PDFViewCtrl ctrl) {
        super(ctrl);

        mCtrlRadius = this.convDp2Pix(7.5f);

        mCtrlPts = new PointF[8];
        mCtrlPts_save = new PointF[8];
        for (int i = 0; i < 8; ++i) {
            mCtrlPts[i] = new PointF();
            mCtrlPts_save[i] = new PointF();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBBox = new RectF();
        mTempRect = new RectF();
        mModifiedAnnot = false;
        mCtrlPtsSet = false;
        mAnnotIsSticky = false;
        mAnnotIsTextMarkup = false;
        mAnnotIsFreeText = false;
        mScaled = false;
        mUpFromStickyCreate = false;
        mUpFromStickyCreateDlgShown = false;
    }

    public void onCreate() {
        super.onCreate();

        mMenuTitles = new LinkedList<MenuEntry>();
        if (mAnnot != null) {
            try {
                // Locks the document first as accessing annotation/doc information isn't thread
                // safe. Since we are not going to modify the doc here, we can use the read lock.
                mPDFView.docLockRead();

                mAnnotIsSticky = (mAnnot.getType() == Annot.e_Text);
                mAnnotIsFreeText = (mAnnot.getType() == Annot.e_FreeText);
                mAnnotIsTextMarkup = (mAnnot.getType() == Annot.e_Highlight ||
                        mAnnot.getType() == Annot.e_Underline ||
                        mAnnot.getType() == Annot.e_StrikeOut ||
                        mAnnot.getType() == Annot.e_Squiggly);

                // Create menu items based on the type of the selected annotation
                int type = mAnnot.getType();
                if (mAnnot.isMarkup()) {
                    // Only markup annotations have "Note" menu item.
                    if (mAnnotIsFreeText) {
                        mMenuTitles.add(new MenuEntry("text", getStringFromResId(R.string.tools_qm_text)));
                    } else {
                        mMenuTitles.add(new MenuEntry("note", getStringFromResId(R.string.tools_qm_note)));
                    }
                }

                if (type == Annot.e_Line ||
                        type == Annot.e_Square ||
                        type == Annot.e_Circle ||
                        type == Annot.e_Polygon ||
                        type == Annot.e_Ink ||
                        type == Annot.e_Polyline ||
                        type == Annot.e_Underline ||
                        type == Annot.e_Squiggly ||
                        type == Annot.e_StrikeOut ||
                        type == Annot.e_Highlight ||
                        type == Annot.e_Text ||
                        type == Annot.e_FreeText) {
                    if (mAnnotIsFreeText) {
                        mMenuTitles.add(new MenuEntry("text color", getStringFromResId(R.string.tools_qm_text_color)));
                        mMenuTitles.add(new MenuEntry("text size", getStringFromResId(R.string.tools_qm_text_size)));
                    } else {
                        mMenuTitles.add(new MenuEntry("color", getStringFromResId(R.string.tools_qm_color)));
                    }

                    if (type != Annot.e_Highlight && type != Annot.e_Text && type != Annot.e_FreeText) {
                        mMenuTitles.add(new MenuEntry("thickness", getStringFromResId(R.string.tools_qm_thickness)));
                    }
                }

                mMenuTitles.add(new MenuEntry("delete", getStringFromResId(R.string.tools_qm_delete)));

                if (mAnnotIsTextMarkup) {
                    mMenuTitles.add(new MenuEntry("copy to clipboard", getStringFromResId(R.string.tools_qm_copy_to_clipboard)));
                }

                // Remember the page bounding box in client space; this is used to ensure while
                // moving/resizing, the widget doesn't go beyond the page boundary.
                mPageCropOnClientF = buildPageBoundBoxOnClient(this.mAnnotPageNum);

            } catch (Exception e) {

            } finally {
                mPDFView.docUnlockRead();
            }
        }
    }

    public int getMode() {
        return ToolManager.e_annot_edit;
    }

    public void setUpFromStickyCreate(boolean flag) {
        mUpFromStickyCreate = flag;
    }

    /**
     * This functions sets the positions of the eight control points based on
     * the bounding box of the annotation.
     */
    private void setCtrlPts() {
        mCtrlPtsSet = true;
        float x1 = mAnnotBBox.left;
        float y1 = mAnnotBBox.bottom;   // mAnnotBBox in page space, reverse the y-order
        float x2 = mAnnotBBox.right;
        float y2 = mAnnotBBox.top;

        float sx = mPDFView.getScrollX();
        float sy = mPDFView.getScrollY();

        // Compute the control points. In case that the page is rotated, have to
        // ensure the control points are properly positioned.
        float min_x, max_x;
        float min_y, max_y;
        float x, y;

        double[] pts;
        pts = mPDFView.convPagePtToScreenPt(x1, y2, mAnnotPageNum);
        min_x = max_x = (float) pts[0] + sx;
        min_y = max_y = (float) pts[1] + sy;

        pts = mPDFView.convPagePtToScreenPt((x1 + x2) / 2, y2, mAnnotPageNum);
        x = (float) pts[0] + sx;
        y = (float) pts[1] + sy;
        min_x = Math.min(x,  min_x);
        max_x = Math.max(x,  max_x);
        min_y = Math.min(y,  min_y);
        max_y = Math.max(y,  max_y);

        pts = mPDFView.convPagePtToScreenPt(x2, y2, mAnnotPageNum);
        x = (float) pts[0] + sx;
        y = (float) pts[1] + sy;
        min_x = Math.min(x,  min_x);
        max_x = Math.max(x,  max_x);
        min_y = Math.min(y,  min_y);
        max_y = Math.max(y,  max_y);

        pts = mPDFView.convPagePtToScreenPt(x2, (y1 + y2) / 2, mAnnotPageNum);
        x = (float) pts[0] + sx;
        y = (float) pts[1] + sy;
        min_x = Math.min(x,  min_x);
        max_x = Math.max(x,  max_x);
        min_y = Math.min(y,  min_y);
        max_y = Math.max(y,  max_y);

        pts = mPDFView.convPagePtToScreenPt(x2, y1, mAnnotPageNum);
        x = (float) pts[0] + sx;
        y = (float) pts[1] + sy;
        min_x = Math.min(x,  min_x);
        max_x = Math.max(x,  max_x);
        min_y = Math.min(y,  min_y);
        max_y = Math.max(y,  max_y);

        pts = mPDFView.convPagePtToScreenPt((x1 + x2) / 2, y1, mAnnotPageNum);
        x = (float) pts[0] + sx;
        y = (float) pts[1] + sy;
        min_x = Math.min(x,  min_x);
        max_x = Math.max(x,  max_x);
        min_y = Math.min(y,  min_y);
        max_y = Math.max(y,  max_y);

        pts = mPDFView.convPagePtToScreenPt(x1, y1, mAnnotPageNum);
        x = (float) pts[0] + sx;
        y = (float) pts[1] + sy;
        min_x = Math.min(x,  min_x);
        max_x = Math.max(x,  max_x);
        min_y = Math.min(y,  min_y);
        max_y = Math.max(y,  max_y);

        pts = mPDFView.convPagePtToScreenPt(x1, (y1 + y2) / 2, mAnnotPageNum);
        x = (float) pts[0] + sx;
        y = (float) pts[1] + sy;
        min_x = Math.min(x,  min_x);
        max_x = Math.max(x,  max_x);
        min_y = Math.min(y,  min_y);
        max_y = Math.max(y,  max_y);

        mCtrlPts[e_ll].x = min_x;
        mCtrlPts[e_ll].y = max_y;

        mCtrlPts[e_lm].x = (min_x + max_x) / 2.0f;
        mCtrlPts[e_lm].y = max_y;

        mCtrlPts[e_lr].x = max_x;
        mCtrlPts[e_lr].y = max_y;

        mCtrlPts[e_mr].x = max_x;
        mCtrlPts[e_mr].y = (min_y + max_y) / 2.0f;

        mCtrlPts[e_ur].x = max_x;
        mCtrlPts[e_ur].y = min_y;

        mCtrlPts[e_um].x = (min_x + max_x) / 2.0f;
        mCtrlPts[e_um].y = min_y;

        mCtrlPts[e_ul].x = min_x;
        mCtrlPts[e_ul].y = min_y;

        mCtrlPts[e_ml].x = min_x;
        mCtrlPts[e_ml].y = (min_y + max_y) / 2.0f;

        // Compute the bounding box
        mBBox.left = mCtrlPts[e_ul].x - mCtrlRadius;
        mBBox.top = mCtrlPts[e_ul].y - mCtrlRadius;
        mBBox.right = mCtrlPts[e_lr].x + mCtrlRadius;
        mBBox.bottom = mCtrlPts[e_lr].y + mCtrlRadius;

        for (int i = 0; i < 8; ++i) {
            mCtrlPts_save[i].set(mCtrlPts[i]);
        }
    }

    /**
     * Draws the annotation widget.
     */
    public void onDraw (Canvas canvas, Matrix tfm) {
        super.onDraw(canvas, tfm);

        float left = mCtrlPts[e_ul].x;
        float top = mCtrlPts[e_ul].y;
        float right = mCtrlPts[e_lr].x;
        float bottom = mCtrlPts[e_lr].y;
        float middle_x = mCtrlPts[e_lm].x;
        float middle_y = mCtrlPts[e_ml].y;

        if (mAnnot != null && right - left > 0 && bottom - top > 0) {
            mPaint.setColor(mPDFView.getResources().getColor(R.color.tools_selection_background));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(left, top, right, bottom, mPaint);

            // Don't draw control points for sticky notes since they cannot be re-sized
            // (movable only).
            if (!mAnnotIsSticky && !mAnnotIsTextMarkup) {
                // Control point fill color
                mPaint.setColor(mPDFView.getResources().getColor(R.color.tools_selection_control_point));
                canvas.drawCircle(left, bottom, mCtrlRadius, mPaint);
                canvas.drawCircle(middle_x, bottom, mCtrlRadius, mPaint);
                canvas.drawCircle(right, bottom, mCtrlRadius, mPaint);
                canvas.drawCircle(right, middle_y, mCtrlRadius, mPaint);
                canvas.drawCircle(right, top, mCtrlRadius, mPaint);
                canvas.drawCircle(middle_x, top, mCtrlRadius, mPaint);
                canvas.drawCircle(left, top, mCtrlRadius, mPaint);
                canvas.drawCircle(left, middle_y, mCtrlRadius, mPaint);

                // Control point border
                mPaint.setColor(mPDFView.getResources().getColor(R.color.tools_selection_control_point_border));
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(left, bottom, mCtrlRadius, mPaint);
                canvas.drawCircle(middle_x, bottom, mCtrlRadius, mPaint);
                canvas.drawCircle(right, bottom, mCtrlRadius, mPaint);
                canvas.drawCircle(right, middle_y, mCtrlRadius, mPaint);
                canvas.drawCircle(right, top, mCtrlRadius, mPaint);
                canvas.drawCircle(middle_x, top, mCtrlRadius, mPaint);
                canvas.drawCircle(left, top, mCtrlRadius, mPaint);
                canvas.drawCircle(left, middle_y, mCtrlRadius, mPaint);
            }
        }
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        super.onSingleTapConfirmed(e);

        int x = (int)(e.getX() + 0.5);
        int y = (int)(e.getY() + 0.5);

        if (mAnnot != null) {
            if (isInsideAnnot(x, y) || mUpFromStickyCreate) {
                // Single clicked within the annotation, set the control points, draw the widget and
                // show the menu.
                mNextToolMode = ToolManager.e_annot_edit;
                setCtrlPts();
                mPDFView.invalidate((int)Math.floor(mBBox.left), (int)Math.floor(mBBox.top), (int)Math.ceil(mBBox.right), (int)Math.ceil(mBBox.bottom));

                if (!mUpFromStickyCreate) {
                    showMenu(mMenuTitles, getAnnotRect());
                }

            } else {
                // Otherwise goes back to the pan mode.
                mAnnot = null;
                mNextToolMode = ToolManager.e_pan;
                setCtrlPts();
                // Draw away the edit widget
                mPDFView.invalidate((int)Math.floor(mBBox.left), (int)Math.floor(mBBox.top), (int)Math.ceil(mBBox.right), (int)Math.ceil(mBBox.bottom));
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
    //@SuppressWarnings("deprecation")
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
                else if (str.equals("color") || str.equals("text color")) {
                    boolean is_markup = mAnnot.isMarkup();
                    mNextToolMode = ToolManager.e_annot_edit;
                    int a = 255;
                    ColorPt color;
                    if (mAnnotIsFreeText) {
                        FreeText freeText = new FreeText(mAnnot);
                        color = freeText.getTextColor();
                    } else {
                        color = mAnnot.getColorAsRGB();
                    }
                    int r = (int)Math.floor(color.get(0) * 255 + 0.5);
                    int g = (int)Math.floor(color.get(1) * 255 + 0.5);
                    int b = (int)Math.floor(color.get(2) * 255 + 0.5);
                    if (is_markup) {
                        Markup m = new Markup(mAnnot);
                        a = (int)Math.floor(m.getOpacity() * 255 + 0.5);
                    }
                    int color_int = Color.argb(a, r, g, b);
                    final DialogColorPicker d = new DialogColorPicker(mPDFView.getContext(), color_int);
                    d.setAlphaSliderVisible(is_markup);
                    d.setButton(DialogInterface.BUTTON_POSITIVE, mPDFView.getResources().getString(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int c = d.getColor();
                            double r = (double)Color.red(c) / 255;
                            double g = (double)Color.green(c) / 255;
                            double b = (double)Color.blue(c) / 255;
                            double a = (double)Color.alpha(c) / 255;

                            try {
                                // Locks the document first as accessing annotation/doc information
                                // isn't thread safe.
                                mPDFView.docLock(true);
                                ColorPt color = new ColorPt(r, g, b);
                                if (mAnnotIsFreeText) {
                                    FreeText freeText = new FreeText(mAnnot);
                                    freeText.setTextColor(color, 3);
                                } else {
                                    mAnnot.setColor(color, 3);
                                    if (mAnnot.isMarkup()) {
                                        Markup m = new Markup(mAnnot);
                                        m.setOpacity(a);
                                    }
                                }
                                mAnnot.refreshAppearance();
                                mPDFView.update(mAnnot, mAnnotPageNum);

                            } catch (Exception e) {

                            } finally {
                                mPDFView.docUnlock();
                            }

                            SharedPreferences settings = mPDFView.getContext().getSharedPreferences(Tool.PREFS_FILE_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            if (mAnnotIsFreeText) {
                                editor.putInt("annotation_freetext_creation_color", c);
                            } else {
                                editor.putInt("annotation_creation_color", c);
                            }
                            editor.commit();

                            showMenu(mMenuTitles, getAnnotRect());
                        }
                    });

                    d.setButton(DialogInterface.BUTTON_NEGATIVE, mPDFView.getResources().getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showMenu(mMenuTitles, getAnnotRect());
                        }
                    });

                    d.show();
                }

                // Add note to the annotation
                else if (str.equals("note") || str.equals("text")) {
                    if (mAnnotIsFreeText) {
                        final Markup m = new Markup(mAnnot);
                        final DialogAnnotNote d = new DialogAnnotNote(mPDFView.getContext(), m.getContents());
                        d.setTitle(mPDFView.getResources().getString(R.string.tools_qm_text));
                        d.setButton(AlertDialog.BUTTON_POSITIVE, mPDFView.getResources().getString(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    // Locks the document first as accessing annotation/doc
                                    // information isn't thread safe.
                                    mPDFView.docLock(true);
                                    m.setContents(d.getNote());

                                    mAnnot.refreshAppearance();
                                    mPDFView.update(mAnnot, mAnnotPageNum);

                                } catch (Exception e) {

                                } finally {
                                    mPDFView.docUnlock();
                                }
                                showMenu(mMenuTitles, getAnnotRect());
                            }
                        });

                        d.setButton(AlertDialog.BUTTON_NEGATIVE, mPDFView.getResources().getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showMenu(mMenuTitles, getAnnotRect());
                            }
                        });

                        d.show();

                    } else {
                        Markup m = new Markup(mAnnot);
                        Popup tp = m.getPopup();
                        if (tp == null || !tp.isValid()) {
                            tp = Popup.create(mPDFView.getDoc(), mAnnot.getRect());
                            m.setPopup(tp);
                        }
                        final Popup p = tp;
                        final DialogAnnotNote d = new DialogAnnotNote(mPDFView.getContext(), p.getContents());
                        d.setTitle(mPDFView.getResources().getString(R.string.tools_qm_note));
                        d.setButton(DialogInterface.BUTTON_POSITIVE, mPDFView.getResources().getString(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
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

                        d.setButton(DialogInterface.BUTTON_NEGATIVE, mPDFView.getResources().getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showMenu(mMenuTitles, getAnnotRect());
                            }
                        });

                        d.show();
                    }
                }

                // Change the size of the font in free text
                else if (str.equals("text size")) {
                    LinkedList<MenuEntry> pt_values = new LinkedList<MenuEntry>();
                    pt_values.add(new MenuEntry("8"));
                    pt_values.add(new MenuEntry("11"));
                    pt_values.add(new MenuEntry("16"));
                    pt_values.add(new MenuEntry("24"));
                    pt_values.add(new MenuEntry("36"));
                    showMenu(pt_values, getAnnotRect());
                }

                // Sizes for the font in free text
                else if (str.equals("8") || str.equals("11") || str.equals("16") ||
                        str.equals("24") || str.equals("36")) {
                    try {
                        mPDFView.docLock(true);
                        FreeText freeText = new FreeText(mAnnot);
                        freeText.setFontSize(Integer.parseInt(str));
                        freeText.refreshAppearance();

                        // Let's recalculate the selection bounding box
                        buildAnnotBBox();
                        setCtrlPts();

                        mPDFView.update(mAnnot, mAnnotPageNum);

                        showMenu(mMenuTitles, getAnnotRect());

                    } catch (Exception e) {

                    } finally {
                        mPDFView.docUnlock();
                    }

                    SharedPreferences settings = mPDFView.getContext().getSharedPreferences(Tool.PREFS_FILE_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("annotation_freetext_creation_size", Integer.parseInt(str));
                    editor.commit();
                }

                // Change the line thickness of the annotation
                else if ( str.equals("thickness") ) {
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
//                                showMenu(mMenuTitles, getAnnotRect());
//                        }
//                    });
//                    d.show();
                }

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

                else if (str.equals("copy to clipboard")) {
                    boolean success = false;
                    pdftron.PDF.TextExtractor te = new pdftron.PDF.TextExtractor();
                    Page page = mPDFView.getDoc().getPage(mAnnotPageNum);
                    if (page != null) {
                        te.begin(page);
                        String text = te.getTextUnderAnnot(mAnnot);
                        if (android.os.Build.VERSION.SDK_INT < 11) { //Build.VERSION_CODES.HONEYCOMB
                            android.text.ClipboardManager mgr = (android.text.ClipboardManager) mPDFView.getContext().getSystemService(Context.CLIPBOARD_SERVICE );
                            if (mgr != null) {
                                mgr.setText(text);
                            }
                        }
                        else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mPDFView.getContext().getSystemService(Context.CLIPBOARD_SERVICE );
                            if (clipboard != null) {
                                ClipData clip = ClipData.newPlainText("text", text);
                                clipboard.setPrimaryClip(clip);
                            }
                        }
                        success = true;
                    }

                    if (!success) {
                        Toast.makeText(mPDFView.getContext(), "failed to copy text", Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {

            }
            finally {
                mPDFView.docUnlock();
            }

            mPDFView.waitForRendering();
        }
        else {
            mNextToolMode = ToolManager.e_pan;
        }
    }

    public boolean onUp(MotionEvent e, int prior_event_type) {
        super.onUp(e, prior_event_type);

        // Avoid double entry, if double tapped.
        if (mUpFromStickyCreateDlgShown) {
            return false;
        }

        mNextToolMode = ToolManager.e_annot_edit;
        mScaled = false;

        if (mAnnot != null &&
                (mModifiedAnnot || !mCtrlPtsSet
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
                    float x1 = mCtrlPts[e_ul].x - mPDFView.getScrollX();
                    float y1 = mCtrlPts[e_ul].y - mPDFView.getScrollY();
                    float x2 = mCtrlPts[e_lr].x - mPDFView.getScrollX();
                    float y2 = mCtrlPts[e_lr].y - mPDFView.getScrollY();
                    double [] pts1, pts2;
                    pts1 = mPDFView.convScreenPtToPagePt(x1, y1, mAnnotPageNum);
                    pts2 = mPDFView.convScreenPtToPagePt(x2, y2, mAnnotPageNum);
                    pdftron.PDF.Rect new_annot_rect = new pdftron.PDF.Rect(pts1[0], pts1[1], pts2[0], pts2[1]);
                    new_annot_rect.normalize();

                    // Compute the old annotation position in screen space for update
                    pdftron.PDF.Rect r = mAnnot.getRect();
                    pts1 = mPDFView.convPagePtToScreenPt(r.getX1(), r.getY1(), mAnnotPageNum);
                    pts2 = mPDFView.convPagePtToScreenPt(r.getX2(), r.getY2(), mAnnotPageNum);
                    pdftron.PDF.Rect old_update_rect = new pdftron.PDF.Rect(pts1[0], pts1[1], pts2[0], pts2[1]);
                    old_update_rect.normalize();

                    mAnnot.resize(new_annot_rect);
                    // We do not want to call refreshAppearance for stamps
                    // to not alter their original appearance.
                    if (mAnnot.getType() != Annot.e_Stamp) {
                        mAnnot.refreshAppearance();
                    }
                    buildAnnotBBox();
                    mPDFView.update(old_update_rect);   // Update the old position
                    mPDFView.update(mAnnot, mAnnotPageNum);

                } else if (prior_event_type == PDFViewCtrl.PRIOR_EVENT_PINCH || prior_event_type == PDFViewCtrl.PRIOR_EVENT_DBLTAP) {
                    setCtrlPts();
                }

                // Show sticky note dialog directly, if set so.
                if (mUpFromStickyCreate && !mUpFromStickyCreateDlgShown) {
                    Markup m = new Markup(mAnnot);
                    Popup tp = m.getPopup();
                    if (tp == null || !tp.isValid()) {
                        tp = Popup.create(mPDFView.getDoc(), mAnnot.getRect());
                        m.setPopup(tp);
                    }
                    final Popup p = tp;
                    final DialogAnnotNote d = new DialogAnnotNote(mPDFView.getContext(), p.getContents());
                    d.setButton(DialogInterface.BUTTON_POSITIVE, mPDFView.getResources().getString(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                // Locks the document first as accessing annotation/doc information
                                // isn't thread safe.
                                mPDFView.docLock(true);
                                p.setContents(d.getNote());
                            } catch (Exception e) {

                            } finally {
                                mPDFView.docUnlock();
                            }
                            mUpFromStickyCreate = false;
                            mUpFromStickyCreateDlgShown = false;
                            showMenu(mMenuTitles, getAnnotRect());
                        }
                    });

                    d.setButton(DialogInterface.BUTTON_NEGATIVE, mPDFView.getResources().getString(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mUpFromStickyCreate = false;
                            mUpFromStickyCreateDlgShown = false;
                            showMenu(mMenuTitles, getAnnotRect());
                        }
                    });

                    d.show();
                    mUpFromStickyCreateDlgShown = true;

                    return false;
                }

            } catch (Exception e1) {

            } finally {
                mPDFView.docUnlock();
            }

            if ( prior_event_type == PDFViewCtrl.PRIOR_EVENT_SCROLLING
                    || prior_event_type == PDFViewCtrl.PRIOR_EVENT_PINCH
                    || prior_event_type == PDFViewCtrl.PRIOR_EVENT_DBLTAP
                    || prior_event_type == PDFViewCtrl.PRIOR_EVENT_FLING) {
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
            // Scaled and if while moving, disable moving and set the control points back to where
            // the annotation is; this is to avoid complications.
            mScaled = true;
            setCtrlPts();
        }
        return false;
    }

    public boolean onFlingStop() {
        super.onFlingStop();

        if (mAnnot != null) {
            if (!mCtrlPtsSet) {
                setCtrlPts();   // May be preceded by annotation creation touch up.
            }
            mPDFView.invalidate((int)Math.floor(mBBox.left), (int)Math.floor(mBBox.top), (int)Math.ceil(mBBox.right), (int)Math.ceil(mBBox.bottom));
        }
        return false;
    }

    public void onLayout (boolean changed, int l, int t, int r, int b) {
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
                mNextToolMode = ToolManager.e_annot_edit;
                setCtrlPts();
                mEffCtrlPtId = 8;
            } else {
                mAnnot = null;
                mNextToolMode = ToolManager.e_pan;
                setCtrlPts();
                mEffCtrlPtId = -1;
            }
            mPDFView.invalidate((int)Math.floor(mBBox.left), (int)Math.floor(mBBox.top), (int)Math.ceil(mBBox.right), (int)Math.ceil(mBBox.bottom));
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
        for (int i = 0; i < 8; ++i) {
            if (!mAnnotIsSticky && !mAnnotIsTextMarkup) {
                // Sticky note and text markup cannot be re-sized
                float s = mCtrlPts[i].x;
                float t = mCtrlPts[i].y;

                float dist = (x - s) * (x - s) + (y - t) * (y - t);
                dist = (float)Math.sqrt(dist);
                if (dist <= thresh && (dist < shortest_dist || shortest_dist < 0)) {
                    mEffCtrlPtId = i;
                    shortest_dist = dist;
                }
            }

            mCtrlPts_save[i].set(mCtrlPts[i]);
        }

        // Check if hit within the bounding box without hitting any control point.
        // Note that text markup cannot be moved.
        if (!mAnnotIsTextMarkup && mEffCtrlPtId < 0 && mBBox.contains(x, y)) {
            mEffCtrlPtId = 8;   // Indicating moving mode;
        }

        // Re-compute the annotation's bounding box on screen, since the zoom
        // factor may have been changed.
        if (mAnnot != null) {
            mPageCropOnClientF = buildPageBoundBoxOnClient(this.mAnnotPageNum);
        }

        return false;
    }

    private boolean boundCornerCtrlPts(float ox, float oy, boolean translate) {
        boolean changed = false;
        if (mPageCropOnClientF != null) {
            float w = mCtrlPts[e_lr].x - mCtrlPts[e_ll].x;
            float h = mCtrlPts[e_ll].y - mCtrlPts[e_ul].y;

            // Bounding along x-axis
            if (mCtrlPts[e_ll].x < mPageCropOnClientF.left && ox < 0) {
                mCtrlPts[e_ll].x = mPageCropOnClientF.left;
                mCtrlPts[e_ul].x = mCtrlPts[e_ll].x;
                if (translate) {
                    mCtrlPts[e_lr].x = mCtrlPts[e_ll].x + w;
                    mCtrlPts[e_ur].x = mCtrlPts[e_lr].x;
                }
                changed = true;

            } else if ( mCtrlPts[e_lr].x > mPageCropOnClientF.right && ox > 0) {
                mCtrlPts[e_lr].x = mPageCropOnClientF.right;
                mCtrlPts[e_ur].x = mCtrlPts[e_lr].x;
                if (translate) {
                    mCtrlPts[e_ll].x = mCtrlPts[e_lr].x - w;
                    mCtrlPts[e_ul].x = mCtrlPts[e_ll].x;
                }
                changed = true;
            }

            // Bounding along y-axis
            if (mCtrlPts[e_ll].y > mPageCropOnClientF.bottom && oy > 0) {
                mCtrlPts[e_ll].y = mPageCropOnClientF.bottom;
                mCtrlPts[e_lr].y = mCtrlPts[e_ll].y;
                if (translate) {
                    mCtrlPts[e_ul].y = mCtrlPts[e_ll].y - h;
                    mCtrlPts[e_ur].y = mCtrlPts[e_ul].y;
                }
                changed = true;

            } else if ( mCtrlPts[e_ur].y < mPageCropOnClientF.top && oy < 0) {
                mCtrlPts[e_ur].y = mPageCropOnClientF.top;
                mCtrlPts[e_ul].y = mCtrlPts[e_ur].y;
                if (translate) {
                    mCtrlPts[e_ll].y = mCtrlPts[e_ur].y + h;
                    mCtrlPts[e_lr].y = mCtrlPts[e_ll].y;
                }
                changed = true;
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
            float thresh = 2 * mCtrlRadius;
            mTempRect.set(mBBox);

            if (mEffCtrlPtId == 8) {
                for (int i = 0; i < 8; ++i) {
                    mCtrlPts[i].x = mCtrlPts_save[i].x + ox;
                    mCtrlPts[i].y = mCtrlPts_save[i].y + oy;
                }

                if (boundCornerCtrlPts(ox, oy, true)) {
                    mCtrlPts[e_ml].x = mCtrlPts[e_ll].x;
                    mCtrlPts[e_ml].y = (mCtrlPts[e_ll].y + mCtrlPts[e_ul].y) / 2;

                    mCtrlPts[e_lm].x = (mCtrlPts[e_ll].x + mCtrlPts[e_lr].x) / 2;
                    mCtrlPts[e_lm].y = mCtrlPts[e_ll].y;

                    mCtrlPts[e_mr].x = mCtrlPts[e_lr].x;
                    mCtrlPts[e_mr].y = (mCtrlPts[e_lr].y + mCtrlPts[e_ur].y) / 2;

                    mCtrlPts[e_um].x = (mCtrlPts[e_ul].x + mCtrlPts[e_ur].x) / 2;
                    mCtrlPts[e_um].y = mCtrlPts[e_ul].y;
                }

                mBBox.left = mCtrlPts[e_ul].x - mCtrlRadius;
                mBBox.top = mCtrlPts[e_ul].y - mCtrlRadius;
                mBBox.right = mCtrlPts[e_lr].x + mCtrlRadius;
                mBBox.bottom = mCtrlPts[e_lr].y + mCtrlRadius;

                mModifiedAnnot = true;

            } else {
                boolean valid = false;
                switch (mEffCtrlPtId) {
                case e_ll:
                    if (mCtrlPts_save[e_ll].x + ox < mCtrlPts_save[e_lr].x - thresh && mCtrlPts_save[e_ll].y + oy > mCtrlPts_save[e_ul].y + thresh) {
                        mCtrlPts[e_ll].x = mCtrlPts_save[e_ll].x + ox;
                        mCtrlPts[e_ul].x = mCtrlPts_save[e_ul].x + ox;
                        mCtrlPts[e_ll].y = mCtrlPts_save[e_ll].y + oy;
                        mCtrlPts[e_lr].y = mCtrlPts_save[e_lr].y + oy;
                        valid = true;
                    }
                    break;
                case e_lm:
                    if (mCtrlPts_save[e_ll].y + oy > mCtrlPts_save[e_ul].y + thresh) {
                        mCtrlPts[e_ll].y = mCtrlPts_save[e_ll].y + oy;
                        mCtrlPts[e_lr].y = mCtrlPts_save[e_lr].y + oy;
                        valid = true;
                    }
                    break;
                case e_lr:
                    if (mCtrlPts_save[e_ll].x  < mCtrlPts_save[e_lr].x + ox - thresh && mCtrlPts_save[e_ll].y + oy > mCtrlPts_save[e_ul].y + thresh) {
                        mCtrlPts[e_lr].x = mCtrlPts_save[e_lr].x + ox;
                        mCtrlPts[e_ur].x = mCtrlPts_save[e_ur].x + ox;
                        mCtrlPts[e_lr].y = mCtrlPts_save[e_lr].y + oy;
                        mCtrlPts[e_ll].y = mCtrlPts_save[e_ll].y + oy;
                        valid = true;
                    }
                    break;
                case e_mr:
                    if (mCtrlPts_save[e_ll].x < mCtrlPts_save[e_lr].x + ox - thresh) {
                        mCtrlPts[e_lr].x = mCtrlPts_save[e_lr].x + ox;
                        mCtrlPts[e_ur].x = mCtrlPts_save[e_ur].x + ox;
                        valid = true;
                    }
                    break;
                case e_ur:
                    if (mCtrlPts_save[e_ll].x  < mCtrlPts_save[e_lr].x + ox - thresh && mCtrlPts_save[e_ll].y > mCtrlPts_save[e_ul].y + oy + thresh) {
                        mCtrlPts[e_lr].x = mCtrlPts_save[e_lr].x + ox;
                        mCtrlPts[e_ur].x = mCtrlPts_save[e_ur].x + ox;
                        mCtrlPts[e_ur].y = mCtrlPts_save[e_ur].y + oy;
                        mCtrlPts[e_ul].y = mCtrlPts_save[e_ul].y + oy;
                        valid = true;
                    }
                    break;
                case e_um:
                    if (mCtrlPts_save[e_ll].y > mCtrlPts_save[e_ul].y + oy + thresh) {
                        mCtrlPts[e_ur].y = mCtrlPts_save[e_ur].y + oy;
                        mCtrlPts[e_ul].y = mCtrlPts_save[e_ul].y + oy;
                        valid = true;
                    }
                    break;
                case e_ul:
                    if (mCtrlPts_save[e_ll].x + ox  < mCtrlPts_save[e_lr].x - thresh && mCtrlPts_save[e_ll].y > mCtrlPts_save[e_ul].y + oy + thresh) {
                        mCtrlPts[e_ul].x = mCtrlPts_save[e_ul].x + ox;
                        mCtrlPts[e_ll].x = mCtrlPts_save[e_ll].x + ox;
                        mCtrlPts[e_ur].y = mCtrlPts_save[e_ur].y + oy;
                        mCtrlPts[e_ul].y = mCtrlPts_save[e_ul].y + oy;
                        valid = true;
                    }
                    break;
                case e_ml:
                    if (mCtrlPts_save[e_ll].x + ox  < mCtrlPts_save[e_lr].x - thresh) {
                        mCtrlPts[e_ul].x = mCtrlPts_save[e_ul].x + ox;
                        mCtrlPts[e_ll].x = mCtrlPts_save[e_ll].x + ox;
                        valid = true;
                    }
                    break;
                }

                if (valid) {
                    boundCornerCtrlPts(ox, oy, false);

                    mCtrlPts[e_ml].x = mCtrlPts[e_ll].x;
                    mCtrlPts[e_ml].y = (mCtrlPts[e_ll].y + mCtrlPts[e_ul].y) / 2;

                    mCtrlPts[e_lm].x = (mCtrlPts[e_ll].x + mCtrlPts[e_lr].x) / 2;
                    mCtrlPts[e_lm].y = mCtrlPts[e_ll].y;

                    mCtrlPts[e_mr].x = mCtrlPts[e_lr].x;
                    mCtrlPts[e_mr].y = (mCtrlPts[e_lr].y + mCtrlPts[e_ur].y) / 2;

                    mCtrlPts[e_um].x = (mCtrlPts[e_ul].x + mCtrlPts[e_ur].x) / 2;
                    mCtrlPts[e_um].y = mCtrlPts[e_ul].y;

                    mBBox.left = mCtrlPts[e_ul].x - mCtrlRadius;
                    mBBox.top = mCtrlPts[e_ul].y - mCtrlRadius;
                    mBBox.right = mCtrlPts[e_lr].x + mCtrlRadius;
                    mBBox.bottom = mCtrlPts[e_lr].y + mCtrlRadius;

                    mModifiedAnnot = true;
                }
            }

            float min_x = Math.min(mTempRect.left, mBBox.left);
            float max_x = Math.max(mTempRect.right, mBBox.right);
            float min_y = Math.min(mTempRect.top, mBBox.top);
            float max_y = Math.max(mTempRect.bottom, mBBox.bottom);
            mPDFView.invalidate((int)min_x - 1, (int)min_y - 1, (int)Math.ceil(max_x) + 1, (int)Math.ceil(max_y) + 1);

            return true;

        } else {
            showTransientPageNumber();
            return false;
        }
    }
}
