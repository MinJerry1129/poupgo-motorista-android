package com.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.poupgo.driver.R;

public class MTextView extends AppCompatTextView {
    private boolean isCustomTypeFaceSet;
    public Typeface mTypeface;

    public MTextView(Context context) {
        this(context, null);
    }

    public MTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.isCustomTypeFaceSet = false;
        TypedArray typeArr = context.obtainStyledAttributes(attrs, R.styleable.MTextView);
        if (this.mTypeface == null) {
            if (typeArr != null) {
                String typeFace_str = typeArr.getString(0);
                if (typeFace_str != null) {
                    try {
                        if (typeFace_str.equalsIgnoreCase("roboto_medium")) {
                            typeFace_str = getResources().getString(R.string.robotomediumFont);
                        } else if (typeFace_str.equalsIgnoreCase("roboto_light")) {
                            typeFace_str = getResources().getString(R.string.robotolightFont);
                        } else if (typeFace_str.equalsIgnoreCase("roboto_bold")) {
                            typeFace_str = getResources().getString(R.string.robotobold);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (typeFace_str != null) {
                    this.mTypeface = Typeface.createFromAsset(context.getAssets(), typeFace_str);
                } else {
                    this.mTypeface = Typeface.createFromAsset(context.getAssets(), getResources().getString(R.string.robotolightFont));
                }
            } else {
                this.mTypeface = Typeface.createFromAsset(context.getAssets(), getResources().getString(R.string.robotolightFont));
            }
        }
        setCustomTypeFace(this.mTypeface);
    }

    private void setCustomTypeFace(Typeface mTypeface) {
        this.mTypeface = mTypeface;
        setTypeface(mTypeface);
        this.isCustomTypeFaceSet = true;
    }

    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
        if (this.isCustomTypeFaceSet) {
            this.mTypeface = tf;
        }
    }
}
