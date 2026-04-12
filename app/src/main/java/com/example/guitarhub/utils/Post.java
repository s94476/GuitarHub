package com.example.guitarhub.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Post in the GuitarHub application.
 * A post contains details about a guitar setup, including amp settings and effects.
 */
public class Post {
    // Title of the song for which the setup was created
    private String songTitle;
    // Name of the artist who performed the song
    private String artist;
    // Genre of the music (e.g., Rock, Blues, Jazz)
    private String genre;
    // Recommended skill level for this setup (e.g., Beginner, Intermediate, Expert)
    private String recommendedLevel;
    
    // Core amplifier settings represented as Effect objects
    private Effect gain;
    private Effect treble;
    private Effect bass;
    private Effect middle;
    
    // Name of the amplifier being modeled or used
    private String ampName;
    // Position of the amplifier or microphone if applicable (e.g., Bridge, Neck)
    private String ampPosition;
    // Overall tone setting of the amplifier
    private int tone;
    
    // The Firebase UID of the user who created this post
    private String ownerUid;
    // The username/nickname of the user who created this post
    private String ownerNickname;
    // The timestamp when the post was created in Firestore
    private Timestamp createdAt;
    
    // List of additional pedal effects used in the guitar setup
    private List<Effect> effects;
    // List of user UIDs who have liked this specific post
    private List<String> likedBy = new ArrayList<>();
    // List of comments associated with this post
    private List<Comment> comments = new ArrayList<>();
    
    // The document ID of this post in Firestore. Excluded from being written back to Firestore.
    @Exclude
    private String postId;

    /**
     * Default constructor required for Firestore's data mapping (toObject()).
     */
    public Post() {}

    /**
     * Parameterized constructor used when initializing a new Post object before saving it to Firestore.
     */
    public Post(String songTitle, String artist, String genre, String recommendedLevel, Effect gain, Effect treble, Effect bass, Effect middle, String ampName, String ampPosition, int tone, String ownerUid, String ownerNickname, Timestamp createdAt, List<Effect> effects) {
        this.songTitle = songTitle;
        this.artist = artist;
        this.genre = genre;
        this.recommendedLevel = recommendedLevel;
        this.gain = gain;
        this.treble = treble;
        this.bass = bass;
        this.middle = middle;
        this.ampName = ampName;
        this.ampPosition = ampPosition;
        this.tone = tone;
        this.ownerUid = ownerUid;
        this.ownerNickname = ownerNickname;
        this.createdAt = createdAt;
        this.effects = effects;
        this.likedBy = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRecommendedLevel() {
        return recommendedLevel;
    }

    public void setRecommendedLevel(String recommendedLevel) {
        this.recommendedLevel = recommendedLevel;
    }

    public Effect getGain() {
        return gain;
    }

    public void setGain(Effect gain) {
        this.gain = gain;
    }

    public Effect getTreble() {
        return treble;
    }

    public void setTreble(Effect treble) {
        this.treble = treble;
    }

    public Effect getBass() {
        return bass;
    }

    public void setBass(Effect bass) {
        this.bass = bass;
    }

    public Effect getMiddle() {
        return middle;
    }

    public void setMiddle(Effect middle) {
        this.middle = middle;
    }

    public String getAmpName() {
        return ampName;
    }

    public void setAmpName(String ampName) {
        this.ampName = ampName;
    }

    public String getAmpPosition() {
        return ampPosition;
    }

    public void setAmpPosition(String ampPosition) {
        this.ampPosition = ampPosition;
    }

    public int getTone() {
        return tone;
    }

    public void setTone(int tone) {
        this.tone = tone;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getOwnerNickname() {
        return ownerNickname;
    }

    public void setOwnerNickname(String ownerNickname) {
        this.ownerNickname = ownerNickname;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
