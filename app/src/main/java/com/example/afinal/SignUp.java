package com.example.afinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Check if the user is already logged in (to prevent navigating to signup if already logged in)
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // If the user is already logged in, directly open the app's home page
            Intent intent = new Intent(SignUp.this, homePage.class);
            startActivity(intent);
            finish();
        }
    }

    // Return to the login screen from the sign-up page.
    public void toHome(View view) {
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Validate the input fields and show the appropriate messages
    public void validateAndSubmit(View view) {
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        EditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);

        // Get the email, password, and confirm password input values
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Regular expression for validating email
        if (!email.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")) {
            emailInput.setError("Invalid email address");
            return;
        }

        // Check if password meets the criteria
        if (password.length() < 6 || !password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*].*")) {
            passwordInput.setError("Password must be at least 6 characters long, contain 1 number, and 1 special character");
            return;
        }

        // Ensure the passwords match
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        // If everything is correct, show success message and navigate to the homePage
        Toast.makeText(this, "Sign-up successful", Toast.LENGTH_SHORT).show();

        // Save user login state in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true); // Mark as logged in
        editor.putString("email", email); // Optionally save the email or username
        editor.apply();

        // Navigate to homePage
        Intent intent = new Intent(SignUp.this, homePage.class);
        startActivity(intent);
        finish(); // Finish the current SignUp activity to prevent back navigation
    }
}
