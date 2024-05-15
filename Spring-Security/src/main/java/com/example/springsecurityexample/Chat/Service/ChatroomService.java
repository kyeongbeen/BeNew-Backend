package com.example.springsecurityexample.Chat.Service;

import com.example.springsecurityexample.Chat.DTO.*;
import com.example.springsecurityexample.Chat.Entity.Chatroom;
import com.example.springsecurityexample.Chat.Entity.ChatroomParticipants;
import com.example.springsecurityexample.Chat.Repository.ChatroomParticipantsRepository;
import com.example.springsecurityexample.Chat.Repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatroomParticipantsRepository chatroomParticipantsRepository;
    private final ChatroomRepository chatroomRepository;

    public List<ChatroomParticipants> findRooms(int userId) {
        return chatroomParticipantsRepository.findAllByUserId(userId);
    }

    public Chatroom createRoom(List<ChatroomCreateRequest> request) {
        String roomId = UUID.randomUUID().toString();
        String roomName = new StringBuilder().append(request.get(0).getUserName()).append("님 외 ").append(request.size()).append("명의 채팅방").toString();
        Timestamp createDate = Timestamp.valueOf(LocalDateTime.now());

        Chatroom chatroom = new Chatroom(roomId, roomName, createDate);

        chatroomRepository.save(chatroom);

        for (var i :
                request) {
            chatroomParticipantsRepository.save(new ChatroomParticipants().builder()
                            .userId(i.getUserId())
                            .roomId(roomId)
                            .enterDate(createDate)
                    .build());
        }
        return chatroom;
    }

    public ChatroomParticipants inviteRoom(ChatroomInviteRequest request) {
        Timestamp invitedDate = Timestamp.valueOf(LocalDateTime.now());
        ChatroomParticipants participants = new ChatroomParticipants().builder()
                .userId(request.getInvitedUser())
                .roomId(request.getRoomId())
                .enterDate(invitedDate)
                .build();
        chatroomParticipantsRepository.save(participants);
        return participants;
    }

    // JPA Dirty Checking
    public Chatroom changeRoomName(ChatroomNameRequest request) {
        Chatroom chatroom = chatroomRepository.findByRoomId(request.getRoomId());
        chatroom.setRoomName(request.getRoomName());
        chatroom = chatroomRepository.findByRoomId(request.getRoomId());
        return chatroom;
    }

    public void leaveRoom(ChatroomLeaveRequest request) {
        chatroomParticipantsRepository.deleteByRoomIdAndUserId(request.getRoomId(), request.getUserId());

    }
}
