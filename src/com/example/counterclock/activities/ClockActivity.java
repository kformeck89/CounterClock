package com.example.counterclock.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.counterclock.DeviceUtils;
import com.example.counterclock.R;


public class ClockActivity extends Activity {
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
	private String minute = "00";
	private String second = "00";
	private boolean unlocking;
	private boolean locking;
	private boolean paused;
	private Handler handler = new Handler();
	private ImageButton btnShowButtons = null;
	private Button btnStop = null;
	private Button btnPause = null;
	private Button btnUnlock = null;
	private Button btnScreenOff = null;
	
	@TargetApi(19)
	private void handleWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			
			// If we are on Kit Kat or higher, then use immersive
			// mode to hide the navigation bar and status bar
			if (Build.VERSION.SDK_INT >= 19 ) {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_FULLSCREEN |
						View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			} else {
				getWindow().getDecorView().setSystemUiVisibility(
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
						View.SYSTEM_UI_FLAG_FULLSCREEN);
			}
		}
	}
	private void lockScreen() {
		locking = true;
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn()) {
			DevicePolicyManager policy = (DevicePolicyManager)
					getSystemService(Context.DEVICE_POLICY_SERVICE);
			try {
				policy.lockNow();
			} catch (SecurityException ex) {
				Toast.makeText(
						this, 
						"You must enable this app as a device administrator\n\n" +
						"Please enable it and press back button to return here.",
						Toast.LENGTH_LONG)
					.show();
				DeviceUtils.launchDeviceAdminSettings(ClockActivity.this);
			}			
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
		
		// Add custom parameters to the window
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES); // last flag may not be needed
		setContentView(R.layout.activity_clock);
		
		// Initialize
		btnShowButtons = (ImageButton)findViewById(R.id.btnShowButtons);
		btnStop = (Button)findViewById(R.id.btnStop);
		btnPause = (Button)findViewById(R.id.btnPause);
		btnUnlock = (Button)findViewById(R.id.btnUnlock);
		btnScreenOff = (Button)findViewById(R.id.btnScreenOff);
		
		TextView txtMinutes = (TextView)findViewById(R.id.txtMinutes);
		TextView txtSeconds = (TextView)findViewById(R.id.txtSeconds);
		
		btnShowButtons.setOnClickListener(showButtonsListener);
		btnStop.setOnClickListener(stopClickListener);
		btnPause.setOnClickListener(pauseClickListener);
		btnUnlock.setOnClickListener(unlockClickListener);
		btnScreenOff.setOnClickListener(screenOffListener);
		txtMinutes.setText(R.string.test_minute);
		txtSeconds.setText(R.string.test_second);
		
		// Register the screen off broadcast receiver
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
	
	private OnClickListener showButtonsListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			
			// If all buttons are visible, 
			if (btnStop.getVisibility() == View.VISIBLE &&
				btnPause.getVisibility() == View.VISIBLE &&
				btnUnlock.getVisibility() == View.VISIBLE &&
				btnScreenOff.getVisibility() == View.VISIBLE) {
				
				// Make them invisible again
				btnStop.setVisibility(View.INVISIBLE);
				btnPause.setVisibility(View.INVISIBLE);
				btnUnlock.setVisibility(View.INVISIBLE);
				btnScreenOff.setVisibility(View.INVISIBLE);
			} else {
				
				// Otherwise, make them visible again
				btnStop.setVisibility(View.VISIBLE);
				btnPause.setVisibility(View.VISIBLE);
				btnUnlock.setVisibility(View.VISIBLE);
				btnScreenOff.setVisibility(View.VISIBLE);
			}
		}
	};
	private OnClickListener stopClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			
		}
	};
	private OnClickListener pauseClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			
		}
	};
	private OnClickListener unlockClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			unlockScreen();
		}
	};
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
	
  	
  	//private class Stopwatch {
	//	
	//	
	//	public Stopwatch() { }
	//	
	//	public void startTimer() {
	//		Timer secondTimer = new Timer();
	//		Timer minuteTimer = new Timer();
	//		if (stopOrPausePressed) {
	//			
	//		}
	//		secondTimer.schedule(new SecondCountTask(), 0, 1000);
	//		minuteTimer.schedule(new MinuteCountTask(), 0 ,10000);		
	//	}
	//}
	//private class SecondCountTask extends TimerTask {
	//	@Override
	//	public void run() {
	//		Stopwatch watch = new StopWatch();
	//	}		
	//}
	//private class MinuteCountTask extends TimerTask {
	//	@Override
	//	public void run() {
	//		
	//	}
	//}
}
