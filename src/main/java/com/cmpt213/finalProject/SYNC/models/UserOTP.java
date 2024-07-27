package com.cmpt213.finalProject.SYNC.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_otps")
public class UserOTP{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    // @Column(nullable = false)
    // private LocalDateTime expiryTime;

    // @Column(nullable = false, updatable = false)
    // private LocalDateTime createdAt;

    public UserOTP(){}

    public UserOTP(String email, String otp){
        this.email = email;
        this.otp = otp;
        // this.expiryTime = expiryTime;
        // this.createdAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    // public LocalDateTime getExpiryTime() {
    //     return expiryTime;
    // }

    // public void setExpiryTime(LocalDateTime expiryTime) {
    //     this.expiryTime = expiryTime;
    // }

    // public LocalDateTime getCreatedAt() {
    //     return createdAt;
    // }

    // public void setCreatedAt(LocalDateTime createdAt) {
    //     this.createdAt = createdAt;
    // }

    // @Override
    // public String toString() {
    //     return "UserOTP [id=" + id + ", email=" + email + ", otp=" + otp + ", expiryTime=" + expiryTime + "]";
    // }

   



}
