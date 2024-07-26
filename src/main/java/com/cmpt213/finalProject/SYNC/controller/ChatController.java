package com.cmpt213.finalProject.SYNC.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.cmpt213.finalProject.SYNC.models.Message;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void send(Message message) {
        messagingTemplate.convertAndSendToUser(message.getReceiver().toString(), "/queue/messages", message);
    }
}
