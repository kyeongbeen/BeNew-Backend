package com.example.springsecurityexample.Chat;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService service;

    @GetMapping(path="/rooms/{userId}")
    @ApiOperation("userId에 해당하는 유저가 속한 모든 채팅방을 보여준다")
    public List<ChatRoom> findRooms(@PathVariable("userId") int userId) {
        return service.findRooms(userId);
    }

    @GetMapping(path="/rooms/{userId}/roomId/{roomId}")
    @ApiOperation("userId, roomId에 해당하는 채팅방을 보여준다" +
            "\n 반드시 rooms/{userId} 요청을 보낸 후 사용해야 함")
    public ChatRoom findRoom(@PathVariable("roomId") String roomId) {
        return service.findRoom(roomId);
    }

    @GetMapping(path="/message/{sendDate}/{roomId}")
    @ApiOperation("특정 채팅방, 날짜의 데이터를 모두 조회")
    public List<ChatDTO> getMessages(@PathVariable("sendDate") String sendDate, @PathVariable("roomId") String roomId) {
        return service.getMessages(sendDate, roomId);
    }


    /** 매칭이 성공되면 user1, user2, session까지 받아서 처리, session은 null이여도 상관 없음*/
//    @PostMapping
//    public ChatRoom createRoom(@RequestParam String name) {
//        return service.createRoom(name);
//    }

    /** 매칭이 성공되면 user1, user2, session까지 받아서 처리, session은 null이여도 상관 없음*/
    @PostMapping(path="/new/{user1}/{user2}")
    @ApiOperation("user1, user2가 1:1 매칭되면 채팅방 생성")
    public ChatRoom createRoom(@PathVariable("user1") int user1, @PathVariable("user2") int user2) {
        return service.createRoom(user1, user2);
    }

    @PostMapping(path="/new/{user1}/{session}/{user2}")
    @ApiOperation("user1이 본인의 채팅방에 user2를 초대. user2의 수락, user1의 채팅방에서 투표가 만장일치 시 ")
    public ChatRoom createRoom(@PathVariable("user1") int user1, @PathVariable("session") String session, @PathVariable("user2") int user2) {
        return service.createRoom(user1, session, user2);
    }

    @PostMapping(path="/new/{user1}/{user2}/{session}")
    @ApiOperation("user1이 user2의 채팅방에 입장, user1이 user2의 팀에 팀에 입장 요청")
    public ChatRoom createRoom(@PathVariable("user1") int user1, @PathVariable("user2") int user2, @PathVariable("session") String session) {
        return service.createRoom(user1, user2, session);
    }

    // roomId는 바꾸고 싶은 채팅방 식별자, roomName은 바꿧을때 표시되고 싶은 채팅방 이름
    @PatchMapping(path = "/roomId/{roomId}/{roomName}")
    @ApiOperation("roomId에 해당하는 채팅방의 이름을 roomName으로 변경")
    public void changeRoomName(@PathVariable("roomId") String roomId, @PathVariable("roomName") String roomName) {
        service.changeRoomName(roomId, roomName);
    }

    @DeleteMapping(path="/room/{userId}/{roomId}")
    @ApiOperation("user가 roomId에 해당하는 채팅방을 나간다고 했을때 해당 채팅방 참가 정보 삭제")
    public void leaveRoom(@PathVariable("userId") String userId, @PathVariable("roomId") String roomId) {
        service.leaveRoom(userId, roomId);
    }

    @PostMapping(path="ws://15.164.217.105:32000/ws/chat/{roomId}")
    @ApiOperation("해당 api는 http통신이 아니라서 swagger에서 테스트가 불가능함\n" +
            "websocket주소를 해당 엔드포인트로 잡아서 해야 함\n" +
            "Body의 type에는 TALK, LEAVE를 넣을수 있고 실질적으로 메세지를 보낼때 TALK를 body에 넣어서 보내야 함" +
            "SendDate는 String으로 보내면 되고 날짜 시간까지 보내면 됨" +
            "추후 전체 메세지를 받을때는 날짜기반으로 parsing 하기 때문에 최소한 날짜까지는 넣어서 보내야 함" +
            "sender의 경우 해당 유저가 int type으로 가지고 있는 id를 포함해서 보내면 됨" +
            "엔드포인트에 있는 roomId와 body에 들어가는 roomId는 같은 값이므로 꼭 포함해서 보내야 원하는 채팅방에 메세지를 보낼 수 있음" +
            "message는 당연히 String으로 보내야 함")
    public MessageDTO webSocketConnectionRequest(@RequestBody MessageDTO MessageDto) {
        return MessageDto;
    }

}
