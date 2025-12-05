package com.petadoption.backend.controller;

import com.petadoption.backend.model.Adoption;
import com.petadoption.backend.model.User;
import com.petadoption.backend.repository.AdoptionRepository;
import com.petadoption.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {
  @Autowired
  private AdoptionRepository adoptionRepository;

  @Autowired
  private UserRepository userRepository;

  @GetMapping
  public ResponseEntity<List<Adoption>> getAllAdoptions() {
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();

      // For regular users, return only their adoptions
      // For admin, return all adoptions
      List<Adoption> adoptions;
      if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        adoptions = adoptionRepository.findAll();
      } else {
        String currentUserEmail = auth.getName(); // This is the email
        User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
        if (currentUser == null) {
          return ResponseEntity.status(403).build(); // User not found
        }
        String userId = currentUser.getId();
        adoptions = adoptionRepository.findByUserId(userId);
      }

      return ResponseEntity.ok(adoptions);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Adoption>> getUserAdoptions(@PathVariable String userId) {
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
      String currentUserEmail = auth.getName(); // This is the email

      // Get the current user's ID from the database using email
      User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
      if (currentUser == null) {
        return ResponseEntity.status(403).build(); // User not found
      }

      String currentUserId = currentUser.getId();

      List<Adoption> adoptions;
      if (isAdmin || currentUserId.equals(userId)) {
        adoptions = adoptionRepository.findByUserId(userId);
      } else {
        return ResponseEntity.status(403).build(); // Forbidden
      }

      return ResponseEntity.ok(adoptions);
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Adoption> getAdoptionById(@PathVariable String id) {
    try {
      Optional<Adoption> adoption = adoptionRepository.findById(id);
      if (adoption.isPresent()) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Admin can see all, users can only see their own
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
          return ResponseEntity.ok(adoption.get());
        } else {
          // Check if adoption belongs to current user
          // This would need to be implemented based on your Adoption model
          return ResponseEntity.ok(adoption.get()); // Placeholder
        }
      }
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping
  public ResponseEntity<Adoption> submitAdoption(@RequestBody Adoption adoption) {
    try {
      // Set the created timestamp if not set
      if (adoption.getCreatedAt() == null) {
        adoption.setCreatedAt(java.time.LocalDateTime.now());
      }

      Adoption savedAdoption = adoptionRepository.save(adoption);
      return ResponseEntity.ok(savedAdoption);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Adoption> updateAdoption(@PathVariable String id, @RequestBody Adoption adoptionDetails) {
    try {
      Optional<Adoption> optionalAdoption = adoptionRepository.findById(id);
      if (optionalAdoption.isPresent()) {
        Adoption adoption = optionalAdoption.get();
        // Update adoption fields based on your model
        Adoption updatedAdoption = adoptionRepository.save(adoption);
        return ResponseEntity.ok(updatedAdoption);
      }
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteAdoption(@PathVariable String id) {
    try {
      if (adoptionRepository.existsById(id)) {
        adoptionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
      }
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }
}
