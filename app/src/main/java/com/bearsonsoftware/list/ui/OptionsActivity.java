package com.bearsonsoftware.list.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.billing.BillingManager;
import com.bearsonsoftware.list.billing.Keys;
import com.bearsonsoftware.list.billing.util.IabHelper;
import com.bearsonsoftware.list.billing.util.IabResult;
import com.bearsonsoftware.list.billing.util.Purchase;
import com.bearsonsoftware.list.settings.ThemeManager;

/**
 * Activity to show list of options
 */
public class OptionsActivity extends Activity {

    private IabHelper mHelper;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        overridePendingTransition(R.anim.back_slide_in, R.anim.back_slide_out);

        if(BillingManager.isAdsHidden()) {
            Button adsButton = (Button) findViewById(R.id.buttonRemoveAds);
            adsButton.setVisibility(View.GONE);
        }

        //init internal payments and check for payment status
        mHelper = new IabHelper(this, Keys.LICENCE_KEY);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    alert("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
            }
        });

    }



    final IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                alert("Error purchasing: " + result);
                return;
            }
            if (!BillingManager.verifyDeveloperPayload(purchase)) {
                alert("Error purchasing. Authenticity verification failed.");
                return;
            }

            // bought the no ads upgrade!
            alert("Thank you for your purchase!");
            BillingManager.setAdsHidden(true);

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        new ThemeManager(this).applyTheme();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important to dispose of billing helper
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonThemes:
                Intent selectThemeActivity = new Intent(OptionsActivity.this, SelectThemeActivity.class);
                startActivity(selectThemeActivity);
                break;
            case R.id.buttonTips:
                Intent tipsActivity = new Intent(OptionsActivity.this, TipsActivity.class);
                startActivity(tipsActivity);
                break;
            case R.id.buttonAbout:
                Intent aboutActivity = new Intent (OptionsActivity.this, AboutActivity.class);
                startActivity(aboutActivity);
                break;
            case R.id.buttonRemoveAds:
                String payload = "pureListNoAdsPurchasePayload";
                try{
                    mHelper.launchPurchaseFlow(this, BillingManager.SKU_NO_ADS, BillingManager.RC_REQUEST,
                            mPurchaseFinishedListener, payload);
                } catch (Exception e){
                    alert("An unexpected error occured");
                }

            case R.id.buttonBackOptions:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        this.finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }
}
