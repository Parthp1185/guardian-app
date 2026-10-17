package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class share extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sh), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Method to share the app link
    public void shareApp(View view) {
        String appLink = "https://play.google.com/store/apps/details?id=com.example.afinal";  // Replace with your app's Play Store link
        String shareMessage = "Hey! Check out this amazing app: " + appLink;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}