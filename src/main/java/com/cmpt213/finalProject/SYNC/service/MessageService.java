// package com.cmpt213.finalProject.SYNC.service;


// import java.time.LocalDateTime;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.cmpt213.finalProject.SYNC.models.Message;
// import com.cmpt213.finalProject.SYNC.repository.MessageRepository;

// @Service
// public class MessageService {
    
//     @Autowired
//     private MessageRepository messageRepository;

//     public Message saveMessage(Message message){
//         message.setTimestamp(LocalDateTime.now());
//         return messageRepository.save(message);
//     }
//     public List<Message> getMessages(Integer senderId, Integer receiverId){
//         return messageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(senderId, receiverId);
//     }
        
// }
