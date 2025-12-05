package com.petadoption.backend.controller;

import com.petadoption.backend.model.Payment;
import com.petadoption.backend.model.Adoption;
import com.petadoption.backend.model.Pet;
import com.petadoption.backend.model.User;
import com.petadoption.backend.services.PaymentService;
import com.petadoption.backend.services.CurrencyService;
import com.petadoption.backend.services.PetService;
import com.petadoption.backend.repository.AdoptionRepository;
import com.petadoption.backend.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PetService petService;

    @Autowired
    private AdoptionRepository adoptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostMapping("/create-intent")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> request) {
        try {
            Stripe.apiKey = stripeSecretKey;

            String petId = (String) request.get("petId");
            Double amount = Double.valueOf(request.get("amount").toString());
            String userId = (String) request.get("userId");
            String paymentMethod = (String) request.getOrDefault("paymentMethod", "card");

            // Save payment record
            Payment payment = new Payment();
            payment.setUserId(userId);
            payment.setPetId(petId);
            payment.setAmount(amount);
            payment.setPaymentMethod(paymentMethod);

            if ("cod".equals(paymentMethod)) {
                // For COD, mark as pending and return success immediately
                payment.setStatus("pending");
                paymentService.createPayment(payment);
                return ResponseEntity.ok(Map.of("paymentId", payment.getId(), "status", "cod_pending"));
            } else {
                // For card payment, create Stripe intent
                PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                        .setAmount((long) (amount * 100)) // amount in paise (1 INR = 100 paise)
                        .setCurrency("inr")
                        .putMetadata("petId", petId)
                        .putMetadata("userId", userId)
                        .build();

                PaymentIntent intent = PaymentIntent.create(params);
                payment.setStatus("pending");
                payment.setStripePaymentIntentId(intent.getId());
                paymentService.createPayment(payment);

                return ResponseEntity.ok(Map.of("clientSecret", intent.getClientSecret(), "paymentId", payment.getId()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating payment intent: " + e.getMessage());
        }
    }

    @PostMapping("/confirm/{paymentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> confirmPayment(@PathVariable String paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment != null && "pending".equals(payment.getStatus())) {
                // Update payment status
                payment.setStatus("succeeded");
                payment = paymentService.updatePaymentStatus(paymentId, "succeeded");

                // Update pet status to adopted
                petService.updatePetStatus(payment.getPetId(), "adopted");

                // Create adoption record
                Adoption adoption = new Adoption();
                adoption.setUserId(payment.getUserId());
                adoption.setPetId(payment.getPetId());
                adoption.setPaymentId(paymentId);
                adoption.setStatus("completed");
                if (adoption.getCreatedAt() == null) {
                    adoption.setCreatedAt(LocalDateTime.now());
                }
                adoption = adoptionRepository.save(adoption);

                return ResponseEntity.ok(Map.of("payment", payment, "adoption", adoption));
            } else {
                return ResponseEntity.badRequest().body("Payment not found or already processed.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error confirming payment: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentHistory() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getUserPayments(@PathVariable String userId) {
        // Check if the authenticated user is accessing their own payments
        String authenticatedUserId = getAuthenticatedUserId();
        if (!userId.equals(authenticatedUserId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(paymentService.getPaymentsByUser(userId));
    }

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Assuming the username is the email, and we need to get the userId
            // Since the UserDetails has the email as username, we need to fetch the userId from the repository
            User user = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            return user != null ? user.getId() : null;
        }
        return null;
    }
}
