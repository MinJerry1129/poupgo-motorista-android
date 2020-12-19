package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BallScaleMultipleIndicator extends BaseIndicatorController {
    int[] alphaInts = new int[]{255, 255, 255};
    float[] scaleFloats = new float[]{1.0f, 1.0f, 1.0f};

    public void draw(Canvas canvas, Paint paint) {
        for (int i = 0; i < 3; i++) {
            paint.setAlpha(this.alphaInts[i]);
            canvas.scale(this.scaleFloats[i], this.scaleFloats[i], (float) (getWidth() / 2), (float) (getHeight() / 2));
            canvas.drawCircle((float) (getWidth() / 2), (float) (getHeight() / 2), ((float) (getWidth() / 2)) - 4.0f, paint);
        }
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0012, element type: float, insn element type: null */
    /* JADX WARNING: Incorrect type for fill-array insn 0x003c, element type: int, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r12 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 3;
        r2 = new long[r1];
        r2 = {0, 200, 400};
        r3 = 0;
    L_0x000c:
        if (r3 >= r1) goto L_0x006a;
    L_0x000e:
        r4 = r3;
        r5 = 2;
        r6 = new float[r5];
        r6 = {0, 1065353216};
        r6 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r6);
        r7 = new android.view.animation.LinearInterpolator;
        r7.<init>();
        r6.setInterpolator(r7);
        r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6.setDuration(r7);
        r9 = -1;
        r6.setRepeatCount(r9);
        r10 = new com.view.anim.loader.indicator.BallScaleMultipleIndicator$1;
        r10.<init>(r4);
        r6.addUpdateListener(r10);
        r10 = r2[r3];
        r6.setStartDelay(r10);
        r6.start();
        r5 = new int[r5];
        r5 = {255, 0};
        r5 = com.nineoldandroids.animation.ValueAnimator.ofInt(r5);
        r10 = new android.view.animation.LinearInterpolator;
        r10.<init>();
        r5.setInterpolator(r10);
        r5.setDuration(r7);
        r5.setRepeatCount(r9);
        r7 = new com.view.anim.loader.indicator.BallScaleMultipleIndicator$2;
        r7.<init>(r4);
        r5.addUpdateListener(r7);
        r7 = r2[r3];
        r6.setStartDelay(r7);
        r5.start();
        r0.add(r6);
        r0.add(r5);
        r3 = r3 + 1;
        goto L_0x000c;
    L_0x006a:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallScaleMultipleIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
