package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "adoptions")
public class Adoption {
  @Id
  private String id;
  private String petId;
  private String userId;
  private String paymentId;
  private String status;
  private LocalDateTime createdAt;

  // Getters and Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getPetId() { return petId; }
  public void setPetId(String petId) { this.petId = petId; }
  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }
  public String getPaymentId() { return paymentId; }
  public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}