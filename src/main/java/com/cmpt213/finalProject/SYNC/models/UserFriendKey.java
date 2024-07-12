package com.cmpt213.finalProject.SYNC.models;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserFriendKey implements Serializable {

    private Integer userId;
    private Integer friendId;

    public UserFriendKey() {
    }

    public UserFriendKey(Integer userId, Integer friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFriendKey that = (UserFriendKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(friendId, that.friendId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}
