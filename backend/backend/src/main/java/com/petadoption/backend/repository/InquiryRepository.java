package com.petadoption.backend.repository;

import com.petadoption.backend.model.Inquiry;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InquiryRepository extends MongoRepository<Inquiry, String> {
    List<Inquiry> findByChatId(String chatId);
}
