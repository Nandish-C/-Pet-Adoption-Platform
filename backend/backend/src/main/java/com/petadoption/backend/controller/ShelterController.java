package com.petadoption.backend.controller;

import com.petadoption.backend.model.Shelter;
import com.petadoption.backend.repository.ShelterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shelters")
public class ShelterController {
  @Autowired
  private ShelterRepository shelterRepository;

  @GetMapping
  public ResponseEntity<List<Shelter>> getAllShelters() {
    try {
      List<Shelter> shelters = shelterRepository.findAll();
      return ResponseEntity.ok(shelters);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Shelter> getShelterById(@PathVariable String id) {
    Optional<Shelter> shelter = shelterRepository.findById(id);
    return shelter.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Shelter> addShelter(@RequestBody Shelter shelter) {
    try {
      // Admin-specific logic can be added here
      Shelter savedShelter = shelterRepository.save(shelter);
      return ResponseEntity.ok(savedShelter);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Shelter> updateShelter(@PathVariable String id, @RequestBody Shelter shelterDetails) {
    try {
      Optional<Shelter> optionalShelter = shelterRepository.findById(id);
      if (optionalShelter.isPresent()) {
        Shelter shelter = optionalShelter.get();
        shelter.setName(shelterDetails.getName());
        shelter.setLocation(shelterDetails.getLocation());
        shelter.setPhone(shelterDetails.getPhone());
        shelter.setEmail(shelterDetails.getEmail());
        Shelter updatedShelter = shelterRepository.save(shelter);
        return ResponseEntity.ok(updatedShelter);
      }
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteShelter(@PathVariable String id) {
    try {
      if (shelterRepository.existsById(id)) {
        shelterRepository.deleteById(id);
        return ResponseEntity.noContent().build();
      }
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
