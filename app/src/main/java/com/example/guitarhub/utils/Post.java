package com.example.guitarhub.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Post {
    private String songTitle;
    private String artist;
    private String genre;
    private String recommendedLevel;
    private Effect gain;
    private Effect treble;
    private Effect bass;
    private Effect middle;
    private String ampName;
    private String ampPosition;
    private int tone;
    private String ownerUid;
    private String ownerNickname;
    private Timestamp createdAt;
    private List<Effect> effects;
    @Exclude
    private String postId;

    public Post() {}

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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
