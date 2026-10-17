package com.example.afinal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

public class InstructionsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private View step1Indicator, step2Indicator, step3Indicator, step4Indicator;
    private MaterialButton nextButton;
    private MaterialButton skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructions);

        // Set edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.instructionsContainer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        viewPager = findViewById(R.id.instructionsViewPager);

        // Step indicators
        step1Indicator = findViewById(R.id.step1Indicator);
        step2Indicator = findViewById(R.id.step2Indicator);
        step3Indicator = findViewById(R.id.step3Indicator);
        step4Indicator = findViewById(R.id.step4Indicator);

        // Buttons
        nextButton = findViewById(R.id.nextButton);
        skipButton = findViewById(R.id.skipButton);

        // Set up the adapter
        InstructionsPagerAdapter adapter = new InstructionsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Set up page change listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateStepIndicators(position);

                // Update button text on the last page
                if (position == adapter.getItemCount() - 1) {
                    nextButton.setText("Get Started");
                } else {
                    nextButton.setText("Next");
                }
            }
        });

        // Set up button actions
        nextButton.setOnClickListener(v -> {
            int currentPosition = viewPager.getCurrentItem();
            if (currentPosition < adapter.getItemCount() - 1) {
                // Go to next page
                viewPager.setCurrentItem(currentPosition + 1);
            } else {
                // On the last page, finish the tutorial
                finishInstructions();
            }
        });

        skipButton.setOnClickListener(v -> finishInstructions());
    }

    private void updateStepIndicators(int currentStep) {
        // Reset all indicators to inactive
        step1Indicator.setBackgroundResource(R.drawable.inactive_step_indicator);
        step2Indicator.setBackgroundResource(R.drawable.inactive_step_indicator);
        step3Indicator.setBackgroundResource(R.drawable.inactive_step_indicator);
        step4Indicator.setBackgroundResource(R.drawable.inactive_step_indicator);

        // Set active indicator based on current step
        switch (currentStep) {
            case 0:
                step1Indicator.setBackgroundResource(R.drawable.active_step_indicator);
                break;
            case 1:
                step2Indicator.setBackgroundResource(R.drawable.active_step_indicator);
                break;
            case 2:
                step3Indicator.setBackgroundResource(R.drawable.active_step_indicator);
                break;
            case 3:
                step4Indicator.setBackgroundResource(R.drawable.active_step_indicator);
                break;
        }
    }

    private void finishInstructions() {
        // Add any necessary logic to mark instructions as seen
        // For example, set a shared preference flag
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("instructions_seen", true)
                .apply();

        // Close this activity and return to previous screen
        finish();
    }
}