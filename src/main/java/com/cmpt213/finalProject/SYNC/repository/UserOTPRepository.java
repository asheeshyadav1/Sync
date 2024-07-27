package com.cmpt213.finalProject.SYNC.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpt213.finalProject.SYNC.models.UserOTP;


public interface UserOTPRepository extends JpaRepository<UserOTP, Integer> {
    public Optional<UserOTP> findByEmail(String email);
    //publ
}
