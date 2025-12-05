package com.petadoption.backend.services;

import com.petadoption.backend.model.Donation;
import com.petadoption.backend.model.User;
import com.petadoption.backend.model.Shelter;
import com.petadoption.backend.repository.DonationRepository;
import com.petadoption.backend.repository.UserRepository;
import com.petadoption.backend.repository.ShelterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    public Donation processDonation(Donation donation) {
        // Simulate payment processing (in production, integrate with a payment gateway)
        return donationRepository.save(donation);
    }

    public Optional<Donation> getDonationById(String id) {
        return donationRepository.findById(id);
    }

    public Map<String, String> createDonationIntent(Donation donationRequest) {
        // In a real implementation, this would create a Stripe PaymentIntent
        // For now, we'll simulate it
        Map<String, String> result = new HashMap<>();
        result.put("clientSecret", "pi_test_" + System.currentTimeMillis());
        result.put("paymentId", "donation_" + System.currentTimeMillis());
        return result;
    }

    public void confirmDonation(String paymentId) {
        // In a real implementation, this would confirm the payment with Stripe
        // For now, we'll just create a donation record
        Donation donation = new Donation();
        donation.setAmount(0.0); // Would be set from the payment intent
        donation.setCurrency("USD");
        donation.setDisplayAmount(0.0);
        donation.setBaseCurrency("USD");
        donation.setTransactionToken(paymentId);
        donation.setFrequency("one-time");
        donation.setMessage("");
        donation.setIsAnonymous(false);
        donation.setStatus("completed");

        // Set user and shelter if available
        // donation.setUser(user);
        // donation.setShelter(shelter);

        donationRepository.save(donation);
    }

    public List<Donation> getDonationsByUserId(String userId) {
        return donationRepository.findByUserId(userId);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }
}
