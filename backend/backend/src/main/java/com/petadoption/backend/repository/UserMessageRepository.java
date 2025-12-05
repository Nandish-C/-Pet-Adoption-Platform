package com.petadoption.backend.repository;

import com.petadoption.backend.model.UserMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageRepository extends MongoRepository<UserMessage, String> {

    // Find all messages for a specific chat
    List<UserMessage> findByUserChatIdOrderByTimestampAsc(String userChatId);

    // Find unread messages for a user
    @Query("{ '$and': [ { 'userChat.recipient.$id': ?0 }, { 'isRead': false } ] }")
    List<UserMessage> findUnreadMessagesForUser(String userId);

    // Find messages between two users
    List<UserMessage> findByUserChatIdOrderByTimestampDesc(String chatId);

    // Count unread messages for a user
    @Query(value = "{ '$and': [ { 'userChat.recipient.$id': ?0 }, { 'isRead': false } ] }", count = true)
    Long countUnreadMessagesForUser(String userId);
}
