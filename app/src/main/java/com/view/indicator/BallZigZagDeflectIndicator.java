package com.view.indicator;

import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.util.ArrayList;
import java.util.List;

public class BallZigZagDeflectIndicator extends BallZigZagIndicator {
    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList();
        float startX = (float) (getWidth() / 6);
        float startY = (float) (getWidth() / 6);
        for (int i = 0; i < 2; i++) {
            final int index = i;
            ValueAnimator translateXAnim = ValueAnimator.ofFloat(new float[]{startX, ((float) getWidth()) - startX, startX, ((float) getWidth()) - startX, startX});
            if (i == 1) {
                translateXAnim = ValueAnimator.ofFloat(((float) getWidth()) - startX, startX, ((float) getWidth()) - startX, startX, ((float) getWidth()) - startX);
            }
            ValueAnimator translateYAnim = ValueAnimator.ofFloat(new float[]{startY, startY, ((float) getHeight()) - startY, ((float) getHeight()) - startY, startY});
            if (i == 1) {
                translateYAnim = ValueAnimator.ofFloat(((float) getHeight()) - startY, ((float) getHeight()) - startY, startY, startY, ((float) getHeight()) - startY);
            }
            translateXAnim.setDuration(2000);
            translateXAnim.setInterpolator(new LinearInterpolator());
            translateXAnim.setRepeatCount(-1);
            translateXAnim.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    BallZigZagDeflectIndicator.this.translateX[index] = ((Float) animation.getAnimatedValue()).floatValue();
                    BallZigZagDeflectIndicator.this.postInvalidate();
                }
            });
            translateXAnim.start();
            translateYAnim.setDuration(2000);
            translateYAnim.setInterpolator(new LinearInterpolator());
            translateYAnim.setRepeatCount(-1);
            translateYAnim.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    BallZigZagDeflectIndicator.this.translateY[index] = ((Float) animation.getAnimatedValue()).floatValue();
                    BallZigZagDeflectIndicator.this.postInvalidate();
                }
            });
            translateYAnim.start();
            animators.add(translateXAnim);
            animators.add(translateYAnim);
        }
        return animators;
    }
}
