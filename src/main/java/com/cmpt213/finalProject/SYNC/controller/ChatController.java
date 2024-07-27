// package com.cmpt213.finalProject.SYNC.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Controller;

// import com.cmpt213.finalProject.SYNC.models.ChatMessage;
// import com.cmpt213.finalProject.SYNC.models.ChatMessageKey;
// import com.cmpt213.finalProject.SYNC.models.UserModel;
// import com.cmpt213.finalProject.SYNC.service.ChatMessageService;

// import java.time.LocalDateTime;

// @Controller
// public class ChatController {

//     private final SimpMessagingTemplate messagingTemplate;
//     private final ChatMessageService chatMessageService;

//     @Autowired
//     public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService) {
//         this.messagingTemplate = messagingTemplate;
//         this.chatMessageService = chatMessageService;
//     }

//     @MessageMapping("/chat")
//     public void send(ChatMessage message) {
//         // Set the timestamp
//         message.setTimestamp(LocalDateTime.now());

//         // Assuming you have a method to fetch UserModel by ID
//         UserModel sender = chatMessageService.getUserById(message.getId().getSenderId());
//         UserModel receiver = chatMessageService.getUserById(message.getId().getReceiverId());
//         message.setSender(sender);
//         message.setReceiver(receiver);

//         // Save the message to the database
//         chatMessageService.saveMessage(message);

//         // Send the message via WebSocket
//         messagingTemplate.convertAndSendToUser(receiver.getId().toString(), "/queue/messages", message);
//     }
// }