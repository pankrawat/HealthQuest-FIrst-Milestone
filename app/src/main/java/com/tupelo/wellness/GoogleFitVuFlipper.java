package com.tupelo.wellness;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

import com.tupelo.wellness.activity.GoogleFitHelpActivity;

public class GoogleFitVuFlipper extends ViewFlipper {

    private GoogleFitHelpActivity activity;
    private DrawCanvasDot dot;

    public GoogleFitVuFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        activity = (GoogleFitHelpActivity) context;
        dot = activity.dot;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (dot == null) {
            dot = activity.dot;
        }
        for (int i = 0; i < getChildCount(); i++) {
            dot.setchildCount(getChildCount(), getDisplayedChild());
        }
        canvas.save();
        canvas.restore();
    }

}
