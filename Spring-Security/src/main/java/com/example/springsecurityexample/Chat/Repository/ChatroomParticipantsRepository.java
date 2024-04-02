package com.example.springsecurityexample.Chat.Repository;

import com.example.springsecurityexample.Chat.Entity.ChatroomParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatroomParticipantsRepository extends JpaRepository<ChatroomParticipants, Long> {
    List<ChatroomParticipants> findAllByUserId(int userId);
    void deleteByRoomIdAndUserId(String roomId, int userId);
}
