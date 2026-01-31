package com.example.guitarhub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddPostFragment extends Fragment {

    private EditText etSongTitle, etArtist, etGain, etTreble, etBass, etMiddle, etAmpName, etTone;
    private Spinner spRecommendedLevel, spAmpPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        etSongTitle = view.findViewById(R.id.et_song_title);
        etArtist = view.findViewById(R.id.et_artist);
        spRecommendedLevel = view.findViewById(R.id.sp_recommended_level);
        etGain = view.findViewById(R.id.et_gain);
        etTreble = view.findViewById(R.id.et_treble);
        etBass = view.findViewById(R.id.et_bass);
        etMiddle = view.findViewById(R.id.et_middle);
        etAmpName = view.findViewById(R.id.et_amp_name);
        spAmpPosition = view.findViewById(R.id.sp_amp_position);
        etTone = view.findViewById(R.id.et_tone);

        // Recommended Level Spinner
        String[] levels = {"Beginner", "Intermediate", "Advanced"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRecommendedLevel.setAdapter(levelAdapter);

        // Amp Position Spinner
        String[] positions = {"Neck", "Middle", "Bridge"};
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, positions);
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAmpPosition.setAdapter(positionAdapter);

        Button submitButton = view.findViewById(R.id.submit_post_button);

        submitButton.setOnClickListener(v -> {
            // Do nothing for now
            Toast.makeText(getContext(), "Submit button clicked", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}