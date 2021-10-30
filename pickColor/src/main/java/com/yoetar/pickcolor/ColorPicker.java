/*
 * <!--
 * Copyright 2022 YOSA Developers (https://yoetar.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -->
 */

package com.yoetar.pickcolor;

import static com.yoetar.pickcolor.ColorUtils.dip2px;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ColorPicker {

    private final WeakReference<Activity> mContext;
    private final String negativeText;
    private final String positiveText;
    private final RecyclerView recyclerView;

    private final View dialogViewLayout;
    private final AppCompatButton positiveButton;
    private final AppCompatButton negativeButton;
    private final int marginColorButtonLeft;
    private final int marginColorButtonRight;
    private final int marginColorButtonTop;
    private final int marginColorButtonBottom;
    private final boolean dismiss;
    private OnChooseColorListener onChooseColorListener;
    private ArrayList<ColorPal> colors;
    private ColorViewAdapter colorViewAdapter;
    private TypedArray ta;
    private int columns;
    private int colorButtonWidth, colorButtonHeight;
    private int colorButtonDrawable;
    private boolean roundColorButton;
    private WeakReference<CustomDialog> mDialog;
    private int default_color;

    /**
     * Constructor
     */
    @SuppressLint("InflateParams")
    public ColorPicker(Activity context) {
        dialogViewLayout = LayoutInflater.from(context).inflate(R.layout.color_palette_layout, null, false);
        recyclerView = dialogViewLayout.findViewById(R.id.color_palette);
        positiveButton = dialogViewLayout.findViewById(R.id.positive);
        negativeButton = dialogViewLayout.findViewById(R.id.negative);
        this.mContext = new WeakReference<>(context);
        this.dismiss = true;
        this.marginColorButtonLeft = this.marginColorButtonTop = this.marginColorButtonRight = this.marginColorButtonBottom = 5;
        this.negativeText = context.getString(R.string.colorpicker_dialog_cancel);
        this.positiveText = context.getString(R.string.colorpicker_dialog_ok);
        this.default_color = 0;
        this.columns = 5;
    }

    /**
     * Set buttons color using a resource array of colors example : check in library  res/values/colorpicker-array.xml
     *
     * @param resId Array resource
     * @return this
     */
    public ColorPicker setColors(int resId) {
        if (mContext == null)
            return this;

        Context context = mContext.get();
        if (context == null)
            return this;

        ta = context.getResources().obtainTypedArray(resId);
        colors = new ArrayList<>();
        for (int i = 0; i < ta.length(); i++) {
            colors.add(new ColorPal(ta.getColor(i, 0), false));
        }
        return this;
    }

    /**
     * Set buttons from an arraylist of Hex values
     *
     * @param colorsHexList List of hex values of the colors
     * @return this
     */
    public ColorPicker setColors(ArrayList<String> colorsHexList) {
        colors = new ArrayList<>();
        for (int i = 0; i < colorsHexList.size(); i++) {
            colors.add(new ColorPal(Color.parseColor(colorsHexList.get(i)), false));
        }
        return this;
    }

    /**
     * Set buttons color  Example : Color.RED,Color.BLACK
     *
     * @param colorsList list of colors
     * @return this
     */
    public ColorPicker setColors(int... colorsList) {
        colors = new ArrayList<>();
        for (int aColorsList : colorsList) {
            colors.add(new ColorPal(aColorsList, false));
        }
        return this;
    }

    /**
     * Choose the color to be selected by default
     *
     * @param color int
     * @return this
     */
    public ColorPicker setDefaultColorButton(int color) {
        this.default_color = color;
        return this;
    }

    /**
     * Show the Material Dialog
     */
    public void show() {
        if (mContext == null)
            return;

        Activity context = mContext.get();
        if (context == null)
            return;

        if (colors == null || colors.isEmpty())
            setColors();

        mDialog = new WeakReference<>(new CustomDialog(context, dialogViewLayout));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, columns);
        recyclerView.setLayoutManager(gridLayoutManager);
        colorViewAdapter = new ColorViewAdapter(colors);

        recyclerView.setAdapter(colorViewAdapter);

        if (marginColorButtonBottom != 0 || marginColorButtonLeft != 0 || marginColorButtonRight != 0 || marginColorButtonTop != 0) {
            colorViewAdapter.setColorButtonMargin(
                    dip2px(marginColorButtonLeft, context), dip2px(marginColorButtonTop, context),
                    dip2px(marginColorButtonRight, context), dip2px(marginColorButtonBottom, context));
        }
        if (colorButtonHeight != 0 || colorButtonWidth != 0) {
            colorViewAdapter.setColorButtonSize(dip2px(colorButtonWidth, context), dip2px(colorButtonHeight, context));
        }
        if (roundColorButton) {
            setColorButtonDrawable(R.drawable.round_button);
        }
        if (colorButtonDrawable != 0) {
            colorViewAdapter.setColorButtonDrawable(colorButtonDrawable);
        }

        if (default_color != 0) {
            colorViewAdapter.setDefaultColor(default_color);
        }


        positiveButton.setText(positiveText);
        negativeButton.setText(negativeText);

        positiveButton.setOnClickListener(v -> {
            if (onChooseColorListener != null)
                onChooseColorListener.onChooseColor(colorViewAdapter.getColorPosition(), colorViewAdapter.getColorSelected());
            if (dismiss) {
                dismissDialog();
            }
        });

        negativeButton.setOnClickListener(v -> {
            if (dismiss)
                dismissDialog();
            if (onChooseColorListener != null)
                onChooseColorListener.onCancel();
        });

        if (mDialog == null) {
            return;
        }

        Dialog dialog = mDialog.get();

        if (dialog != null && !context.isFinishing()) {
            dialog.show();
            //Keep mDialog open when rotate
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);
        }

    }

    /**
     * Define the number of columns by default value= 3
     *
     * @param c Columns number
     * @return this
     */
    public ColorPicker setColumns(int c) {
        columns = c;
        return this;
    }

    /**
     * Set a single drawable for all buttons example : you can define a different shape ( then round or square )
     *
     * @param drawable Resource
     */
    public void setColorButtonDrawable(int drawable) {
        this.colorButtonDrawable = drawable;
    }

    /**
     * Set the buttons size in DP
     *
     * @param width  width
     * @param height height
     * @return this
     */
    public ColorPicker setColorButtonSize(int width, int height) {
        this.colorButtonWidth = width;
        this.colorButtonHeight = height;
        return this;
    }

    /**
     * Set round button
     *
     * @param roundButton true if you want a round button
     * @return this
     */
    public ColorPicker setRoundColorButton(boolean roundButton) {
        this.roundColorButton = roundButton;
        return this;
    }

    /**
     * set a listener for the color picked
     *
     * @param listener OnChooseColorListener
     */
    public ColorPicker setOnChooseColorListener(OnChooseColorListener listener) {
        onChooseColorListener = listener;
        return this;
    }

    /**
     * dismiss the mDialog
     */
    public void dismissDialog() {
        if (mDialog == null)
            return;

        Dialog dialog = mDialog.get();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Set default colors defined in colorpicker-array.xml of the library
     */
    private void setColors() {
        if (mContext == null)
            return;

        Context context = mContext.get();
        if (context == null)
            return;

        ta = context.getResources().obtainTypedArray(R.array.default_colors);
        colors = new ArrayList<>();
        for (int i = 0; i < ta.length(); i++) {
            colors.add(new ColorPal(ta.getColor(i, 0), false));
        }
    }

    public interface OnChooseColorListener {
        void onChooseColor(int position, int color);

        void onCancel();
    }

    public interface OnFastChooseColorListener {
        void setOnFastChooseColorListener(int position, int color);
    }

}
