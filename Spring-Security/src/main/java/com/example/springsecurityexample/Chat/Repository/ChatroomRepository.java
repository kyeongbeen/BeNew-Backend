package com.example.springsecurityexample.Chat.Repository;

import com.example.springsecurityexample.Chat.Entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
    Chatroom findByRoomId(String roomId);
    Chatroom findByProjectId(Long projectId);
}
