package com.example.afinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);

        // Check if the user is already logged in
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Intent serviceIntent = new Intent(this, SOSService.class);
            startService(serviceIntent);
            requestAccessibilityPermission();
            // If the user is already logged in, directly open the app
            Intent intent = new Intent(MainActivity.this, homePage.class);
            startActivity(intent);
            finish();
        }
    }

    public void toSOS(View view) {
        // Validate email/phone and password
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Username/Email is required");
            usernameInput.requestFocus();
            return;
        }

        if (!isValidEmail(username) && !isValidPhone(username)) {
            usernameInput.setError("Please enter a valid email or phone number");
            usernameInput.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters long");
            passwordInput.requestFocus();
            return;
        }

        // If validation passes, save login status in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true); // Mark as logged in
        editor.putString("username", username); // Optionally save the username
        editor.apply();

        // Proceed to the home page
        Intent intent = new Intent(MainActivity.this, homePage.class);
        startActivity(intent);
        finish();
    }

    public void toSignUp(View view) {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
    }

    public void toForgot(View view) {
        Intent intent = new Intent(MainActivity.this, ForgotPass.class);
        startActivity(intent);
    }

    private boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(CharSequence phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }
    private void requestAccessibilityPermission() {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Enable 'GestureDetectionService' for SOS feature", Toast.LENGTH_LONG).show();
    }
}