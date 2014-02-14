package com.example.counterclock.activities;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.example.counterclock.R;

public class ClockActivity extends Activity {
	private boolean unlocking;
	private boolean locking;
	private boolean paused;
	private Handler handler = new Handler();
	private final BroadcastReceiver turnScreenOffReceiver = new TurnScreenOffReceiver();
	private final Runnable pauseShieldRunnable = new Runnable() {
		@Override
		public void run() {
			if (!locking && !unlocking && paused) {
				PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
				if (pm.isScreenOn()) {
					lockScreen();
				}
			}
		}
	};
	
	private void handleWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
					View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
					View.SYSTEM_UI_FLAG_FULLSCREEN |
					View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}
	private void lockScreen() {
locking = true;
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn()) {
			DevicePolicyManager policy = (DevicePolicyManager)
					getSystemService(Context.DEVICE_POLICY_SERVICE);
			policy.lockNow();
		}
	}
	private void unlockScreen() {
		unlocking = true;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
				overridePendingTransition(0, 0);
			}
		}, 100);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES); // last flag may not be needed
		setContentView(R.layout.activity_clock);
		Button btnScreenOff = (Button)findViewById(R.id.btnScreenOff);
		btnScreenOff.setOnClickListener(screenOffListener);
		registerReceiver(turnScreenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}
	@Override
	protected void onResume() {
		super.onResume();
		paused = false;
		locking = false;
		unlocking = false;
		handler.removeCallbacks(pauseShieldRunnable);
		handleWindowFocusChanged(true);
	}
	@Override
	protected void onPause() {
		super.onPause();
		paused = true;
		if (!unlocking && !locking) {
			handler.postDelayed(pauseShieldRunnable, 600);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(turnScreenOffReceiver);
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		handleWindowFocusChanged(hasFocus);
	}
	
	private OnClickListener screenOffListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			lockScreen();
		}
	};
	
 	private class TurnScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(final Context context, Intent intent) {
			finish();
		}
	}
}
