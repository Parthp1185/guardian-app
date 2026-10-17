package com.example.afinal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class ShakeDetectorService extends Service implements SensorEventListener {

    private static final String TAG = "ShakeDetectorService";
    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "ShakeDetectorChannel";

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float accelValue;
    private float accelLast;
    private float accelCurrent;
    private long lastShakeTime = 0;
    private static final int SHAKE_THRESHOLD = 12; // Adjust this value as needed
    private static final int COOLDOWN_TIME = 30000; // 30 seconds cooldown between SMS sends
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ShakeDetectorService onCreate");

        // Initialize sensor manager and accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "Accelerometer sensor registered");
            } else {
                Log.e(TAG, "No accelerometer found on device");
            }
        } else {
            Log.e(TAG, "Could not get sensor manager");
        }

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize acceleration values
        accelValue = 0.00f;
        accelCurrent = SensorManager.GRAVITY_EARTH;
        accelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ShakeDetectorService onStartCommand");

        // Create and show a notification for foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY; // Service will be restarted if terminated by the system
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Shake Detector Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Channel for Shake Detector Service");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, homePage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Guardian is active")
                .setContentText("Shake detection is running")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Get the acceleration values from the sensor
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Calculate acceleration value
            accelLast = accelCurrent;
            accelCurrent = (float) Math.sqrt(x*x + y*y + z*z);
            float delta = accelCurrent - accelLast;
            accelValue = accelValue * 0.9f + delta;

            // Check if shake threshold exceeded and cooldown period has passed
            long currentTime = System.currentTimeMillis();
            if (accelValue > SHAKE_THRESHOLD && (currentTime - lastShakeTime > COOLDOWN_TIME)) {
                lastShakeTime = currentTime;
                Log.d(TAG, "Shake detected! accelValue = " + accelValue);

                // Vibrate to let the user know the shake was detected
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(500); // Vibrate for 500ms
                }

                // Get location and send SOS
                getLocationAndSendSOS();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }

    private void getLocationAndSendSOS() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            Log.d(TAG, "Location obtained: " + location.getLatitude() + ", " + location.getLongitude());
                            sendSMSToAll(location);
                        } else {
                            Log.d(TAG, "Location is null, sending SMS without location");
                            sendSMSWithoutLocation();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get location: " + e.getMessage());
                        sendSMSWithoutLocation();
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception when getting location: " + e.getMessage());
            sendSMSWithoutLocation();
        }
    }

    private void sendSMSToAll(Location location) {
        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getAllContacts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String phone = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE));
                sendSMSWithLocation(location, phone);
                Log.d(TAG, "Sending SMS with location to: " + phone);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.d(TAG, "No contacts found in database");
        }
    }

    private void sendSMSWithLocation(Location location, String phone) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        DBHelperMessages dbHelperMessages = new DBHelperMessages(this);
        Cursor cursor = dbHelperMessages.getAllMessages();

        if (cursor.moveToFirst()) {
            String message = cursor.getString(cursor.getColumnIndex(DBHelperMessages.COLUMN_MESSAGE));
            String locationMessage = message + "\nMy Location: https://maps.google.com/?q=" + latitude + "," + longitude;

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone, null, locationMessage, null, null);
                Log.d(TAG, "SMS sent successfully to " + phone);
            } catch (Exception e) {
                Log.e(TAG, "Failed to send SMS to " + phone + ": " + e.getMessage());
            }
        }
        cursor.close();
    }

    private void sendSMSWithoutLocation() {
        DBHelper dbHelper = new DBHelper(this);
        Cursor contacts = dbHelper.getAllContacts();

        DBHelperMessages dbHelperMessages = new DBHelperMessages(this);
        Cursor messagesCursor = dbHelperMessages.getAllMessages();

        String message = "SOS Alert!"; // Default message
        if (messagesCursor.moveToFirst()) {
            message = messagesCursor.getString(messagesCursor.getColumnIndex(DBHelperMessages.COLUMN_MESSAGE));
        }
        messagesCursor.close();

        if (contacts.moveToFirst()) {
            do {
                String phoneNumber = contacts.getString(contacts.getColumnIndex(DBHelper.COLUMN_PHONE));
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    Log.d(TAG, "SMS sent without location to " + phoneNumber);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send SMS without location to " + phoneNumber + ": " + e.getMessage());
                }
            } while (contacts.moveToNext());
        }
        contacts.close();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "ShakeDetectorService onDestroy");
        // Unregister the sensor listener when service is destroyed
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        super.onDestroy();
    }
}