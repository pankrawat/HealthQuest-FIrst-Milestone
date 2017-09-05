package com.tupelo.wellness.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tupelo.wellness.helper.AppTheme;

/**
 * Created by admin1 on 21/9/16.
 */
public class TwoButtons extends View {

    private Path path;
    private Path path1;
    private Region region;
    private Region region1;
    private static int GAP=2;

    private ButtonClickEvents buttonClickEvent;

    public interface ButtonClickEvents{
        public void redButtonClick();
        public void blueButtonClick();
    }

    public void setOnButtonClickEvent(ButtonClickEvents buttonClickEvent) {
        this.buttonClickEvent=buttonClickEvent;
    }

    public TwoButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        canvas.drawPaint(paint);

        paint.setStrokeWidth(0);

        paint.setColor(android.graphics.Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Point a = new Point(GAP, GAP);
        Point b = new Point(GAP, getHeight()-2*GAP);
        Point c = new Point(getWidth()-2*GAP, GAP);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.close();

        canvas.drawPath(path, paint);

        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        region = new Region();
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));

        paint.setColor(Color.WHITE);
        Point a1 = new Point(getWidth()-GAP, getHeight()-GAP);
        Point b1 = new Point(getWidth()-GAP, 2*GAP);
        Point c1 = new Point(2*GAP, getHeight()-GAP);

        path1 = new Path();
        path1.setFillType(Path.FillType.EVEN_ODD);
        path1.moveTo(a1.x, a1.y);
        path1.lineTo(b1.x, b1.y);
        path1.lineTo(c1.x, c1.y);

        path1.close();
        canvas.drawPath(path1, paint);

        RectF rectF1 = new RectF();
        path1.computeBounds(rectF1, true);
        region1 = new Region();
        region1.setPath(path1, new Region((int) rectF1.left, (int) rectF1.top, (int) rectF1.right, (int) rectF1.bottom));


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Point point = new Point();
                point.x = (int) event.getX();
                point.y = (int) event.getY();

                invalidate();


                if(region.contains((int)point.x,(int) point.y))
                {
                    if(buttonClickEvent!=null)
                        buttonClickEvent.redButtonClick();
                }else if(region1.contains((int)point.x,(int) point.y))
                {
                    if(buttonClickEvent!=null)
                        buttonClickEvent.blueButtonClick();
                }

                return true;
        }

        return false;
    }

}
