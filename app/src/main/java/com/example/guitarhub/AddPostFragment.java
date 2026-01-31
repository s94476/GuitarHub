package com.example.guitarhub;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class AddPostFragment extends Fragment {

    private EditText etSongTitle, etArtist, etGain, etTreble, etBass, etMiddle, etAmpName, etTone;
    private Spinner spRecommendedLevel, spAmpPosition;
    private LinearLayout effectsContainer;
    private List<EditText> effectLevelEditTexts = new ArrayList<>();
    private List<EditText> timingEditTexts = new ArrayList<>();

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
        effectsContainer = view.findViewById(R.id.effects_container);

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

        Button addEffectButton = view.findViewById(R.id.add_effect_button);
        addEffectButton.setOnClickListener(v -> addEffectField());

        Button submitButton = view.findViewById(R.id.submit_post_button);
        submitButton.setOnClickListener(v -> {
            boolean allEffectsValid = true;
            for (EditText effectLevelEditText : effectLevelEditTexts) {
                if (effectLevelEditText.getVisibility() == View.VISIBLE && !validate(effectLevelEditText)) {
                    allEffectsValid = false;
                }
            }

            boolean allTimingsValid = true;
            for (EditText timingEditText : timingEditTexts) {
                if (timingEditText.getVisibility() == View.VISIBLE && !validateTiming(timingEditText)) {
                    allTimingsValid = false;
                }
            }

            if (validate(etGain) && validate(etTreble) && validate(etBass) && validate(etMiddle) && validate(etTone) && allEffectsValid && allTimingsValid) {
                Toast.makeText(getContext(), "Submit button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void addEffectField() {
        final LinearLayout effectWrapper = new LinearLayout(getContext());
        effectWrapper.setOrientation(LinearLayout.VERTICAL);

        LinearLayout effectRow = new LinearLayout(getContext());
        effectRow.setOrientation(LinearLayout.HORIZONTAL);

        EditText effectName = new EditText(getContext());
        effectName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        effectName.setHint("Effect Name");

        EditText effectLevel = new EditText(getContext());
        effectLevel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        effectLevel.setHint("Level: 0/10");
        effectLevel.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        CheckBox timingCb = new CheckBox(getContext());
        timingCb.setText("Timing");

        Button deleteButton = new Button(getContext());
        deleteButton.setText("Delete");

        final EditText timingEditText = new EditText(getContext());
        timingEditText.setHint("Milliseconds (1-1000)");
        timingEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        timingEditText.setVisibility(View.GONE);

        timingCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timingEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        deleteButton.setOnClickListener(v -> {
            effectsContainer.removeView(effectWrapper);
            effectLevelEditTexts.remove(effectLevel);
            timingEditTexts.remove(timingEditText);
        });

        effectRow.addView(effectName);
        effectRow.addView(effectLevel);
        effectRow.addView(timingCb);
        effectRow.addView(deleteButton);

        effectWrapper.addView(effectRow);
        effectWrapper.addView(timingEditText);

        effectsContainer.addView(effectWrapper);
        effectLevelEditTexts.add(effectLevel);
        timingEditTexts.add(timingEditText);
    }

    private boolean validate(EditText editText) {
        String input = editText.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            editText.setError("Field cannot be empty");
            return false;
        }

        try {
            int value = Integer.parseInt(input);
            if (value < 0 || value > 10) {
                editText.setError("Value must be between 0 and 10");
                return false;
            }
        } catch (NumberFormatException e) {
            editText.setError("Invalid number");
            return false;
        }

        return true;
    }

    private boolean validateTiming(EditText editText) {
        String input = editText.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            editText.setError("Field cannot be empty");
            return false;
        }

        try {
            int value = Integer.parseInt(input);
            if (value < 1 || value > 1000) {
                editText.setError("Value must be between 1 and 1000");
                return false;
            }
        } catch (NumberFormatException e) {
            editText.setError("Invalid number");
            return false;
        }

        return true;
    }
}