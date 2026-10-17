package com.example.afinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ShakeDetectorBootReceiver extends BroadcastReceiver {
    private static final String TAG = "ShakeDetectorBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                        intent.getAction().equals(Intent.ACTION_REBOOT) ||
                        intent.getAction().equals(Intent.ACTION_MY_PACKAGE_REPLACED))) {

            Log.d(TAG, "Boot completed received, starting ShakeDetectorService");

            // Start the shake detector service
            Intent serviceIntent = new Intent(context, ShakeDetectorService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}