package com.bearsonsoftware.list.billing;

import android.app.Activity;

import com.bearsonsoftware.list.billing.util.Purchase;


/**
 * Convenience class to handle all billing-related features and listeners
 */
public class BillingManager {

    public static final String SKU_NO_ADS = "no_ads";
    //public static final String SKU_NO_ADS = "android.test.purchased";
    public static final int RC_REQUEST = 10001;
    private static boolean adsHidden = true;

    private final Activity activity;

    public BillingManager(Activity activity){
        this.activity = activity;
    }

    /** Verifies the developer payload of a purchase. */
    public static boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    public static boolean isAdsHidden(){
        return adsHidden;
    }

    public static void setAdsHidden(boolean value){
        adsHidden = value;
    }
}
