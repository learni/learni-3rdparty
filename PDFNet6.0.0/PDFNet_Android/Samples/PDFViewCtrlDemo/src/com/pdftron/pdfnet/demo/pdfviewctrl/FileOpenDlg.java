//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdfnet.demo.pdfviewctrl;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * In this simple example, it only searches the "/mnt" directory and displays all the PDF files found.
 */
public class FileOpenDlg extends ListActivity {
    private ArrayList<File> mFiles = null;
    private FileAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFiles = getPDFFiles("/mnt");
        mAdapter = new FileAdapter(this, R.layout.file_row, inflater, mFiles);
        setListAdapter(mAdapter);
        setContentView(R.layout.file_open_dlg);
    }
    
    private ArrayList<File> getPDFFiles(String location) {
        try {
            FileUtil fu = new FileUtil();
            TreeSet<File> pdf_files = fu.findAllPDFs(location);
            ArrayList<File> files = new ArrayList<File>();
            files.addAll(pdf_files);
            return files;
        } catch (Exception e) {
        }
        return null;
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (mFiles.size() > 0 && id >= 0 && id <= mFiles.size()) {
            openFile(mFiles.get((int) id));
        }
    }
    
    private void openFile(File file) {
        if (file == null) {
            setResult(RESULT_CANCELED);
        } else {
            Intent intent1 = new Intent();
            String str = file.getAbsolutePath();
            intent1.putExtra("com.pdftron.pdfnet.demo.pdfviewctrl.FileOpenData", str);
            setResult(RESULT_OK, intent1);
            finish();
        }
    }
}

class FileUtil {
    class FileCompare implements Comparator<File> {
        public int compare(File f1, File f2) {
            return f1.getAbsolutePath().toLowerCase().compareTo(f2.getAbsolutePath().toLowerCase());
        }
    }
    
    TreeSet<File> mAllPDFs = new TreeSet<File>(new FileCompare());
    
    public FileUtil() {
    }
    
    private void findAllPDFsHelper(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                int sz = files.length;
                for (int i = 0; i < sz; ++i) {
                    if (files[i].isDirectory()) {
                        findAllPDFsHelper(files[i]);
                    } else if (files[i].isFile() && files[i].getName().toLowerCase().endsWith("pdf")) {
                        mAllPDFs.add(files[i]);
                    }
                }
            }
        }
    }
    
    public TreeSet<File> findAllPDFs(String location) {
        File root = new File(location);
        mAllPDFs.clear();
        findAllPDFsHelper(root);
        return mAllPDFs;
    }
}

class FileAdapter extends ArrayAdapter<File> {
    private ArrayList<File> mItems;
    private LayoutInflater mInflater;
    
    public FileAdapter(Context context, int textViewResourceId, LayoutInflater inflater, ArrayList<File> items) {
        super(context, textViewResourceId, items);
        mItems = items;
        mInflater = inflater;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(R.layout.file_row, null);
        }
        File f = mItems.get(position);
        if (f != null) {
            TextView fn = (TextView) v.findViewById(R.id.file_name);
            TextView fs = (TextView) v.findViewById(R.id.file_size);
            if (fn != null) {
                fn.setText(f.getAbsolutePath());
            }
            if (fs != null) {
                long bytes = f.length();
                float mbs = (float) (bytes) / (1024 * 1024);
                mbs = (float) (Math.round(mbs * 100)) / 100;
                fs.setText(mbs + "MB");
            }
        }
        return v;
    }
}