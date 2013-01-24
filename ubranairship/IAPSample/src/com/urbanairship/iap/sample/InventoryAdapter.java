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

import java.util.Collections;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.urbanairship.iap.IAPManager;
import com.urbanairship.iap.Inventory;
import com.urbanairship.iap.Product;
import com.urbanairship.util.AsyncImageLoader;
import com.urbanairship.util.DrawableCache;

/**
 * ListView adapter for feeding a custom inventory list
 */
public class InventoryAdapter extends BaseAdapter implements Observer {

    private static final int defaultMaxCacheSize = 2*1024*1024;
    private static DrawableCache cache = new DrawableCache(defaultMaxCacheSize);

    private Inventory inventory;
    private Context context;
    private Map<ImageView, String> iconViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    public InventoryAdapter(Context ctx) {
        context = ctx;
        inventory = IAPManager.shared().getInventory();
        inventory.addObserver(this);
    }

    public void update(Observable o, Object arg) {

        switch ( ((Inventory)arg).getStatus() ) {
        case LOADED:
            this.notifyDataSetChanged();
            break;
        default:
            break;
        }
    }

    @Override
    public int getCount() {
        return inventory.size(Inventory.FilterType.ALL);
    }

    @Override
    public Object getItem(int position) {
        return inventory.getProducts(Inventory.FilterType.ALL).get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = (Product) getItem(position);

        View view;
        final ImageView iconView;;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.inventory_list_item, null);
            iconView = (ImageView) view.findViewById(R.id.product_icon);
        }

        else {
            view = convertView;
            iconView = (ImageView) view.findViewById(R.id.product_icon);
            iconView.setImageDrawable(null);
        }

        TextView titleView = (TextView) view.findViewById(R.id.product_title);
        titleView.setText(product.getTitle());

        TextView priceView = (TextView) view.findViewById(R.id.product_price);
        priceView.setText(product.getPrice());

        String iconURL = product.getIconURLString();

        iconViews.put(iconView, iconURL);

        if (cache.containsKey(iconURL)) {
            iconView.setImageDrawable(cache.get(iconURL));
        } else {
            new AsyncImageLoader(iconURL, new AsyncImageLoader.Delegate() {
                public void imageLoaded(String urlString, Drawable imageDrawable) {
                    String lastFetched = iconViews.get(iconView);
                    if(lastFetched != null && lastFetched == urlString) {
                        cache.put(urlString, imageDrawable);
                        iconView.setImageDrawable(imageDrawable);
                    }
                }
            });
        }

        TextView descriptionView = (TextView) view.findViewById(R.id.product_description);
        descriptionView.setText(product.getDescription());

        return view;
    }

}
