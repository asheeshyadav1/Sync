package com.cmpt213.finalProject.SYNC.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cmpt213.finalProject.SYNC.models.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);}