package com.petadoption.backend.repository;

import com.petadoption.backend.model.Donation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DonationRepository extends MongoRepository<Donation, String> {
    List<Donation> findByUserId(String userId);
}
