package com.example.counterclock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.example.counterclock.receivers.AdminReceiver;

public class DeviceUtils {
	public static void launchDeviceAdminSettings(Context context) {
		ComponentName admin = new ComponentName(context, AdminReceiver.class);
		Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
							.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
		context.startActivity(intent);
	}	
}
