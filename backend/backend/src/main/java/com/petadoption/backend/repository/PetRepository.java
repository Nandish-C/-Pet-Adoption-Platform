package com.petadoption.backend.repository;

import com.petadoption.backend.model.Pet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PetRepository extends MongoRepository<Pet, String> {
    List<Pet> findByStatus(String status);

    @Query("{ 'species': ?0, 'age': ?1, 'shelter.location': ?2 }")
    List<Pet> findBySpeciesAndAgeAndShelterLocation(String species, int age, String location);
}
