package com.petadoption.backend.services;

import com.petadoption.backend.model.User;
import com.petadoption.backend.model.UserChat;
import com.petadoption.backend.model.UserMessage;
import com.petadoption.backend.repository.UserChatRepository;
import com.petadoption.backend.repository.UserMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserChatService {

    @Autowired
    private UserChatRepository userChatRepository;

    @Autowired
    private UserMessageRepository userMessageRepository;

    @Autowired
    private UserService userService;

    // Start a new direct chat between two users
    public UserChat startDirectChat(String initiatorId, String recipientId) {
        System.out.println("UserChatService.startDirectChat called with initiatorId=" + initiatorId + ", recipientId=" + recipientId);

        // Check if chat already exists
        Optional<UserChat> existingChat = userChatRepository.findDirectChatBetweenUsers(initiatorId, recipientId);
        if (existingChat.isPresent()) {
            System.out.println("Direct chat already exists with ID: " + existingChat.get().getId());
            return existingChat.get();
        }

        UserChat chat = new UserChat();
        chat.setStartedAt(new java.util.Date());
        chat.setStatus("ACTIVE");
        chat.setChatType("DIRECT");

        // Fetch and set users
        try {
            var initiatorOpt = userService.getUserById(initiatorId);
            if (initiatorOpt.isEmpty()) {
                throw new RuntimeException("Initiator user not found");
            }
            chat.setInitiator(initiatorOpt.get());

            var recipientOpt = userService.getUserById(recipientId);
            if (recipientOpt.isEmpty()) {
                throw new RuntimeException("Recipient user not found");
            }
            chat.setRecipient(recipientOpt.get());

            var savedChat = userChatRepository.save(chat);
            System.out.println("New direct chat created with ID: " + savedChat.getId());
            return savedChat;
        } catch (Exception e) {
            System.out.println("Error in startDirectChat: " + e.getMessage());
            throw e;
        }
    }

    // Send a message in a chat
    public UserMessage sendMessage(String chatId, String senderId, String content) {
        Optional<UserChat> chatOpt = userChatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new RuntimeException("Chat not found");
        }

        Optional<User> senderOpt = userService.getUserById(senderId);
        if (senderOpt.isEmpty()) {
            throw new RuntimeException("Sender not found");
        }

        UserMessage message = new UserMessage();
        message.setContent(content);
        message.setTimestamp(new java.util.Date());
        message.setIsRead(false);
        message.setSender(senderOpt.get());
        message.setUserChat(chatOpt.get());

        return userMessageRepository.save(message);
    }

    // Get all messages for a chat
    public List<UserMessage> getChatMessages(String chatId) {
        return userMessageRepository.findByUserChatIdOrderByTimestampAsc(chatId);
    }

    // Get all chats for a user
    public List<UserChat> getUserChats(String userId) {
        return userChatRepository.findChatsByUserId(userId);
    }

    // Get active chats for a user
    public List<UserChat> getActiveUserChats(String userId) {
        return userChatRepository.findActiveChatsByUserId(userId);
    }

    // Mark messages as read
    public void markMessagesAsRead(String chatId, String userId) {
        List<UserMessage> unreadMessages = userMessageRepository.findUnreadMessagesForUser(userId);
        for (UserMessage message : unreadMessages) {
            if (message.getUserChat().getId().equals(chatId)) {
                message.setIsRead(true);
                userMessageRepository.save(message);
            }
        }
    }

    // Get unread message count for a user
    public Long getUnreadMessageCount(String userId) {
        return userMessageRepository.countUnreadMessagesForUser(userId);
    }

    // End a chat
    public UserChat endChat(String chatId) {
        Optional<UserChat> chatOpt = userChatRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            UserChat chat = chatOpt.get();
            chat.setStatus("CLOSED");
            chat.setEndedAt(new java.util.Date());
            return userChatRepository.save(chat);
        }
        return null;
    }

    // Get chat by ID
    public Optional<UserChat> getChatById(String chatId) {
        return userChatRepository.findById(chatId);
    }
}