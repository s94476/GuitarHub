package com.example.guitarhub;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.guitarhub.utils.GeminiManager;
import com.example.guitarhub.utils.Post;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPostFragment extends Fragment {

    private static final String TAG = "AddPostFragment";
    private EditText etSongTitle, etArtist, etGain, etTreble, etBass, etMiddle, etAmpName, etTone;
    private AutoCompleteTextView actvGenre;
    private ChipGroup chipGroupGenres;
    private Spinner spRecommendedLevel, spAmpPosition;
    private LinearLayout effectsContainer;
    private String currentUsername;
    private Button aiButton; // AI button for generating settings

    private List<String> availableGenres = Arrays.asList(
            "Rock", "Jazz", "Blues", "Metal", "Classical", "Pop", "Country", "Folk", "Reggae", "Electronic",
            "Punk", "Grunge", "Soul", "R&B", "Funk", "Disco", "Techno", "House", "Trance", "Dubstep",
            "Ska", "Latin", "Salsa", "Bossa Nova", "Samba", "Bluegrass", "Gospel", "Opera", "Ambient", "Industrial",
            "New Wave", "Synthpop", "Indie", "Alternative", "Psychedelic", "Progressive", "Hardcore", "Emo", "Rap", "Hip Hop",
            "Trap", "Lo-fi", "K-pop", "J-pop", "Shoegaze", "Post-rock", "Math Rock", "Flamenco", "Klezmer", "Surf Rock"
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        etSongTitle = view.findViewById(R.id.et_song_title);
        etArtist = view.findViewById(R.id.et_artist);
        actvGenre = view.findViewById(R.id.actv_genre);
        chipGroupGenres = view.findViewById(R.id.chip_group_genres);
        spRecommendedLevel = view.findViewById(R.id.sp_recommended_level);
        etGain = view.findViewById(R.id.et_gain);
        etTreble = view.findViewById(R.id.et_treble);
        etBass = view.findViewById(R.id.et_bass);
        etMiddle = view.findViewById(R.id.et_middle);
        etAmpName = view.findViewById(R.id.et_amp_name);
        spAmpPosition = view.findViewById(R.id.sp_amp_position);
        etTone = view.findViewById(R.id.et_tone);
        effectsContainer = view.findViewById(R.id.effects_container);

        setupGenreSearch();

        // Recommended Level Spinner
        String[] levels = {"Beginner", "Novice", "Intermediate", "Advanced", "Expert"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levels);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRecommendedLevel.setAdapter(levelAdapter);

        // Amp Position Spinner
        String[] positions = {"Neck", "Middle", "Bridge"};
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, levels); // Reusing levels layout
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAmpPosition.setAdapter(positionAdapter);

        Button addEffectButton = view.findViewById(R.id.add_effect_button);
        addEffectButton.setOnClickListener(v -> addEffectField(null, null));

        Button submitButton = view.findViewById(R.id.submit_post_button);
        submitButton.setOnClickListener(v -> sendPost());

        // Initialize AI button and set click listener
        aiButton = view.findViewById(R.id.ai_button);
        aiButton.setOnClickListener(v -> handleAiHelp());

        fetchCurrentUsername();

        return view;
    }

    private void setupGenreSearch() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, availableGenres);
        actvGenre.setAdapter(adapter);

        actvGenre.setOnItemClickListener((parent, view, position, id) -> {
            String selectedGenre = (String) parent.getItemAtPosition(position);
            setGenreChip(selectedGenre);
            actvGenre.setText("");
        });
    }

    private void setGenreChip(String genre) {
        chipGroupGenres.removeAllViews();
        Chip chip = new Chip(getContext());
        chip.setText(genre);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> chipGroupGenres.removeView(chip));
        chipGroupGenres.addView(chip);
    }

    /**
     * Handles the AI Help button click.
     * Validates input, builds the Gemini prompt, and processes the AI response.
     */
    private void handleAiHelp() {
        String songTitle = etSongTitle.getText().toString().trim();
        String artist = etArtist.getText().toString().trim();
        String ampName = etAmpName.getText().toString().trim();

        // Ensure at least a song title or amp name is provided
        if (TextUtils.isEmpty(songTitle) && TextUtils.isEmpty(ampName)) {
            Toast.makeText(getContext(), "Insufficient details for AI help. Please enter a song title or amplifier name.", Toast.LENGTH_LONG).show();
            return;
        }

        aiButton.setEnabled(false); // Disable button while processing
        Toast.makeText(getContext(), "Generating settings with Gemini...", Toast.LENGTH_SHORT).show();

        // Construct a detailed prompt for Gemini
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Suggest the best guitar settings for the song '").append(songTitle).append("'");
        
        if (!TextUtils.isEmpty(artist)) {
            promptBuilder.append(" by the artist '").append(artist).append("'");
            promptBuilder.append(". IMPORTANT: You MUST provide settings specifically for this artist's version of the song, even if there are more popular versions by other artists.");
        } else {
            promptBuilder.append(". Since no artist is specified, if there are multiple versions of this song by different artists, choose the most popular or well-known version.");
        }

        if (!TextUtils.isEmpty(ampName)) {
            promptBuilder.append(" Assume the user is using a '").append(ampName).append("' amplifier.");
        }

        // Specify the JSON output format
        promptBuilder.append(" Return ONLY a JSON object with these keys (no other text or markdown tags): ");
        promptBuilder.append("artist (the name of the artist for this song), ");
        promptBuilder.append("genre (choose one from: ").append(availableGenres.toString()).append("), ");
        promptBuilder.append("level (one of: Beginner, Novice, Intermediate, Advanced, Expert), ");
        promptBuilder.append("gain (0-10), treble (0-10), bass (0-10), middle (0-10), tone (0-10), ");
        promptBuilder.append("position (one of: Neck, Middle, Bridge), ");
        promptBuilder.append("effects (array of objects with 'name' and 'level' (0-10)).");

        // Send prompt to Gemini via GeminiManager
        GeminiManager.getInstance().sendText(promptBuilder.toString(), getContext(), new GeminiManager.GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                if (!isAdded()) return;
                aiButton.setEnabled(true);
                try {
                    // Clean the response from markdown if necessary
                    String jsonStr = result.trim();
                    if (jsonStr.startsWith("```json")) {
                        jsonStr = jsonStr.substring(7, jsonStr.length() - 3).trim();
                    } else if (jsonStr.startsWith("```")) {
                        jsonStr = jsonStr.substring(3, jsonStr.length() - 3).trim();
                    }
                    
                    JSONObject json = new JSONObject(jsonStr);
                    
                    // Fill Artist if it was empty
                    if (json.has("artist") && TextUtils.isEmpty(etArtist.getText().toString().trim())) {
                        etArtist.setText(json.getString("artist"));
                    }

                    // Set Genre Chip
                    if (json.has("genre")) setGenreChip(json.getString("genre"));
                    
                    // Set Level Spinner
                    if (json.has("level")) {
                        String level = json.getString("level");
                        for (int i = 0; i < spRecommendedLevel.getCount(); i++) {
                            if (spRecommendedLevel.getItemAtPosition(i).toString().equalsIgnoreCase(level)) {
                                spRecommendedLevel.setSelection(i);
                                break;
                            }
                        }
                    }
                    
                    // Set Amplifier Settings
                    if (json.has("gain")) etGain.setText(String.valueOf(json.getInt("gain")));
                    if (json.has("treble")) etTreble.setText(String.valueOf(json.getInt("treble")));
                    if (json.has("bass")) etBass.setText(String.valueOf(json.getInt("bass")));
                    if (json.has("middle")) etMiddle.setText(String.valueOf(json.getInt("middle")));
                    if (json.has("tone")) etTone.setText(String.valueOf(json.getInt("tone")));
                    
                    // Set Position Spinner
                    if (json.has("position")) {
                        String pos = json.getString("position");
                        for (int i = 0; i < spAmpPosition.getCount(); i++) {
                            if (spAmpPosition.getItemAtPosition(i).toString().equalsIgnoreCase(pos)) {
                                spAmpPosition.setSelection(i);
                                break;
                            }
                        }
                    }
                    
                    // Clear and set Suggested Effects
                    if (json.has("effects")) {
                        effectsContainer.removeAllViews();
                        JSONArray effects = json.getJSONArray("effects");
                        for (int i = 0; i < effects.length(); i++) {
                            JSONObject effect = effects.getJSONObject(i);
                            addEffectField(effect.getString("name"), String.valueOf(effect.getInt("level")));
                        }
                    }
                    
                    Toast.makeText(getContext(), "Details filled successfully!", Toast.LENGTH_SHORT).show();
                    
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse AI response: " + result, e);
                    Toast.makeText(getContext(), "Error parsing AI response.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable error) {
                if (!isAdded()) return;
                aiButton.setEnabled(true);
                Log.e(TAG, "Gemini error", error);
                Toast.makeText(getContext(), "AI Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        chipGroupGenres.removeAllViews();
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

        if (chipGroupGenres.getChildCount() == 0) {
            Toast.makeText(getContext(), "Please select a genre", Toast.LENGTH_SHORT).show();
            return null;
        }

        String songTitle = etSongTitle.getText().toString().trim();
        String artist = etArtist.getText().toString().trim();
        
        Chip chip = (Chip) chipGroupGenres.getChildAt(0);
        String genre = chip.getText().toString();

        String recommendedLevel = spRecommendedLevel.getSelectedItem().toString();
        String ampName = etAmpName.getText().toString().trim();
        String ampPosition = spAmpPosition.getSelectedItem().toString();

        int tone = Integer.parseInt(etTone.getText().toString().trim());
        Effect gain = new Effect("gain", Integer.parseInt(etGain.getText().toString().trim()));
        Effect treble = new Effect("treble", Integer.parseInt(etTreble.getText().toString().trim()));
        Effect bass = new Effect("bass", Integer.parseInt(etBass.getText().toString().trim()));
        Effect middle = new Effect("middle", Integer.parseInt(etMiddle.getText().toString().trim()));

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

            Effect effect = new Effect(effectName, effectLevel);
            effects.add(effect);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String ownerUid = "";
        String ownerNickname = "";
        if (user != null) {
            ownerUid = user.getUid();
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

        return new Post(songTitle, artist, genre, recommendedLevel, gain, treble, bass, middle, ampName, ampPosition, tone, ownerUid, ownerNickname, createdAt, effects);
    }

    /**
     * Dynamically adds an effect input field to the container.
     * @param initialName Optional preset name for the effect (used by AI).
     * @param initialLevel Optional preset level for the effect (used by AI).
     */
    private void addEffectField(String initialName, String initialLevel) {
        final LinearLayout effectWrapper = new LinearLayout(getContext());
        effectWrapper.setOrientation(LinearLayout.VERTICAL);

        LinearLayout effectRow = new LinearLayout(getContext());
        effectRow.setOrientation(LinearLayout.HORIZONTAL);

        EditText effectName = new EditText(getContext());
        effectName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        effectName.setHint("Effect Name");
        if (initialName != null) effectName.setText(initialName);

        EditText effectLevel = new EditText(getContext());
        effectLevel.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        effectLevel.setHint("Level: 0/10");
        effectLevel.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (initialLevel != null) effectLevel.setText(initialLevel);

        Button deleteButton = new Button(getContext());
        deleteButton.setText("Delete");

        deleteButton.setOnClickListener(v -> {
            effectsContainer.removeView(effectWrapper);
        });

        effectRow.addView(effectName);
        effectRow.addView(effectLevel);
        effectRow.addView(deleteButton);

        effectWrapper.addView(effectRow);

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
}
