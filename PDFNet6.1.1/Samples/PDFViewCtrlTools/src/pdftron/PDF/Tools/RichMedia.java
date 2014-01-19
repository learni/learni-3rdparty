//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package pdftron.PDF.Tools;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.Arrays;

import pdftron.Filters.Filter;
import pdftron.PDF.Annot;
import pdftron.PDF.FileSpec;
import pdftron.PDF.PDFViewCtrl;
import pdftron.SDF.NameTree;
import pdftron.SDF.NameTreeIterator;
import pdftron.SDF.Obj;
import com.pdftron.pdf.tools.R;

class RichMedia extends Tool {

    // http://developer.android.com/guide/appendix/media-formats.html#core
    protected static final String[] SUPPORTED_FORMATS = {".3gp", ".mp4", ".ts", ".webm", ".mkv"};

    private VideoView mVideoView;

    // When adding or removing the VideoView widget, the content behind the widget is shown in its
    // place for an instant. To avoid this, we use another view on top of it to temporarily hide
    // any content.
    private FrameLayout mCoverView;
    protected int mCoverHideTime = 250;
    private int mCoverColor = Color.LTGRAY;

    public RichMedia(PDFViewCtrl ctrl) {
        super(ctrl);
        mVideoView = null;
        mCoverView = null;
    }

    @Override
    public int getMode() {
        return ToolManager.e_rich_media;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        handleRichMediaAnnot(e);
        return false;
    }

    @Override
    public boolean onLongPress(MotionEvent e) {
        handleRichMediaAnnot(e);
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScaleBegin(float x, float y) {
        // VideoView widget do not behave properly when scaling for older versions of Android
        return !(android.os.Build.VERSION.SDK_INT >= 16);   // Build.VERSION_CODES.JELLY_BEAN
    }

    @Override
    public boolean onScale(float x, float y) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {   // Build.VERSION_CODES.JELLY_BEAN
            adjustViewPosition(mVideoView);
            adjustViewPosition(mCoverView);
            return false;
        }

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null || mCoverView != null) {
            new CloseVideoViewTask().execute();
        }
    }

    private void handleRichMediaAnnot(MotionEvent e) {
        int x = (int) (e.getX() + 0.5);
        int y = (int) (e.getY() + 0.5);

        // Let's stay on this mode by default
        mNextToolMode = this.getMode();

        if (mAnnot != null) {
            if (isInsideAnnot(x, y)) {
                if (mVideoView == null) {
                    // Extract embedded media from PDF and start playing
                    new ExtractMediaTask().execute(mAnnot);
                }
            } else {
                // Stop playback and quit current mode
                new CloseVideoViewTask().execute();
                mNextToolMode = ToolManager.e_pan;
            }
        }
    }

    @Override
    public void onClose() {
        if (mVideoView != null || mCoverView != null) {
            new CloseVideoViewTask().execute();
        }
    }

    private void setupAndPlayMedia(String path) {
        mVideoView = new VideoView(mPDFView.getContext());

        mCoverView = new FrameLayout(mPDFView.getContext());
        mCoverView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mCoverView.setBackgroundColor(mCoverColor);
        mCoverView.setVisibility(View.VISIBLE);

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                new StartVideoViewTask().execute();
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_richmedia_playback_end), Toast.LENGTH_SHORT).show();
            }
        });

        adjustViewPosition(mVideoView);
        adjustViewPosition(mCoverView);

        mVideoView.setVideoPath(path);
        mVideoView.setMediaController(new MediaController(mPDFView.getContext()));

        mPDFView.addView(mVideoView);
        mPDFView.addView(mCoverView);
    }

    private void adjustViewPosition(View view) {
        if (view != null) {
            double x1 = mAnnotBBox.left;
            double y1 = mAnnotBBox.bottom;
            double x2 = mAnnotBBox.right;
            double y2 = mAnnotBBox.top;
            double pts1[] = mPDFView.convPagePtToScreenPt(x1, y1, mAnnotPageNum);
            double pts2[] = mPDFView.convPagePtToScreenPt(x2, y2, mAnnotPageNum);
            x1 = pts1[0];
            y1 = pts1[1];
            x2 = pts2[0];
            y2 = pts2[1];

            int sx = mPDFView.getScrollX();
            int sy = mPDFView.getScrollY();
            int anchor_x = (int) (x1 + sx + 0.5);
            int anchor_y = (int) (y1 + sy + 0.5);
            view.layout(anchor_x, anchor_y, (int) (anchor_x + x2 - x1 + 0.5), (int) (anchor_y + y2 - y1 + 0.5));
        }
    }

    private boolean isMediaFileValid(String result) {
        if (result.isEmpty()) {
            // Error while extracting the media
            Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_richmedia_error_extracting_media), Toast.LENGTH_LONG).show();
            return false;

        } else if (!isMediaFileSupported(result)) {
            // Let's do a preliminary filter on the file before letting the VideoView complain
            Toast.makeText(mPDFView.getContext(), getStringFromResId(R.string.tools_richmedia_unsupported_format), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    protected boolean isMediaFileSupported(String fileName) {
        int idx = fileName.lastIndexOf(".");
        return ((idx != -1) && Arrays.asList(SUPPORTED_FORMATS).contains(fileName.substring(idx)));
    }

    private class StartVideoViewTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress();
            try {
                Thread.sleep(mCoverHideTime);
            } catch (InterruptedException e) {
                // Do nothing
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if (mVideoView != null) {
                mVideoView.start();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mCoverView != null) {
                mCoverView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class CloseVideoViewTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress();
            try {
                Thread.sleep(mCoverHideTime);
            } catch (InterruptedException e) {
                // Do nothing
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if (mCoverView != null) {
                mCoverView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mVideoView != null) {
                mVideoView.stopPlayback();
                mPDFView.removeView(mVideoView);
                mVideoView = null;
            }
            if (mCoverView != null) {
                mPDFView.removeView(mCoverView);
                mCoverView = null;
            }
        }
    }

    private class ExtractMediaTask extends AsyncTask<Annot, Void, String> {

        private ProgressDialog dialog = null;

        @Override
        protected String doInBackground(Annot... annots) {
            String fileName = "";

            mPDFView.docLockRead();
            try {
                // Extract media to device
                Obj ad = annots[0].getSDFObj();
                Obj mc = ad.findObj("RichMediaContent");
                if (mc != null) {
                    NameTree assets = new NameTree(mc.findObj("Assets"));
                    if (assets.isValid()) {
                        NameTreeIterator j = assets.getIterator();
                        for (; j.hasNext(); j.next()) {
                            String asset_name = j.key().getAsPDFText();

                            // Before going on with the extraction, let's check if the file
                            // already exists in our temp folder and if it is in the supported
                            // formats list.

                            // TODO Make the file name unique
                            // We could have in the same document two or more rich media annotations
                            // with the same asset name.
                            File file = new File(mPDFView.getContext().getExternalFilesDir(null), asset_name);
                            if (file.exists()) {
                                fileName = file.getAbsolutePath();
                                break;
                            } else {
                                if (isMediaFileSupported(asset_name)) {
                                    FileSpec file_spec = new FileSpec(j.value());
                                    Filter stm = file_spec.getFileData();
                                    if (stm != null) {
                                        stm.writeToFile(file.getAbsolutePath(), false);
                                        fileName = file.getAbsolutePath();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Error while extracting media
                fileName = "";
            } finally {
                mPDFView.docUnlockRead();
            }

            return fileName;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (dialog != null) {
                dialog.dismiss();
            }

            if (isMediaFileValid(result)) {
                setupAndPlayMedia(result);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(mPDFView.getContext());
            dialog.setMessage(getStringFromResId(R.string.tools_richmedia_please_wait_loading));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }
    }
}
