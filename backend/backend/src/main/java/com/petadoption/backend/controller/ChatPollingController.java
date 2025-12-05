package com.petadoption.backend.controller;

import com.petadoption.backend.model.Chat;
import com.petadoption.backend.model.Inquiry;
import com.petadoption.backend.services.ChatService;
import com.petadoption.backend.services.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatPollingController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private InquiryService inquiryService;

    @PostMapping("/start")
    public ResponseEntity<Chat> startChat(@RequestParam String petId, @RequestParam String userId) {
        try {
            Chat chat = chatService.startChat(petId, userId);
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/start-general")
    public ResponseEntity<Chat> startGeneralChat(@RequestParam String userId) {
        try {
            // Create a general chat without pet association
            Chat chat = new Chat();
            // For general chat, we don't set user/pet objects, just status and dates
            chat.setStatus("ACTIVE");
            chat.setStartedAt(new Date());

            Chat savedChat = chatService.saveChat(chat);
            return ResponseEntity.ok(savedChat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{chatId}/message")
    public ResponseEntity<Inquiry> sendMessage(@PathVariable String chatId,
                                               @RequestParam String message,
                                               @RequestParam String senderType,
                                               @RequestParam(required = false) String userId) {
        try {
            Optional<Chat> chatOpt = chatService.getChatById(chatId);
            if (!chatOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Inquiry inquiry = new Inquiry();
            inquiry.setMessage(message);
            inquiry.setChat(chatOpt.get());
            inquiry.setSenderType(senderType);
            inquiry.setTimestamp(new Date());

            Inquiry savedInquiry = inquiryService.addInquiry(inquiry);
            return ResponseEntity.ok(savedInquiry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Inquiry>> getMessages(@PathVariable String chatId,
                                                     @RequestParam(required = false) Long since) {
        try {
            List<Inquiry> messages = inquiryService.getInquiriesByChat(chatId);

            // Filter messages since timestamp if provided
            if (since != null) {
                Date sinceTimestamp = new Date(since);
                messages = messages.stream()
                    .filter(msg -> msg.getTimestamp().after(sinceTimestamp))
                    .toList();
            }

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{chatId}/poll")
    public ResponseEntity<List<Inquiry>> pollMessages(@PathVariable String chatId,
                                                      @RequestParam String lastMessageId) {
        try {
            List<Inquiry> messages = inquiryService.getInquiriesByChat(chatId);

            // Return only messages newer than lastMessageId
            messages = messages.stream()
                .filter(msg -> Long.parseLong(msg.getId()) > Long.parseLong(lastMessageId))
                .toList();

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{chatId}/status")
    public ResponseEntity<Chat> updateChatStatus(@PathVariable String chatId,
                                                 @RequestParam String status) {
        try {
            Optional<Chat> chatOpt = chatService.getChatById(chatId);
            if (!chatOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Chat chat = chatOpt.get();
            chat.setStatus(status);

            if ("CLOSED".equals(status)) {
                chat.setEndedAt(new Date());
            }

            Chat savedChat = chatService.saveChat(chat);
            return ResponseEntity.ok(savedChat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Chat>> getUserChats(@PathVariable String userId) {
        try {
            List<Chat> chats = chatService.getChatsByUser(userId);
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Chat>> getActiveChats() {
        try {
            List<Chat> chats = chatService.getActiveChats();
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{chatId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String chatId,
                                           @RequestParam String userId) {
        try {
            inquiryService.markMessagesAsRead(chatId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
