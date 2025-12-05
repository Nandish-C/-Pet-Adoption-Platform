package com.petadoption.backend.controller;

import com.petadoption.backend.model.*;
import com.petadoption.backend.services.*;
import com.petadoption.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdoptionRepository adoptionRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AdoptedPetService adoptedPetService;

    // Pet management endpoints
    @GetMapping("/pets")
    public List<Pet> getAllPets() {
        System.out.println("AdminController - getAllPets called");
        List<Pet> pets = petService.getAllPets();
        System.out.println("AdminController - Found " + pets.size() + " pets");
        return pets;
    }

    @PostMapping("/pets")
    public Pet addPet(@RequestBody Pet pet) {
        System.out.println("AdminController - addPet called");
        return petService.savePet(pet);
    }

    @PutMapping("/pets/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable String id, @RequestBody Pet pet) {
        Pet updatedPet = petService.updatePet(id, pet);
        return updatedPet != null ? ResponseEntity.ok(updatedPet) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable String id) {
        petService.deletePet(id);
        return ResponseEntity.ok().build();
    }

    // User management endpoints
    @GetMapping("/users")
    public List<User> getAllUsers() {
        System.out.println("AdminController - getAllUsers called");
        List<User> users = userService.getAllUsers();
        System.out.println("AdminController - Found " + users.size() + " users");
        return users;
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            // Only update password if provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().trim().isEmpty()) {
                user.setPassword(userDetails.getPassword());
            }
            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable String id, @RequestBody String role) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(role.replace("\"", "")); // Remove quotes from JSON string
            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Adoption management endpoints
    @GetMapping("/adoptions")
    public List<Adoption> getAllAdoptions() {
        return adoptionRepository.findAll();
    }

    @PostMapping("/adoptions")
    public Adoption addAdoption(@RequestBody Adoption adoption) {
        return adoptionRepository.save(adoption);
    }

    @PutMapping("/adoptions/{id}")
    public ResponseEntity<Adoption> updateAdoption(@PathVariable String id, @RequestBody Adoption adoptionDetails) {
        Optional<Adoption> optionalAdoption = adoptionRepository.findById(id);
        if (optionalAdoption.isPresent()) {
            Adoption adoption = optionalAdoption.get();
            adoption.setStatus(adoptionDetails.getStatus());
            Adoption updatedAdoption = adoptionRepository.save(adoption);
            return ResponseEntity.ok(updatedAdoption);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/adoptions/{id}")
    public ResponseEntity<Void> deleteAdoption(@PathVariable String id) {
        if (adoptionRepository.existsById(id)) {
            adoptionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Chat management endpoints
    @GetMapping("/chats")
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    @PostMapping("/chats")
    public Chat addChat(@RequestBody Chat chat) {
        return chatRepository.save(chat);
    }

    @PutMapping("/chats/{id}")
    public ResponseEntity<Chat> updateChat(@PathVariable String id, @RequestBody Chat chatDetails) {
        Optional<Chat> optionalChat = chatRepository.findById(id);
        if (optionalChat.isPresent()) {
            Chat chat = optionalChat.get();
            chat.setStatus(chatDetails.getStatus());
            chat.setEndedAt(chatDetails.getEndedAt());
            Chat updatedChat = chatRepository.save(chat);
            return ResponseEntity.ok(updatedChat);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/chats/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable String id) {
        if (chatRepository.existsById(id)) {
            chatRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Donation management endpoints
    @GetMapping("/donations")
    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    @PostMapping("/donations")
    public Donation addDonation(@RequestBody Donation donation) {
        return donationRepository.save(donation);
    }

    @PutMapping("/donations/{id}")
    public ResponseEntity<Donation> updateDonation(@PathVariable String id, @RequestBody Donation donationDetails) {
        Optional<Donation> optionalDonation = donationRepository.findById(id);
        if (optionalDonation.isPresent()) {
            Donation donation = optionalDonation.get();
            donation.setAmount(donationDetails.getAmount());
            donation.setTransactionToken(donationDetails.getTransactionToken());
            Donation updatedDonation = donationRepository.save(donation);
            return ResponseEntity.ok(updatedDonation);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/donations/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable String id) {
        if (donationRepository.existsById(id)) {
            donationRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Shelter management endpoints
    @GetMapping("/shelters")
    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    @PostMapping("/shelters")
    public Shelter addShelter(@RequestBody Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    @PutMapping("/shelters/{id}")
    public ResponseEntity<Shelter> updateShelter(@PathVariable String id, @RequestBody Shelter shelterDetails) {
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
    }

    @DeleteMapping("/shelters/{id}")
    public ResponseEntity<Void> deleteShelter(@PathVariable String id) {
        if (shelterRepository.existsById(id)) {
            shelterRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Inquiry management endpoints
    @GetMapping("/inquiries")
    public List<Inquiry> getAllInquiries(@RequestParam(required = false) String chatId) {
        if (chatId != null) {
            return inquiryService.getInquiriesByChat(chatId);
        }
        return inquiryService.getAllInquiries();
    }

    @PostMapping("/inquiries")
    public Inquiry addInquiry(@RequestBody Inquiry inquiry) {
        return inquiryService.addInquiry(inquiry);
    }

    @DeleteMapping("/inquiries/{id}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable String id) {
        inquiryService.deleteInquiry(id);
        return ResponseEntity.noContent().build();
    }

    // Chat reply endpoint
    @PostMapping("/chats/reply")
    public ResponseEntity<Inquiry> replyToChat(@RequestBody ReplyRequest replyRequest) {
        try {
            Optional<Chat> chatOpt = chatRepository.findById(replyRequest.getChatId());
            if (!chatOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Inquiry reply = new Inquiry();
            reply.setMessage(replyRequest.getMessage());
            reply.setChat(chatOpt.get());
            reply.setSenderType("admin");
            reply.setTimestamp(new java.util.Date());

            Inquiry savedReply = inquiryService.addInquiry(reply);
            return ResponseEntity.ok(savedReply);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTO for reply request
    public static class ReplyRequest {
        private String chatId;
        private String message;

        public String getChatId() { return chatId; }
        public void setChatId(String chatId) { this.chatId = chatId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // Payment management endpoints
    @GetMapping("/payments")
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @PostMapping("/payments")
    public Payment addPayment(@RequestBody Payment payment) {
        return paymentRepository.save(payment);
    }

    @PutMapping("/payments/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable String id, @RequestBody Payment paymentDetails) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setStatus(paymentDetails.getStatus());
            payment.setAmount(paymentDetails.getAmount());
            Payment updatedPayment = paymentRepository.save(payment);
            return ResponseEntity.ok(updatedPayment);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        if (paymentRepository.existsById(id)) {
            paymentRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Manual matching endpoints
    @PostMapping("/manual-match")
    public ResponseEntity<String> manualMatch(@RequestBody ManualMatchRequest matchRequest) {
        try {
            // Find the inquiry
            Optional<Inquiry> inquiryOpt = inquiryService.getInquiryById(matchRequest.getInquiryId());
            if (!inquiryOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Inquiry not found");
            }

            // Find the pet
            Optional<Pet> petOpt = petService.getPetById(matchRequest.getPetId());
            if (!petOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Pet not found");
            }

            Pet pet = petOpt.get();
            if (!"available".equals(pet.getStatus())) {
                return ResponseEntity.badRequest().body("Pet is not available for adoption");
            }

            // Create adoption record
            Adoption adoption = new Adoption();
            adoption.setUserId(matchRequest.getUserId());
            adoption.setPetId(matchRequest.getPetId());
            adoption.setStatus("approved");
            adoptionRepository.save(adoption);

            // Update pet status
            pet.setStatus("adopted");
            petService.savePet(pet);

            // Update inquiry with pet assignment
            Inquiry inquiry = inquiryOpt.get();
            inquiry.setPet(pet);
            inquiryService.addInquiry(inquiry);

            return ResponseEntity.ok("Pet successfully matched to inquiry");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to match pet: " + e.getMessage());
        }
    }

    @GetMapping("/matching-stats")
    public ResponseEntity<MatchingStats> getMatchingStats() {
        try {
            List<Adoption> allAdoptions = adoptionRepository.findAll();
            long totalMatches = allAdoptions.size();
            long successfulMatches = allAdoptions.stream()
                .filter(adoption -> "completed".equals(adoption.getStatus()) || "approved".equals(adoption.getStatus()))
                .count();
            long pendingMatches = allAdoptions.stream()
                .filter(adoption -> "pending".equals(adoption.getStatus()))
                .count();

            MatchingStats stats = new MatchingStats(totalMatches, successfulMatches, pendingMatches);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTOs for matching
    public static class ManualMatchRequest {
        private String inquiryId;
        private String petId;
        private String userId;

        public String getInquiryId() { return inquiryId; }
        public void setInquiryId(String inquiryId) { this.inquiryId = inquiryId; }
        public String getPetId() { return petId; }
        public void setPetId(String petId) { this.petId = petId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    public static class MatchingStats {
        private long totalMatchesGenerated;
        private long successfulMatches;
        private long pendingMatches;

        public MatchingStats(long totalMatchesGenerated, long successfulMatches, long pendingMatches) {
            this.totalMatchesGenerated = totalMatchesGenerated;
            this.successfulMatches = successfulMatches;
            this.pendingMatches = pendingMatches;
        }

        public long getTotalMatchesGenerated() { return totalMatchesGenerated; }
        public void setTotalMatchesGenerated(long totalMatchesGenerated) { this.totalMatchesGenerated = totalMatchesGenerated; }
        public long getSuccessfulMatches() { return successfulMatches; }
        public void setSuccessfulMatches(long successfulMatches) { this.successfulMatches = successfulMatches; }
        public long getPendingMatches() { return pendingMatches; }
        public void setPendingMatches(long pendingMatches) { this.pendingMatches = pendingMatches; }
    }

    // Adopted Pets management endpoints
    @GetMapping("/adopted-pets")
    public List<AdoptedPet> getAllAdoptedPets() {
        return adoptedPetService.getAllAdoptedPets();
    }

    @GetMapping("/adopted-pets/{id}")
    public ResponseEntity<AdoptedPet> getAdoptedPetById(@PathVariable String id) {
        Optional<AdoptedPet> adoptedPet = adoptedPetService.getAdoptedPetById(id);
        return adoptedPet.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/adopted-pets/{id}")
    public ResponseEntity<Void> deleteAdoptedPet(@PathVariable String id) {
        if (adoptedPetService.getAdoptedPetById(id).isPresent()) {
            adoptedPetService.deleteAdoptedPet(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Approve adoption endpoint
    @PostMapping("/adoptions/{id}/approve")
    public ResponseEntity<String> approveAdoption(@PathVariable String id) {
        try {
            Optional<Adoption> adoptionOpt = adoptionRepository.findById(id);
            if (!adoptionOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Adoption adoption = adoptionOpt.get();
            if (!"pending".equals(adoption.getStatus())) {
                return ResponseEntity.badRequest().body("Adoption is not in pending status");
            }

            // Find the pet
            Optional<Pet> petOpt = petService.getPetById(adoption.getPetId());
            if (!petOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Pet not found");
            }

            Pet pet = petOpt.get();
            if (!"available".equals(pet.getStatus())) {
                return ResponseEntity.badRequest().body("Pet is not available for adoption");
            }

            // Create adopted pet record
            AdoptedPet adoptedPet = adoptedPetService.createAdoptedPetFromPet(adoption.getPetId(), adoption.getUserId());

            // Update adoption status
            adoption.setStatus("approved");
            adoptionRepository.save(adoption);

            // Update pet status
            pet.setStatus("adopted");
            petService.savePet(pet);

            return ResponseEntity.ok("Adoption approved successfully. Pet moved to adopted pets collection.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to approve adoption: " + e.getMessage());
        }
    }

    // Reject adoption endpoint
    @PostMapping("/adoptions/{id}/reject")
    public ResponseEntity<String> rejectAdoption(@PathVariable String id) {
        try {
            Optional<Adoption> adoptionOpt = adoptionRepository.findById(id);
            if (!adoptionOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Adoption adoption = adoptionOpt.get();
            if (!"pending".equals(adoption.getStatus())) {
                return ResponseEntity.badRequest().body("Adoption is not in pending status");
            }

            // Update adoption status to rejected
            adoption.setStatus("rejected");
            adoptionRepository.save(adoption);

            return ResponseEntity.ok("Adoption rejected successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to reject adoption: " + e.getMessage());
        }
    }

}
