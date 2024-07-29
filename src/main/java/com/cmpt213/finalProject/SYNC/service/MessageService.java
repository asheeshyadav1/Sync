package com.cmpt213.finalProject.SYNC.service;

import com.cmpt213.finalProject.SYNC.models.ChatMessage;
import com.cmpt213.finalProject.SYNC.models.UserModel;
import com.cmpt213.finalProject.SYNC.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getMessages(UserModel sender, UserModel receiver) {
        return chatMessageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(sender.getId().longValue(), receiver.getId().longValue());
    }

    public ChatMessage sendMessage(UserModel sender, UserModel receiver, String content) {
        ChatMessage message = new ChatMessage(sender, receiver, content, LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesBetweenUsers(UserModel user1, UserModel user2) {
        return chatMessageRepository.findBySenderAndReceiverOrReceiverAndSender(user1, user2, user2, user1);
    }
}
