package com.example.guitarhub.utils;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String nickname;
    private List<String> favoritePosts;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.favoritePosts = new ArrayList<>();
    }

    public User(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
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
    }

    public List<String> getFavoritePosts() {
        return favoritePosts;
    }

    public void setFavoritePosts(List<String> favoritePosts) {
        this.favoritePosts = favoritePosts;
    }
}
