package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

public class LineScalePartyIndicator extends BaseIndicatorController {
    public static final float SCALE = 1.0f;
    float[] scaleFloats = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

    public void draw(Canvas canvas, Paint paint) {
        float translateX = (float) (getWidth() / 9);
        float translateY = (float) (getHeight() / 2);
        for (int i = 0; i < 4; i++) {
            canvas.save();
            canvas.translate((((float) ((i * 2) + 2)) * translateX) - (translateX / 2.0f), translateY);
            canvas.scale(this.scaleFloats[i], this.scaleFloats[i]);
            canvas.drawRoundRect(new RectF((-translateX) / 2.0f, ((float) (-getHeight())) / 2.5f, translateX / 2.0f, ((float) getHeight()) / 2.5f), 5.0f, 5.0f, paint);
            canvas.restore();
        }
    }

    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        ArrayList var1 = new ArrayList();
        long[] var2 = new long[]{1260L, 430L, 1010L, 730L};
        long[] var3 = new long[]{770L, 290L, 280L, 740L};

        for(int var4 = 0; var4 < 4; ++var4) {
            ValueAnimator var6 = ValueAnimator.ofFloat(new float[]{1.0F, 0.4F, 1.0F});
            var6.setDuration(var2[var4]);
            var6.setRepeatCount(-1);
            var6.setStartDelay(var3[var4]);
            int finalVar = var4;
            var6.addUpdateListener(animation -> {
                LineScalePartyIndicator.this.scaleFloats[finalVar] = (Float)animation.getAnimatedValue();
                LineScalePartyIndicator.this.postInvalidate();
            });
            var6.start();
            var1.add(var6);
        }

        return var1;
    }
}
