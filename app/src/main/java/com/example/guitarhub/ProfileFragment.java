package com.example.guitarhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarhub.utils.Post;
import com.example.guitarhub.utils.PostsAdapter;
import com.example.guitarhub.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Fragment that displays the user's profile information.
 * It shows the user's email, username, and provides an option to change their password.
 */
public class ProfileFragment extends Fragment {

    // UI Components for displaying user details
    private TextView tvEmail;
    private TextView tvUsername;
    // Button to navigate to the ChangePasswordActivity
    private Button btnChangePassword;

    // Firebase instances for authentication and database access
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI component references
        tvEmail = view.findViewById(R.id.tv_email);
        tvUsername = view.findViewById(R.id.tv_username);
        btnChangePassword = view.findViewById(R.id.btn_change_password);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Load the current user's data from Firebase
        loadUserProfile();

        // Set up click listener for the change password button
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Fetches the current user's profile information from Firebase Auth and Firestore.
     * Sets the email from Auth and the username from the Firestore "users" collection.
     */
    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Display email from Firebase Auth
            tvEmail.setText(user.getEmail());
            
            // Fetch additional user data (like username) from Firestore
            db.collection("users").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Update UI with the username from the document
                    tvUsername.setText(documentSnapshot.getString("username"));
                }
            });
        }
    }
}
