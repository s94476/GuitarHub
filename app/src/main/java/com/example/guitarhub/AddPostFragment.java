package com.example.guitarhub;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.example.guitarhub.utils.Effect;
import com.example.guitarhub.utils.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class  AddPostFragment extends Fragment {

    private static final String TAG = "AddPostFragment";
    private EditText etSongTitle, etArtist, etGain, etTreble, etBass, etMiddle, etAmpName, etTone;
    private Spinner spRecommendedLevel, spAmpPosition;
    private LinearLayout effectsContainer;
    private String currentUsername;

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
        String[] levels = {"Beginner", "Novice", "Intermediate", "Advanced", "Expert"};
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
        submitButton.setOnClickListener(v -> sendPost());

        fetchCurrentUsername();

        return view;
    }

    private void fetchCurrentUsername() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            currentUsername = documentSnapshot.getString("username");
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching username", e));
        }
    }

    private void sendPost() {
        Log.d(TAG, "sendPost: start");
        Post post = createPost();

        if (post == null) {
            Log.d(TAG, "sendPost: post creation failed, validation error.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "Post saved successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(getContext(), "Error saving post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
        Log.d(TAG, "sendPost: done");
    }

    private void clearFields() {
        etSongTitle.setText("");
        etArtist.setText("");
        spRecommendedLevel.setSelection(0);
        etGain.setText("");
        etTreble.setText("");
        etBass.setText("");
        etMiddle.setText("");
        etAmpName.setText("");
        spAmpPosition.setSelection(0);
        etTone.setText("");
        effectsContainer.removeAllViews();
    }

    private Post createPost() {
        if (!validate(etSongTitle, false) || !validate(etArtist, false) || !validate(etGain, true) || !validate(etTreble, true) || !validate(etBass, true) || !validate(etMiddle, true) || !validate(etTone, true)) {
            return null;
        }

        String songTitle = etSongTitle.getText().toString().trim();
        String artist = etArtist.getText().toString().trim();
        String recommendedLevel = spRecommendedLevel.getSelectedItem().toString();
        String ampName = etAmpName.getText().toString().trim();
        String ampPosition = spAmpPosition.getSelectedItem().toString();

        int tone = Integer.parseInt(etTone.getText().toString().trim());
        Effect gain = new Effect("gain", Integer.parseInt(etGain.getText().toString().trim()), 0);
        Effect treble = new Effect("treble", Integer.parseInt(etTreble.getText().toString().trim()), 0);
        Effect bass = new Effect("bass", Integer.parseInt(etBass.getText().toString().trim()), 0);
        Effect middle = new Effect("middle", Integer.parseInt(etMiddle.getText().toString().trim()), 0);

        List<Effect> effects = new ArrayList<>();
        for (int i = 0; i < effectsContainer.getChildCount(); i++) {
            LinearLayout effectWrapper = (LinearLayout) effectsContainer.getChildAt(i);
            LinearLayout effectRow = (LinearLayout) effectWrapper.getChildAt(0);

            EditText effectNameEt = (EditText) effectRow.getChildAt(0);
            if (!validate(effectNameEt, false)) return null;
            String effectName = effectNameEt.getText().toString().trim();

            EditText effectLevelEt = (EditText) effectRow.getChildAt(1);
            if (!validate(effectLevelEt, true)) return null;
            int effectLevel = Integer.parseInt(effectLevelEt.getText().toString().trim());

            CheckBox timingCb = (CheckBox) effectRow.getChildAt(2);
            EditText timingEt = (EditText) effectWrapper.getChildAt(1);

            Effect effect = new Effect(effectName, effectLevel, 0);
            if (timingCb.isChecked()) {
                if (!validateTiming(timingEt)) return null;
                int timingMs = Integer.parseInt(timingEt.getText().toString().trim());
                effect.setTimingMs(timingMs);
            }
            effects.add(effect);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String ownerUid = "";
        String ownerNickname = "";
        if (user != null) {
            ownerUid = user.getUid();
            // Try Firestore username first, then Display Name, then Email
            if (!TextUtils.isEmpty(currentUsername)) {
                ownerNickname = currentUsername;
            } else if (!TextUtils.isEmpty(user.getDisplayName())) {
                ownerNickname = user.getDisplayName();
            } else {
                ownerNickname = user.getEmail();
            }
        } else {
            Toast.makeText(getContext(), "You must be logged in to post.", Toast.LENGTH_SHORT).show();
            return null;
        }

        Timestamp createdAt = Timestamp.now();

        return new Post(songTitle, artist, recommendedLevel, gain, treble, bass, middle, ampName, ampPosition, tone, ownerUid, ownerNickname, createdAt, effects);
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
        });

        effectRow.addView(effectName);
        effectRow.addView(effectLevel);
        effectRow.addView(timingCb);
        effectRow.addView(deleteButton);

        effectWrapper.addView(effectRow);
        effectWrapper.addView(timingEditText);

        effectsContainer.addView(effectWrapper);
    }

    private boolean validate(EditText editText, boolean isNumeric) {
        String input = editText.getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            editText.setError("Field cannot be empty");
            return false;
        }

        if (isNumeric) {
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
