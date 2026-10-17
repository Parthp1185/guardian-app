package com.example.afinal;

import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class sosmessage extends AppCompatActivity {

    EditText messageInput;
    DBHelperMessages dbHelperMessages;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosmessage);

        messageInput = findViewById(R.id.messageInput);
        saveButton = findViewById(R.id.saveButton);
        dbHelperMessages = new DBHelperMessages(this);

        loadSavedMessage();

        saveButton.setOnClickListener(v -> saveSosMessage());
    }

    private void loadSavedMessage() {
        Cursor cursor = dbHelperMessages.getAllMessages();
        if (cursor.moveToLast()) {
            String message = cursor.getString(cursor.getColumnIndex(DBHelperMessages.COLUMN_MESSAGE));
            messageInput.setText(message);
        }
        cursor.close();
    }

    public void saveSosMessage() {
        String message = messageInput.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isSaved = dbHelperMessages.addMessage(message);

        if (isSaved) {
            Toast.makeText(this, "Message Saved Successfully", Toast.LENGTH_SHORT).show();
            loadSavedMessage();
            shareLatestMessage(message);
        } else {
            Toast.makeText(this, "Failed to Save Message", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareLatestMessage(String message) {
        try {
            String phoneNumber = "1234567890"; // Replace with your emergency contact number
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to Send Message", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}