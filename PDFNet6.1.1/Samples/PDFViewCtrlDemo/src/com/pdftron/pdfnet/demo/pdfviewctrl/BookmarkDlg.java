//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdfnet.demo.pdfviewctrl;

import java.util.ArrayList;

import pdftron.Common.PDFNetException;
import pdftron.PDF.Action;
import pdftron.PDF.Bookmark;
import pdftron.PDF.PDFDoc;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BookmarkDlg extends DialogFragment {
    
    public interface OnBookmarkSelectedListener {
        public void onBookmarkSelected(int pageNum);
    }
    
    ListViewBookmarkAdapter listViewBookmarkAdapter = null;
    OnBookmarkSelectedListener mOnBookmarkSelectedListener;
    
    public Bookmark mCurrentBookmark;
    private PDFDoc mPDFDoc;
    public ArrayList<Bookmark> mBookmarks;
    
    private TextView mBookmarkTitle;
    private RelativeLayout mNavBookmark;
    private View emptyViewBookmark;
    
    public BookmarkDlg() {
    }
    
    public BookmarkDlg(PDFDoc pdfDoc) {
        mPDFDoc = pdfDoc;
        mCurrentBookmark = null;
        
        mBookmarks = getChildrenBookmarks(mPDFDoc);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnBookmarkSelectedListener = (OnBookmarkSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " + e.getClass().toString());
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        // Let's change the position of the dialog
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = android.view.Gravity.TOP;
        window.setAttributes(wlp);
        getDialog().setCanceledOnTouchOutside(true);
        // getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setTitle(getResources().getString(R.string.demo_menu_bookmarks));
        
        // Inflate our dialog view
        View view = inflater.inflate(R.layout.bookmarks_dlg, container, false);
        
        // Instantiate our bookmark list view
        ListView listViewBookmark = (ListView) view.findViewById(R.id.listViewBookmark);
        
        // Instantiate and set our custom list view adapter
        listViewBookmarkAdapter = new ListViewBookmarkAdapter(getActivity(), mBookmarks);
        listViewBookmark.setAdapter(listViewBookmarkAdapter);
        listViewBookmark.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Action action = mBookmarks.get(position).getAction();
                    if (action != null && action.isValid()
                            && action.getType() == Action.e_GoTo
                            && action.getDest().isValid()) {
                        mOnBookmarkSelectedListener.onBookmarkSelected(action.getDest().getPage().getIndex());
                    }
                } catch (PDFNetException e) {
                }
            }
        });
        
        emptyViewBookmark = (View) view.findViewById(R.id.emptyTextViewBookmark);
        if (emptyViewBookmark != null) {
            listViewBookmark.setEmptyView(emptyViewBookmark);
        }
        
        // Get our back button
        ImageView backButton = (ImageView) view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });
        
        // Get our bookmark title text view
        mBookmarkTitle = (TextView) view.findViewById(R.id.textViewBookmarkTitle);
        mBookmarkTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });
        
        // Get our top navigation panel
        mNavBookmark = (RelativeLayout) view.findViewById(R.id.navBookmark);
        mNavBookmark.setVisibility(View.GONE);
        
        return view;
    }
    
    private void navigateBack() {
        ArrayList<Bookmark> temp = null;
        try {
            if (mCurrentBookmark != null && mCurrentBookmark.getIndent() > 0) {
                mCurrentBookmark = mCurrentBookmark.getParent();
                temp = getChildrenBookmarks(mCurrentBookmark);
                mBookmarkTitle.setText(mCurrentBookmark.getTitle());
                if (mCurrentBookmark.getIndent() <= 0) {
                    mNavBookmark.setVisibility(View.GONE);
                }
            } else {
                temp = getChildrenBookmarks(mPDFDoc);
                mCurrentBookmark = null;
                mBookmarkTitle.setText("");
                mNavBookmark.setVisibility(View.GONE);
            }
        } catch (PDFNetException e) {
            mCurrentBookmark = null;
            temp = null;
        }
        
        mBookmarks = temp;
        listViewBookmarkAdapter.mBookmarks = temp;
        listViewBookmarkAdapter.notifyDataSetChanged();
    }
    
    private class ListViewBookmarkAdapter extends ArrayAdapter<Bookmark> {
        private ArrayList<Bookmark> mBookmarks;
        LayoutInflater inflater;
        
        public ListViewBookmarkAdapter(Context context, ArrayList<Bookmark> bookmarks) {
            super(context, R.layout.bookmark_row, bookmarks);
            mBookmarks = bookmarks;
            inflater = LayoutInflater.from(context);
        }
        
        @Override
        public int getCount() {
            return mBookmarks.size();
        }
        
        /*
         * This is used to define each row in the list view.
         */
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            
            // Our custom holder will represent the view on each row. See class below.
            ListViewBookmarkHolder holder = null;
            
            if (row == null) {
                // Inflate our row from xml
                row = inflater.inflate(R.layout.bookmark_row, parent, false);
                
                // Instantiate our holder
                holder = new ListViewBookmarkHolder();
                holder.image = (ImageView) row.findViewById(R.id.list_view_bookmark_row_image_view);
                holder.text = (TextView) row.findViewById(R.id.list_view_bookmark_row_text_view);
                
                // Set our holder to the row
                row.setTag(holder);
            } else {
                holder = (ListViewBookmarkHolder) row.getTag();
            }
            
            // We need to add the click listener for the arrow button here because the click listener on the
            // list will not detect if we clicked the text or the icon.
            holder.image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentBookmark = mBookmarks.get(position);
                    // Update mBookmarks with the children
                    mBookmarks = getChildrenBookmarks(mCurrentBookmark);
                    BookmarkDlg.this.mBookmarks = mBookmarks;
                    mNavBookmark.setVisibility(View.VISIBLE);
                    try {
                        mBookmarkTitle.setText(mCurrentBookmark.getTitle());
                    } catch (PDFNetException e) {
                        e.printStackTrace();
                    }
                    notifyDataSetChanged();
                }
            });
            
            Bookmark bookmark = mBookmarks.get(position);
            try {
                holder.text.setText(bookmark.getTitle());
                if (bookmark.hasChildren()) {
                    holder.image.setVisibility(View.VISIBLE);
                } else {
                    holder.image.setVisibility(View.INVISIBLE);
                }
            } catch (PDFNetException e) {
            }
            
            return row;
        }
        
        class ListViewBookmarkHolder {
            private TextView text = null;
            private ImageView image = null;
        }
    }
    
    /**
     * Get the bookmark's siblings to the right.
     * 
     * @param bookmark
     *            the bookmark to start looking for siblings.
     * @return an array list with this bookmark and all its siblings to the right, or null if any.
     */
    public static ArrayList<Bookmark> getSiblingsBookmarks(final Bookmark bookmark) {
        ArrayList<Bookmark> siblingBookmarks = null;
        
        if (bookmark != null) {
            try {
                if (bookmark != null) {
                    Bookmark temp = bookmark;
                    siblingBookmarks = new ArrayList<Bookmark>();
                    while (temp.isValid()) {
                        siblingBookmarks.add(temp);
                        temp = temp.getNext();
                    }
                }
            } catch (Exception e) {
            }
        }
        
        return siblingBookmarks;
    }
    
    /**
     * Get the children bookmarks from the PDFDoc document.
     * 
     * @param document
     *            the PDFDoc document to extract the bookmarks.
     * @return an ArrayList<Bookmark> of all the children bookmarks of this document or null if any.
     */
    public static ArrayList<Bookmark> getChildrenBookmarks(final PDFDoc document) {
        Bookmark bookmark = null;
        
        if (document != null) {
            try {
                bookmark = document.getFirstBookmark();
            } catch (PDFNetException e) {
            }
        }
        
        return getSiblingsBookmarks(bookmark);
    }
    
    /**
     * Get the children bookmarks from this bookmark.
     * 
     * @param bookmark
     *            The Bookmark bookmark to extract the bookmarks.
     * @return an ArrayList<Bookmark> of all the children bookmarks, or null if any.
     */
    public static ArrayList<Bookmark> getChildrenBookmarks(final Bookmark bookmark) {
        Bookmark childBookmark = null;
        
        if (bookmark != null) {
            try {
                childBookmark = bookmark.getFirstChild();
            } catch (PDFNetException e) {
            }
        }
        
        return getSiblingsBookmarks(childBookmark);
    }
}
