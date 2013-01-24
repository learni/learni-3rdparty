/*
 * Copyright 2011 Urban Airship Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE URBAN AIRSHIP INC ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL URBAN AIRSHIP INC OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.urbanairship.iap.sample;

import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.urbanairship.iap.IAPManager;
import com.urbanairship.iap.Inventory;
import com.urbanairship.iap.Product;

/**
 * Example implementation of a ListActivity that utilizes
 * {@link com.urbanairship.iap.InventoryAdapter}
 */

public class InventoryListActivity extends ListActivity {

    private InventoryAdapter adapter;
    private InventoryObserver inventoryObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_list);

        if(!IAPManager.isBillingSupported())
            showDialog(1);

        /**
         * Create an instance of Inventory adapter and give it the resource ids
         * of the views which correspond to the product's members
         */

        adapter = new InventoryAdapter(this);
        setListAdapter(adapter);

        inventoryObserver = new InventoryObserver();
        IAPManager.shared().getInventory().addObserver(inventoryObserver);

    }

    public void onDestroy() {
        super.onDestroy();
        IAPManager.shared().getInventory().deleteObserver(inventoryObserver);
    }

    protected Dialog onCreateDialog(int id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Attention");
        builder.setIcon(R.drawable.icon);
        builder.setMessage("The version of Android Market installed on this device does not support in-app purchase.");

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    /**
     * When someone clicks on an inventory item, launch the product
     * detail activity
     */

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        Intent i = new Intent(getBaseContext(), ProductDetailActivity.class);
        Product product = (Product) adapter.getItem(pos);
        i.putExtra("product_id", product.getIdentifier());
        startActivity(i);
    }

    private class InventoryObserver implements Observer {

        public void update(Observable o, Object inventory) {

            TextView loadingTextView = (TextView) findViewById(R.id.inventory_loading_text);

            switch(((Inventory)inventory).getStatus()) {

            case EMPTY:
                loadingTextView.setText("No products found.");
                break;
            case FAILED:
                loadingTextView.setText("Inventory failed to load.");
                break;
            case LOADED:
                loadingTextView.setVisibility(TextView.GONE);
            default:
                break;
            }
        }

    }
}
