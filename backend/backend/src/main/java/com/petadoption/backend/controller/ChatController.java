package com.petadoption.backend.controller;

import com.petadoption.backend.model.Chat;
import com.petadoption.backend.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<Chat> startChat(@RequestParam String petId, @RequestParam String userId) {
        Chat chat = chatService.startChat(petId, userId);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable String id) {
        Optional<Chat> chat = chatService.getChatById(id);
        return chat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<Chat> endChat(@PathVariable String id) {
        Chat endedChat = chatService.endChat(id);
        return endedChat != null ? ResponseEntity.ok(endedChat) : ResponseEntity.notFound().build();
    }

    @GetMapping("/active/pet/{petId}")
    public List<Chat> getActiveChatsByPet(@PathVariable String petId) {
        return chatService.getActiveChatsByPet(petId);
    }
}