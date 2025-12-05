package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "donations")
public class Donation {
    @Id
    private String id;
    private Double amount; // Amount in base currency (USD)
    private String currency; // Display currency for the user (e.g., "USD", "EUR", "GBP")
    private Double displayAmount; // Amount converted to user's preferred currency
    private String baseCurrency; // Base currency for storage (always "USD")
    private String transactionToken;
    private String frequency;
    private String message;
    private boolean isAnonymous;
    private String status;
    private String paymentMethod; // e.g., "card", "upi"
    private String timestamp; // ISO date string for when donation was made

    @DBRef
    private User user;

    @DBRef
    private Shelter shelter;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Double getDisplayAmount() { return displayAmount; }
    public void setDisplayAmount(Double displayAmount) { this.displayAmount = displayAmount; }
    public String getBaseCurrency() { return baseCurrency != null ? baseCurrency : "USD"; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public String getTransactionToken() { return transactionToken; }
    public void setTransactionToken(String transactionToken) { this.transactionToken = transactionToken; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Shelter getShelter() { return shelter; }
    public void setShelter(Shelter shelter) { this.shelter = shelter; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
