package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BallRotateIndicator extends BaseIndicatorController {
    float scaleFloat = 0.5f;

    public void draw(Canvas canvas, Paint paint) {
        float radius = (float) (getWidth() / 10);
        float x = (float) (getWidth() / 2);
        float y = (float) (getHeight() / 2);
        canvas.save();
        canvas.translate((x - (radius * 2.0f)) - radius, y);
        canvas.scale(this.scaleFloat, this.scaleFloat);
        canvas.drawCircle(0.0f, 0.0f, radius, paint);
        canvas.restore();
        canvas.save();
        canvas.translate(x, y);
        canvas.scale(this.scaleFloat, this.scaleFloat);
        canvas.drawCircle(0.0f, 0.0f, radius, paint);
        canvas.restore();
        canvas.save();
        canvas.translate(((2.0f * radius) + x) + radius, y);
        canvas.scale(this.scaleFloat, this.scaleFloat);
        canvas.drawCircle(0.0f, 0.0f, radius, paint);
        canvas.restore();
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0008, element type: float, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r8 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 3;
        r2 = new float[r1];
        r2 = {1056964608, 1065353216, 1056964608};
        r2 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r2);
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r2.setDuration(r3);
        r5 = -1;
        r2.setRepeatCount(r5);
        r6 = new com.view.anim.loader.indicator.BallRotateIndicator$1;
        r6.<init>();
        r2.addUpdateListener(r6);
        r2.start();
        r6 = r8.getTarget();
        r7 = "rotation";
        r1 = new float[r1];
        r1 = {0, 1127481344, 1135869952};
        r1 = com.nineoldandroids.animation.ObjectAnimator.ofFloat(r6, r7, r1);
        r1.setDuration(r3);
        r1.setRepeatCount(r5);
        r1.start();
        r0.add(r2);
        r0.add(r1);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallRotateIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
