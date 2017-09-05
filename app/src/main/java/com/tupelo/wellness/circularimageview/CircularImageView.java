package com.tupelo.wellness.circularimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircularImageView extends ImageView {
	private int viewWidth;
	private int viewHeight;
	private Paint paint;
	private Paint paintBorder;
	private int borderWidth = 10;

	public CircularImageView(Context context) {
		super(context);
		setup();
	}

	public CircularImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setup();
	}

	public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setup();
	}

	private void setup()
	{
		paint = new Paint();
		paint.setAntiAlias(true);
		paintBorder = new Paint();
		setBorderColor(Color.parseColor("#60ffffff"));
		paintBorder.setAntiAlias(true);		
	}

	public void setBorderColor(int borderColor)
	{		
		if(paintBorder != null)
			paintBorder.setColor(borderColor);
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas){
		int circleCenter = viewWidth / 2;
		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return; 
		}
		Bitmap b =  ((BitmapDrawable)drawable).getBitmap() ;
		if(b==null)
			return;
		try{
			Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
			int w = getWidth();
			canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth, paintBorder);
			Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
			canvas.drawBitmap(roundBitmap, 0,0, null);}
		catch(Exception exception){
			exception.printStackTrace();
		}


	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
		Bitmap sbmp;
		if(bmp.getWidth() != radius || bmp.getHeight() != radius)
			sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
		else
			sbmp = bmp;
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
				sbmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.DKGRAY);
		canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2,
				sbmp.getWidth() / 2-2.0f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);


		return output;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec, widthMeasureSpec);    	

		viewWidth = width - (borderWidth *2);
		viewHeight = height - (borderWidth*2);

		setMeasuredDimension(width, height);
	}

	private int measureWidth(int measureSpec)
	{
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = viewWidth;

		}

		return result;
	}

	private int measureHeight(int measureSpecHeight, int measureSpecWidth) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = viewHeight;           
		}
		return result;
	}

}
