package com.example.afinal;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class RegistrationActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 1;
    private DBHelper dbHelper;
    EditText nameInput, regNoInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameInput = findViewById(R.id.nameInput);
        regNoInput = findViewById(R.id.regNoInput);
        dbHelper = new DBHelper(this);

        // Request permission at runtime
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
    }

    public void register(View view) {
        String name = nameInput.getText().toString();
        String regNo = regNoInput.getText().toString();

        if (name.isEmpty() || !name.matches("[a-zA-Z ]+") || regNo.isEmpty() || regNo.length() < 5) {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = dbHelper.addContact(name, regNo);
        if (inserted) {
            Toast.makeText(this, "Contact Saved Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to Save Contact", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);

                nameInput.setText(name);
                regNoInput.setText(number);

                cursor.close();
            }
        }
    }
}