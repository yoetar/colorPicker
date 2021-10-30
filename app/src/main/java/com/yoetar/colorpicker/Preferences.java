package com.yoetar.colorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences extends Activity {

    private static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Preferences(Context context) {
        sharedPreferences = context.getSharedPreferences("PREF", MODE_PRIVATE);
    }

    public String getCircleColor() {
        return sharedPreferences.getString("circlecolor", "#3B5996");
    }

    public void setCircleColor(String s) {
        editor = sharedPreferences.edit();
        editor.putString("circlecolor", s).apply();
    }

    public void setThemeNo(int i) {
        editor = sharedPreferences.edit();
        editor.putInt("theme", i).apply();
    }

}
