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

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ColorViewAdapter extends RecyclerView.Adapter<ColorViewAdapter.ViewHolder> {

    private final ArrayList<ColorPal> mDataset;
    ColorPicker.OnFastChooseColorListener onFastChooseColorListener;
    WeakReference<CustomDialog> mDialog;
    private int colorPosition = -1;
    private int colorSelected;
    private int marginButtonLeft = 0, marginButtonRight = 0, marginButtonTop = 3, marginButtonBottom = 3;
    private int buttonWidth = -1, buttonHeight = -1;
    private int buttonDrawable;

    public ColorViewAdapter(ArrayList<ColorPal> myDataset) {
        mDataset = myDataset;
    }

    private void dismissDialog() {
        if (mDialog == null)
            return;
        Dialog dialog = mDialog.get();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public int getColorSelected() {
        return colorSelected;
    }

    public int getColorPosition() {
        return colorPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.palette_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int color = mDataset.get(position).getColor();

        int textColor = ColorUtils.isWhiteText(color) ? Color.WHITE : Color.BLACK;

        if (mDataset.get(position).isCheck()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                holder.colorItem.setText("✔");
            } else {

                holder.colorItem.setText(Html.fromHtml("&#x2713;"));
            }
        } else
            holder.colorItem.setText("");

        holder.colorItem.setTextColor(textColor);
        if (buttonDrawable != 0) {
            holder.colorItem.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } else {
            holder.colorItem.setBackgroundColor(color);
        }
        holder.colorItem.setTag(color);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setDefaultColor(int color) {
        for (int i = 0; i < mDataset.size(); i++) {
            ColorPal colorPal = mDataset.get(i);
            if (colorPal.getColor() == color) {
                colorPal.setCheck(true);
                colorPosition = i;
                notifyItemChanged(i);
                colorSelected = color;
            }
        }
    }

    public void setColorButtonMargin(int left, int top, int right, int bottom) {
        this.marginButtonLeft = left;
        this.marginButtonRight = right;
        this.marginButtonTop = top;
        this.marginButtonBottom = bottom;
    }

    public void setColorButtonSize(int width, int height) {
        this.buttonWidth = width;
        this.buttonHeight = height;
    }

    public void setColorButtonDrawable(int drawable) {
        this.buttonDrawable = drawable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public AppCompatButton colorItem;

        public ViewHolder(View v) {
            super(v);
            //buttons settings
            colorItem = v.findViewById(R.id.color);
            int tickColor = Color.WHITE;
            colorItem.setTextColor(tickColor);
            colorItem.setBackgroundResource(buttonDrawable);
            colorItem.setOnClickListener(this);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) colorItem.getLayoutParams();
            layoutParams.setMargins(marginButtonLeft, marginButtonTop, marginButtonRight, marginButtonBottom);
            if (buttonWidth != -1)
                layoutParams.width = buttonWidth;
            if (buttonHeight != -1)
                layoutParams.height = buttonHeight;
        }

        @Override
        public void onClick(View v) {
            if (colorPosition != -1 && colorPosition != getLayoutPosition()) {
                mDataset.get(colorPosition).setCheck(false);
                notifyItemChanged(colorPosition);
            }
            colorPosition = getLayoutPosition();
            colorSelected = (int) v.getTag();
            mDataset.get(getLayoutPosition()).setCheck(true);
            notifyItemChanged(colorPosition);

            if (onFastChooseColorListener != null && mDialog != null) {
                onFastChooseColorListener.setOnFastChooseColorListener(colorPosition, colorSelected);
                dismissDialog();
            }
        }
    }

}