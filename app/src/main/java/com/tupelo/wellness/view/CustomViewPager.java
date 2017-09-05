package com.tupelo.wellness.view;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.tupelo.wellness.OnBoard.OnboarderActivity;
import com.tupelo.wellness.helper.Helper;

/**
 * Created by owner on 25/07/16.
 */public class CustomViewPager extends android.support.v4.view.ViewPager {

    private Context context;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private boolean isPagingEnabled = true;
    private int flag = 0;

    public CustomViewPager(android.content.Context context) {
        super(context);
    }

    public CustomViewPager(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
       Log.e("fooooooooooo", " coming on touch event falg is " + flag);

        if(flag == 1)
        {
            //return false on swipe right to left

            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    Log.e("foooooooooo","Action Down the value of x is " + x1);
                    return true;
                case MotionEvent.ACTION_UP:
                    x2 = event.getX();

                    Log.e("foooooooooo","Action Up the value of x is " + x2);
                    float deltaX = x2 - x1;

                    if (Math.abs(deltaX) > MIN_DISTANCE)
                    {
                        // Left to Right swipe action
                        if (x2 < x1)
                        {
                          //  Toast.makeText(context, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show();
                            Helper.mymoSerialCheck(flag,context);
                            return true;
                        }
                        else
                        {
                            ((OnboarderActivity) context).backFlip();
                        }

                    }
                    break;
            }

        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public void setFlag(int flag) {
        this.flag =flag;
    }
    public boolean isPagingEnabled() {
       return this.isPagingEnabled ;
    }
}
