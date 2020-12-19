package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class BallClipRotateIndicator extends BaseIndicatorController {
    float degrees;
    float scaleFloat = 1.0f;

    public void draw(Canvas canvas, Paint paint) {
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3.0f);
        float x = (float) (getWidth() / 2);
        float y = (float) (getHeight() / 2);
        canvas.translate(x, y);
        canvas.scale(this.scaleFloat, this.scaleFloat);
        canvas.rotate(this.degrees);
        canvas.drawArc(new RectF((-x) + 12.0f, (-y) + 12.0f, (x + 0.0f) - 12.0f, (0.0f + y) - 12.0f), -45.0f, 270.0f, false, paint);
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0008, element type: float, insn element type: null */
    /* JADX WARNING: Incorrect type for fill-array insn 0x0026, element type: float, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r6 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 4;
        r1 = new float[r1];
        r1 = {1065353216, 1058642330, 1056964608, 1065353216};
        r1 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r1);
        r2 = 750; // 0x2ee float:1.051E-42 double:3.705E-321;
        r1.setDuration(r2);
        r4 = -1;
        r1.setRepeatCount(r4);
        r5 = new com.view.anim.loader.indicator.BallClipRotateIndicator$1;
        r5.<init>();
        r1.addUpdateListener(r5);
        r1.start();
        r5 = 3;
        r5 = new float[r5];
        r5 = {0, 1127481344, 1135869952};
        r5 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r5);
        r5.setDuration(r2);
        r5.setRepeatCount(r4);
        r2 = new com.view.anim.loader.indicator.BallClipRotateIndicator$2;
        r2.<init>();
        r5.addUpdateListener(r2);
        r5.start();
        r0.add(r1);
        r0.add(r5);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallClipRotateIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
