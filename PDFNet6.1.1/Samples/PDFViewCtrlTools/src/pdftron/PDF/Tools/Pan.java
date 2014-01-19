//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import java.util.LinkedList;

import pdftron.PDF.Annot;
import pdftron.PDF.PDFDoc;
import pdftron.PDF.PDFViewCtrl;
import pdftron.PDF.PDFViewCtrl.LinkInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.pdftron.pdf.tools.R;

/**
 * Pan tool implements the following functions:
 * <ol>
 *     <li>Bring up the text search tool bar, if the top quarter of the client region is tapped;</li>
 *     <li>Bring up the page navigation search tool bar, if the bottom quarter of the client region is tapped;</li>
 *     <li>Select the hit annotation and switch to annotation edit tool on single tap event;</li>
 *     <li>Bring up annotation creation menu upon long press event.</li>
 * </ol>
 */
class Pan extends Tool {

    TopToolbar mTopToolbar;
    BottomToolbar mBottomToolbar;
    Paint mPaint;

    public Pan(PDFViewCtrl ctrl) {
        super(ctrl);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTopToolbar = new TopToolbar(ctrl, Toolbar.STYLE_TOP);
        mBottomToolbar = new BottomToolbar(ctrl, Toolbar.STYLE_BOTTOM);

        // Enable page turning (in non-continuous page presentation mode).
        // It is only turned on in Pan tool.
        mPDFView.setBuiltInPageSlidingEnabled(true);
    }

    public int getMode() {
        return ToolManager.e_pan;
    }

    public void onCreate() {
        mMenuTitles = new LinkedList<MenuEntry>();
        mMenuTitles.add(new MenuEntry("line",           getStringFromResId(R.string.tools_qm_line)));
        mMenuTitles.add(new MenuEntry("arrow",          getStringFromResId(R.string.tools_qm_arrow)));
        mMenuTitles.add(new MenuEntry("rectangle",      getStringFromResId(R.string.tools_qm_rectangle)));
        mMenuTitles.add(new MenuEntry("oval",           getStringFromResId(R.string.tools_qm_oval)));
        mMenuTitles.add(new MenuEntry("freehand",       getStringFromResId(R.string.tools_qm_freehand)));
        mMenuTitles.add(new MenuEntry("free text",      getStringFromResId(R.string.tools_qm_free_text)));
        mMenuTitles.add(new MenuEntry("sticky note",    getStringFromResId(R.string.tools_qm_sticky_note)));
    }

    public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
        super.onMove(e1, e2, x_dist, y_dist);
        mJustSwitchedFromAnotherTool = false;
        return false;
    }

    public boolean onUp(MotionEvent e, int prior_event_type) {
        super.onUp(e, prior_event_type);
        mJustSwitchedFromAnotherTool = false;
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        super.onSingleTapConfirmed(e);
        showTransientPageNumber();

        int x = (int)(e.getX() + 0.5);
        int y = (int)(e.getY() + 0.5);
        selectAnnot(x, y);

        if (mAnnot != null) {
            try {
                mPDFView.docLockRead();
                if (mAnnot.getType() == Annot.e_Link) {
                    // Link navigation
                    mNextToolMode = ToolManager.e_link_action;

                } else if (mAnnot.getType() == Annot.e_Widget) {
                    // Form filling
                    mNextToolMode = ToolManager.e_form_fill;

                } else if (mAnnot.getType() == Annot.e_RichMedia) {
                    // Rich media content
                    mNextToolMode = ToolManager.e_rich_media;

                } else {
                    // Annotation editing
                    if (mAnnot.getType() == Annot.e_Line) {
                        mNextToolMode = ToolManager.e_annot_edit_line;
                    } else {
                         mNextToolMode = ToolManager.e_annot_edit;
                    }
                }
                mAnnotPageNum = mPDFView.getPageNumberFromScreenPt(x, y);

            } catch (Exception ex) {

            } finally {
                mPDFView.docUnlockRead();
            }
        } else {
            mNextToolMode = ToolManager.e_pan;

            // If PDFViewCtrl.setUrlExtraction is enabled, do the test for a possible link here.
            LinkInfo linkInfo = mPDFView.getLinkAt(x,  y);
            if (linkInfo != null) {
                try {
                    String url = linkInfo.getURL();
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(url).matches()) {
                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", url, null));
                        mPDFView.getContext().startActivity(Intent.createChooser(i, getStringFromResId(R.string.tools_misc_sendemail)));
                    } else {
                        // ACTION_VIEW needs the address to have http or https
                        if (!url.startsWith("https://") && !url.startsWith("http://")){
                            url = "http://" + url;
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mPDFView.getContext().startActivity(Intent.createChooser(i, getStringFromResId(R.string.tools_misc_openwith)));
                    }
                } catch (Exception ex) {
                    //Log.v("PDFNet", ex.getMessage());
                }

            } else {
                // Show top/bottom tool bar, if not just switched from another tool
                if (!mJustSwitchedFromAnotherTool) {
                    float height = mPDFView.getHeight();
                    float thresh =  height / 4;
                    if (y <= thresh) {
                        // Clicked the top quarter, show top toolbar.
                        mTopToolbar.show();
                    } else if (y >= height - thresh) {
                        // Clicked the bottom quarter, show bottom toolbar.
                        mBottomToolbar.show();
                    }
                }
            }
        }

        mPDFView.invalidate();

        mJustSwitchedFromAnotherTool = false;
        return false;
    }

    public void onLayout (boolean changed, int l, int t, int r, int b) {
        if (mTopToolbar != null && mTopToolbar.isShowing()) {
            // If there is selected text being shown, need to recompute the
            // highlight quads and draw them again.
            Path path = mTopToolbar.getSelectedTextPath();
            if (!path.isEmpty()) {
                mTopToolbar.rePopulateSearchResult();
            }
            mPDFView.invalidate();
        }
    }

    public boolean onLongPress(MotionEvent e) {
        int x = (int)(e.getX() + 0.5);
        int y = (int)(e.getY() + 0.5);
        selectAnnot(x, y);

        try {
            mPDFView.docLockRead();

            // If hit a link, do the link action
            if (mAnnot != null && mAnnot.getType() == Annot.e_Link) {
                mNextToolMode = ToolManager.e_link_action;
                mAnnotPageNum = mPDFView.getPageNumberFromScreenPt(x, y);;
            } else {
                boolean is_form = mAnnot == null ? false : mAnnot.getType() == Annot.e_Widget;

                // If hit text, run text select tool
                RectF textSelectRect = getTextSelectRect(e.getX(), e.getY());
                if (!is_form && selectTextWithMode(textSelectRect.left, textSelectRect.top, textSelectRect.right, textSelectRect.bottom, PDFViewCtrl.TEXT_SELECTION_MODE_RECTANGULAR)) {
                    mNextToolMode = ToolManager.e_text_select;
                }

                // If hit any annotation, run annotation edit tool
                else if (!is_form && mAnnot != null) {

                    if (mAnnot.getType() == Annot.e_Line) {
                        mNextToolMode = ToolManager.e_annot_edit_line;
                    } else {
                        mNextToolMode = ToolManager.e_annot_edit;
                    }
                    mAnnotPageNum = mPDFView.getPageNumberFromScreenPt(x, y);
                }

                // Hit a form
                else if (is_form) {
                    mNextToolMode = ToolManager.e_form_fill;
                    mAnnotPageNum = mPDFView.getPageNumberFromScreenPt(x, y);
                }

                // Otherwise, stay in pan tool.
                else {
                    mNextToolMode = ToolManager.e_pan;
                    RectF anchor = new RectF(x - 5, y, x + 5, y + 1);
                    showMenu(mMenuTitles, anchor);
                }
            }
        }
        catch (Exception e1) {

        } finally {
            mPDFView.docUnlockRead();
        }

        mJustSwitchedFromAnotherTool = false;
        return false;
    }
    
    private boolean selectTextWithMode(double x1, double y1, double x2, double y2, int mode) {
        mPDFView.setTextSelectionMode(mode);
        return mPDFView.select(x1, y1, x2, y2);
    }

    public boolean onScaleBegin(float x, float y) {
        return false;
    }

    public boolean onScaleEnd(float x, float y) {
        super.onScaleEnd(x, y);
        mJustSwitchedFromAnotherTool = false;
        return false;
    }

    public void onClose() {
        super.onClose();
        mTopToolbar.dismiss();
        mBottomToolbar.dismiss();
    }

    public void onDraw(Canvas canvas, Matrix tfm) {
        mPageNumPosAdjust = 0;
        if (mBottomToolbar != null && mBottomToolbar.isShowing()) {
            mPageNumPosAdjust = mTextVOffset;
            if (mPageNumPosAdjust < 0) {
                mPageNumPosAdjust = 0;
            }
        }
        super.onDraw(canvas, tfm);

        if (mTopToolbar != null) {
            Path path = mTopToolbar.getSelectedTextPath();
            if (!path.isEmpty()) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(Color.rgb(0, 100, 175));
                mPaint.setAlpha(127);
                canvas.drawPath(path, mPaint);
            }
        }
    }

    protected void onQuickMenuClicked(int menu_id, String menu_title) {
        String str = menu_title.toLowerCase();
        if (str.equals("line")) {
            mNextToolMode = ToolManager.e_line_create;
        } else if ( str.equals("arrow")) {
            mNextToolMode = ToolManager.e_arrow_create;
        } else if ( str.equals("rectangle")) {
            mNextToolMode = ToolManager.e_rect_create;
        } else if ( str.equals("oval") ) {
            mNextToolMode = ToolManager.e_oval_create;
        } else if ( str.equals("freehand") ) {
            mNextToolMode = ToolManager.e_ink_create;
        } else if ( str.equals("free text") ) {
            mNextToolMode = ToolManager.e_text_create;
        } else if ( str.equals("sticky note") ) {
            mNextToolMode = ToolManager.e_text_annot_create;
        }
    }

    private void selectAnnot(int x, int y) {
        mAnnot = null;
        mAnnotPageNum = 0;

        // Since find text locks the document, cancel it to release the document.
        mPDFView.cancelFindText();
        try {
            mPDFView.docLockRead();
            Annot a = mPDFView.getAnnotationAt(x, y);
            if (a != null && a.isValid()) {
                mAnnot = a;
                buildAnnotBBox();
            }
        } catch (Exception e1) {

        } finally {
            mPDFView.docUnlockRead();
        }
    }

    /**
     * Base class for TopToolbar and BottomToolbar
     */
    class Toolbar extends PopupWindow implements PopupWindow.OnDismissListener {

        static public final int STYLE_TOP = 1;
        static public final int STYLE_BOTTOM = 2;

        protected Context mContext;
        protected PDFViewCtrl mPDFView;
        protected int mStyle;
        protected EditText mDummy;

        public Toolbar(PDFViewCtrl ctrl, int style) {
            super(ctrl.getContext());
            mPDFView = ctrl;
            mContext = mPDFView.getContext();
            mStyle = style;

            setOnDismissListener(this);

            // Dummy view used to determine the height of the tool bar.
            mDummy = new EditText(mContext);
            mDummy.setText("");
            mDummy.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            mDummy.setGravity(Gravity.CENTER_VERTICAL);
            mDummy.setMaxEms(0);
            mDummy.setVisibility(View.INVISIBLE);
            mDummy.setFocusable(false);

            //setBackgroundDrawable(new BitmapDrawable());  // This is needed for setTouchInterceptor to work. Strange!
            setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

            setFocusable(true);
            setTouchable(true);
            setOutsideTouchable(true);
            setAnimationStyle(-1);
        }

        public void show () {
            int[] sc = new int[2];
            mPDFView.getLocationOnScreen(sc);
            setWidth(mPDFView.getWidth());

            if (mStyle == STYLE_TOP) {
                // showAtLocation()'s 'parent' parameter only ensures the window
                // is shown above it, but the position is not relative to it; instead, the position
                // seems to be relative to the current Activity/Screen.
                // So have to use 'sc' to offset it.
                showAtLocation(mPDFView, Gravity.TOP, sc[0], sc[1]);

            } else if (mStyle == STYLE_BOTTOM) {
                //showAtLocation(mPDFView, Gravity.BOTTOM, sc[0], 0);
                showAtLocation(mPDFView, Gravity.TOP, sc[0], sc[1] + mPDFView.getHeight() - (int)mTextVOffset);
            }
        }

        @Override
        public void onDismiss() {
        }

        public class TButton extends Button {

            final public static int TYPE_NEXT_SEARCH = 1;
            final public static int TYPE_PREV_SEARCH = 2;
            final public static int TYPE_SETTING_SEARCH = 3;
            final public static int TYPE_PREV_PAGE = 4;
            final public static int TYPE_NEXT_PAGE = 5;

            private Paint mPaint;
            private Path mPath;
            private int mType;

            public TButton (Context ctx, int type) {
                super(ctx);
                mType = type;
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPath = new Path();
            }

            protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
                int height = mDummy.getHeight();
                this.setMeasuredDimension(height, height);
            }

            protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
                int width = getWidth();
                int height = getHeight();
                float len = Math.min(width, height);
                float r = len/4;
                mPath.reset();

                if (mType == TYPE_PREV_SEARCH) {
                    mPath.moveTo(r, height-r);
                    mPath.rLineTo(2*r, 0);
                    mPath.rLineTo(-r, -2*r);
                    mPath.close();

                } else if (mType == TYPE_NEXT_SEARCH) {
                    mPath.moveTo(r, r);
                    mPath.rLineTo(2*r, 0);
                    mPath.rLineTo(-r, 2*r);
                    mPath.close();

                } else if (mType == TYPE_SETTING_SEARCH) {

                } else if (mType == TYPE_PREV_PAGE) {
                    mPath.moveTo(r, height-r);
                    mPath.rLineTo(2*r, 0);
                    mPath.rLineTo(-r, -2*r);
                    mPath.close();

                } else if (mType == TYPE_NEXT_PAGE) {
                    mPath.moveTo(r, r);
                    mPath.rLineTo(2*r, 0);
                    mPath.rLineTo(-r, 2*r);
                    mPath.close();
                }
            }

            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawPath(mPath, mPaint);
            }
        }
    }

    /*
     * TopToolbar that implements the text search functional module
     */
    class TopToolbar extends Toolbar implements
            View.OnClickListener,
            View.OnKeyListener,
            View.OnLongClickListener,
            PDFViewCtrl.TextSearchListener {

        private EditText mSearchText;
        private TButton mPrevSearchBtn;
        private TButton mNextSearchBtn;
        private ProgressBar mSearchProgress;
        private boolean mMatchCase;
        private boolean mMatchWholeWord;
        private boolean mUseRegex;
        private boolean mForward;
        private Path mSelPath;
        private boolean mSearchRunning;

        public TopToolbar(PDFViewCtrl ctrl, int style) {
            super(ctrl, style);

            mSelPath = new Path();

            // Retrieve persistent data for text search options
            SharedPreferences settings = mPDFView.getContext().getSharedPreferences(Tool.PREFS_FILE_NAME, 0);
            mMatchWholeWord = settings.getBoolean("text_search_whole_word", false);
            mMatchCase = settings.getBoolean("text_search_case_sensitive", true);
            mUseRegex = settings.getBoolean("text_search_use_regs", false);

            // Create the widgets
            mPrevSearchBtn = new TButton(mContext, TButton.TYPE_PREV_SEARCH);
            mPrevSearchBtn.setGravity(Gravity.CENTER_VERTICAL);
            mPrevSearchBtn.setOnClickListener(this);

            mNextSearchBtn = new TButton(mContext, TButton.TYPE_NEXT_SEARCH);
            mNextSearchBtn.setGravity(Gravity.CENTER_VERTICAL);
            mNextSearchBtn.setOnClickListener(this);

            mSearchText = new EditText(mContext);
            mSearchText.setText("");
            mSearchText.setHint(getStringFromResId(R.string.tools_misc_longpressforoptions));
            mSearchText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            mSearchText.setOnKeyListener(this);
            mSearchText.setSingleLine(true);
            mSearchText.setGravity(Gravity.CENTER_VERTICAL);
            mSearchText.setLongClickable(true);
            mSearchText.setOnLongClickListener(this);
            mSearchText.setId(1);

            mSearchProgress = new ProgressBar(mContext, null,  android.R.attr.progressBarStyleHorizontal);
            mSearchProgress.getProgressDrawable().setAlpha(125);
            mSearchProgress.setVisibility(View.GONE);
            mSearchProgress.setIndeterminate(false);
            mSearchProgress.setMax(100);
            mSearchProgress.setPadding(2, 3, 2, 7); // Shrink it to be within mSearchText.

            // Create search text / progress bar panel
            RelativeLayout panel = new RelativeLayout(mContext);
            RelativeLayout.LayoutParams r = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            r.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            panel.addView(mSearchText, r);
            RelativeLayout.LayoutParams q = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            q.addRule(RelativeLayout.ALIGN_TOP, mSearchText.getId());
            q.addRule(RelativeLayout.ALIGN_LEFT, mSearchText.getId());
            q.addRule(RelativeLayout.ALIGN_BOTTOM, mSearchText.getId());
            q.addRule(RelativeLayout.ALIGN_RIGHT, mSearchText.getId());
            panel.addView(mSearchProgress, q);

            // Add search buttons and the panel into the main layout
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            p.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            LinearLayout main_layout = new LinearLayout(mContext);
            main_layout.addView(mPrevSearchBtn, p);
            main_layout.addView(mNextSearchBtn, p);
            main_layout.addView(panel, p);
            main_layout.addView(mDummy);

            setContentView(main_layout);
        }

        public void onClick (View v) {
            if (v == mPrevSearchBtn) {
                boolean save = mForward;
                mForward = false;
                findText();
                mForward = save;
            } else if (v == mNextSearchBtn) {
                boolean save = mForward;
                mForward = true;
                findText();
                mForward = save;
            }
        }

        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                mForward = true;
                findText();
                return true;
            }
            return false;
        }

        public Path getSelectedTextPath() {
            return mSelPath;
        }

        public void rePopulateSearchResult() {
            mSelPath.reset();
            makeSearchResult(PDFViewCtrl.TEXT_SEARCH_FOUND);
        }

        void findText() {
            mSelPath.reset();
            String str = mSearchText.getText().toString();
            str = str.trim();
            if (str.length() > 0) {
                mPDFView.setTextSearchListener(this);
                mPDFView.findText(str, mMatchCase, mMatchWholeWord, !mForward, mUseRegex);
            }
        }

        private void makeSearchResult(int result) {
            if (result == PDFViewCtrl.TEXT_SEARCH_FOUND) {
                float sx = mPDFView.getScrollX();
                float sy = mPDFView.getScrollY();
                float x, y;
                int sel_pg_begin = mPDFView.getSelectionBeginPage();
                int sel_pg_end = mPDFView.getSelectionEndPage();

                for (int pg = sel_pg_begin; pg <= sel_pg_end; ++pg) {
                    PDFViewCtrl.Selection sel = mPDFView.getSelection(pg);
                    double[] quads = sel.getQuads();
                    double[] pts;
                    int sz = quads.length / 8;

                    if (sz == 0) {
                        continue;
                    }

                    int k = 0;
                    for (int i = 0; i < sz; ++i, k += 8) {
                        pts = mPDFView.convPagePtToScreenPt(quads[k], quads[k + 1], pg);
                        x = (float) pts[0] + sx;
                        y = (float) pts[1] + sy;
                        mSelPath.moveTo(x, y);

                        pts = mPDFView.convPagePtToScreenPt(quads[k + 2], quads[k + 3], pg);
                        x = (float) pts[0] + sx;
                        y = (float) pts[1] + sy;
                        mSelPath.lineTo(x, y);

                        pts = mPDFView.convPagePtToScreenPt(quads[k + 4], quads[k + 5], pg);
                        x = (float) pts[0] + sx;
                        y = (float) pts[1] + sy;
                        mSelPath.lineTo(x, y);

                        pts = mPDFView.convPagePtToScreenPt(quads[k + 6], quads[k + 7], pg);
                        x = (float) pts[0] + sx;
                        y = (float) pts[1] + sy;
                        mSelPath.lineTo(x, y);

                        mSelPath.close();
                    }
                }
            }

            mPDFView.requestRendering();
            mPDFView.invalidate();  // Needed to clear previous search result.
        }

        public void onTextSearchStart() {
            mSearchProgress.setProgress(0);
            mSearchProgress.setVisibility(View.VISIBLE);
            mSearchRunning = true;
        }

        public void onTextSearchProgress(int progress) {
            mSearchProgress.setProgress(progress);
        }

        public void onTextSearchEnd(int result) {
            if (result == PDFViewCtrl.TEXT_SEARCH_FOUND) {
                // Hide soft keyboard just in it blocks the found result.
                InputMethodManager imm = (InputMethodManager)mPDFView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
                showTransientPageNumber();
            }

            makeSearchResult(result);
            mSearchProgress.setVisibility(View.GONE);
            mSearchRunning = false;

            if (result == PDFViewCtrl.TEXT_SEARCH_NOT_FOUND) {
                Toast t = Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_dialog_textsearch_nothing_found), Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
            else if ( result == PDFViewCtrl.TEXT_SEARCH_INVALID_INPUT ) {
                Toast t = Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_dialog_textsearch_invalid_string), Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }
        }

        public boolean onLongClick(View v) {
            if ( v == mSearchText ) {
                final DialogTextSearchOption d = new DialogTextSearchOption(mPDFView.getContext());
                d.setButton(DialogInterface.BUTTON_POSITIVE, getStringFromResId(R.string.tools_misc_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mMatchCase = d.getCaseSensitive();
                        mMatchWholeWord = d.getWholeWord();
                        mUseRegex = d.getRegExps();

                        SharedPreferences settings = mPDFView.getContext().getSharedPreferences(PREFS_FILE_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("text_search_whole_word", mMatchWholeWord);
                        editor.putBoolean("text_search_case_sensitive", mMatchCase);
                        editor.putBoolean("text_search_use_regs", mUseRegex);
                        editor.commit();
                    }
                });

                d.setButton(DialogInterface.BUTTON_NEGATIVE, getStringFromResId(R.string.tools_misc_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                try {
                    d.setCaseSensitive(mMatchCase);
                    d.setWholeWord(mMatchWholeWord);
                    d.setRegExps(mUseRegex);
                    d.show();
                }
                catch (Exception e) {
                    //Log.v("PDFNet", e.toString());
                }
            }
            return false;
        }

        public void onDismiss() {
            boolean searching = mSearchRunning;
            mPDFView.cancelFindText();
            if ( searching )
            {
                Toast t = Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_dialog_textsearch_search_canceled), Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();
            }

            // Clear the selected text
            mPDFView.invalidate();
            mSelPath.reset();
        }
    }

    /*
     * BottomToolbar that implements page navigation module
     */
    class BottomToolbar extends Toolbar implements
            View.OnClickListener,
            SeekBar.OnSeekBarChangeListener {

        private TextView mDocTitle;
        private SeekBar mPageSlider;
        private TButton mPrevPage;
        private TButton mNextPage;
        private boolean mIgnoreChange;

        public BottomToolbar(PDFViewCtrl ctrl, int style) {
            super(ctrl, style);

            setTouchInterceptor(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        dismiss();
                        return true;
                    }
                    return false;
                }
            });

            // Create the controls.
            mPrevPage = new TButton(mContext, TButton.TYPE_PREV_PAGE);
            mPrevPage.setGravity(Gravity.CENTER_VERTICAL);
            mPrevPage.setOnClickListener(this);

            mNextPage = new TButton(mContext, TButton.TYPE_NEXT_PAGE);
            mNextPage.setGravity(Gravity.CENTER_VERTICAL);
            mNextPage.setOnClickListener(this);

            mDocTitle = new TextView(mContext);
            mDocTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

            mPageSlider = new SeekBar(mContext);
            mPageSlider.setOnSeekBarChangeListener(this);
            mIgnoreChange = false;

            // Create button panel, for vertically centering them
            LinearLayout btn_panel = new LinearLayout(mContext);
            LinearLayout.LayoutParams btn_panel_lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            btn_panel.addView(mPrevPage, btn_panel_lp);
            btn_panel.addView(mNextPage, btn_panel_lp);

            // Create right-side panel
            LinearLayout right_panel = new LinearLayout(mContext);

            right_panel.setId(1);
            right_panel.addView(btn_panel);
            right_panel.addView(mDummy);
            right_panel.addView(mDocTitle);

            // Add slider and right-side panel to the main layout
            RelativeLayout.LayoutParams panel_lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            panel_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            panel_lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

            RelativeLayout.LayoutParams slider_lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            slider_lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            slider_lp.addRule(RelativeLayout.LEFT_OF, right_panel.getId());
            slider_lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

            RelativeLayout main_layout = new RelativeLayout(mContext);
            main_layout.addView(right_panel, panel_lp);
            main_layout.addView(mPageSlider, slider_lp);

            // Set content view
            setContentView(main_layout);
        }

        public void show () {
            PDFDoc doc = mPDFView.getDoc();
            if (doc != null) {
                try {
                    // Update slider position
                    int pc = doc.getPageCount();
                    mIgnoreChange = true;
                    mPageSlider.setMax(pc - 1);
                    mIgnoreChange = false;
                    int page = mPDFView.getCurrentPage();
                    mPageSlider.setProgress(page - 1);

                    // Update doc title
                    String name = doc.getFileName();
                    if (name != null && name.length() > 0) {
                        int p = name.lastIndexOf('/') + 1;
                        name = name.substring(p);
                        mDocTitle.setText(name);
                    } else {
                        mDocTitle.setText(getStringFromResId(R.string.tools_misc_nofilename));
                    }

                    // Update buttons.
                    int cp = mPDFView.getCurrentPage();
                    mPrevPage.setEnabled(cp>1);
                    mNextPage.setEnabled(cp<pc);

                } catch (Exception e) {

                }
            }
            super.show();
        }

        public void onClick (View v) {
            if (v == mPrevPage) {
                mPDFView.gotoPreviousPage();
                this.mPageSlider.setProgress(mPDFView.getCurrentPage() - 1);
            } else if (v == mNextPage) {
                mPDFView.gotoNextPage();
                this.mPageSlider.setProgress(mPDFView.getCurrentPage() - 1);
            }
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == mPageSlider && !mIgnoreChange) {
                try {
                    mPDFView.docLock(true);
                    PDFDoc doc = mPDFView.getDoc();
                    if (doc != null) {
                        if (fromUser) {
                            mPDFView.setCurrentPage(progress + 1);
                        }

                        showTransientPageNumber();

                        // Update buttons
                        int pc = doc.getPageCount();
                        int cp = mPDFView.getCurrentPage();
                        mPrevPage.setEnabled(cp > 1);
                        mNextPage.setEnabled(cp < pc);
                    }
                } catch (Exception e) {

                } finally {
                    mPDFView.docUnlock();
                }
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        public void onDismiss() {
            // Called to redraw the transient page number, if shown.
            mPDFView.invalidate();
        }
    }
}
