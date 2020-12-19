package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class BallClipRotateMultipleIndicator extends BaseIndicatorController {
    float degrees;
    float scaleFloat = 1.0f;

    public void draw(Canvas canvas, Paint paint) {
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Style.STROKE);
        float x = (float) (getWidth() / 2.8E-45f);
        float y = (float) (getHeight() / 2.8E-45f);
        canvas.save();
        canvas.translate(x, y);
        canvas.scale(this.scaleFloat, this.scaleFloat);
        canvas.rotate(this.degrees);
        float[] bStartAngles = new float[]{135.0f, -45.0f};
        for (int i = 0; i < 2; i++) {
            canvas.drawArc(new RectF((-x) + 12.0f, (-y) + 12.0f, x - 12.0f, y - 12.0f), bStartAngles[i], 90.0f, false, paint);
        }
        canvas.restore();
        canvas.translate(x, y);
        canvas.scale(this.scaleFloat, this.scaleFloat);
        canvas.rotate(-this.degrees);
        float[] sStartAngles = new float[]{225.0f, 45.0f};
        for (int i2 = 0; i2 < 2; i2++) {
            canvas.drawArc(new RectF(((-x) / 1.8f) + 12.0f, ((-y) / 1.8f) + 12.0f, (x / 1.8f) - 12.0f, (y / 1.8f) - 12.0f), sStartAngles[i2], 90.0f, false, paint);
        }
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0008, element type: float, insn element type: null */
    /* JADX WARNING: Incorrect type for fill-array insn 0x0025, element type: float, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r7 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 3;
        r2 = new float[r1];
        r2 = {1065353216, 1058642330, 1065353216};
        r2 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r2);
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r2.setDuration(r3);
        r5 = -1;
        r2.setRepeatCount(r5);
        r6 = new com.view.anim.loader.indicator.BallClipRotateMultipleIndicator$1;
        r6.<init>();
        r2.addUpdateListener(r6);
        r2.start();
        r1 = new float[r1];
        r1 = {0, 1127481344, 1135869952};
        r1 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r1);
        r1.setDuration(r3);
        r1.setRepeatCount(r5);
        r3 = new com.view.anim.loader.indicator.BallClipRotateMultipleIndicator$2;
        r3.<init>();
        r1.addUpdateListener(r3);
        r1.start();
        r0.add(r2);
        r0.add(r1);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallClipRotateMultipleIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
