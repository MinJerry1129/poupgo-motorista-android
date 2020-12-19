package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;

public class SemiCircleSpinIndicator extends BaseIndicatorController {
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawArc(new RectF(0.0f, 0.0f, (float) getWidth(), (float) getHeight()), -60.0f, 120.0f, false, paint);
    }

    public List<Animator> createAnimation() {
        List<Animator> animators = new ArrayList();
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(getTarget(), "rotation", 0.0f, 180.0f, 360.0f);
        rotateAnim.setDuration(600);
        rotateAnim.setRepeatCount(-1);
        rotateAnim.start();
        animators.add(rotateAnim);
        return animators;
    }
}
