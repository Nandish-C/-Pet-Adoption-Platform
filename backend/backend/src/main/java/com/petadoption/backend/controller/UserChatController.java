package com.petadoption.backend.controller;

import com.petadoption.backend.model.UserChat;
import com.petadoption.backend.model.UserMessage;
import com.petadoption.backend.services.UserChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-chats")
@CrossOrigin(origins = "http://localhost:4200")
@Controller
public class UserChatController {

    @Autowired
    private UserChatService userChatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Start a new direct chat between two users
    @PostMapping("/start")
    public ResponseEntity<?> startDirectChat(@RequestBody Map<String, String> request) {
        try {
            String initiatorId = request.get("initiatorId");
            String recipientId = request.get("recipientId");

            if (initiatorId == null || recipientId == null) {
                return ResponseEntity.badRequest().body("initiatorId and recipientId are required");
            }

            UserChat chat = userChatService.startDirectChat(initiatorId, recipientId);
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error starting chat: " + e.getMessage());
        }
    }

    // Send a message in a chat
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable String chatId, @RequestBody Map<String, Object> request) {
        try {
            String senderId = request.get("senderId").toString();
            String content = (String) request.get("content");

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message content cannot be empty");
            }

            UserMessage message = userChatService.sendMessage(chatId, senderId, content);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending message: " + e.getMessage());
        }
    }

    // Get all messages for a chat
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable String chatId) {
        try {
            List<UserMessage> messages = userChatService.getChatMessages(chatId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving messages: " + e.getMessage());
        }
    }

    // Get all chats for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserChats(@PathVariable String userId) {
        try {
            List<UserChat> chats = userChatService.getUserChats(userId);
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving chats: " + e.getMessage());
        }
    }

    // Get active chats for a user
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveUserChats(@PathVariable String userId) {
        try {
            List<UserChat> chats = userChatService.getActiveUserChats(userId);
            return ResponseEntity.ok(chats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving active chats: " + e.getMessage());
        }
    }

    // Mark messages as read
    @PutMapping("/{chatId}/read")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable String chatId, @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            userChatService.markMessagesAsRead(chatId, userId);
            return ResponseEntity.ok("Messages marked as read");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking messages as read: " + e.getMessage());
        }
    }

    // Get unread message count for a user
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<?> getUnreadMessageCount(@PathVariable String userId) {
        try {
            Long count = userChatService.getUnreadMessageCount(userId);
            return ResponseEntity.ok(Map.of("unreadCount", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving unread count: " + e.getMessage());
        }
    }

    // End a chat
    @PutMapping("/{chatId}/end")
    public ResponseEntity<?> endChat(@PathVariable String chatId) {
        try {
            UserChat chat = userChatService.endChat(chatId);
            if (chat != null) {
                return ResponseEntity.ok(chat);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error ending chat: " + e.getMessage());
        }
    }

    // Get chat by ID
    @GetMapping("/{chatId}")
    public ResponseEntity<?> getChatById(@PathVariable String chatId) {
        try {
            var chat = userChatService.getChatById(chatId);
            if (chat.isPresent()) {
                return ResponseEntity.ok(chat.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving chat: " + e.getMessage());
        }
    }

    // WebSocket message mappings for real-time user chat

    @MessageMapping("/user-chat/{chatId}/sendMessage")
    public void sendUserChatMessage(@DestinationVariable String chatId, @Payload UserChatMessage message) {
        try {
            // Save the message using the service
            UserMessage savedMessage = userChatService.sendMessage(chatId, message.getSenderId(), message.getContent());

            // Create DTO for WebSocket transmission
            UserChatMessageDTO messageDTO = new UserChatMessageDTO();
            messageDTO.setId(savedMessage.getId());
            messageDTO.setContent(savedMessage.getContent());
            messageDTO.setSenderId(savedMessage.getSender().getId());
            messageDTO.setSenderName(savedMessage.getSender().getName());
            messageDTO.setChatId(chatId);
            messageDTO.setTimestamp(savedMessage.getTimestamp());
            messageDTO.setRead(savedMessage.getIsRead());

            // Send to chat topic for all subscribers
            messagingTemplate.convertAndSend("/topic/user-chat/" + chatId, messageDTO);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send user chat message: " + e.getMessage());
        }
    }

    @MessageMapping("/user-chat/{chatId}/join")
    public void joinUserChat(@DestinationVariable String chatId, @Payload JoinUserChatMessage message) {
        try {
            // Update chat status if needed
            var chatOpt = userChatService.getChatById(chatId);
            if (chatOpt.isPresent()) {
                UserChat chat = chatOpt.get();
                if ("PENDING".equals(chat.getStatus())) {
                    chat.setStatus("ACTIVE");
                    userChatService.endChat(chat.getId()); // This will save the chat
                }
            }

            // Send join notification
            UserChatNotification notification = new UserChatNotification();
            notification.setType("JOIN");
            notification.setMessage(message.getUsername() + " joined the chat");
            notification.setChatId(chatId);
            notification.setTimestamp(new Date());

            messagingTemplate.convertAndSend("/topic/user-chat/" + chatId, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeMapping("/user-chat/{chatId}")
    public List<UserChatMessageDTO> onSubscribeToUserChat(@DestinationVariable String chatId) {
        // Send chat history when user subscribes
        List<UserMessage> messages = userChatService.getChatMessages(chatId);
        return messages.stream().map(message -> {
            UserChatMessageDTO dto = new UserChatMessageDTO();
            dto.setId(message.getId());
            dto.setContent(message.getContent());
            dto.setSenderId(message.getSender().getId());
            dto.setSenderName(message.getSender().getName());
            dto.setChatId(chatId);
            dto.setTimestamp(message.getTimestamp());
            dto.setRead(message.getIsRead());
            return dto;
        }).collect(Collectors.toList());
    }

    // DTOs for WebSocket messages
    public static class UserChatMessage {
        private String senderId;
        private String content;

        public UserChatMessage() {}

        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class JoinUserChatMessage {
        private String username;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    public static class UserChatNotification {
        private String type;
        private String message;
        private String chatId;
        private Date timestamp;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }

    public static class UserChatMessageDTO {
        private String id;
        private String content;
        private String senderId;
        private String senderName;
        private String chatId;
        private Date timestamp;
        private boolean read;

        public UserChatMessageDTO() {}

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSenderId() { return senderId; }
        public void setSenderId(String senderId) { this.senderId = senderId; }
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }
}