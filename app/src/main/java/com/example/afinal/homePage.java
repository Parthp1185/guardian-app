package com.example.afinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class homePage extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private static final int SMS_PERMISSION_REQUEST = 2;
    private FusedLocationProviderClient fusedLocationClient;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ShakeServiceManager.startShakeService(this);

        dbHelper = new DBHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView sosImage = findViewById(R.id.sosImage);
        sosImage.setOnClickListener(v -> {
            Toast.makeText(this, "Wait for 5 seconds, SOS is sending...", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    sendSMSToAll(location);
                } else {
                    Toast.makeText(this, "Could not get location!", Toast.LENGTH_SHORT).show();
                }
            });
            new Handler().postDelayed(() -> Toast.makeText(this, "SOS successfully sent!", Toast.LENGTH_LONG).show(), 5000);
        });
    }

    private void sendSMSToAll(Location location) {
        Cursor cursor = dbHelper.getAllContacts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String phone = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE));
                sendSMSWithLocation(location, phone);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void sendSMSWithLocation(Location location, String phone) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        DBHelperMessages messagesDb = new DBHelperMessages(this);
        Cursor messageCursor = messagesDb.getAllMessages();
        try {
            if (messageCursor != null && messageCursor.moveToFirst()) {
                int messageIndex = messageCursor.getColumnIndex(DBHelperMessages.COLUMN_MESSAGE);
                String message = messageIndex >= 0 ? messageCursor.getString(messageIndex) : "";
                String locationMessage = message + "\nMy Location: https://maps.google.com/?q="
                        + latitude + "," + longitude;
                try {
                    SmsManager.getDefault().sendTextMessage(phone, null, locationMessage, null, null);
                    Toast.makeText(this, "SOS sent to " + phone, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to send SOS to " + phone, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No SOS message found!", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (messageCursor != null) messageCursor.close();
            messagesDb.close();
        }
    }

    // Share the current location using the Android share sheet.
    public void shareLocation(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            Toast.makeText(this, "Allow location access, then tap Share Location again.", Toast.LENGTH_LONG).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(this, "Current location is unavailable.", Toast.LENGTH_SHORT).show();
                return;
            }
            String link = "https://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "My current location: " + link);
            startActivity(Intent.createChooser(shareIntent, "Share location"));
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Unable to retrieve location.", Toast.LENGTH_SHORT).show());
    }


    public void tomenu(View view) {

        Intent intent = new Intent(homePage.this, menu.class);
        startActivity(intent);
    }

    public void toSOSServicePage(View view) {
        Intent intent = new Intent(homePage.this, EmergencyServiceActivity.class);
        startActivity(intent);
    }
}