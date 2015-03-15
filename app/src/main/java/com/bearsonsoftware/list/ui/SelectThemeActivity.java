package com.bearsonsoftware.list.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bearsonsoftware.list.PureListApplication;
import com.bearsonsoftware.list.R;
import com.bearsonsoftware.list.settings.ThemeManager;
import com.google.android.gms.analytics.GoogleAnalytics;

/**
 * Activity to let user select, preview and save app theme
 */
public class SelectThemeActivity extends Activity{

    private ThemeManager themeManager;

    private RadioGroup radioGroup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_theme);
        overridePendingTransition(R.anim.back_slide_in, R.anim.back_slide_out);

        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        radioGroup = (RadioGroup) findViewById(R.id.radioTheme);
        RadioButton radioDefault = (RadioButton) findViewById(R.id.radioDefault);
        RadioButton radioForest = (RadioButton) findViewById(R.id.radioForest);
        RadioButton radioPrincess = (RadioButton) findViewById(R.id.radioPrincess);
        RadioButton radioSunset = (RadioButton) findViewById(R.id.radioSunset);
        RadioButton radioIris = (RadioButton) findViewById(R.id.radioIris);

        switch (themeManager.getTheme()){
            case ThemeManager.DEFAULT_THEME:
                radioDefault.setChecked(true);
                break;
            case ThemeManager.FOREST_THEME:
                radioForest.setChecked(true);
                break;
            case ThemeManager.PRINCESS_THEME:
                radioPrincess.setChecked(true);
                break;
            case ThemeManager.SUNSET_THEME:
                radioSunset.setChecked(true);
                break;
            case ThemeManager.IRIS_THEME:
                radioIris.setChecked(true);
                break;
        }

        //set up google analytics
        //Get a Tracker (should auto-report)
        ((PureListApplication) getApplication()).getTracker(PureListApplication.TrackerName.APP_TRACKER);

    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buttonBackSelectTheme:
                onBackPressed();
                break;
            case R.id.buttonSaveSelectTheme:
                themeManager.saveTheme(radioGroup.getCheckedRadioButtonId());
                onBackPressed();
                break;
        }
    }

    public void onClickRadioSelect(View view){

        switch (view.getId()) {
            case R.id.radioDefault:
                getWindow().setBackgroundDrawableResource(R.drawable.glass_bg);
                break;
            case R.id.radioForest:
                getWindow().setBackgroundDrawableResource(R.drawable.forest_bg);
                break;
            case R.id.radioPrincess:
                getWindow().setBackgroundDrawableResource(R.drawable.princess_bg);
                break;
            case R.id.radioSunset:
                getWindow().setBackgroundDrawableResource(R.drawable.sunset_bg);
                break;
            case R.id.radioIris:
                getWindow().setBackgroundDrawableResource(R.drawable.iris_bg);
                break;
        }
    }

    @Override
    public void onBackPressed(){
        this.finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onStart(){
        super.onStart();
        //Get an Analytics tracker to report app starts and uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}
