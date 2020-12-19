package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import java.util.ArrayList;
import java.util.List;

public class BallTrianglePathIndicator extends BaseIndicatorController {
    float[] translateX = new float[3];
    float[] translateY = new float[3];

    public void draw(Canvas canvas, Paint paint) {
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Style.STROKE);
        for (int i = 0; i < 3; i++) {
            canvas.save();
            canvas.translate(this.translateX[i], this.translateY[i]);
            canvas.drawCircle(0.0f, 0.0f, (float) (getWidth() / 10), paint);
            canvas.restore();
        }
    }

    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList();
        float startX = (float) (getWidth() / 5);
        float startY = (float) (getWidth() / 5);
        for (int i = 0; i < 3; i++) {
            final int index = i;
            ValueAnimator translateXAnim = ValueAnimator.ofFloat(new float[]{(float) (getWidth() / 2), ((float) getWidth()) - startX, startX, (float) (getWidth() / 2)});
            if (i == 1) {
                translateXAnim = ValueAnimator.ofFloat(((float) getWidth()) - startX, startX, (float) (getWidth() / 2), ((float) getWidth()) - startX);
            } else if (i == 2) {
                translateXAnim = ValueAnimator.ofFloat(startX, (float) (getWidth() / 2), ((float) getWidth()) - startX, startX);
            }
            ValueAnimator translateYAnim = ValueAnimator.ofFloat(new float[]{startY, ((float) getHeight()) - startY, ((float) getHeight()) - startY, startY});
            if (i == 1) {
                translateYAnim = ValueAnimator.ofFloat(((float) getHeight()) - startY, ((float) getHeight()) - startY, startY, ((float) getHeight()) - startY);
            } else if (i == 2) {
                translateYAnim = ValueAnimator.ofFloat(((float) getHeight()) - startY, startY, ((float) getHeight()) - startY, ((float) getHeight()) - startY);
            }
            translateXAnim.setDuration(2000);
            translateXAnim.setInterpolator(new LinearInterpolator());
            translateXAnim.setRepeatCount(-1);
            translateXAnim.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    BallTrianglePathIndicator.this.translateX[index] = ((Float) animation.getAnimatedValue()).floatValue();
                    BallTrianglePathIndicator.this.postInvalidate();
                }
            });
            translateXAnim.start();
            translateYAnim.setDuration(2000);
            translateYAnim.setInterpolator(new LinearInterpolator());
            translateYAnim.setRepeatCount(-1);
            translateYAnim.addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    BallTrianglePathIndicator.this.translateY[index] = ((Float) animation.getAnimatedValue()).floatValue();
                    BallTrianglePathIndicator.this.postInvalidate();
                }
            });
            translateYAnim.start();
            animators.add(translateXAnim);
            animators.add(translateYAnim);
        }
        return animators;
    }
}
