package com.petadoption.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "adopted_pets")
public class AdoptedPet {
    @Id
    private String id;
    private String originalPetId;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String description;
    private String imageUrl;
    private String userId;
    private String userName;
    private String userEmail;
    private Date adoptionDate;
    private String adoptionStatus; // "completed", "transferred", etc.

    // Constructors
    public AdoptedPet() {}

    public AdoptedPet(Pet pet, String userId, String userName, String userEmail) {
        this.originalPetId = pet.getId();
        this.name = pet.getName();
        this.species = pet.getSpecies();
        this.breed = pet.getBreed();
        this.age = pet.getAge();
        this.description = pet.getDescription();
        this.imageUrl = pet.getImageUrl();
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.adoptionDate = new Date();
        this.adoptionStatus = "completed";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalPetId() {
        return originalPetId;
    }

    public void setOriginalPetId(String originalPetId) {
        this.originalPetId = originalPetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Date getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(Date adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public String getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(String adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }
}
