package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class BallClipRotatePulseIndicator extends BaseIndicatorController {
    float degrees;
    float scaleFloat1;
    float scaleFloat2;

    public void draw(Canvas canvas, Paint paint) {
        float x = (float) (getWidth() / 2.8E-45f);
        float y = (float) (getHeight() / 2.8E-45f);
        canvas.save();
        canvas.translate(x, y);
        canvas.scale(this.scaleFloat1, this.scaleFloat1);
        paint.setStyle(Style.FILL);
        canvas.drawCircle(0.0f, 0.0f, x / 2.5f, paint);
        canvas.restore();
        canvas.translate(x, y);
        canvas.scale(this.scaleFloat2, this.scaleFloat2);
        canvas.rotate(this.degrees);
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Style.STROKE);
        float[] startAngles = new float[]{225.0f, 45.0f};
        for (int i = 0; i < 2; i++) {
            canvas.drawArc(new RectF((-x) + 12.0f, (-y) + 12.0f, x - 12.0f, y - 12.0f), startAngles[i], 90.0f, false, paint);
        }
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0003, element type: float, insn element type: null */
    /* JADX WARNING: Incorrect type for fill-array insn 0x0020, element type: float, insn element type: null */
    /* JADX WARNING: Incorrect type for fill-array insn 0x003a, element type: float, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r7 = this;
        r0 = 3;
        r1 = new float[r0];
        r1 = {1065353216, 1050253722, 1065353216};
        r1 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r1);
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r1.setDuration(r2);
        r4 = -1;
        r1.setRepeatCount(r4);
        r5 = new com.view.anim.loader.indicator.BallClipRotatePulseIndicator$1;
        r5.<init>();
        r1.addUpdateListener(r5);
        r1.start();
        r5 = new float[r0];
        r5 = {1065353216, 1058642330, 1065353216};
        r5 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r5);
        r5.setDuration(r2);
        r5.setRepeatCount(r4);
        r6 = new com.view.anim.loader.indicator.BallClipRotatePulseIndicator$2;
        r6.<init>();
        r5.addUpdateListener(r6);
        r5.start();
        r0 = new float[r0];
        r0 = {0, 1127481344, 1135869952};
        r0 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r0);
        r0.setDuration(r2);
        r0.setRepeatCount(r4);
        r2 = new com.view.anim.loader.indicator.BallClipRotatePulseIndicator$3;
        r2.<init>();
        r0.addUpdateListener(r2);
        r0.start();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r2.add(r1);
        r2.add(r5);
        r2.add(r0);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallClipRotatePulseIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
