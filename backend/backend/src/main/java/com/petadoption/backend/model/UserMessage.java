package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "user_messages")
public class UserMessage {
    @Id
    private String id;
    private String content;
    private Date timestamp;
    private Boolean isRead = false;

    @DBRef
    private User sender;

    @DBRef
    private UserChat userChat;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public UserChat getUserChat() { return userChat; }
    public void setUserChat(UserChat userChat) { this.userChat = userChat; }
}