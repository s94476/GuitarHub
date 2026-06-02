package com.example.guitarhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnboardingActivity extends AppCompatActivity {

    private static final String TAG = "OnboardingActivity";
    private Spinner spinnerGuitarLevel;
    private AutoCompleteTextView actvMusicSearch;
    private ChipGroup chipGroupMusicTaste;
    private Button btnFinishOnboarding;

    private List<String> availableGenres = Arrays.asList(
            "Rock", "Jazz", "Blues", "Metal", "Classical", "Pop", "Country", "Folk", "Reggae", "Electronic",
            "Punk", "Grunge", "Soul", "R&B", "Funk", "Disco", "Techno", "House", "Trance", "Dubstep",
            "Ska", "Latin", "Salsa", "Bossa Nova", "Samba", "Bluegrass", "Gospel", "Opera", "Ambient", "Industrial",
            "New Wave", "Synthpop", "Indie", "Alternative", "Psychedelic", "Progressive", "Hardcore", "Emo", "Rap", "Hip Hop",
            "Trap", "Lo-fi", "K-pop", "J-pop", "Shoegaze", "Post-rock", "Math Rock", "Flamenco", "Klezmer", "Surf Rock"
    );

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
        actvMusicSearch = findViewById(R.id.actv_music_search);
        chipGroupMusicTaste = findViewById(R.id.chip_group_music_taste);
        btnFinishOnboarding = findViewById(R.id.btn_finish_onboarding);

        setupSpinner();
        setupMusicSearch();

        btnFinishOnboarding.setOnClickListener(v -> {
            finishOnboarding();
        });
    }

    private void setupSpinner() {
        String[] levels = new String[]{"Beginner", "Novice", "Intermediate", "Advanced", "Expert"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, levels);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGuitarLevel.setAdapter(adapter);
    }

    private void setupMusicSearch() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.autocomplete_dropdown_item, availableGenres);
        actvMusicSearch.setAdapter(adapter);

        actvMusicSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGenre = (String) parent.getItemAtPosition(position);
            addChipToGroup(selectedGenre);
            actvMusicSearch.setText("");
        });
    }

    private void addChipToGroup(String genre) {
        // Check if chip already exists
        for (int i = 0; i < chipGroupMusicTaste.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupMusicTaste.getChildAt(i);
            if (chip.getText().toString().equalsIgnoreCase(genre)) {
                return;
            }
        }

        Chip chip = new Chip(this);
        chip.setText(genre);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> chipGroupMusicTaste.removeView(chip));
        chipGroupMusicTaste.addView(chip);
    }

    private void finishOnboarding() {
        String selectedLevel = spinnerGuitarLevel.getSelectedItem().toString();

        List<String> selectedGenres = new ArrayList<>();
        for (int i = 0; i < chipGroupMusicTaste.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupMusicTaste.getChildAt(i);
            selectedGenres.add(chip.getText().toString());
        }

        if (selectedGenres.isEmpty()) {
            Toast.makeText(this, "Please select at least one music taste preference", Toast.LENGTH_SHORT).show();
            return;
        }

        saveOnboardingData(selectedLevel, selectedGenres);
    }

    private void saveOnboardingData(String level, List<String> genres) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Error: User not signed in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("guitarLevel", level);
        data.put("musicTastes", genres);
        data.put("onboardingComplete", true);

        FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Onboarding data saved successfully");
                    Toast.makeText(OnboardingActivity.this, "Setup complete!", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(OnboardingActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving onboarding data", e);
                    Toast.makeText(OnboardingActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
