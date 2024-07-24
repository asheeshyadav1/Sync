package com.cmpt213.finalProject.SYNC.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cmpt213.finalProject.SYNC.models.Message;
import com.cmpt213.finalProject.SYNC.repository.MessageRepository;

public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;

    public Message savMessage(Message message){
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }
    public List<Message> getMessages(long senderId, Long receiverIdLong){
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverIdLong);
    }
        
}
