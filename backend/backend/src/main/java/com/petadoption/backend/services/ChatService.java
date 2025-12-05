package com.petadoption.backend.services;

import com.petadoption.backend.model.Chat;
import com.petadoption.backend.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private PetService petService; // Added to fetch Pet

    @Autowired
    private UserService userService; // Added to fetch User

    public Chat startChat(String petId, String userId) {
        System.out.println("ChatService.startChat called with petId=" + petId + ", userId=" + userId);

        Chat chat = new Chat();
        chat.setStartedAt(new java.util.Date());
        chat.setStatus("ACTIVE");

        // Fetch Pet and User by ID and set them
        try {
            var petOpt = petService.getPetById(petId);
            if (petOpt.isEmpty()) {
                System.out.println("Pet not found with ID: " + petId);
                throw new RuntimeException("Pet not found");
            }
            chat.setPet(petOpt.get());
            System.out.println("Pet found: " + petOpt.get().getName());

            var userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                System.out.println("User not found with ID: " + userId);
                throw new RuntimeException("User not found");
            }
            chat.setUser(userOpt.get());
            System.out.println("User found: " + userOpt.get().getName());

            var savedChat = chatRepository.save(chat);
            System.out.println("Chat created with ID: " + savedChat.getId());
            return savedChat;
        } catch (Exception e) {
            System.out.println("Error in startChat: " + e.getMessage());
            throw e;
        }
    }

    public Chat startUserAdminChat(String userId) {
        System.out.println("ChatService.startUserAdminChat called with userId=" + userId);

        Chat chat = new Chat();
        chat.setStartedAt(new java.util.Date());
        chat.setStatus("ACTIVE");

        // Fetch User by ID and set them
        try {
            var userOpt = userService.getUserById(userId);
            if (userOpt.isEmpty()) {
                System.out.println("User not found with ID: " + userId);
                throw new RuntimeException("User not found");
            }
            chat.setUser(userOpt.get());
            System.out.println("User found: " + userOpt.get().getName());

            var savedChat = chatRepository.save(chat);
            System.out.println("User-Admin chat created with ID: " + savedChat.getId());
            return savedChat;
        } catch (Exception e) {
            System.out.println("Error in startUserAdminChat: " + e.getMessage());
            throw e;
        }
    }

    public Optional<Chat> getChatById(String id) {
        return chatRepository.findById(id);
    }

    public Chat endChat(String id) {
        Optional<Chat> chatOpt = chatRepository.findById(id);
        if (chatOpt.isPresent()) {
            Chat chat = chatOpt.get();
            chat.setStatus("CLOSED");
            chat.setEndedAt(new java.util.Date());
            return chatRepository.save(chat);
        }
        return null;
    }

    public List<Chat> getActiveChatsByPet(String petId) {
        return chatRepository.findByPet_IdAndStatus(petId, "ACTIVE");
    }

    public Chat saveChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    public List<Chat> getChatsByUser(String userId) {
        return chatRepository.findByUser_Id(userId);
    }

    public List<Chat> getActiveChats() {
        return chatRepository.findByStatus("ACTIVE");
    }
}
