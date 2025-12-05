package com.petadoption.backend.services;

import com.petadoption.backend.model.Inquiry;
import com.petadoption.backend.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    public List<Inquiry> getInquiriesByChat(String chatId) {
        return inquiryRepository.findByChatId(chatId);
    }

    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    public Inquiry addInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    public void deleteInquiry(String id) {
        inquiryRepository.deleteById(id);
    }

    public Optional<Inquiry> getInquiryById(String id) {
        return inquiryRepository.findById(id);
    }

    public void markMessagesAsRead(String chatId, String userId) {
        
        // For now, this is a placeholder that could be implemented with:
        // - Add 'readByUsers' field to Inquiry model (Set<String> or similar)
        // - Update repository to mark messages as read for specific user
        // - Query unread messages for notifications

        // Placeholder implementation - could log or track read status
        System.out.println("Messages marked as read for chat " + chatId + " by user " + userId);
    }

    public List<Inquiry> getUnreadMessagesForUser(String userId) {
    
        // This would return messages not read by the user
        return inquiryRepository.findAll();
    }

    // Additional methods can be added when corresponding repository methods are implemented
    // public List<Inquiry> getInquiriesByPet(Long petId) { ... }
    // public List<Inquiry> getInquiriesByUser(Long userId) { ... }
}
