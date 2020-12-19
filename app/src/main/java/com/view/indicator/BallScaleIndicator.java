package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BallScaleIndicator extends BaseIndicatorController {
    int alpha = 255;
    float scale = 1.0f;

    public void draw(Canvas canvas, Paint paint) {
        paint.setAlpha(this.alpha);
        canvas.scale(this.scale, this.scale, (float) (getWidth() / 2), (float) (getHeight() / 2));
        paint.setAlpha(this.alpha);
        canvas.drawCircle((float) (getWidth() / 2), (float) (getHeight() / 2), ((float) (getWidth() / 2)) - 4.0f, paint);
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0008, element type: float, insn element type: null */
    /* JADX WARNING: Incorrect type for fill-array insn 0x002d, element type: int, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r7 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 2;
        r2 = new float[r1];
        r2 = {0, 1065353216};
        r2 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r2);
        r3 = new android.view.animation.LinearInterpolator;
        r3.<init>();
        r2.setInterpolator(r3);
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r2.setDuration(r3);
        r5 = -1;
        r2.setRepeatCount(r5);
        r6 = new com.view.anim.loader.indicator.BallScaleIndicator$1;
        r6.<init>();
        r2.addUpdateListener(r6);
        r2.start();
        r1 = new int[r1];
        r1 = {255, 0};
        r1 = com.nineoldandroids.animation.ValueAnimator.ofInt(r1);
        r6 = new android.view.animation.LinearInterpolator;
        r6.<init>();
        r1.setInterpolator(r6);
        r1.setDuration(r3);
        r1.setRepeatCount(r5);
        r3 = new com.view.anim.loader.indicator.BallScaleIndicator$2;
        r3.<init>();
        r1.addUpdateListener(r3);
        r1.start();
        r0.add(r2);
        r0.add(r1);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallScaleIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
