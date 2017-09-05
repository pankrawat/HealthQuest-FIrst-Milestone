package com.tupelo.wellness;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DrawCanvasDot extends View {

	private Paint paint;
	private int childCount;
	int visibleChild;

	public DrawCanvasDot(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (childCount != 0) {
			// float margin =4;
			// float radius=getWidth()/50;
			float radius = (float) (getHeight() / 2.5);
			float margin = radius / 2;
			float cx = 0;
			if (childCount == 1) {
				cx = getWidth() / 2;
			} else {
				cx = getWidth() / 2 - ((radius + margin) * (childCount - 1));
			}
			// float cy =25;
			float cy = getHeight() - radius;
			canvas.save();
			for (int i = 0; i < childCount; i++) {
				if (i == visibleChild) {
					paint.setColor(Color.WHITE);
					canvas.drawCircle(cx, cy, radius, paint);
				} else {
					paint.setColor(Color.parseColor("#32ffffff"));
					canvas.drawCircle(cx, cy, radius, paint);
				}
				cx += 2 * (radius + margin);
			}
		}
		canvas.restore();
	}

	public void setchildCount(int c, int visible) {
		childCount = c;
		this.visibleChild = visible;
		invalidate();
	}
}
