package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.view.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

public class TriangleSkewSpinIndicator extends BaseIndicatorController {
    public void draw(Canvas canvas, Paint paint) {
        Path path = new Path();
        path.moveTo((float) (getWidth() / 5), (float) ((getHeight() * 4) / 5));
        path.lineTo((float) ((getWidth() * 4) / 5), (float) ((getHeight() * 4) / 5));
        path.lineTo((float) (getWidth() / 2), (float) (getHeight() / 5));
        path.close();
        canvas.drawPath(path, paint);
    }

    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList();
        PropertyValuesHolder rotation5 = PropertyValuesHolder.ofFloat("rotationX", 0.0f, 180.0f, 180.0f, 0.0f, 0.0f);
        PropertyValuesHolder rotation6 = PropertyValuesHolder.ofFloat("rotationY", 0.0f, 0.0f, 180.0f, 180.0f, 0.0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(getTarget(), rotation6, rotation5);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        animator.setDuration((long) MaterialRippleLayout.HOVER_DURATION);
        animator.start();
        animators.add(animator);
        return animators;
    }
}
