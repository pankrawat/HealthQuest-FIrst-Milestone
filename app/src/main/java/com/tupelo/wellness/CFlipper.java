package com.tupelo.wellness;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

import com.tupelo.wellness.activity.Dashboard;


public class CFlipper extends ViewFlipper {

    private Dashboard activity;
    private DrawCanvasDot dot;

    public CFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    public void setReference(Dashboard ref) {
        this.activity = ref;
        dot = activity.dot;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(activity!=null) {
            if (dot == null) {
                dot = activity.dot;
            }
            for (int i = 0; i < getChildCount(); i++) {
                dot.setchildCount(getChildCount(), getDisplayedChild());
            }
        }
        canvas.restore();
    }
}
