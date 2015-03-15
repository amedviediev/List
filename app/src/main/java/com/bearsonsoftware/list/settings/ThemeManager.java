package com.bearsonsoftware.list.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bearsonsoftware.list.R;

/**
 * Check and apply application theme
 */
public class ThemeManager {
    public static final String DEFAULT_THEME = "default";
    public static final String FOREST_THEME = "forest";
    public static final String PRINCESS_THEME = "princess";
    public static final String SUNSET_THEME = "sunset";
    public static final String IRIS_THEME = "iris";

    private SharedPreferences sharedPreferences;
    private final Activity activity;

    public ThemeManager(Activity activity){
        this.activity = activity;
    }

    //applies all appropriate variables to window decor, according to selected theme
    public void applyTheme(){
        sharedPreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        if(sharedPreferences.contains("theme")){
            switch (sharedPreferences.getString("theme", null)){
                case DEFAULT_THEME:
                    activity.getWindow().setBackgroundDrawableResource(R.drawable.glass_bg);
                    break;
                case FOREST_THEME:
                    activity.getWindow().setBackgroundDrawableResource(R.drawable.forest_bg);
                    break;
                case PRINCESS_THEME:
                    activity.getWindow().setBackgroundDrawableResource(R.drawable.princess_bg);
                    break;
                case SUNSET_THEME:
                    activity.getWindow().setBackgroundDrawableResource(R.drawable.sunset_bg);
                    break;
                case IRIS_THEME:
                    activity.getWindow().setBackgroundDrawableResource(R.drawable.iris_bg);
                    break;
            }

        } else {
            activity.getWindow().setBackgroundDrawableResource(R.drawable.glass_bg);
        }
    }

    public String getTheme(){
        if(sharedPreferences.contains("theme")){
            return sharedPreferences.getString("theme", null);
        } else {
            return DEFAULT_THEME;
        }
    }

    public void saveTheme(int id){
        Editor editor = sharedPreferences.edit();
        switch (id){
            case R.id.radioDefault:
                editor.putString("theme", DEFAULT_THEME);
                break;
            case R.id.radioForest:
                editor.putString("theme", FOREST_THEME);
                break;
            case R.id.radioPrincess:
                editor.putString("theme", PRINCESS_THEME);
                break;
            case R.id.radioSunset:
                editor.putString("theme", SUNSET_THEME);
                break;
            case R.id.radioIris:
                editor.putString("theme", IRIS_THEME);
                break;
        }
        editor.apply();
    }
}
