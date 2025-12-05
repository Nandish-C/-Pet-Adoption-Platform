package com.petadoption.backend.controller;

import com.petadoption.backend.model.Inquiry;
import com.petadoption.backend.services.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@CrossOrigin(origins = "http://localhost:4200")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @GetMapping("/chat/{chatId}")
    public List<Inquiry> getInquiriesByChat(@PathVariable String chatId) {
        return inquiryService.getInquiriesByChat(chatId);
    }

    @GetMapping
    public ResponseEntity<List<Inquiry>> getAllInquiries() {
        // Add authorization check here if needed
        List<Inquiry> inquiries = inquiryService.getAllInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @PostMapping
    public ResponseEntity<Inquiry> addInquiry(@RequestBody Inquiry inquiry) {
        Inquiry savedInquiry = inquiryService.addInquiry(inquiry);
        return ResponseEntity.ok(savedInquiry);
    }
}
