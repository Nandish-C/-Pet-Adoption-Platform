package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pets")
public class Pet {
    @Id
    private String id;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String description;
    private String status;
    private String imageUrl;
    private Double price; // Price in base currency (USD)
    private String currency; // Display currency for the pet (based on shelter)
    private Double displayPrice; // Price converted to display currency
    private String baseCurrency; // Base currency for storage (always "USD")
    private String size; // e.g., small, medium, large
    private String energyLevel; // e.g., low, medium, high
    private Boolean goodWithKids; // true if good with kids
    private Boolean goodWithPets; // true if good with other pets
    private Double maxBudget; // Maximum budget in rupees for adoption
    private Double adoptionFee; // Adoption processing fee in rupees

    @DBRef
    private Shelter shelter;

    // Add shelterLocation if needed, or use shelter.getLocation()
    // public String getShelterLocation() { return shelter != null ? shelter.getLocation() : null; }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Double getDisplayPrice() { return displayPrice; }
    public void setDisplayPrice(Double displayPrice) { this.displayPrice = displayPrice; }
    public String getBaseCurrency() { return baseCurrency != null ? baseCurrency : "USD"; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
    public Shelter getShelter() { return shelter; }
    public void setShelter(Shelter shelter) { this.shelter = shelter; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(String energyLevel) { this.energyLevel = energyLevel; }
    public Boolean getGoodWithKids() { return goodWithKids; }
    public void setGoodWithKids(Boolean goodWithKids) { this.goodWithKids = goodWithKids; }
    public Boolean getGoodWithPets() { return goodWithPets; }
    public void setGoodWithPets(Boolean goodWithPets) { this.goodWithPets = goodWithPets; }
    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }
    public Double getAdoptionFee() { return adoptionFee; }
    public void setAdoptionFee(Double adoptionFee) { this.adoptionFee = adoptionFee; }
}
