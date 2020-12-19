package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

import java.util.ArrayList;
import java.util.List;

public class BallPulseRiseIndicator extends BaseIndicatorController {
    public void draw(Canvas canvas, Paint paint) {
        float radius = (float) (getWidth() / 10);
        canvas.drawCircle((float) (getWidth() / 4), radius * 2.0f, radius, paint);
        canvas.drawCircle((float) ((getWidth() * 3) / 4), radius * 2.0f, radius, paint);
        canvas.drawCircle(radius, ((float) getHeight()) - (radius * 2.0f), radius, paint);
        canvas.drawCircle((float) (getWidth() / 2), ((float) getHeight()) - (radius * 2.0f), radius, paint);
        canvas.drawCircle(((float) getWidth()) - radius, ((float) getHeight()) - (2.0f * radius), radius, paint);
    }

    public List<Animator> createAnimation() {
        PropertyValuesHolder rotation6 = PropertyValuesHolder.ofFloat("rotationX", 0.0f, 360.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(getTarget(), rotation6);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.setDuration(1500);
        animator.start();
        List<Animator> animators = new ArrayList();
        animators.add(animator);
        return animators;
    }
}
