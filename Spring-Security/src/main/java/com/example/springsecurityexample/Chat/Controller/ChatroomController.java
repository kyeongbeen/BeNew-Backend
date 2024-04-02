package com.example.springsecurityexample.Chat.Controller;

import com.example.springsecurityexample.Chat.DTO.ChatroomCreateRequest;
import com.example.springsecurityexample.Chat.DTO.ChatroomInviteRequest;
import com.example.springsecurityexample.Chat.DTO.ChatroomLeaveRequest;
import com.example.springsecurityexample.Chat.DTO.ChatroomNameRequest;
import com.example.springsecurityexample.Chat.Entity.Chatroom;
import com.example.springsecurityexample.Chat.Entity.ChatroomParticipants;
import com.example.springsecurityexample.Chat.Service.ChatroomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/chat")
@RequiredArgsConstructor
public class ChatroomController {

    private final ChatroomService chatroomService;

    @GetMapping(path = "/rooms/{userId}")
    public ResponseEntity<List<ChatroomParticipants>> findRooms(@PathVariable int userId) {
        return new ResponseEntity<>(chatroomService.findRooms(userId), HttpStatus.OK);
    }

    @PostMapping(path = "/new")
    public ResponseEntity<Chatroom> createRoom(@RequestBody ChatroomCreateRequest request) {
        return new ResponseEntity<>(chatroomService.createRoom(request), HttpStatus.CREATED);
    }

    @PostMapping(path = "/invite")
    public ResponseEntity<ChatroomParticipants> inviteRoom(@RequestBody ChatroomInviteRequest request) {
        return new ResponseEntity<>(chatroomService.inviteRoom(request), HttpStatus.ACCEPTED);
    }

    @PatchMapping(path = "/roomId")
    public ResponseEntity<Chatroom> changeRoomName(@RequestBody ChatroomNameRequest request) {
        return new ResponseEntity<>(chatroomService.changeRoomName(request), HttpStatus.ACCEPTED);
    }

    @DeleteMapping(path = "/room")
    public void leaveRoom(@RequestBody ChatroomLeaveRequest request) {
        chatroomService.leaveRoom(request);
    }
}
