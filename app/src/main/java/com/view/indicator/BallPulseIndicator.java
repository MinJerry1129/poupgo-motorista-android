package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

public class BallPulseIndicator extends BaseIndicatorController {
    public static final float SCALE = 1.0f;
    private float[] scaleFloats = new float[]{1.0f, 1.0f, 1.0f};

    public void draw(Canvas canvas, Paint paint) {
        float radius = (((float) Math.min(getWidth(), getHeight())) - (4.0f * 2.0f)) / 6.0f;
        float x = ((float) (getWidth() / 2)) - ((radius * 2.0f) + 4.0f);
        float y = (float) (getHeight() / 2);
        for (int i = 0; i < 3; i++) {
            canvas.save();
            canvas.translate((((radius * 2.0f) * ((float) i)) + x) + (((float) i) * 4.0f), y);
            canvas.scale(this.scaleFloats[i], this.scaleFloats[i]);
            canvas.drawCircle(0.0f, 0.0f, radius, paint);
            canvas.restore();
        }
    }

    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        ArrayList var1 = new ArrayList();
        int[] var2 = new int[]{120, 240, 360};

        for(int var3 = 0; var3 < 3; ++var3) {
            ValueAnimator var5 = ValueAnimator.ofFloat(new float[]{1.0F, 0.3F, 1.0F});
            var5.setDuration(750L);
            var5.setRepeatCount(-1);
            var5.setStartDelay((long)var2[var3]);
            int finalVar = var3;
            var5.addUpdateListener(animation -> {
                BallPulseIndicator.this.scaleFloats[finalVar] = (Float)animation.getAnimatedValue();
                BallPulseIndicator.this.postInvalidate();
            });
            var5.start();
            var1.add(var5);
        }

        return var1;
    }
}
