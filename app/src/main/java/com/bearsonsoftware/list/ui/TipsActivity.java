package com.bearsonsoftware.list.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.settings.ThemeManager;

/**
 * Activity to show tips and tricks for app
 */
public class TipsActivity extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        overridePendingTransition(R.anim.back_slide_in, R.anim.back_slide_out);
    }

    @Override
    protected void onResume(){
        super.onResume();
        new ThemeManager(this).applyTheme();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonBackTips:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed(){
        this.finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
