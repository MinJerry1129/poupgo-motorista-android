package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BallGridPulseIndicator extends BaseIndicatorController {
    public static final int ALPHA = 255;
    public static final float SCALE = 1.0f;
    int[] alphas = new int[]{255, 255, 255, 255, 255, 255, 255, 255, 255};
    float[] scaleFloats = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

    public void draw(Canvas canvas, Paint paint) {
        Canvas canvas2 = canvas;
        Paint paint2 = paint;
        float radius = (((float) getWidth()) - (4.0f * 4.0f)) / 6.0f;
        float x = ((float) (getWidth() / 2)) - ((radius * 2.0f) + 4.0f);
        float y = ((float) (getWidth() / 2)) - ((radius * 2.0f) + 4.0f);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                canvas.save();
                canvas2.translate((((radius * 2.0f) * ((float) j)) + x) + (((float) j) * 4.0f), (((radius * 2.0f) * ((float) i)) + y) + (((float) i) * 4.0f));
                canvas2.scale(this.scaleFloats[(i * 3) + j], this.scaleFloats[(i * 3) + j]);
                paint2.setAlpha(this.alphas[(i * 3) + j]);
                canvas2.drawCircle(0.0f, 0.0f, radius, paint2);
                canvas.restore();
            }
        }
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0018, element type: float, insn element type: null */
    /* JADX WARNING: Incorrect type for fill-array insn 0x003d, element type: int, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r11 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 9;
        r2 = new int[r1];
        r2 = {720, 1020, 1280, 1420, 1450, 1180, 870, 1450, 1060};
        r3 = new int[r1];
        r3 = {-60, 250, -170, 480, 310, 30, 460, 780, 450};
        r4 = 0;
    L_0x0012:
        if (r4 >= r1) goto L_0x0067;
    L_0x0014:
        r5 = r4;
        r6 = 3;
        r6 = new float[r6];
        r6 = {1065353216, 1056964608, 1065353216};
        r6 = com.nineoldandroids.animation.ValueAnimator.ofFloat(r6);
        r7 = r2[r4];
        r7 = (long) r7;
        r6.setDuration(r7);
        r7 = -1;
        r6.setRepeatCount(r7);
        r8 = r3[r4];
        r8 = (long) r8;
        r6.setStartDelay(r8);
        r8 = new com.view.anim.loader.indicator.BallGridPulseIndicator$1;
        r8.<init>(r5);
        r6.addUpdateListener(r8);
        r6.start();
        r8 = 4;
        r8 = new int[r8];
        r8 = {255, 210, 122, 255};
        r8 = com.nineoldandroids.animation.ValueAnimator.ofInt(r8);
        r9 = r2[r4];
        r9 = (long) r9;
        r8.setDuration(r9);
        r8.setRepeatCount(r7);
        r7 = r3[r4];
        r9 = (long) r7;
        r8.setStartDelay(r9);
        r7 = new com.view.anim.loader.indicator.BallGridPulseIndicator$2;
        r7.<init>(r5);
        r8.addUpdateListener(r7);
        r8.start();
        r0.add(r6);
        r0.add(r8);
        r4 = r4 + 1;
        goto L_0x0012;
    L_0x0067:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallGridPulseIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
