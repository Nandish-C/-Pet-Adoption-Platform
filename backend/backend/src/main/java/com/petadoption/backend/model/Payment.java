package com.petadoption.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String userId;
    private String petId;
    private String adoptionId;
    private Double amount; // Amount in base currency (USD)
    private String currency; // Display currency for the user (e.g., "USD", "EUR", "GBP")
    private Double displayAmount; // Amount converted to user's preferred currency
    private String baseCurrency; // Base currency for storage (always "USD")
    private String status; // e.g., "pending", "succeeded", "failed"
    private String paymentMethod; // e.g., "card", "cod", "upi"
    private String stripePaymentIntentId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructors
    public Payment() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPetId() { return petId; }
    public void setPetId(String petId) { this.petId = petId; }
    public String getAdoptionId() { return adoptionId; }
    public void setAdoptionId(String adoptionId) { this.adoptionId = adoptionId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public void setStripePaymentIntentId(String stripePaymentIntentId) { this.stripePaymentIntentId = stripePaymentIntentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Double getDisplayAmount() { return displayAmount; }
    public void setDisplayAmount(Double displayAmount) { this.displayAmount = displayAmount; }
    public String getBaseCurrency() { return baseCurrency != null ? baseCurrency : "USD"; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
}
