package com.petadoption.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAdoptionConfirmationEmail(String toEmail, String userName, String petName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Adoption Confirmation - Pet Adoption Platform");
        message.setText("Dear " + userName + ",\n\n" +
                "Congratulations! Your adoption of " + petName + " has been successfully processed.\n\n" +
                "Thank you for choosing our platform to find your new family member.\n\n" +
                "Best regards,\n" +
                "Pet Adoption Platform Team");

        mailSender.send(message);
    }

    public void sendPaymentRefundEmail(String toEmail, String userName, String petName, Double amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Payment Refund Processed - Pet Adoption Platform");
        message.setText("Dear " + userName + ",\n\n" +
                "Your refund for the adoption of " + petName + " has been processed successfully.\n" +
                "Refund Amount: $" + amount + "\n\n" +
                "The refund will appear in your original payment method within 5-10 business days.\n\n" +
                "Best regards,\n" +
                "Pet Adoption Platform Team");

        mailSender.send(message);
    }
}
