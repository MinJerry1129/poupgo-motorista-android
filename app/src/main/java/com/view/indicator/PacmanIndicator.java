package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

public class PacmanIndicator extends BaseIndicatorController {
    private int alpha;
    private float degrees1;
    private float degrees2;
    private float translateX;

    public void draw(Canvas canvas, Paint paint) {
        drawPacman(canvas, paint);
        drawCircle(canvas, paint);
    }

    private void drawPacman(Canvas canvas, Paint paint) {
        float x = (float) (getWidth() / 2);
        float y = (float) (getHeight() / 2);
        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(this.degrees1);
        paint.setAlpha(255);
        canvas.drawArc(new RectF((-x) / 1.7f, (-y) / 1.7f, x / 1.7f, y / 1.7f), 0.0f, 270.0f, true, paint);
        canvas.restore();
        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(this.degrees2);
        paint.setAlpha(255);
        canvas.drawArc(new RectF((-x) / 1.7f, (-y) / 1.7f, x / 1.7f, y / 1.7f), 90.0f, 270.0f, true, paint);
        canvas.restore();
    }

    private void drawCircle(Canvas canvas, Paint paint) {
        float radius = (float) (getWidth() / 11);
        paint.setAlpha(this.alpha);
        canvas.drawCircle(this.translateX, (float) (getHeight() / 2), radius, paint);
    }

    public java.util.List<com.nineoldandroids.animation.Animator> createAnimation() {
        ArrayList var1 = new ArrayList();
        float var2 = (float) (this.getWidth() / 11);
        ValueAnimator var3 = ValueAnimator.ofFloat(new float[]{(float) this.getWidth() - var2, (float) (this.getWidth() / 2)});
        var3.setDuration(650L);
        var3.setInterpolator(new LinearInterpolator());
        var3.setRepeatCount(-1);
        var3.addUpdateListener(animation -> {
            PacmanIndicator.this.degrees1 = (Float) animation.getAnimatedValue();
            PacmanIndicator.this.postInvalidate();
        });
        var3.start();
        ValueAnimator var4 = ValueAnimator.ofInt(new int[]{255, 122});
        var4.setDuration(650L);
        var4.setRepeatCount(-1);
        var4.addUpdateListener(animation -> {
            PacmanIndicator.this.degrees1 = (Integer) animation.getAnimatedValue();
            PacmanIndicator.this.postInvalidate();
        });
        var4.start();
        ValueAnimator var5 = ValueAnimator.ofFloat(new float[]{0.0F, 45.0F, 0.0F});
        var5.setDuration(650L);
        var5.setRepeatCount(-1);
        var5.addUpdateListener(animation -> {
            PacmanIndicator.this.degrees2 = (Float) animation.getAnimatedValue();
            PacmanIndicator.this.postInvalidate();
        });
        var5.start();
        ValueAnimator var6 = ValueAnimator.ofFloat(new float[]{0.0F, -45.0F, 0.0F});
        var6.setDuration(650L);
        var6.setRepeatCount(-1);
        var6.addUpdateListener(animation -> {
            PacmanIndicator.this.translateX = (Float) animation.getAnimatedValue();
            PacmanIndicator.this.postInvalidate();
        });
        var6.start();
        var1.add(var3);
        var1.add(var4);
        var1.add(var5);
        var1.add(var6);
        return var1;
    }
}
