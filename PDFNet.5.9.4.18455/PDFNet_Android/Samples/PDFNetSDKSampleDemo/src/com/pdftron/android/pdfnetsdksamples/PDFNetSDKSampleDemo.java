//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.android.pdfnetsdksamples;

import pdftron.PDF.PDFNet;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pdftron.android.pdfnetsdksamples.util.Utils;

/**
 * An activity representing a list of Samples.
 */
public class PDFNetSDKSampleDemo extends FragmentActivity
        implements SampleListFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        // In two-pane mode, list items should be given the
        // 'activated' state when touched.
        ((SampleListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.sample_list))
                .setActivateOnItemClick(true);

        try {
            PDFNet.initialize(this);
        }
        catch (Exception e) {
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_about:
                Utils.showAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Callback method from {@link SampleListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(int id) {
        Bundle arguments = new Bundle();
        arguments.putInt(SampleDetailFragment.ARG_SAMPLE_ID, id);
        SampleDetailFragment fragment = new SampleDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_detail_container, fragment)
                .commit();
    }
}
