package com.petadoption.backend.repository;

import com.petadoption.backend.model.Adoption;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdoptionRepository extends MongoRepository<Adoption, String> {
    List<Adoption> findByUserId(String userId);
}
