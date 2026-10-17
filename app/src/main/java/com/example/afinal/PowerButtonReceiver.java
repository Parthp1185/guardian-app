package com.example.afinal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PowerButtonReceiver extends BroadcastReceiver {
    private static int powerPressCount = 0;
    private static long lastPressTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) ||
                        intent.getAction().equals(Intent.ACTION_SCREEN_ON))) {

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPressTime < 1000) { // Check if presses are within 1 sec
                powerPressCount++;
            } else {
                powerPressCount = 1; // Reset count if time gap is too long
            }
            lastPressTime = currentTime;

            Log.d("PowerButtonReceiver", "Power button pressed: " + powerPressCount);

            if (powerPressCount >= 3) { // If pressed 3 times quickly
                Log.d("PowerButtonReceiver", "SOS Triggered!");
                Intent sosIntent = new Intent(context, homePage.class);
                sosIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(sosIntent);
                powerPressCount = 0; // Reset count after triggering
            }
        }
    }
}