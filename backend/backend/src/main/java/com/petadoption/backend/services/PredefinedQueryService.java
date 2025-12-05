package com.petadoption.backend.services;

import com.petadoption.backend.model.PredefinedQuery;
import com.petadoption.backend.repository.PredefinedQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PredefinedQueryService {

    @Autowired
    private PredefinedQueryRepository predefinedQueryRepository;

    public List<PredefinedQuery> getAllActiveQueries() {
        return predefinedQueryRepository.findByIsActiveTrue();
    }

    public List<PredefinedQuery> getQueriesByCategory(String category) {
        return predefinedQueryRepository.findByCategoryAndIsActiveTrue(category, true);
    }

    public PredefinedQuery getQueryById(String id) {
        return predefinedQueryRepository.findById(id).orElse(null);
    }

    public PredefinedQuery saveQuery(PredefinedQuery query) {
        return predefinedQueryRepository.save(query);
    }

    public void deleteQuery(String id) {
        predefinedQueryRepository.deleteById(id);
    }
}
