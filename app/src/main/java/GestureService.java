package com.example.afinal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;


public class GestureService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private int shakeCount = 0;
    private long lastShakeTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create Notification for Foreground Service
        createNotification();

        // Initialize Shake Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    private void createNotification() {
        String channelId = "sos_gesture_service";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Gesture Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Gesture Service Running")
                .setContentText("Listening for SOS gestures...")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .build();

        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Shake Detection
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
            long currentTime = System.currentTimeMillis();

            if (acceleration > 15) { // Shake Threshold
                if (currentTime - lastShakeTime < 1000) {
                    shakeCount++;
                } else {
                    shakeCount = 1;
                }

                lastShakeTime = currentTime;

                if (shakeCount >= 3) { // Triple Shake triggers SOS
                    sendSOSMessage();
                    shakeCount = 0;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void sendSOSMessage() {
        Toast.makeText(this, "SOS Triggered by Gesture!", Toast.LENGTH_SHORT).show();
        // Start your SOS Activity or Send SOS Message here
        Intent sosIntent = new Intent(this, MainActivity.class);
        sosIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(sosIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}