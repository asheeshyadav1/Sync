package com.cmpt213.finalProject.SYNC.repository;

import com.cmpt213.finalProject.SYNC.models.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<UserModel, Integer>{
    Optional<UserModel> findByLoginAndPassword(String login, String password);
    Optional<UserModel> findByLogin(String login);
    List<UserModel> findByLoginStartingWith(String prefix);
    Optional<UserModel> findById(Long id);
    List<UserModel> findByEmail(String email);
}
