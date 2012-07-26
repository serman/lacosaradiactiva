package com.lacosaradioactiva.geiger.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

class GraphView extends View {
	// paint
	private Paint mPaint = new Paint();
	private int mColors[] = new int[3 * 2];
	private Canvas mCanvas = new Canvas();
	private Bitmap bitmap; // Cache


	// values to plot
	private float totalValues[]; 

	private float mSpeed = 1.0f;
	private float mLastX;
	private float mMaxX;

	// widget size
	private float mWidth;
	private float mHeight;

	public GraphView(Context context) {
		super(context);
		mColors[0] = Color.argb(192, 255, 64, 64);
		mColors[1] = Color.argb(192, 64, 128, 64);
		mColors[2] = Color.argb(192, 64, 64, 255);
		mColors[3] = Color.argb(192, 64, 255, 255);
		mColors[4] = Color.argb(192, 128, 64, 128);
		mColors[5] = Color.argb(192, 255, 255, 64);

		mPaint.setStrokeWidth(2.0f); 
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mHeight = h;

		if (mWidth < mHeight) {
			mMaxX = w;
		} else {
			mMaxX = w - 50;
		}

		mLastX = mMaxX;

		// create a bitmap for caching what was drawn
		if (bitmap != null) {
			bitmap.recycle();
		}
		mCanvas = new Canvas();
		bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(bitmap);
        mCanvas.drawColor(0xFFFFFFFF); 
        
        totalValues = new float[w];

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		synchronized (this) {

			// draw the bitmap to the real canvas c
			canvas.drawBitmap(bitmap,
					new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
					new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), null);

			final Paint paint = mPaint;
			float deltaX = mSpeed;
			float newX = mLastX + deltaX;

			// canvas.save(Canvas.MATRIX_SAVE_FLAG);
			paint.setColor(mColors[0]);
			for (int i = 0; i < 1; i++) {
				
				for (int j = 0; j < (int) (mLastX - 1); j++) {
					//Log.d("", "" + totalValues[j] + " " + pv + " " + totalValues[j + 1] + " " + v + " " + paint);
					// sumarle offset y multiplicar por escala
					canvas.drawLine(j, totalValues[j], j + 1, totalValues[j + 1], paint);
				} 
			} 

			// canvas.restore();
			mLastX += mSpeed;

			// check if plot has reached the max x and reset
			if (mLastX >= mMaxX) {
				mLastX = 0;
			}

		}
	}

	public void setVal(float[] values) {
		
		int index = (int) mLastX; 
		totalValues[index] = values[0]; 
		invalidate();

	}

	public void destroy() {
		if (bitmap != null) {
			bitmap.recycle();
		}
	}

}