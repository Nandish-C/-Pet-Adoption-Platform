package com.petadoption.backend.repository;

import com.petadoption.backend.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findByPet_IdAndStatus(String petId, String status);
    List<Chat> findByUser_Id(String userId);
    List<Chat> findByStatus(String status);
}
