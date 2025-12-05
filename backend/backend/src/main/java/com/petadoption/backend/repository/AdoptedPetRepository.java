package com.petadoption.backend.repository;

import com.petadoption.backend.model.AdoptedPet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptedPetRepository extends MongoRepository<AdoptedPet, String> {
    List<AdoptedPet> findByUserId(String userId);
    List<AdoptedPet> findByOriginalPetId(String originalPetId);
    List<AdoptedPet> findByAdoptionStatus(String adoptionStatus);
}
