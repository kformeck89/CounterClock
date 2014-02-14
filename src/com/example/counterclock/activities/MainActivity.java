package com.example.counterclock.activities;

import com.example.counterclock.R;
import com.example.counterclock.activities.receivers.AdminReceiver;

import android.os.Bundle;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btnTest = (Button)findViewById(R.id.btnTest);
		Button btnAdmin = (Button)findViewById(R.id.btnAdmin);
		btnTest.setOnClickListener(testClickListener);
		btnAdmin.setOnClickListener(adminClickListener);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private OnClickListener testClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			startActivity(new Intent(MainActivity.this, ClockActivity.class));
		}
	};
	private OnClickListener adminClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			ComponentName admin = new ComponentName(MainActivity.this, AdminReceiver.class);
			Intent intent = new Intent(
						DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
								.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
			startActivity(intent);
		}
	};

}
