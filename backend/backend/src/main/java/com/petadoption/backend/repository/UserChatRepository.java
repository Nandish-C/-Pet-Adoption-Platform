package com.petadoption.backend.repository;

import com.petadoption.backend.model.UserChat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChatRepository extends MongoRepository<UserChat, String> {

    // Find all chats for a specific user (either as initiator or recipient)
    @Query("{ '$or': [ { 'initiator.$id': ?0 }, { 'recipient.$id': ?0 } ] }")
    List<UserChat> findChatsByUserId(String userId);

    // Find direct chat between two users
    @Query("{ '$or': [ { '$and': [ { 'initiator.$id': ?0 }, { 'recipient.$id': ?1 } ] }, { '$and': [ { 'initiator.$id': ?1 }, { 'recipient.$id': ?0 } ] } ] }")
    Optional<UserChat> findDirectChatBetweenUsers(String userId1, String userId2);

    // Find active chats for a user
    @Query("{ '$and': [ { '$or': [ { 'initiator.$id': ?0 }, { 'recipient.$id': ?0 } ] }, { 'status': 'ACTIVE' } ] }")
    List<UserChat> findActiveChatsByUserId(String userId);

    // Find chats by type
    List<UserChat> findByChatType(String chatType);
}
