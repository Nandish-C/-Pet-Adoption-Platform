package com.petadoption.backend.controller;

import com.petadoption.backend.model.PredefinedQuery;
import com.petadoption.backend.services.PredefinedQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predefined-queries")
public class PredefinedQueryController {

    @Autowired
    private PredefinedQueryService predefinedQueryService;

    @GetMapping
    public ResponseEntity<List<PredefinedQuery>> getAllActiveQueries() {
        return ResponseEntity.ok(predefinedQueryService.getAllActiveQueries());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<PredefinedQuery>> getQueriesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(predefinedQueryService.getQueriesByCategory(category));
    }
}
