package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;

public class BallGridBeatIndicator extends BaseIndicatorController {
    public static final int ALPHA = 255;
    int[] alphas = new int[]{255, 255, 255, 255, 255, 255, 255, 255, 255};

    public void draw(Canvas canvas, Paint paint) {
        float radius = (((float) getWidth()) - (4.0f * 4.0f)) / 6.0f;
        float x = ((float) (getWidth() / 2)) - ((radius * 2.0f) + 4.0f);
        float y = ((float) (getWidth() / 2)) - ((radius * 2.0f) + 4.0f);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                canvas.save();
                canvas.translate((((radius * 2.0f) * ((float) j)) + x) + (((float) j) * 4.0f), (((radius * 2.0f) * ((float) i)) + y) + (((float) i) * 4.0f));
                paint.setAlpha(this.alphas[(i * 3) + j]);
                canvas.drawCircle(0.0f, 0.0f, radius, paint);
                canvas.restore();
            }
        }
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0018, element type: int, insn element type: null */
    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        /*
        r9 = this;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 9;
        r2 = new int[r1];
        r2 = {960, 930, 1190, 1130, 1340, 940, 1200, 820, 1190};
        r3 = new int[r1];
        r3 = {360, 400, 680, 410, 710, -150, -120, 10, 320};
        r4 = 0;
    L_0x0012:
        if (r4 >= r1) goto L_0x0040;
    L_0x0014:
        r5 = r4;
        r6 = 3;
        r6 = new int[r6];
        r6 = {255, 168, 255};
        r6 = com.nineoldandroids.animation.ValueAnimator.ofInt(r6);
        r7 = r2[r4];
        r7 = (long) r7;
        r6.setDuration(r7);
        r7 = -1;
        r6.setRepeatCount(r7);
        r7 = r3[r4];
        r7 = (long) r7;
        r6.setStartDelay(r7);
        r7 = new com.view.anim.loader.indicator.BallGridBeatIndicator$1;
        r7.<init>(r5);
        r6.addUpdateListener(r7);
        r6.start();
        r0.add(r6);
        r4 = r4 + 1;
        goto L_0x0012;
    L_0x0040:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.view.anim.loader.indicator.BallGridBeatIndicator.createAnimation():java.util.List<com.nineoldandroids.animation.Animator>");
    }
}
