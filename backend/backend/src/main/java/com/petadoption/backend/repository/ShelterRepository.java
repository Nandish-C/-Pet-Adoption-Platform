package com.petadoption.backend.repository;

import com.petadoption.backend.model.Shelter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShelterRepository extends MongoRepository<Shelter, String> {
}
