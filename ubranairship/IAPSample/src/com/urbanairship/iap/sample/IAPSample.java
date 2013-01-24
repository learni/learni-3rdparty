
package com.urbanairship.iap.sample;

import android.app.Application;

import com.urbanairship.UAirship;
import com.urbanairship.iap.IAPManager;

public class IAPSample extends Application {

    @Override
    public void onCreate() {
        UAirship.takeOff(this);
        CustomPurchaseNotificationBuilder builder = new CustomPurchaseNotificationBuilder();
        IAPManager.shared().setNotificationBuilder(builder);
    }
}
