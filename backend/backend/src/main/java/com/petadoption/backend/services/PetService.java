package com.petadoption.backend.services;

import com.petadoption.backend.model.Pet;
import com.petadoption.backend.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private CurrencyService currencyService;

    public List<Pet> getPets() {
        return petRepository.findAll();
    }

    public List<Pet> matchPets(String species, int age, String location) {
        // Simplified matching logic based on shelter location
        return petRepository.findBySpeciesAndAgeAndShelterLocation(species, age, location);
    }

    public Optional<Pet> getPetById(String id) {
        return petRepository.findById(id);
    }

    public Pet savePet(Pet pet) {
        // Set currency and display price based on shelter location
        if (pet.getShelter() != null && pet.getPrice() != null) {
            String shelterCurrency = currencyService.getCurrencyForCountry(pet.getShelter().getCountry());
            pet.setCurrency(shelterCurrency);
            pet.setDisplayPrice(currencyService.convertFromUSD(pet.getPrice(), shelterCurrency));
            pet.setBaseCurrency("USD");
        }
        return petRepository.save(pet);
    }

    public Pet updatePetStatus(String id, String status) {
        Optional<Pet> petOpt = petRepository.findById(id);
        if (petOpt.isPresent()) {
            Pet pet = petOpt.get();
            pet.setStatus(status);
            return petRepository.save(pet);
        }
        return null; // Handle error case appropriately in production
    }

    public void deletePet(String id) {
        petRepository.deleteById(id);
    }

    public Pet updatePet(String id, Pet pet) {
        Optional<Pet> petOpt = petRepository.findById(id);
        if (petOpt.isPresent()) {
            Pet existingPet = petOpt.get();
            existingPet.setName(pet.getName());
            existingPet.setSpecies(pet.getSpecies());
            existingPet.setBreed(pet.getBreed());
            existingPet.setAge(pet.getAge());
            existingPet.setDescription(pet.getDescription());
            existingPet.setStatus(pet.getStatus());
            existingPet.setImageUrl(pet.getImageUrl());
            return petRepository.save(existingPet);
        }
        return null;
    }

	public List<Pet> getAllPets() {
		return getPets();
	}

    public List<Pet> advancedMatch(String species, String breed, Integer minAge, Integer maxAge, String size, String energyLevel, Boolean goodWithKids, Boolean goodWithPets, Double budget) {
        List<Pet> allPets = getPets().stream()
            .filter(pet -> "available".equalsIgnoreCase(pet.getStatus()))
            .collect(Collectors.toList());

        if (species != null && !species.isEmpty()) {
            allPets = allPets.stream()
                .filter(pet -> species.equalsIgnoreCase(pet.getSpecies()))
                .collect(Collectors.toList());
        }

        if (breed != null && !breed.isEmpty()) {
            allPets = allPets.stream()
                .filter(pet -> pet.getBreed() != null && pet.getBreed().toLowerCase().contains(breed.toLowerCase()))
                .collect(Collectors.toList());
        }

        if (minAge != null && maxAge != null) {
            allPets = allPets.stream()
                .filter(pet -> pet.getAge() >= minAge && pet.getAge() <= maxAge)
                .collect(Collectors.toList());
        } else if (minAge != null) {
            allPets = allPets.stream()
                .filter(pet -> pet.getAge() >= minAge)
                .collect(Collectors.toList());
        } else if (maxAge != null) {
            allPets = allPets.stream()
                .filter(pet -> pet.getAge() <= maxAge)
                .collect(Collectors.toList());
        }

        if (size != null && !size.isEmpty()) {
            allPets = allPets.stream()
                .filter(pet -> size.equalsIgnoreCase(pet.getSize()))
                .collect(Collectors.toList());
        }

        if (energyLevel != null && !energyLevel.isEmpty()) {
            allPets = allPets.stream()
                .filter(pet -> energyLevel.equalsIgnoreCase(pet.getEnergyLevel()))
                .collect(Collectors.toList());
        }

        if (goodWithKids != null) {
            allPets = allPets.stream()
                .filter(pet -> pet.getGoodWithKids() == goodWithKids)
                .collect(Collectors.toList());
        }

        if (goodWithPets != null) {
            allPets = allPets.stream()
                .filter(pet -> pet.getGoodWithPets() == goodWithPets)
                .collect(Collectors.toList());
        }

        if (budget != null) {
            allPets = allPets.stream()
                .filter(pet -> pet.getPrice() != null && pet.getPrice() <= budget)
                .collect(Collectors.toList());
        }

        return allPets;
    }
}