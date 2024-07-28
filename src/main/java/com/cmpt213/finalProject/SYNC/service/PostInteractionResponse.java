package com.cmpt213.finalProject.SYNC.service;

public class PostInteractionResponse {
    private int likes;
    private int dislikes;

    // Constructors, getters, and setters
    public PostInteractionResponse(int likes, int dislikes) {
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
