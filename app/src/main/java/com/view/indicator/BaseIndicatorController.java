package com.view.indicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import com.nineoldandroids.animation.Animator;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class BaseIndicatorController {
    private List<Animator> mAnimators;
    private WeakReference<View> mTarget;

    public enum AnimStatus {
        START,
        END,
        CANCEL
    }

    public abstract List<Animator> createAnimation();

    public abstract void draw(Canvas canvas, Paint paint);

    public View getTarget() {
        return this.mTarget != null ? (View) this.mTarget.get() : null;
    }

    public void setTarget(View target) {
        this.mTarget = new WeakReference(target);
    }

    public int getWidth() {
        return getTarget() != null ? getTarget().getWidth() : 0;
    }

    public int getHeight() {
        return getTarget() != null ? getTarget().getHeight() : 0;
    }

    public void postInvalidate() {
        if (getTarget() != null) {
            getTarget().postInvalidate();
        }
    }

    public void initAnimation() {
        this.mAnimators = createAnimation();
    }

    public void setAnimationStatus(AnimStatus animStatus) {
        if (this.mAnimators != null) {
            int count = this.mAnimators.size();
            for (int i = 0; i < count; i++) {
                Animator animator = (Animator) this.mAnimators.get(i);
                boolean isRunning = animator.isRunning();
                switch (animStatus) {
                    case START:
                        if (!isRunning) {
                            animator.start();
                            break;
                        }
                        break;
                    case END:
                        if (!isRunning) {
                            break;
                        }
                        animator.end();
                        break;
                    case CANCEL:
                        if (!isRunning) {
                            break;
                        }
                        animator.cancel();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
