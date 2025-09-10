package com.fares7elsadek.syncspace.friendship.repository;

import com.fares7elsadek.syncspace.friendship.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.model.Friendships;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendships,String> {

    @EntityGraph(attributePaths = {"requester", "addressee"})
    @Query("""
           SELECT request FROM Friendships request WHERE request.requester.id = :senderUserId
                      AND request.addressee.id = :targetUserId
           """)
    Optional<Friendships> findFriendshipRequest(String senderUserId,String targetUserId);

    @EntityGraph(attributePaths = {"requester", "addressee"})
    @Query("""
           SELECT request FROM Friendships request WHERE (request.requester.id = :currentUserId
                      AND request.addressee.id = :targetUserId) 
                                 OR (request.requester.id = :targetUserId
                      AND request.addressee.id = :currentUserId)
           """)
    Optional<Friendships> findFriendshipBetweenUsers(String currentUserId,String targetUserId);

    @EntityGraph(attributePaths = {"requester", "addressee"})
    @Query("""
            SELECT request FROM Friendships request
            WHERE (request.addressee.id = :userId OR 
            request.requester.id = :userId) 
            AND (request.friendShipStatus = :status)
            """)
    List<Friendships> findFriendshipsByUserId(String userId, FriendShipStatus status);
}
