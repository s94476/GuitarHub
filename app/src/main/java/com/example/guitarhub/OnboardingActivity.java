package com.example.guitarhub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private Spinner spinnerGuitarLevel;
    private ChipGroup chipGroupMusicTaste;
    private Button btnFinishOnboarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerGuitarLevel = findViewById(R.id.spinner_guitar_level);
        chipGroupMusicTaste = findViewById(R.id.chip_group_music_taste);
        btnFinishOnboarding = findViewById(R.id.btn_finish_onboarding);

        setupSpinner();
        
        btnFinishOnboarding.setOnClickListener(v -> {
            finishOnboarding();
        });
    }

    private void setupSpinner() {
        String[] levels = new String[]{"Beginner", "Novice", "Intermediate", "Advanced", "Expert"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels);
        spinnerGuitarLevel.setAdapter(adapter);
    }

    private void finishOnboarding() {
        String selectedLevel = spinnerGuitarLevel.getSelectedItem().toString();
        
        List<String> selectedGenres = new ArrayList<>();
        for (int i = 0; i < chipGroupMusicTaste.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupMusicTaste.getChildAt(i);
            if (chip.isChecked()) {
                selectedGenres.add(chip.getText().toString());
            }
        }

        if (selectedGenres.isEmpty()) {
            Toast.makeText(this, "Please select at least one music taste preference", Toast.LENGTH_SHORT).show();
            return;
        }

        // Here you would typically save these preferences to Firestore or SharedPreferences
        // For now, we'll just navigate to the main activity
        
        Toast.makeText(this, "Setup complete! Level: " + selectedLevel + ", Genres: " + selectedGenres, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(OnboardingActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
