package com.petadoption.backend.controller;

import com.petadoption.backend.model.Chat;
import com.petadoption.backend.model.Inquiry;
import com.petadoption.backend.model.User;
import com.petadoption.backend.services.ChatService;
import com.petadoption.backend.services.InquiryService;
import com.petadoption.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class WebSocketChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private UserService userService;

    @MessageMapping("/chat/{chatId}/sendMessage")
    public void sendMessage(@DestinationVariable String chatId, @Payload ChatMessage message, @Header("simpSessionId") String sessionId) {
        try {
            Optional<Chat> chatOpt = chatService.getChatById(chatId);
            if (!chatOpt.isPresent()) {
                throw new RuntimeException("Chat not found");
            }

            Inquiry inquiry = new Inquiry();
            inquiry.setMessage(message.getMessage());
            inquiry.setChat(chatOpt.get());
            inquiry.setSenderType(message.getSenderType());
            inquiry.setTimestamp(new Date());

            // Set user if sender is user
            if ("user".equals(message.getSenderType()) && message.getUserId() != null) {
                Optional<User> userOpt = userService.getUserById(message.getUserId());
                userOpt.ifPresent(inquiry::setUser);
            }

            Inquiry savedInquiry = inquiryService.addInquiry(inquiry);

            // Create DTO for WebSocket transmission to avoid lazy loading issues
            ChatMessageDTO messageDTO = new ChatMessageDTO();
            messageDTO.setId(savedInquiry.getId());
            messageDTO.setMessage(savedInquiry.getMessage());
            messageDTO.setSenderType(savedInquiry.getSenderType());
            messageDTO.setTimestamp(savedInquiry.getTimestamp());
            messageDTO.setChatId(chatId);

            // Set user info if available (without lazy collections)
            if (savedInquiry.getUser() != null) {
                messageDTO.setUserId(savedInquiry.getUser().getId());
                messageDTO.setUserName(savedInquiry.getUser().getName());
                messageDTO.setUserEmail(savedInquiry.getUser().getEmail());
            }

            // Send to chat topic for all subscribers
            messagingTemplate.convertAndSend("/topic/chat/" + chatId, messageDTO);

            // Send to admin topic for real-time admin updates
            messagingTemplate.convertAndSend("/topic/admin/chat/" + chatId, messageDTO);

            // Send to specific user if it's an admin reply
            if ("admin".equals(message.getSenderType()) && message.getUserId() != null) {
                messagingTemplate.convertAndSendToUser(message.getUserId().toString(), "/queue/notifications", messageDTO);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    @MessageMapping("/chat/{chatId}/join")
    @SendTo("/topic/chat/{chatId}")
    public ChatNotification joinChat(@DestinationVariable String chatId, @Payload JoinLeaveMessage message) {
        try {
            Optional<Chat> chatOpt = chatService.getChatById(chatId);
            if (!chatOpt.isPresent()) {
                throw new RuntimeException("Chat not found");
            }

            ChatNotification notification = new ChatNotification();
            notification.setType("JOIN");
            notification.setMessage(message.getUsername() + " joined the chat");
            notification.setChatId(chatId);
            notification.setTimestamp(new Date());

            // Update chat status if needed
            Chat chat = chatOpt.get();
            if ("PENDING".equals(chat.getStatus())) {
                chat.setStatus("ACTIVE");
                chatService.saveChat(chat);
            }

            return notification;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to join chat: " + e.getMessage());
        }
    }

    @MessageMapping("/chat/{chatId}/leave")
    @SendTo("/topic/chat/{chatId}")
    public ChatNotification leaveChat(@DestinationVariable String chatId, @Payload JoinLeaveMessage message) {
        ChatNotification notification = new ChatNotification();
        notification.setType("LEAVE");
        notification.setMessage(message.getUsername() + " left the chat");
        notification.setChatId(chatId);
        notification.setTimestamp(new Date());
        return notification;
    }

    @MessageMapping("/chat/{chatId}/typing")
    @SendTo("/topic/chat/{chatId}/typing")
    public TypingIndicator typing(@DestinationVariable String chatId, @Payload TypingMessage message) {
        TypingIndicator indicator = new TypingIndicator();
        indicator.setUsername(message.getUsername());
        indicator.setTyping(message.isTyping());
        indicator.setChatId(chatId);
        return indicator;
    }

    @SubscribeMapping("/chat/{chatId}")
    public List<ChatMessageDTO> onSubscribe(@DestinationVariable String chatId) {
        // Send chat history when user subscribes
        List<Inquiry> inquiries = inquiryService.getInquiriesByChat(chatId);
        return inquiries.stream().map(inquiry -> {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setId(inquiry.getId());
            dto.setMessage(inquiry.getMessage());
            dto.setSenderType(inquiry.getSenderType());
            dto.setTimestamp(inquiry.getTimestamp());
            dto.setChatId(chatId);

            if (inquiry.getUser() != null) {
                dto.setUserId(inquiry.getUser().getId());
                dto.setUserName(inquiry.getUser().getName());
                dto.setUserEmail(inquiry.getUser().getEmail());
            }

            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @MessageMapping("/chat/{chatId}/markAsRead")
    public void markAsRead(@DestinationVariable String chatId, @Payload MarkAsReadMessage message) {
        // Mark messages as read for the user
        try {
            inquiryService.markMessagesAsRead(chatId, message.getUserId());
            // Notify other participants that messages were read
            messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/read",
                new ReadReceipt(chatId, message.getUserId(), new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DTOs for various message types
    public static class ChatMessage {
        private String message;
        private String senderType; // "user" or "admin"
        private String userId; // optional, for user messages

        public ChatMessage() {}

        public ChatMessage(String message, String senderType, String userId) {
            this.message = message;
            this.senderType = senderType;
            this.userId = userId;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSenderType() { return senderType; }
        public void setSenderType(String senderType) { this.senderType = senderType; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    public static class JoinLeaveMessage {
        private String username;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    public static class TypingMessage {
        private String username;
        private boolean typing;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
    }

    public static class MarkAsReadMessage {
        private String userId;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    public static class ChatNotification {
        private String type; // "JOIN", "LEAVE", "SYSTEM"
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

    public static class TypingIndicator {
        private String username;
        private boolean typing;
        private String chatId;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
    }

    public static class ReadReceipt {
        private String chatId;
        private String userId;
        private Date timestamp;

        public ReadReceipt(String chatId, String userId, Date timestamp) {
            this.chatId = chatId;
            this.userId = userId;
            this.timestamp = timestamp;
        }

        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }

    // DTO for WebSocket messages to avoid lazy loading issues
    public static class ChatMessageDTO {
        private String id;
        private String message;
        private String senderType;
        private Date timestamp;
        private String chatId;
        private String userId;
        private String userName;
        private String userEmail;

        public ChatMessageDTO() {}

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getSenderType() { return senderType; }
        public void setSenderType(String senderType) { this.senderType = senderType; }

        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    }
}
