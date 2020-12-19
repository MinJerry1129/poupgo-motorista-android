package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

public class BallSpinFadeLoaderIndicator extends BaseIndicatorController {
    public static final int ALPHA = 255;
    public static final float SCALE = 1.0f;
    int[] alphas = new int[]{255, 255, 255, 255, 255, 255, 255, 255};
    float[] scaleFloats = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};

    final class Point {
        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        float radius = (float) (getWidth() / 10);
        for (int i = 0; i < 8; i++) {
            canvas.save();
            Point point = circleAt(getWidth(), getHeight(), ((float) (getWidth() / 2)) - radius, 0.7853981633974483d * ((double) i));
            canvas.translate(point.x, point.y);
            canvas.scale(this.scaleFloats[i], this.scaleFloats[i]);
            paint.setAlpha(this.alphas[i]);
            canvas.drawCircle(0.0f, 0.0f, radius, paint);
            canvas.restore();
        }
    }

    Point circleAt(int width, int height, float radius, double angle) {
        return new Point((float) (((double) (width / 2)) + (((double) radius) * Math.cos(angle))), (float) (((double) (height / 2)) + (((double) radius) * Math.sin(angle))));
    }

    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        ArrayList var1 = new ArrayList();
        int[] var2 = new int[]{0, 120, 240, 360, 480, 600, 720, 780, 840};

        for(int var3 = 0; var3 < 8; ++var3) {
            ValueAnimator var5 = ValueAnimator.ofFloat(new float[]{1.0F, 0.4F, 1.0F});
            var5.setDuration(1000L);
            var5.setRepeatCount(-1);
            var5.setStartDelay((long)var2[var3]);
            int finalVar = var3;
            var5.addUpdateListener(animation -> {
                BallSpinFadeLoaderIndicator.this.scaleFloats[finalVar] = (Float)animation.getAnimatedValue();
                BallSpinFadeLoaderIndicator.this.postInvalidate();
            });
            var5.start();
            ValueAnimator var6 = ValueAnimator.ofInt(new int[]{255, 77, 255});
            var6.setDuration(1000L);
            var6.setRepeatCount(-1);
            var6.setStartDelay((long)var2[var3]);
            int finalVar1 = var3;
            var6.addUpdateListener(animation -> {
                BallSpinFadeLoaderIndicator.this.scaleFloats[finalVar1] = (Integer)animation.getAnimatedValue();
                BallSpinFadeLoaderIndicator.this.postInvalidate();
            });
            var6.start();
            var1.add(var5);
            var1.add(var6);
        }

        return var1;
    }
}
