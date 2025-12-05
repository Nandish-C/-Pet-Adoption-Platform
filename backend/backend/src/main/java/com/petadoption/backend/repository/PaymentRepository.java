package com.petadoption.backend.repository;

import com.petadoption.backend.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByUserId(String userId);
    List<Payment> findByStatus(String status);
    List<Payment> findByPetId(String petId);
}
