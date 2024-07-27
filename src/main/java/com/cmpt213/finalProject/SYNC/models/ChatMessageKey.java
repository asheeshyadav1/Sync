// package com.cmpt213.finalProject.SYNC.models;

// import java.io.Serializable;
// import java.util.Objects;

// public class ChatMessageKey implements Serializable {
//     private Integer senderId;
//     private Integer receiverId;

//     // Getters, setters, hashCode, and equals methods
//     public Integer getSenderId() {
//         return senderId;
//     }

//     public void setSenderId(Integer senderId) {
//         this.senderId = senderId;
//     }

//     public Integer getReceiverId() {
//         return receiverId;
//     }

//     public void setReceiverId(Integer receiverId) {
//         this.receiverId = receiverId;
//     }

//     @Override
//     public boolean equals(Object o) {
//         if (this == o) return true;
//         if (o == null || getClass() != o.getClass()) return false;
//         ChatMessageKey that = (ChatMessageKey) o;
//         return Objects.equals(senderId, that.senderId) && Objects.equals(receiverId, that.receiverId);
//     }

//     @Override
//     public int hashCode() {
//         return Objects.hash(senderId, receiverId);
//     }
// }
