package com.petadoption.backend.services;

import com.petadoption.backend.model.Payment;
import com.petadoption.backend.model.User;
import com.petadoption.backend.model.Shelter;
import com.petadoption.backend.repository.PaymentRepository;
import com.petadoption.backend.repository.UserRepository;
import com.petadoption.backend.repository.ShelterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private CurrencyService currencyService;

    public Payment createPayment(Payment payment) {
        // Set base currency to INR if not set
        if (payment.getBaseCurrency() == null) {
            payment.setBaseCurrency("INR");
        }

        // Determine display currency based on user or shelter location
        if (payment.getCurrency() == null) {
            payment.setCurrency(determineDisplayCurrency(payment.getUserId()));
        }

        // Convert amount for display from base currency
        if (payment.getAmount() != null && payment.getCurrency() != null) {
            String base = payment.getBaseCurrency() != null ? payment.getBaseCurrency() : "INR";
            if (base.equals(payment.getCurrency())) {
                payment.setDisplayAmount(payment.getAmount());
            } else {
                Double usdAmount = currencyService.convertToUSD(payment.getAmount(), base);
                payment.setDisplayAmount(currencyService.convertFromUSD(usdAmount, payment.getCurrency()));
            }
        }

        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(String id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public List<Payment> getPaymentsByUser(String userId) {
        return paymentRepository.findByUserId(userId);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment updatePaymentStatus(String id, String status) {
        Payment payment = getPaymentById(id);
        if (payment != null) {
            payment.setStatus(status);
            return paymentRepository.save(payment);
        }
        return null;
    }

    public Payment processRefund(String paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment != null && "succeeded".equals(payment.getStatus())) {
            payment.setStatus("refunded");
            return paymentRepository.save(payment);
        }
        return null;
    }

    /**
     * Determine the display currency based on user's preferred currency or country
     * @param userId User ID
     * @return Currency code
     */
    private String determineDisplayCurrency(String userId) {
        if (userId != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // First check if user has preferred currency set
                if (user.getPreferredCurrency() != null) {
                    return user.getPreferredCurrency();
                }
                // Otherwise use country-based currency
                if (user.getCountry() != null) {
                    return currencyService.getCurrencyForCountry(user.getCountry());
                }
            }
        }
        // Default to INR
        return "INR";
    }

    /**
     * Convert payment amount for a specific user
     * @param payment Payment object
     * @param userId User ID
     * @return Payment with converted display amount
     */
    public Payment convertPaymentForUser(Payment payment, String userId) {
        if (payment == null || userId == null) {
            return payment;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String targetCurrency = user.getPreferredCurrency() != null ?
                user.getPreferredCurrency() :
                currencyService.getCurrencyForCountry(user.getCountry());

            payment.setCurrency(targetCurrency);
            if (payment.getAmount() != null) {
                String base = payment.getBaseCurrency() != null ? payment.getBaseCurrency() : "INR";
                if (base.equals(targetCurrency)) {
                    payment.setDisplayAmount(payment.getAmount());
                } else {
                    Double usdAmount = currencyService.convertToUSD(payment.getAmount(), base);
                    payment.setDisplayAmount(currencyService.convertFromUSD(usdAmount, targetCurrency));
                }
            }
        }

        return payment;
    }
}
