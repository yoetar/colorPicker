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

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.ColorInt;

/**
 * @author Kristiyan Petrov
 */

public class ColorUtils {

    /**
     * Returns true if the text color should be white, given a background color
     *
     * @param color background color
     * @return true if the text should be white, false if the text should be black
     */
    public static boolean isWhiteText(@ColorInt final int color) {
        final int red = Color.red(color);
        final int green = Color.green(color);
        final int blue = Color.blue(color);

        // https://en.wikipedia.org/wiki/YIQ
        // https://24ways.org/2010/calculating-color-contrast/
        final int yiq = ((red * 299) + (green * 587) + (blue * 114)) / 1000;
        return yiq < 192;
    }

    public static int dip2px(float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
