package com.example.afinal;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class SOSService extends Service {
    private PowerButtonReceiver powerButtonReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        powerButtonReceiver = new PowerButtonReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(powerButtonReceiver, filter);
        Log.d("SOSService", "Power Button Receiver Registered");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(powerButtonReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}