package com.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.poupgo.driver.R;

public class MButton extends AppCompatButton {
    private static Typeface mTypeface;

    public MButton(Context context) {
        super(context);
        init(null);
    }

    public MButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typeArr = getContext().obtainStyledAttributes(attrs, R.styleable.MButton);
        if (typeArr != null) {
            String typeFace_str = typeArr.getResources().getString(R.string.robotolightFont);
            if (typeFace_str != null) {
                mTypeface = Typeface.createFromAsset(getContext().getAssets(), typeFace_str);
            } else {
                mTypeface = Typeface.createFromAsset(getContext().getAssets(), getResources().getString(R.string.robotolightFont));
            }
        } else if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(getContext().getAssets(), getResources().getString(R.string.robotolightFont));
        }
        setTypeface(mTypeface);
        setAllCaps(true);
    }
}
