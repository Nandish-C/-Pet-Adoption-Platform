package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "inquiries")
public class Inquiry {
    @Id
    private String id;
    private String message;
    private Date timestamp;
    private String senderType; // "user" or "admin"

    @DBRef
    private Pet pet;

    @DBRef
    private User user;

    @DBRef
    private Chat chat;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }
}
