// package com.cmpt213.finalProject.SYNC.controller;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;

// import com.cmpt213.finalProject.SYNC.models.ChatMessage;
// import com.cmpt213.finalProject.SYNC.models.ChatMessageKey;
// import com.cmpt213.finalProject.SYNC.models.UserFriendKey;
// import com.cmpt213.finalProject.SYNC.models.UserModel;
// import com.cmpt213.finalProject.SYNC.repository.UserRepository;
// import com.cmpt213.finalProject.SYNC.service.ChatMessageService;

// import jakarta.servlet.http.HttpSession;

// @Controller
// public class MessageController {

//     @Autowired
//     private ChatMessageService chatMessageService;

//     @Autowired
//     private UserRepository userRepository;

//     @PostMapping("/api/messages")
//     @ResponseBody
//     public ChatMessage sendMessage(@RequestParam Integer senderId, @RequestParam Integer receiverId, @RequestBody String content) {
//         ChatMessage message = new ChatMessage();
//         ChatMessageKey key = new ChatMessageKey();
//         key.setSenderId(senderId);
//         key.setReceiverId(receiverId);
//         message.setId(key);
//         message.setContent(content);
//         message.setTimestamp(LocalDateTime.now());

//         // Assuming you have a method to fetch UserModel by ID
//         UserModel sender = chatMessageService.getUserById(senderId);
//         UserModel receiver = chatMessageService.getUserById(receiverId);
//         message.setSender(sender);
//         message.setReceiver(receiver);

//         return chatMessageService.saveMessage(message);
//     }

//     @GetMapping("/api/messages/{senderId}/{receiverId}")
//     @ResponseBody
//     public List<ChatMessage> getMessages(@PathVariable("senderId") Integer senderId, @PathVariable("receiverId") Integer receiverId) {
//         return chatMessageService.getMessagesBetweenUsers(senderId, receiverId);
//     }

    
// }