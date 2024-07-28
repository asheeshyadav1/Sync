package com.cmpt213.finalProject.SYNC.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cmpt213.finalProject.SYNC.models.ChatMessage;
import com.cmpt213.finalProject.SYNC.models.UserModel;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndReceiverOrReceiverAndSender(UserModel sender1, UserModel receiver1, UserModel sender2, UserModel receiver2);
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampAsc(Long senderId, Long receiverId);
}

