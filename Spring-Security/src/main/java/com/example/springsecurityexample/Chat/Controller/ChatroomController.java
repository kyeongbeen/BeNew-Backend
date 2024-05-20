package com.example.springsecurityexample.Chat.Controller;

import com.example.springsecurityexample.Chat.DTO.*;
import com.example.springsecurityexample.Chat.Entity.Chatroom;
import com.example.springsecurityexample.Chat.Entity.ChatroomParticipants;
import com.example.springsecurityexample.Chat.Service.ChatroomService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(
            value = "채팅방 조회"
            , notes = "현재 내가 속해있는 채팅방 전체를 조회한다.\n")
    public ResponseEntity<List<Chatroom>> findRooms(@PathVariable int userId) {
        return new ResponseEntity<>(chatroomService.findRooms(userId), HttpStatus.OK);
    }

    @PostMapping(path = "/new")
    @ApiOperation(
            value = "채팅방 생성"
            , notes = "채팅방을 생성한다.\n" +
            "userId, userName을 List형태로 담아서 API를 호출하면 됨.\n" +
            "1:1 매칭이 성공하였을때도 user1, user2의 정보를 담아서 API를 호출하면 됨.")
    public ResponseEntity<Chatroom> createRoom(@RequestBody List<ChatroomCreateRequest> request) {
        return new ResponseEntity<>(chatroomService.createRoom(request), HttpStatus.CREATED);
    }

    @PostMapping(path = "/invite")
    @ApiOperation(
            value = "채팅방 생성"
            , notes = "채팅방에 매칭성공된 사람을 추가한다. \n" +
            "만들어져 있는 채팅방에서 추가적인 인원을 추가할 때 사용한다.\n")
    public ResponseEntity<ChatroomParticipants> inviteRoom(@RequestBody ChatroomInviteRequest request) {
        return new ResponseEntity<>(chatroomService.inviteRoom(request), HttpStatus.ACCEPTED);
    }

    @PatchMapping(path = "/roomId")
    @ApiOperation(
            value = "채팅방 이름 변경"
            , notes = "채팅방 이름을 변경한다. \n" +
            "초기값이 \"A님 외 n명의 채팅방\"로 설정되기 때문에 필요하다고 판단하였음.\n")
    public ResponseEntity<Chatroom> changeRoomName(@RequestBody ChatroomNameRequest request) {
        return new ResponseEntity<>(chatroomService.changeRoomName(request), HttpStatus.ACCEPTED);
    }

    @DeleteMapping(path = "/room")
    @ApiOperation(
            value = "채팅방 퇴장"
            , notes = "사용자가 채팅방에서 나간다. \n" +
            "카카오톡에서 채팅방을 나가는것과 같은 기능이라고 생각하면 됨.\n")
    public void leaveRoom(@RequestBody ChatroomLeaveRequest request) {
        chatroomService.leaveRoom(request);
    }
}
