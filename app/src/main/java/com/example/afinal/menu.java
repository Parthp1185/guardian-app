package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menupage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Navigate to Instructions Page
    public void toInstruction(View view) {
        Intent intent = new Intent(menu.this, InstructionsActivity.class);
        startActivity(intent);
    }

    // Navigate to Registration Page
    public void toRegestration(View view) {
        Intent intent = new Intent(menu.this, RegistrationActivity.class);
        startActivity(intent);
    }

    // Navigate to View Members Page
    public void toViewMember(View view) {
        Intent intent = new Intent(menu.this, ContactsActivity.class);
        startActivity(intent);
    }

    // Navigate to Edit SOS Message Page
    public void toSosmess(View view) {
        Intent intent = new Intent(menu.this, sosmessage.class);
        startActivity(intent);
    }

    // Share App
    public void toShare(View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Download the Guardian App for emergency safety: https://play.google.com/store/apps/details?id=com.example.afinal");
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share Guardian App via"));
    }
}
