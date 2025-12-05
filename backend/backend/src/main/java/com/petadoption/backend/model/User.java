package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String role; // Default role
    private String country; // User's country for currency display
    private String preferredCurrency; // User's preferred currency (e.g., "USD", "EUR", "GBP")

    @DBRef
    private List<Donation> donations;

    @DBRef
    private List<Inquiry> inquiries;

    // Getters, setters, and constructors
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<Donation> getDonations() { return donations; }
    public void setDonations(List<Donation> donations) { this.donations = donations; }
    public List<Inquiry> getInquiries() { return inquiries; }
    public void setInquiries(List<Inquiry> inquiries) { this.inquiries = inquiries; }
    public String getRole() {
        return role != null ? role : "USER";
    }
    public void setRole(String role) { this.role = role; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPreferredCurrency() { return preferredCurrency; }
    public void setPreferredCurrency(String preferredCurrency) { this.preferredCurrency = preferredCurrency; }
}
