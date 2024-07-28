package com.cmpt213.finalProject.SYNC.controller;

import com.cmpt213.finalProject.SYNC.models.ChatMessage;
import com.cmpt213.finalProject.SYNC.models.ChatMessageDTO;
import com.cmpt213.finalProject.SYNC.models.UserModel;
import com.cmpt213.finalProject.SYNC.repository.UserRepository;
import com.cmpt213.finalProject.SYNC.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<ChatMessageDTO> getMessages(@RequestParam Integer friendId, HttpSession session) {
        UserModel currentUser = (UserModel) session.getAttribute("session_user");
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        UserModel friend = userRepository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        List<ChatMessage> messages = messageService.getMessages(currentUser, friend);
        List<ChatMessage> opMessages = messageService.getMessages(friend, currentUser);

        // Combine both lists of messages
        List<ChatMessage> allMessages = new ArrayList<>(messages);
        allMessages.addAll(opMessages);

        // Map to ChatMessageDTO
        List<ChatMessageDTO> messageDTOs = allMessages.stream().map(message -> {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setId(message.getId());
            dto.setSenderLogin(message.getSender().getLogin());
            dto.setReceiverLogin(message.getReceiver().getLogin());
            dto.setContent(message.getContent());
            dto.setTimestamp(message.getTimestamp());
            return dto;
        }).collect(Collectors.toList());

        // Optional: Sort by timestamp if order is important
        messageDTOs.sort(Comparator.comparing(ChatMessageDTO::getTimestamp));

        return messageDTOs;
    }

    @PostMapping
    public ChatMessageDTO sendMessage(@RequestBody Map<String, Object> payload, HttpSession session) {
        UserModel currentUser = (UserModel) session.getAttribute("session_user");

        Long receiverId = ((Number) payload.get("id")).longValue();
        String content = (String) payload.get("message");

        UserModel receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage sentMessage = messageService.sendMessage(currentUser, receiver, content);
        ChatMessage sentMessagetoUser = messageService.sendMessage(receiver,currentUser,content);

        // Convert ChatMessage to ChatMessageDTO
        ChatMessageDTO messageDTO = new ChatMessageDTO();
        messageDTO.setId(sentMessage.getId());
        messageDTO.setSenderLogin(sentMessage.getSender().getLogin());
        messageDTO.setReceiverLogin(sentMessage.getReceiver().getLogin());
        messageDTO.setContent(sentMessage.getContent());
        messageDTO.setTimestamp(sentMessage.getTimestamp());

        // Broadcast the message to the /topic/messages topic
        messagingTemplate.convertAndSend("/topic/messages", messageDTO);

        return messageDTO;
    }
}
