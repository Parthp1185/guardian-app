package com.example.afinal;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;

public class MyAccessibilityService extends AccessibilityService {

    private static final int REQUIRED_PRESSES = 3;
    private static final long MAX_TIME_BETWEEN_PRESSES = 2000; // 2 seconds
    private static final String NOTIFICATION_CHANNEL_ID = "sos_channel";
    private static final int NOTIFICATION_ID = 1001;

    private int volumeDownCount = 0;
    private long lastPressTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;

    private Runnable resetCounter = () -> volumeDownCount = 0;

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        info.eventTypes = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create notification channel for Android O and above
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "SOS Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for SOS messages");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
            long currentTime = System.currentTimeMillis();


            Log.d("SOSService", "Volume down pressed. Count: " + volumeDownCount +
                    ", Time since last press: " + (currentTime - lastPressTime) + "ms");

            // If it's been too long since the last press, reset the counter
            if (lastPressTime > 0 && currentTime - lastPressTime > MAX_TIME_BETWEEN_PRESSES) {
                volumeDownCount = 0;
            }

            lastPressTime = currentTime;
            volumeDownCount++;

            handler.removeCallbacks(resetCounter);
            handler.postDelayed(resetCounter, MAX_TIME_BETWEEN_PRESSES);

            if (volumeDownCount == REQUIRED_PRESSES) {
                volumeDownCount = 0;
                vibrate();
                showSendingNotification();
                sendSOSMessage();
                return true;
            }
        }

        return super.onKeyEvent(event);
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((android.os.Vibrator) getSystemService(VIBRATOR_SERVICE))
                    .vibrate(android.os.VibrationEffect.createOneShot(500, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((android.os.Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(500);
        }
    }

    private void showSendingNotification() {
        Intent intent = new Intent(this, homePage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_emergency) // Make sure to have this icon resource
                .setContentTitle("SOS Alert")
                .setContentText("Sending SOS message to your emergency contacts...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void sendSOSMessage() {
        // Check for both location and SMS permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            showPermissionNotification();
            return;
        }

        // First try to get last known location (fastest)
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                sendSOSWithLocation(location);
            } else {
                // If last location is null, try to get current location (more accurate but slower)
                getCurrentLocation();
            }
        }).addOnFailureListener(e -> {
            // Try alternative location method
            requestLocationUpdates();
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @Override
                public boolean isCancellationRequested() {
                    return false;
                }

                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return this;
                }
            }).addOnSuccessListener(location -> {
                if (location != null) {
                    sendSOSWithLocation(location);
                } else {
                    // If still null, fallback to sending without location
                    sendSOSWithoutLocation();
                }
            }).addOnFailureListener(e -> sendSOSWithoutLocation());
        } catch (Exception e) {
            // Last resort
            sendSOSWithoutLocation();
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            sendSOSWithoutLocation();
            return;
        }

        try {
            // Setting up a one-time location listener
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    sendSOSWithLocation(location);
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}
            };

            // Request a single location update
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, Looper.getMainLooper());

            // Set timeout to ensure we still send SOS if location takes too long
            handler.postDelayed(() -> {
                locationManager.removeUpdates(locationListener);
                sendSOSWithoutLocation();
            }, 10000); // 10 seconds timeout
        } catch (Exception e) {
            sendSOSWithoutLocation();
        }
    }

    private void sendSOSWithLocation(Location location) {
        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getAllContacts();
        DBHelperMessages dbHelperMessages = new DBHelperMessages(this);
        Cursor msgCursor = dbHelperMessages.getAllMessages();

        if (msgCursor != null && msgCursor.moveToFirst()) {
            String message = msgCursor.getString(msgCursor.getColumnIndex(DBHelperMessages.COLUMN_MESSAGE));
            String locationUrl = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
            String fullMessage = message + "\nMy Location: " + locationUrl;

            sendMessages(cursor, fullMessage);
            msgCursor.close();
        } else {
            // Default message if no custom message is found
            String locationUrl = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
            String fullMessage = "EMERGENCY! I need help!\nMy Location: " + locationUrl;
            sendMessages(cursor, fullMessage);
        }
    }

    private void sendSOSWithoutLocation() {
        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getAllContacts();
        DBHelperMessages dbHelperMessages = new DBHelperMessages(this);
        Cursor msgCursor = dbHelperMessages.getAllMessages();

        if (msgCursor != null && msgCursor.moveToFirst()) {
            String message = msgCursor.getString(msgCursor.getColumnIndex(DBHelperMessages.COLUMN_MESSAGE));
            String fullMessage = message + "\nUnable to share my location.";

            sendMessages(cursor, fullMessage);
            msgCursor.close();
        } else {
            // Default message if no custom message is found
            String fullMessage = "EMERGENCY! I need help! Unable to share my location.";
            sendMessages(cursor, fullMessage);
        }
    }

    private void sendMessages(Cursor cursor, String message) {
        int successCount = 0;
        int totalContacts = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                totalContacts++;
                String phone = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE));
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    // For long messages, we need to divide them
                    if (message.length() > 160) {
                        android.telephony.SmsManager.getDefault().sendMultipartTextMessage(
                                phone, null, smsManager.divideMessage(message), null, null);
                    } else {
                        smsManager.sendTextMessage(phone, null, message, null, null);
                    }
                    successCount++;
                } catch (Exception e) {
                    // Continue with other contacts if one fails
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        showSOSResultNotification(successCount, totalContacts);
    }

    private void showPermissionNotification() {
        Intent intent = new Intent(this, homePage.class);  // Should point to a settings activity
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_emergency)  // Make sure to have this icon resource
                .setContentTitle("SOS Permission Error")
                .setContentText("Location or SMS permission missing. Please enable in settings.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID + 1, builder.build());
    }

    private void showSOSResultNotification(int successCount, int totalContacts) {
        String resultMessage;
        if (successCount == totalContacts && totalContacts > 0) {
            resultMessage = "SOS successfully sent to all " + totalContacts + " contacts";
        } else if (successCount > 0) {
            resultMessage = "SOS sent to " + successCount + " out of " + totalContacts + " contacts";
        } else {
            resultMessage = "Failed to send SOS messages. Please try again.";
        }

        Intent intent = new Intent(this, homePage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_emergency)  // Make sure to have this icon resource
                .setContentTitle("SOS Status")
                .setContentText(resultMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID + 2, builder.build());
    }

    @Override
    public void onAccessibilityEvent(android.view.accessibility.AccessibilityEvent event) {
        // Not used for key event monitoring
    }

    @Override
    public void onInterrupt() {
        // Service interrupted
    }
}