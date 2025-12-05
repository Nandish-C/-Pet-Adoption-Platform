package com.petadoption.backend.repository;

import com.petadoption.backend.model.PredefinedQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredefinedQueryRepository extends MongoRepository<PredefinedQuery, String> {
    List<PredefinedQuery> findByCategory(String category);
    List<PredefinedQuery> findByIsActiveTrue();
    List<PredefinedQuery> findByCategoryAndIsActiveTrue(String category, Boolean isActive);
}
