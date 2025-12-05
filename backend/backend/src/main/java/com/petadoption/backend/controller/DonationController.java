package com.petadoption.backend.controller;

import com.petadoption.backend.model.Donation;
import com.petadoption.backend.services.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "http://localhost:4200")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @PostMapping("/create-intent")
    public ResponseEntity<?> createDonationIntent(@RequestBody Donation donationRequest) {
        try {
            String paymentMethod = donationRequest.getPaymentMethod() != null ? donationRequest.getPaymentMethod() : "card";

            if ("upi".equals(paymentMethod)) {
                // For UPI, create a mock payment intent (in real implementation, integrate with UPI gateway)
                donationRequest.setStatus("pending");
                donationRequest.setTransactionToken("upi_" + System.currentTimeMillis());
                Donation savedDonation = donationService.processDonation(donationRequest);

                return ResponseEntity.ok(Map.of(
                    "paymentId", savedDonation.getId(),
                    "status", "upi_pending",
                    "upiApps", new String[]{"PhonePe", "Google Pay", "Paytm", "BHIM UPI"}
                ));
            } else {
                // For card payment, use existing Stripe logic
                var result = donationService.createDonationIntent(donationRequest);
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create donation intent: " + e.getMessage());
        }
    }

    @PostMapping("/confirm/{paymentId}")
    public ResponseEntity<?> confirmDonation(@PathVariable String paymentId) {
        try {
            donationService.confirmDonation(paymentId);
            return ResponseEntity.ok(Map.of("message", "Donation confirmed successfully", "status", "confirmed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to confirm donation: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Donation> processDonation(@RequestBody Donation donation) {
        Donation processedDonation = donationService.processDonation(donation);
        return ResponseEntity.ok(processedDonation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donation> getDonationById(@PathVariable String id) {
        Optional<Donation> donation = donationService.getDonationById(id);
        return donation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Donation>> getUserDonations(@PathVariable String userId) {
        try {
            List<Donation> donations;
            if ("all".equals(userId)) {
                // Return recent donations from all users (for public display)
                donations = donationService.getAllDonations();
                // Sort by timestamp if available, otherwise return as is
                donations.sort((a, b) -> {
                    if (a.getTimestamp() != null && b.getTimestamp() != null) {
                        try {
                            return b.getTimestamp().compareTo(a.getTimestamp());
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                    return 0;
                });
                // Limit to 10 most recent
                if (donations.size() > 10) {
                    donations = donations.subList(0, 10);
                }
            } else {
                donations = donationService.getDonationsByUserId(userId);
            }
            return ResponseEntity.ok(donations);
        } catch (Exception e) {
            e.printStackTrace(); // Add debug logging
            return ResponseEntity.badRequest().build();
        }
    }
}
