package com.petadoption.backend.repository;

import com.petadoption.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByEmail(String email);

  @Query(value = "{ 'email' : ?0 }", fields = "{ 'id' : 1, 'name' : 1, 'email' : 1, 'password' : 1, 'role' : 1 }")
  Optional<User> findUserDataByEmail(String email);
}
