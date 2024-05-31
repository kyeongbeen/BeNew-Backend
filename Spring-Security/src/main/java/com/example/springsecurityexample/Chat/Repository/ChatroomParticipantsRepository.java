package com.example.springsecurityexample.Chat.Repository;

import com.example.springsecurityexample.Chat.Entity.ChatroomParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.desktop.QuitEvent;
import java.util.List;

@Repository
public interface ChatroomParticipantsRepository extends JpaRepository<ChatroomParticipants, Long> {
    List<ChatroomParticipants> findAllByRoomId(String roomId);
    void deleteByRoomIdAndUserId(String roomId, int userId);
    ChatroomParticipants findByUserId(int userId);


    @Query("select cp " +
            "from ChatroomParticipants cp " +
            "where cp.roomId = (select c.roomId " +
                                "from Chatroom c " +
                                "where c.projectId = :projectId) ")
    List<ChatroomParticipants> findMembers(Long projectId);

}
