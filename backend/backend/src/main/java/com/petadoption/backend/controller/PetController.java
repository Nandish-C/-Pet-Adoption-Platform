package com.petadoption.backend.controller;

import com.petadoption.backend.model.Pet;
import com.petadoption.backend.services.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "http://localhost:4200")
public class PetController {

    @Autowired
    private PetService petService;

    @Value("${file.upload-dir:uploads/pets}")
    private String uploadDir;

    @GetMapping
    public List<Pet> getAllPets() {
        return petService.getAllPets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable String id) {
        return petService.getPetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Pet> getPetDetails(@PathVariable String id) {
        return petService.getPetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Pet addPet(@RequestBody Pet pet) {
        return petService.savePet(pet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable String id, @RequestBody Pet pet) {
        Pet updatedPet = petService.updatePet(id, pet);
        return updatedPet != null ? ResponseEntity.ok(updatedPet) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable String id) {
        petService.deletePet(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Pet> updatePetStatus(@PathVariable String id, @RequestBody String status) {
        Pet updatedPet = petService.updatePetStatus(id, status.replace("\"", "")); // Remove quotes from JSON string
        return updatedPet != null ? ResponseEntity.ok(updatedPet) : ResponseEntity.notFound().build();
    }

    @GetMapping("/advanced-match")
    public List<Pet> advancedMatch(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String energyLevel,
            @RequestParam(required = false) Boolean goodWithKids,
            @RequestParam(required = false) Boolean goodWithPets,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double budget) {

        return petService.advancedMatch(species, breed, minAge, maxAge, size, energyLevel, goodWithKids, goodWithPets, budget);
    }

    @PostMapping(value = "/{id}/upload-image", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadPetImage(@PathVariable String id, @RequestParam("image") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Update pet with image URL
            String imageUrl = "/uploads/pets/" + filename;
            Pet pet = petService.getPetById(id).orElse(null);
            if (pet == null) {
                return ResponseEntity.notFound().build();
            }
            pet.setImageUrl(imageUrl);
            petService.savePet(pet);

            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload image: " + e.getMessage());
        }
    }
}
