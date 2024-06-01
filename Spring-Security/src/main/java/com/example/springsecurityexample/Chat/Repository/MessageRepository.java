package com.example.springsecurityexample.Chat.Repository;

import com.example.springsecurityexample.Chat.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findMessagesByRoomIdAndSendDate(String roomId, LocalDate sendDate);
}
