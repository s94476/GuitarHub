package com.example.guitarhub.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a User in the GuitarHub application.
 * This class is used for mapping Firestore user documents to Java objects.
 */
public class User {

    // Unique identifier for the user (Firebase UID)
    private String id;
    // User's display name
    private String nickname;
    // Alias for nickname, used for consistency across the app
    private String username;
    // User's first name
    private String firstName;
    // User's last name
    private String lastName;
    // User's email address
    private String email;
    // User's skill level on the guitar (e.g., Beginner, Advanced)
    private String guitarLevel;
    // Indicates if the user has completed the onboarding process
    private boolean onboardingComplete;
    // List of music genres or tastes preferred by the user
    private List<String> musicTastes;
    // List of Post IDs that the user has added to their favorites
    private List<String> favoritePosts;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // Initializes favoritePosts to an empty list to avoid null pointer issues
        this.favoritePosts = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.username = nickname;
    }

    public String getUsername() {
        return username != null ? username : nickname;
    }

    public void setUsername(String username) {
        this.username = username;
        this.nickname = username;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGuitarLevel() { return guitarLevel; }
    public void setGuitarLevel(String guitarLevel) { this.guitarLevel = guitarLevel; }

    public boolean isOnboardingComplete() { return onboardingComplete; }
    public void setOnboardingComplete(boolean onboardingComplete) { this.onboardingComplete = onboardingComplete; }

    public List<String> getMusicTastes() { return musicTastes; }
    public void setMusicTastes(List<String> musicTastes) { this.musicTastes = musicTastes; }

    public List<String> getFavoritePosts() {
        return favoritePosts;
    }

    public void setFavoritePosts(List<String> favoritePosts) {
        this.favoritePosts = favoritePosts;
    }
}
