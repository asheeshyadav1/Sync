package com.cmpt213.finalProject.SYNC.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;

@Entity
public class UserFriend {

    @EmbeddedId
    private UserFriendKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne
    @MapsId("friendId")
    @JoinColumn(name = "friend_id")
    private UserModel friend;

    // Getters and setters
    public UserFriendKey getId() {
        return id;
    }

    public void setId(UserFriendKey id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public UserModel getFriend() {
        return friend;
    }

    public void setFriend(UserModel friend) {
        this.friend = friend;
    }
}