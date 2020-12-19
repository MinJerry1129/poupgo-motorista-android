package com.view.editBox;

import android.graphics.Color;

public class Colors {
    public static boolean isLight(int color) {
        return Math.sqrt(((((double) (Color.red(color) * Color.red(color))) * 0.241d) + (((double) (Color.green(color) * Color.green(color))) * 0.691d)) + (((double) (Color.blue(color) * Color.blue(color))) * 0.068d)) > 130.0d;
    }
}
