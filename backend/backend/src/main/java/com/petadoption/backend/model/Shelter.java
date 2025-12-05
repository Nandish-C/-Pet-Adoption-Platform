package com.petadoption.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "shelters")
public class Shelter {
    @Id
    private String id;
    private String name;
    private String location; // City, State format
    private String country; // Shelter's country for currency display
    private String currency; // Shelter's local currency (e.g., "USD", "EUR", "GBP")
    private String phone; // Shelter contact phone number
    private String email; // Shelter contact email

    @DBRef
    @JsonIgnore
    private List<Pet> pets;

    @DBRef
    @JsonIgnore
    private List<Donation> donations;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; } // Added
    public void setLocation(String location) { this.location = location; } // Added
    public List<Pet> getPets() { return pets; }
    public void setPets(List<Pet> pets) { this.pets = pets; }
    public List<Donation> getDonations() { return donations; }
    public void setDonations(List<Donation> donations) { this.donations = donations; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
