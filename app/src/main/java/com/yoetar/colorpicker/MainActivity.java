package com.yoetar.colorpicker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yoetar.pickcolor.ColorPicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ArrayList<String> colors = new ArrayList<>();
    public Preferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = new Preferences(MainActivity.this);

        colors.add("#506D84");
        colors.add("#0B6E64");
        colors.add("#3B5996");
        colors.add("#423F3E");
        colors.add("#9A9483");
        colors.add("#4B3869");
        colors.add("#191A19");
        colors.add("#424642");
        colors.add("#C36A2D");
        colors.add("#6C4A4A");
        colors.add("#464D5C");
        colors.add("#FF5C58");

        findViewById(R.id.chooseColor).setOnClickListener(v-> showColorDialog());
    }

    private void showColorDialog() {
        final ColorPicker colorPicker = new ColorPicker(MainActivity.this);
        colorPicker.setColors(colors)
                .setColumns(4)
                .setDefaultColorButton(Color.parseColor(preferences.getCircleColor()))
                .setRoundColorButton(true)
                .setColorButtonSize(40, 40)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        preferences.setThemeNo(position + 1);
                        preferences.setCircleColor(colors.get(position));
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }
}