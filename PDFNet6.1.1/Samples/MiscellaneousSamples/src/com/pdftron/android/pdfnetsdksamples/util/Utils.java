//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.android.pdfnetsdksamples.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;

import pdftron.PDF.PDFNet;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.pdftron.android.pdfnetsdksamples.MiscellaneousSamplesApplication;
import com.pdftron.android.pdfnetsdksamples.R;

public class Utils {
    
    /**
     * Get an InputStream object from an asset file. The file path is
     * relative to the root of the assets folder.
     * 
     * @param filePath the file path to the file in assets folder
     * @return an InputStream of the file
     */
    public static InputStream getAssetInputStream(String filePath) {
        try {
            return MiscellaneousSamplesApplication.getInstance().getAssets().open(filePath);
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Creates a temporary file in the application specific cache directory
     * for the file stored in the assets folder. The file path is relative 
     * to the root of the assets folder.
     * 
     * @param filePath the file path to the file in assets folder
     * @return a File object for the supplied file path
     */
    public static File getAssetTempFile(String filePath) {
        File file = null;
        try {
            file = new File(MiscellaneousSamplesApplication.getInstance().getCacheDir(), FilenameUtils.getName(filePath));
            InputStream inputStream = getAssetInputStream(filePath);
            FileOutputStream output = new FileOutputStream(file); 
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            inputStream.close();
        } catch (Exception e) {
            return null;
        }
        return file;
    }
    
    /**
     * Get a Bitmap object from a file stored in the assets folder.
     * The file path is relative to the root of the assets folder.
     * 
     * @param filePath the file path to the file in assets folder
     * @return a Bitmap object for the supplied file
     */
    public static Bitmap getAssetBitmap(String filePath) {
        return BitmapFactory.decodeStream(getAssetInputStream(filePath));
    }
    
    /**
     * Creates an external file on the external filesystem where the
     * application can place persistent files it owns.
     *  
     * @param fileName the file name for the file to be created
     * @return a File object
     */
    public static File createExternalFile(String fileName) {
        return new File(MiscellaneousSamplesApplication.getInstance().getExternalFilesDir(null), fileName);
    }
    
    /**
     * Returns the absolute path to the directory on the external filesystem
     * (that is somewhere on Environment.getExternalStorageDirectory()) where
     * the application can place persistent files it owns.
     * 
     * @return the absolute file path
     */
    public static String getExternalFilesDirPath() {
        return MiscellaneousSamplesApplication.getInstance().getExternalFilesDir(null).getAbsolutePath();
    }
    
    public static void showAbout(FragmentActivity activity) {
        DialogFragment newFragment = new AboutDialog();
        newFragment.show(activity.getSupportFragmentManager(), "dialog_about");
    }

    public static class AboutDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get PDFNet version
            double pdfnetVersion = 0;
            try {
                pdfnetVersion = PDFNet.getVersion();
            } catch (Exception e) {
                // Do nothing
            }
            // Get application version
            String versionName = String.valueOf(pdfnetVersion);

            // Build the dialog body view
            SpannableStringBuilder aboutBody = new SpannableStringBuilder();
            aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)));

            LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView aboutBodyView = (TextView) layoutInflater.inflate(R.layout.dialog_about, null);
            aboutBodyView.setText(aboutBody);
            aboutBodyView.setMovementMethod(new LinkMovementMethod());

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.about)
                    .setView(aboutBodyView)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }
}
