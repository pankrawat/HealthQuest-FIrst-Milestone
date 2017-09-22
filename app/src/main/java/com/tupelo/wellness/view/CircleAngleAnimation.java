package com.tupelo.wellness.view;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.github.lzyzsd.circleprogress.DonutProgress;

/**
 * Created by admin1 on 14/9/17.
 */

public class CircleAngleAnimation extends Animation {

    private DonutProgress circle;

    private float oldAngle;
    private float newAngle;

    public CircleAngleAnimation(DonutProgress circle, int newAngle) {
        this.oldAngle = 0;
        this.newAngle = newAngle;
        this.circle = circle;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {
        float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);
        circle.setProgress(angle);
        circle.requestLayout();
    }
}