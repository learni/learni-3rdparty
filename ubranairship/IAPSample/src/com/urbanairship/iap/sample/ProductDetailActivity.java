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

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.urbanairship.iap.IAPManager;
import com.urbanairship.iap.Product;
import com.urbanairship.util.AsyncImageLoader;

public class ProductDetailActivity extends Activity {

    private Product product;
    private ProductObserver productObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        Bundle bundle = getIntent().getExtras();
        String productId = (bundle != null ? bundle.getString("product_id") : "");

        product = IAPManager.shared().getInventory().getProduct(productId);
        productObserver = new ProductObserver();
        product.addObserver(productObserver);

        TextView title = (TextView) findViewById(R.id.detail_item_product_title);
        TextView description = (TextView) findViewById(R.id.detail_item_product_description);
        TextView revision = (TextView) findViewById(R.id.detail_item_product_revision);
        TextView filesize = (TextView) findViewById(R.id.detail_item_product_filesize);

        Button buy = (Button) findViewById(R.id.detail_buy);

        final ImageView icon = (ImageView) findViewById(R.id.detail_item_product_icon);

        title.setText(product.getTitle());
        description.setText(product.getDescription());
        revision.setText("Revision: " + product.getRevision());
        filesize.setText("File Size: " + product.getHumanReadableFileSize());

        productObserver.refresh(product.getStatus());

        new AsyncImageLoader(product.getIconURLString(), new AsyncImageLoader.Delegate(){

            @Override
            public void imageLoaded(String urlString, Drawable imageDrawable) {
                icon.setImageDrawable(imageDrawable);
            }
        });

        if (product.getPreviewURLString() != null && product.getPreviewURLString().length() != 0) {
            final ImageView preview = (ImageView) findViewById(R.id.detail_item_product_preview);
            new AsyncImageLoader(product.getPreviewURLString(), new AsyncImageLoader.Delegate() {

                @Override
                public void imageLoaded(String urlString, Drawable imageDrawable) {
                    preview.setImageDrawable(imageDrawable);

                }
            });
        }

        buy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                purchase(product);
            }
        });
    }

    public void purchase(Product product) {
        IAPManager.shared().getInventory().purchase(this, product);
    }

    public void onDestroy() {
        super.onDestroy();
        product.deleteObserver(productObserver);
    }

    private class ProductObserver implements Observer {

        public void refresh(Product.Status status) {

            Button buy = (Button) findViewById(R.id.detail_buy);
            TextView price = (TextView) findViewById(R.id.detail_item_product_price);

            switch (status) {
            case WAITING:
                buy.setEnabled(false);
                break;
            case PURCHASED:
                buy.setEnabled(false);
                price.setText("Purchased");
                break;
            case DOWNLOADING:
                buy.setEnabled(false);
                price.setText("Loading");
                break;
            case INSTALLED:
                buy.setEnabled(false);
                price.setText("Installed");
                break;
            case UPDATE:
                if(IAPManager.isBillingSupported())
                    buy.setEnabled(true);
                else
                    buy.setEnabled(false);
                price.setText("Update");
                break;
            case UNPURCHASED:
                if(IAPManager.isBillingSupported())
                    buy.setEnabled(true);
                else
                    buy.setEnabled(false);
                price.setText(product.isFree() ? "Free" : product.getPrice() + "");
                break;
            default:
                break;
            }

        }

        public void update(Observable o, Object arg) {
            Product.Status status = ((Product)arg).getStatus();
            refresh(status);
        }
    }
}
