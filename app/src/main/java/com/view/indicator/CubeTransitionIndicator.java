package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

public class CubeTransitionIndicator extends BaseIndicatorController {
    float degrees;
    float scaleFloat = 1.0f;
    float[] translateX = new float[2];
    float[] translateY = new float[2];

    public void draw(Canvas canvas, Paint paint) {
        float rWidth = (float) (getWidth() / 5);
        float rHeight = (float) (getHeight() / 5);
        for (int i = 0; i < 2; i++) {
            canvas.save();
            canvas.translate(this.translateX[i], this.translateY[i]);
            canvas.rotate(this.degrees);
            canvas.scale(this.scaleFloat, this.scaleFloat);
            canvas.drawRect(new RectF((-rWidth) / 2.0f, (-rHeight) / 2.0f, rWidth / 2.0f, rHeight / 2.0f), paint);
            canvas.restore();
        }
    }

    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        ArrayList var1 = new ArrayList();
        float var2 = (float)(this.getWidth() / 5);
        float var3 = (float)(this.getHeight() / 5);

        for(int var4 = 0; var4 < 2; ++var4) {
            this.translateX[var4] = var2;
            ValueAnimator var6 = ValueAnimator.ofFloat(new float[]{var2, (float)this.getWidth() - var2, (float)this.getWidth() - var2, var2, var2});
            if (var4 == 1) {
                var6 = ValueAnimator.ofFloat(new float[]{(float)this.getWidth() - var2, var2, var2, (float)this.getWidth() - var2, (float)this.getWidth() - var2});
            }

            var6.setInterpolator(new LinearInterpolator());
            var6.setDuration(1600L);
            var6.setRepeatCount(-1);
            int finalVar = var4;
            var6.addUpdateListener(animation -> {
                CubeTransitionIndicator.this.translateX[finalVar] = (Float)animation.getAnimatedValue();
                CubeTransitionIndicator.this.postInvalidate();
            });
            var6.start();
            this.translateY[var4] = var3;
            ValueAnimator var7 = ValueAnimator.ofFloat(new float[]{var3, var3, (float)this.getHeight() - var3, (float)this.getHeight() - var3, var3});
            if (var4 == 1) {
                var7 = ValueAnimator.ofFloat(new float[]{(float)this.getHeight() - var3, (float)this.getHeight() - var3, var3, var3, (float)this.getHeight() - var3});
            }

            var7.setDuration(1600L);
            var7.setInterpolator(new LinearInterpolator());
            var7.setRepeatCount(-1);
            int finalVar1 = var4;
            var7.addUpdateListener(animation -> {
                CubeTransitionIndicator.this.translateY[finalVar1] = (Float)animation.getAnimatedValue();
                CubeTransitionIndicator.this.postInvalidate();
            });
            var7.start();
            var1.add(var6);
            var1.add(var7);
        }

        ValueAnimator var8 = ValueAnimator.ofFloat(new float[]{1.0F, 0.5F, 1.0F, 0.5F, 1.0F});
        var8.setDuration(1600L);
        var8.setInterpolator(new LinearInterpolator());
        var8.setRepeatCount(-1);
        var8.addUpdateListener(animation -> {
            CubeTransitionIndicator.this.degrees = (Float)animation.getAnimatedValue();
            CubeTransitionIndicator.this.postInvalidate();
        });
        var8.start();
        ValueAnimator var5 = ValueAnimator.ofFloat(new float[]{0.0F, 180.0F, 360.0F, 540.0F, 720.0F});
        var5.setDuration(1600L);
        var5.setInterpolator(new LinearInterpolator());
        var5.setRepeatCount(-1);
        var5.addUpdateListener(animation -> {
            CubeTransitionIndicator.this.scaleFloat = (Float)animation.getAnimatedValue();
            CubeTransitionIndicator.this.postInvalidate();
        });
        var5.start();
        var1.add(var8);
        var1.add(var5);
        return var1;
    }
}
