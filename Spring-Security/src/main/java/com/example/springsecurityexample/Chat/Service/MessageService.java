package com.example.springsecurityexample.Chat.Service;


import com.example.springsecurityexample.Chat.DTO.MessageRequest;
import com.example.springsecurityexample.Chat.Entity.Message;
import com.example.springsecurityexample.Chat.Repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> getMessages(String roomId, LocalDate sendDate) {
        return messageRepository.findMessagesByRoomIdAndSendDate(roomId, sendDate);
    }

    public Message insertData(MessageRequest data) {
        Message message = Message.builder()
                .roomId(data.getRoomId())
                .sender(data.getSender())
                .senderName(data.getSenderName())
                .message(data.getMessage())
                .sendDate(LocalDate.now())
                .build();
        messageRepository.save(message);
        return message;
    }
}