package com.urbanairship.iap.sample;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.urbanairship.UAirship;
import com.urbanairship.iap.BasicPurchaseNotificationBuilder;
import com.urbanairship.iap.PurchaseNotificationBuilder;
import com.urbanairship.iap.PurchaseNotificationInfo;
import com.urbanairship.iap.PurchaseNotificationInfo.NotificationType;

public class CustomPurchaseNotificationBuilder extends BasicPurchaseNotificationBuilder implements PurchaseNotificationBuilder {

    private Context context = UAirship.shared().getApplicationContext();

    public Notification buildNotification(PurchaseNotificationInfo info) {

        PendingIntent launchIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);

        Notification n = new Notification(R.drawable.icon, info.getProductName(), info.getTimestamp());
        n.flags = info.getFlags();

        n.contentView = new RemoteViews(context.getPackageName(), R.layout.purchase_notification);
        n.contentView.setImageViewResource(R.id.status_icon, R.drawable.icon);
        n.contentIntent = launchIntent;
        n.contentView.setTextViewText(R.id.status_text, this.getNotificationMessage(info));
        n.contentView.setProgressBar(R.id.status_progress, 100, info.getProgress(), false);

        NotificationType type = info.getNotificationType();

        if(type == NotificationType.DOWNLOADING) {
            n.contentView.setViewVisibility(R.id.status_wheel_wrapper, View.GONE);
            n.contentView.setViewVisibility(R.id.status_progress_wrapper, View.VISIBLE);
            n.contentView.setProgressBar(R.id.status_progress, 100, info.getProgress(), false);
        }

        else if(type == NotificationType.VERIFYING_RECEIPT || type == NotificationType.DECOMPRESSING) {
            n.contentView.setViewVisibility(R.id.status_progress_wrapper, View.GONE);
            n.contentView.setViewVisibility(R.id.status_wheel_wrapper, View.VISIBLE);
        }

        else {
            n.contentView.setViewVisibility(R.id.status_wheel_wrapper, View.GONE);
            n.contentView.setViewVisibility(R.id.status_progress_wrapper, View.GONE);
        }

        return n;
    }
}
