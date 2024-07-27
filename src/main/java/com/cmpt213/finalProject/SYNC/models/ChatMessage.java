// package com.cmpt213.finalProject.SYNC.models;

// import jakarta.persistence.EmbeddedId;
// import jakarta.persistence.Entity;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.MapsId;
// import jakarta.persistence.JoinColumn;
// import java.time.LocalDateTime;

// @Entity
// public class ChatMessage {

//     @EmbeddedId
//     private ChatMessageKey id;

//     @ManyToOne
//     @MapsId("senderId")
//     @JoinColumn(name = "sender_id")
//     private UserModel sender;

//     @ManyToOne
//     @MapsId("receiverId")
//     @JoinColumn(name = "receiver_id")
//     private UserModel receiver;

//     private String content;
//     private LocalDateTime timestamp;

//     // Getters and setters
//     public ChatMessageKey getId() {
//         return id;
//     }

//     public void setId(ChatMessageKey id) {
//         this.id = id;
//     }

//     public UserModel getSender() {
//         return sender;
//     }

//     public void setSender(UserModel sender) {
//         this.sender = sender;
//     }

//     public UserModel getReceiver() {
//         return receiver;
//     }

//     public void setReceiver(UserModel receiver) {
//         this.receiver = receiver;
//     }

//     public String getContent() {
//         return content;
//     }

//     public void setContent(String content) {
//         this.content = content;
//     }

//     public LocalDateTime getTimestamp() {
//         return timestamp;
//     }

//     public void setTimestamp(LocalDateTime timestamp) {
//         this.timestamp = timestamp;
//     }
// }