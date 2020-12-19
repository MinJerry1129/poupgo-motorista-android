package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.util.ArrayList;
import java.util.List;

public class BallZigZagIndicator extends BaseIndicatorController {
    float[] translateX = new float[2];
    float[] translateY = new float[2];

    public void draw(Canvas canvas, Paint paint) {
        for (int i = 0; i < 2; i++) {
            canvas.save();
            canvas.translate(this.translateX[i], this.translateY[i]);
            canvas.drawCircle(0.0f, 0.0f, (float) (getWidth() / 10), paint);
            canvas.restore();
        }
    }

    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList();
        float startX = (float) (getWidth() / 6);
        float startY = (float) (getWidth() / 6);
        for (int i = 0; i < 2; i++) {
            final int index = i;
            ValueAnimator translateXAnim = ValueAnimator.ofFloat(new float[]{startX, ((float) getWidth()) - startX, (float) (getWidth() / 2), startX});
            if (i == 1) {
                translateXAnim = ValueAnimator.ofFloat(((float) getWidth()) - startX, startX, (float) (getWidth() / 2), ((float) getWidth()) - startX);
            }
            ValueAnimator translateYAnim = ValueAnimator.ofFloat(new float[]{startY, startY, (float) (getHeight() / 2), startY});
            if (i == 1) {
                translateYAnim = ValueAnimator.ofFloat(((float) getHeight()) - startY, ((float) getHeight()) - startY, (float) (getHeight() / 2), ((float) getHeight()) - startY);
            }
            translateXAnim.setDuration(1000);
            translateXAnim.setInterpolator(new LinearInterpolator());
            translateXAnim.setRepeatCount(-1);
            translateXAnim.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    BallZigZagIndicator.this.translateX[index] = ((Float) animation.getAnimatedValue()).floatValue();
                    BallZigZagIndicator.this.postInvalidate();
                }
            });
            translateXAnim.start();
            translateYAnim.setDuration(1000);
            translateYAnim.setInterpolator(new LinearInterpolator());
            translateYAnim.setRepeatCount(-1);
            translateYAnim.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    BallZigZagIndicator.this.translateY[index] = ((Float) animation.getAnimatedValue()).floatValue();
                    BallZigZagIndicator.this.postInvalidate();
                }
            });
            translateYAnim.start();
            animators.add(translateXAnim);
            animators.add(translateYAnim);
        }
        return animators;
    }
}
