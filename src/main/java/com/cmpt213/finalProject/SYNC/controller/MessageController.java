package com.cmpt213.finalProject.SYNC.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.cmpt213.finalProject.SYNC.models.Message;
import com.cmpt213.finalProject.SYNC.service.MessageService;





@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/api/messages")
    public Message sendMessage(@RequestBody Message message) {
        return messageService.saveMessage(message);
    }

    @GetMapping("/api/messages/{senderId}/{receiverId}")
    public List<Message> getMessages(@PathVariable("senderId") Long senderId, @PathVariable("receiverId") Long receiverId) {
        return messageService.getMessages(senderId, receiverId);
    }

    @GetMapping("/message")
    public String showMessagePage() {
        return "message"; 
    }
}