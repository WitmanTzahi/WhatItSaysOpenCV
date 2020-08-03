package com.lbmotion.whatitsays.splash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.lbmotion.whatitsays.R;
import com.lbmotion.whatitsays.data.LoginData;

public class SplashScreen extends View {
	private Bitmap p = null;

	public SplashScreen(Context context) {
		super(context);
		init();
	}

	public SplashScreen(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		this.setFocusableInTouchMode(true);
		this.setFocusable(true);
		p = BitmapFactory.decodeResource(getResources(), getResourceId());
	}

	@Override
	protected void onDraw(Canvas canvas) {
	    p = Bitmap.createScaledBitmap(p, getWidth()*4/5, getHeight()*4/5, false);
		canvas.drawBitmap(p, (getWidth()/2 - p.getWidth()/2), 0, null);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    try {
	    	p = BitmapFactory.decodeResource(getResources(), getResourceId());
	    	this.setMeasuredDimension(p.getWidth(), p.getHeight());
	    }
	    catch (Exception e) {}
	    catch (OutOfMemoryError e) {}
	}

	private int getResourceId() {
		try {
			switch(Integer.parseInt(LoginData.theFlashAuthority)) {
				case 920020:
					return R.drawable.c920020;
				case 920038:
					return R.drawable.c920038;
				case 920025:
					return R.drawable.c920025;
				case 920035:
					return R.drawable.c920035;
				case 920019:
					return R.drawable.c920019;
				case 920015:
					return R.drawable.c920015;
				case 920036:
					return R.drawable.c920036;
				case 920037:
					return R.drawable.c920037;
				case 920042:
					return R.drawable.c920042;
				case 920056:
					return R.drawable.c920056;
				case 920031:
					return R.drawable.c920031;
				case 920030:
					return R.drawable.c920030;
				case 920039:
					return R.drawable.c920039;
				case 920052:
					return R.drawable.c920052;
				case 920041:
					return R.drawable.c920041;
				case 920044:
					return R.drawable.c920044;
				case 920043:
					return R.drawable.c920043;
				case 14000:
				case 14001:
					return R.drawable.c14000;
				case 526100:
					return R.drawable.c526100;
				case 920013:
					return R.drawable.c920013;
				case 920016:
					return R.drawable.c920016;
				case 225503:
					return R.drawable.c225503;
				case 920022:
					return R.drawable.c920022;
				case 920010:
					return R.drawable.c920010;
				case 9700:
					return R.drawable.c9700;
				case 265000:
					return R.drawable.c265000;
				case 920011:
					return R.drawable.c920011;
				case 836160:
					return R.drawable.c836160;
				case 920012:
					return R.drawable.c920012;
//			case 920021:
//				return R.drawable.c920021;
				case 279000://׳₪׳×׳— ׳×׳§׳•׳”
					return R.drawable.c279000;
//			case 920009:
//				return R.drawable.c920009;
				case 186111:
					return R.drawable.c186111;
				default:
					return R.drawable.splash;
			}
		}
		catch (Exception e) {}
		return R.drawable.splash;
	}
}