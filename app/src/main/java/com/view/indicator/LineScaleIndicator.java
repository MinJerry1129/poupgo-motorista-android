package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

public class LineScaleIndicator extends BaseIndicatorController {
    public static final float SCALE = 1.0f;
    float[] scaleYFloats = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

    public void draw(Canvas canvas, Paint paint) {
        float translateX = (float) (getWidth() / 11);
        float translateY = (float) (getHeight() / 2);
        for (int i = 0; i < 5; i++) {
            canvas.save();
            canvas.translate((((float) ((i * 2) + 2)) * translateX) - (translateX / 2.0f), translateY);
            canvas.scale(1.0f, this.scaleYFloats[i]);
            canvas.drawRoundRect(new RectF((-translateX) / 2.0f, ((float) (-getHeight())) / 2.5f, translateX / 2.0f, ((float) getHeight()) / 2.5f), 5.0f, 5.0f, paint);
            canvas.restore();
        }
    }

    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        ArrayList var1 = new ArrayList();
        long[] var2 = new long[]{100L, 200L, 300L, 400L, 500L};

        for (int var3 = 0; var3 < 5; ++var3) {
            ValueAnimator var5 = ValueAnimator.ofFloat(new float[]{1.0F, 0.4F, 1.0F});
            var5.setDuration(1000L);
            var5.setRepeatCount(-1);
            var5.setStartDelay(var2[var3]);
            int finalVar = var3;
            var5.addUpdateListener(animation -> {
                LineScaleIndicator.this.scaleYFloats[finalVar] = (Float) animation.getAnimatedValue();
                LineScaleIndicator.this.postInvalidate();
            });
            var5.start();
            var1.add(var5);
        }

        return var1;
    }
}
