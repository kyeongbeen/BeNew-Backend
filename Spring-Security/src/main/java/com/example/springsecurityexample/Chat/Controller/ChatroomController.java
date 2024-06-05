package com.example.springsecurityexample.Chat.Controller;

import com.example.springsecurityexample.Chat.DTO.*;
import com.example.springsecurityexample.Chat.Entity.Chatroom;
import com.example.springsecurityexample.Chat.Entity.ChatroomParticipants;
import com.example.springsecurityexample.Chat.Service.ChatroomService;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Response;
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

    @GetMapping(path = "/room/member/{roomId}")
    @ApiOperation(
            value = "채팅방에 속한 유저들 조회",
            notes = "채팅방에 현재 있는 유저들을 조회한다.\n" +
                    "A 채팅방에 김, 이, 박 세명의 유저가 있으면 세명의 정보를 알려준다."
    )
    public ResponseEntity<List<ChatroomParticipants>> findMembers(@PathVariable String roomId) {
        return new ResponseEntity<>(chatroomService.findMembers(roomId), HttpStatus.OK);
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

    @PatchMapping("/room/project/bind")
    @ApiOperation(value = "채팅방과 프로젝트를 바인딩한다.",
                    notes = "어떤 채팅방에서 어떤 프로젝트가 진행되는지 볼 수 있으면 좋을 것 같음")
    public ResponseEntity<Chatroom> bindChatroomAndProject(@RequestBody ChatroomBindingRequest chatroomBindingRequest) {
        return new ResponseEntity<>(chatroomService.registerProject(chatroomBindingRequest.getProjectId(), chatroomBindingRequest.getRoomId()), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/room/project/{projectId}/start")
    @ApiOperation(value = "프로젝트의 상태를 시작으로 변경한다.",
                    notes = "종료날짜를 입력하지 않으면 프로젝트가 시작된것이 아니기 때문에 추가함")
    public ResponseEntity<Chatroom> startChatroom(@PathVariable Long projectId) {
        return new ResponseEntity<>(chatroomService.startProject(projectId), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/room/project/{projectId}/end")
    @ApiOperation(value = "프로젝트의 상태를 종료로 변경한다.",
                    notes = "동료평가를 진행하기 위해서 상태를 변경하는 것")
    public ResponseEntity<Chatroom> endProject(@PathVariable Long projectId) {
        return new ResponseEntity<>(chatroomService.endProject(projectId), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/room/project/{projectId}/delete")
    @ApiOperation(value = "채팅방과 프로젝트의 바인딩된 정보를 삭제한다.")
    public ResponseEntity<Chatroom> deleteProject(@PathVariable Long projectId) {
        return new ResponseEntity<>(chatroomService.deleteProject(projectId), HttpStatus.ACCEPTED);
    }

}
