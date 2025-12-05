package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Document(collection = "user_chats")
public class UserChat {
    @Id
    private String id;
    private Date startedAt;
    private Date endedAt;
    private String status;
    private String chatType; // "DIRECT" for user-to-user, "PET_INQUIRY" for pet-related

    @DBRef
    private User initiator; // User who started the chat

    @DBRef
    private User recipient; // User who receives the chat

    @DBRef
    private Pet pet; // Optional: for pet-related chats

    @DBRef
    private List<UserMessage> messages;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getStartedAt() { return startedAt; }
    public void setStartedAt(Date startedAt) { this.startedAt = startedAt; }
    public Date getEndedAt() { return endedAt; }
    public void setEndedAt(Date endedAt) { this.endedAt = endedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getChatType() { return chatType; }
    public void setChatType(String chatType) { this.chatType = chatType; }
    public User getInitiator() { return initiator; }
    public void setInitiator(User initiator) { this.initiator = initiator; }
    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public List<UserMessage> getMessages() { return messages; }
    public void setMessages(List<UserMessage> messages) { this.messages = messages; }
}