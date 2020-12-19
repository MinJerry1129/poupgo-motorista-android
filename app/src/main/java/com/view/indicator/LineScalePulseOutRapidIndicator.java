package com.view.indicator;

import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

public class LineScalePulseOutRapidIndicator extends LineScaleIndicator {

    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        ArrayList var1 = new ArrayList();
        long[] var2 = new long[]{400L, 200L, 0L, 200L, 400L};

        for(int var3 = 0; var3 < 5; ++var3) {
            ValueAnimator var5 = ValueAnimator.ofFloat(new float[]{1.0F, 0.4F, 1.0F});
            var5.setDuration(1000L);
            var5.setRepeatCount(-1);
            var5.setStartDelay(var2[var3]);
            int finalVar = var3;
            var5.addUpdateListener(animation -> {
                LineScalePulseOutRapidIndicator.this.scaleYFloats[finalVar] = (Float)animation.getAnimatedValue();
                LineScalePulseOutRapidIndicator.this.postInvalidate();
            });
            var5.start();
            var1.add(var5);
        }

        return var1;
    }
}
