package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BallBeatIndicator extends BaseIndicatorController {
    public static final int ALPHA = 255;
    public static final float SCALE = 1.0f;
    int[] alphas = new int[]{255, 255, 255};
    private float[] scaleFloats = new float[]{1.0f, 1.0f, 1.0f};

    public void draw(Canvas canvas, Paint paint) {
        float radius = (((float) getWidth()) - (4.0f * 2.0f)) / 6.0f;
        float x = ((float) (getWidth() / 2)) - ((radius * 2.0f) + 4.0f);
        float y = (float) (getHeight() / 2);
        for (int i = 0; i < 3; i++) {
            canvas.save();
            canvas.translate((((radius * 2.0f) * ((float) i)) + x) + (((float) i) * 4.0f), y);
            canvas.scale(this.scaleFloats[i], this.scaleFloats[i]);
            paint.setAlpha(this.alphas[i]);
            canvas.drawCircle(0.0f, 0.0f, radius, paint);
            canvas.restore();
        }
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0011, element type: float, insn element type: null */
    /* JADX WARNING: Incorrect type for fill-array insn 0x0034, element type: int, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r11 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 3;
        r2 = new int[r1];
        r2 = {350, 0, 350};
        r3 = 0;
    L_0x000c:
        if (r3 >= r1) goto L_0x005b;
    L_0x000e:
        r4 = r3;
        r5 = new float[r1];
        r5 = {1065353216, 1061158912, 1065353216};
        r5 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r5);
        r6 = 700; // 0x2bc float:9.81E-43 double:3.46E-321;
        r5.setDuration(r6);
        r8 = -1;
        r5.setRepeatCount(r8);
        r9 = r2[r3];
        r9 = (long) r9;
        r5.setStartDelay(r9);
        r9 = new com.view.anim.loader.indicator.BallBeatIndicator$1;
        r9.<init>(r4);
        r5.addUpdateListener(r9);
        r5.start();
        r9 = new int[r1];
        r9 = {255, 51, 255};
        r9 = com.nineoldandroids.animation.ValueAnimator.ofInt(r9);
        r9.setDuration(r6);
        r9.setRepeatCount(r8);
        r6 = r2[r3];
        r6 = (long) r6;
        r9.setStartDelay(r6);
        r6 = new com.view.anim.loader.indicator.BallBeatIndicator$2;
        r6.<init>(r4);
        r9.addUpdateListener(r6);
        r9.start();
        r0.add(r5);
        r0.add(r9);
        r3 = r3 + 1;
        goto L_0x000c;
    L_0x005b:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallBeatIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
