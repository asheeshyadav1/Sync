package com.cmpt213.finalProject.SYNC.models;


import java.time.LocalDateTime;

public class ChatMessageDTO {
    private Long id;
    private String senderLogin;
    private String receiverLogin;
    private String content;
    private LocalDateTime timestamp;

    // Default constructor
    public ChatMessageDTO() {}

    // Parameterized constructor
    public ChatMessageDTO(Long id, String senderLogin, String receiverLogin, String content, LocalDateTime timestamp) {
        this.id = id;
        this.senderLogin = senderLogin;
        this.receiverLogin = receiverLogin;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderLogin() {
        return senderLogin;
    }

    public void setSenderLogin(String senderLogin) {
        this.senderLogin = senderLogin;
    }

    public String getReceiverLogin() {
        return receiverLogin;
    }

    public void setReceiverLogin(String receiverLogin) {
        this.receiverLogin = receiverLogin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "id=" + id +
                ", senderLogin='" + senderLogin + '\'' +
                ", receiverLogin='" + receiverLogin + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
