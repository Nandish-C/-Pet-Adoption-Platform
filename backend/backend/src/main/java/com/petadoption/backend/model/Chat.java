package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Document(collection = "chats")
public class Chat {
    @Id
    private String id;
    private Date startedAt;
    private Date endedAt;
    private String status;

    @DBRef
    private Pet pet;

    @DBRef
    private User user;

    @DBRef
    private List<Inquiry> inquiries;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getStartedAt() { return startedAt; }
    public void setStartedAt(Date startedAt) { this.startedAt = startedAt; }
    public Date getEndedAt() { return endedAt; }
    public void setEndedAt(Date endedAt) { this.endedAt = endedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Inquiry> getInquiries() { return inquiries; }
    public void setInquiries(List<Inquiry> inquiries) { this.inquiries = inquiries; }
}