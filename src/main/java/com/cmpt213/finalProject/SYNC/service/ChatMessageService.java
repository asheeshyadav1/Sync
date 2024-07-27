// package com.cmpt213.finalProject.SYNC.service;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.cmpt213.finalProject.SYNC.models.ChatMessage;
// import com.cmpt213.finalProject.SYNC.models.UserModel;
// import com.cmpt213.finalProject.SYNC.repository.MessageRepository;
// import com.cmpt213.finalProject.SYNC.repository.UserRepository;

// @Service
// public class ChatMessageService {

//     @Autowired
//     private MessageRepository chatMessageRepository;

//     @Autowired
//     private UserRepository userRepository;

//     public ChatMessage saveMessage(ChatMessage message) {
//         return chatMessageRepository.save(message);
//     }

//     public List<ChatMessage> getMessagesBetweenUsers(Integer senderId, Integer receiverId) {
//         return chatMessageRepository.findMessagesBetweenUsers(senderId, receiverId);
//     }

//     public UserModel getUserById(Integer userId) {
//         return userRepository.findById(userId).orElse(null);
//     }

//     public void sendMessage(UserModel sender, UserModel receiver, String content) {
//         ChatMessage message = new ChatMessage();
//         message.setSender(sender);
//         message.setReceiver(receiver);
//         message.setContent(content);
//         chatMessageRepository.save(message);
//     }

// }