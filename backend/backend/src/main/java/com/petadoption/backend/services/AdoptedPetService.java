package com.petadoption.backend.services;

import com.petadoption.backend.model.AdoptedPet;
import com.petadoption.backend.model.Pet;
import com.petadoption.backend.model.User;
import com.petadoption.backend.repository.AdoptedPetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdoptedPetService {

    @Autowired
    private AdoptedPetRepository adoptedPetRepository;

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    public List<AdoptedPet> getAllAdoptedPets() {
        return adoptedPetRepository.findAll();
    }

    public Optional<AdoptedPet> getAdoptedPetById(String id) {
        return adoptedPetRepository.findById(id);
    }

    public List<AdoptedPet> getAdoptedPetsByUserId(String userId) {
        return adoptedPetRepository.findByUserId(userId);
    }

    public AdoptedPet saveAdoptedPet(AdoptedPet adoptedPet) {
        return adoptedPetRepository.save(adoptedPet);
    }

    public void deleteAdoptedPet(String id) {
        adoptedPetRepository.deleteById(id);
    }

    public AdoptedPet createAdoptedPetFromPet(String petId, String userId) {
        Optional<Pet> petOpt = petService.getPetById(petId);
        Optional<User> userOpt = userService.getUserById(userId);

        if (petOpt.isPresent() && userOpt.isPresent()) {
            Pet pet = petOpt.get();
            User user = userOpt.get();

            AdoptedPet adoptedPet = new AdoptedPet(pet, userId, user.getName(), user.getEmail());
            return adoptedPetRepository.save(adoptedPet);
        }

        throw new RuntimeException("Pet or User not found");
    }

    public List<AdoptedPet> getAdoptedPetsByStatus(String status) {
        return adoptedPetRepository.findByAdoptionStatus(status);
    }
}
