package com.example.afinal;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ShakeServiceManager {
    private static final String TAG = "ShakeServiceManager";

    /**
     * Start the shake detector service
     * @param context Application context
     */
    public static void startShakeService(Context context) {
        Log.d(TAG, "Starting ShakeDetectorService");
        Intent serviceIntent = new Intent(context, ShakeDetectorService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    /**
     * Stop the shake detector service
     * @param context Application context
     */
    public static void stopShakeService(Context context) {
        Log.d(TAG, "Stopping ShakeDetectorService");
        Intent serviceIntent = new Intent(context, ShakeDetectorService.class);
        context.stopService(serviceIntent);
    }
}