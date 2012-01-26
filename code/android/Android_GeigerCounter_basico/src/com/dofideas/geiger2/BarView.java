package com.dofideas.geiger2;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class BarView extends View implements Observer{

	private static final float LOW_VALUE = 0.1f;
	private static final float HIGH_VALUE = 1.0f;
	
	private static final String TAG = "BAR_VIEW";
	
	private static final int NUMBER_LEDS = 50;
	static final float MAX_USV1MIN = 1.0f;
	
	private int barWidth;
	private int barHeight;
	
	private float usv1min = 0.0f;

	
	public BarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG,"BarView()");
	}
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		barWidth = w;
		barHeight = h;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint mPaint = new Paint();
		float[] mHsvColor = {120.0f,1.0f,0.0f};
		int ledHeight = barHeight/NUMBER_LEDS;
		Rect mRect;
		
		int level = (int) (usv1min/MAX_USV1MIN*NUMBER_LEDS);
		
		for (int n=0; n<NUMBER_LEDS; n++) {
			mHsvColor[0] = 120-n/(float)NUMBER_LEDS*120;
			mHsvColor[1] = 1.0f;
			if (n<level){
				mHsvColor[2] = 1.0f;
			} else {
				mHsvColor[2] = 0.1f;
			}
			mRect = new Rect(0,barHeight-(n+1)*ledHeight+1,barWidth,barHeight-n*ledHeight-1);
			mPaint.setColor(Color.HSVToColor(mHsvColor));
			canvas.drawRect( mRect, mPaint);
		}
	}
	
	public void update(Observable obs, Object arg1) {
		Log.d(TAG,"update()");		
		
		GeigerModel model = (GeigerModel) obs;
		usv1min = model.getUsv1min();
		Log.d(TAG,"update() : "+usv1min+" usv/h");
		//invalidate();
		
		// Calculate number of leds on
		int numLedsOn = (int) (usv1min/MAX_USV1MIN*NUMBER_LEDS);
		
	}
	
	
	
	

}
