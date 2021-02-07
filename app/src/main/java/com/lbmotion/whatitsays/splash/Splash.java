package com.lbmotion.whatitsays.splash;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.lbmotion.whatitsays.R;


public class Splash extends AppCompatActivity {
	public static boolean _active;
//	protected int _splashTime = 15000;
//	AnimationSet rootSet;
	SplashScreen graphics;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
        if(savedInstanceState == null)
		    _active = true;
		graphics = (SplashScreen)findViewById(R.id.graphics);
//		rootSet = getAnimation();
		new Thread() { public void run() {
//			graphics.startAnimation(rootSet);
			try {
				int waited = 0;
				while (_active && waited != 3500) {
					sleep(100);
					if (_active) {
						waited += 100;
					}
				}
			} 
			catch (InterruptedException e) {}
			finally {
				_active = false;
				finish();
			}
		}}.start();
	}
/*
	private AnimationSet getAnimation() {
		rootSet = new AnimationSet(true);
		rootSet.setInterpolator(new BounceInterpolator());
		TranslateAnimation trans1 = new TranslateAnimation(0, 0, -400, 0);
		trans1.setStartOffset(0);
		trans1.setDuration(1000);
		trans1.setFillAfter(true);
		rootSet.addAnimation(trans1);
		ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		scale.setDuration(1000);
		scale.setFillAfter(true);
		AnimationSet childSet = new AnimationSet(true);
		childSet.addAnimation(scale);
		childSet.setInterpolator(new BounceInterpolator());
		rootSet.addAnimation(childSet);
		Animation outtoRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoRight.setStartOffset(2000);
		outtoRight.setDuration(1000);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		rootSet.addAnimation(outtoRight);
		return rootSet;
	}
*/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			_active = false;
		}
		return true;
	}
}