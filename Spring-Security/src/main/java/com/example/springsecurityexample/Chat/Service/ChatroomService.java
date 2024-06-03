package com.example.springsecurityexample.Chat.Service;

import com.example.springsecurityexample.Chat.DTO.*;
import com.example.springsecurityexample.Chat.Entity.Chatroom;
import com.example.springsecurityexample.Chat.Entity.ChatroomParticipants;
import com.example.springsecurityexample.Chat.Repository.ChatroomParticipantsRepository;
import com.example.springsecurityexample.Chat.Repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {

    private final ChatroomParticipantsRepository chatroomParticipantsRepository;
    private final ChatroomRepository chatroomRepository;
    private final JdbcTemplate jdbcTemplate;


    public List<Chatroom> findRooms(int userId) {

        String selectQuery = "select * from chatroom where room_id in (select room_id from chatroom_participants where user_id = ?)";
        Object[] user = {userId};
        List<Map<String,Object>> roomList = jdbcTemplate.queryForList(selectQuery, user);

        Map<String, Chatroom> chatrooms = new LinkedHashMap<>();
        for (Map<String, Object> row : roomList) {
            Chatroom chatroom = new Chatroom().builder()
                    .roomId(row.get("room_id").toString())
                    .roomName(row.get("room_name").toString())
                    .createDate(Timestamp.valueOf(row.get("create_date").toString()))
                    .isProjectStarted(Integer.parseInt(row.get("is_project_started").toString()))
                    .projectId(Long.parseLong(row.get("project_id").toString()))
                    .build();
            chatrooms.put(row.get("room_id").toString(), chatroom);
        }

        return new ArrayList<>(chatrooms.values());
    }

    public Chatroom createRoom(List<ChatroomCreateRequest> request) {
        String roomId = UUID.randomUUID().toString();
        String roomName = new StringBuilder().append(request.get(0).getUserName()).append("님 외 ").append(request.size() - 1).append("명의 채팅방").toString();
        Timestamp createDate = Timestamp.valueOf(LocalDateTime.now());

        Chatroom chatroom = new Chatroom().builder()
                .roomId(roomId)
                .roomName(roomName)
                .createDate(createDate)
                .isProjectStarted(0)
                .projectId(-1L)
                .build();
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


    // JPA Dirty Checking
    public void registerProject(long projectId, String chatRoomId) {
        Chatroom chatroom = chatroomRepository.findByRoomId(chatRoomId);
        chatroom.setProjectId(projectId);
    }

    public void startProject(Long projectId) {
        Chatroom chatroom = chatroomRepository.findByProjectId(projectId);
        chatroom.setIsProjectStarted(1);
    }

    public List<ChatroomParticipants> findMembers(String roomId) {
        return chatroomParticipantsRepository.findAllByRoomId(roomId);
    }
}
