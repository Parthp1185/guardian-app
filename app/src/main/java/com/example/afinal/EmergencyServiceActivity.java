package com.example.afinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class EmergencyServiceActivity extends AppCompatActivity {

    private RecyclerView rvEmergencyContacts;
    private List<EmergencyContact> contactsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize recycler view
        rvEmergencyContacts = findViewById(R.id.rvEmergencyContacts);
        rvEmergencyContacts.setLayoutManager(new LinearLayoutManager(this));
        rvEmergencyContacts.setHasFixedSize(true);

        // Prepare data
        prepareEmergencyContacts();

        // Setup adapter
        EmergencyContactAdapter adapter = new EmergencyContactAdapter(this, contactsList);
        rvEmergencyContacts.setAdapter(adapter);

        // Setup FAB for quick emergency call
        FloatingActionButton fabEmergencyCall = findViewById(R.id.fabEmergencyCall);
        fabEmergencyCall.setOnClickListener(v -> {
            // Call national emergency number
            makeQuickEmergencyCall();
        });
    }

    private void prepareEmergencyContacts() {
        contactsList = new ArrayList<>();

        // High priority emergency contacts (will be highlighted)
        contactsList.add(new EmergencyContact("National Emergency Number", "112", true));
        contactsList.add(new EmergencyContact("Police", "100", true));
        contactsList.add(new EmergencyContact("Ambulance", "102", true));
        contactsList.add(new EmergencyContact("Fire Brigade", "101", true));
        contactsList.add(new EmergencyContact("Disaster Management", "108", true));

        // Other emergency contacts
        contactsList.add(new EmergencyContact("Women Helpline", "1091", false));
        contactsList.add(new EmergencyContact("Child Helpline", "1098", false));
        contactsList.add(new EmergencyContact("Senior Citizen Helpline", "14567", false));
        contactsList.add(new EmergencyContact("Traffic Helpline", "1033", false));
        contactsList.add(new EmergencyContact("Railway Enquiry", "139", false));
        contactsList.add(new EmergencyContact("Cyber Crime Helpline", "1930", false));
        contactsList.add(new EmergencyContact("Tourist Helpline", "1363", false));
        contactsList.add(new EmergencyContact("Anti-Poison Helpline", "1066", false));
        contactsList.add(new EmergencyContact("Gas Leakage (LPG)", "1906", false));
        contactsList.add(new EmergencyContact("Electricity Helpline", "1912", false));
    }

    private void makeQuickEmergencyCall() {
        // Get the national emergency number (usually 112)
        String emergencyNumber = "112";
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + emergencyNumber));

        try {
            startActivity(callIntent);
        } catch (Exception e) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Unable to make emergency call", Snackbar.LENGTH_LONG).show();
        }
    }
}