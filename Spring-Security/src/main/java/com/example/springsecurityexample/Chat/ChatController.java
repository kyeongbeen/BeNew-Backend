package com.example.springsecurityexample.Chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService service;

    @GetMapping(path="/rooms/{userId}")
    public List<ChatRoom> findRooms(@PathVariable("userId") int userId) {
        return service.findRooms(userId);
    }

    @GetMapping(path="/rooms/{userId}/roomId/{roomId}")
    public ChatRoom findRoom(@PathVariable("roomId") String roomId) {
        return service.findRoom(roomId);
    }

    @GetMapping(path="/message/{sendDate}/{roomId}")
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
    public ChatRoom createRoom(@PathVariable("user1") int user1, @PathVariable("user2") int user2) {
        return service.createRoom(user1, user2);
    }

//    @PostMapping(path="/new/{user1}/{user2}/{session}")
//    public ChatRoom createRoom(@PathVariable("user1") String user1, @PathVariable("user2") String user2, @PathVariable("session") String session) {
//        return service.createRoom(user1, user2, session);
//    }

    @PostMapping(path="/new/{user1}/{session}/{user2}")
    public ChatRoom createRoom(@PathVariable("user1") int user1, @PathVariable("session") String session, @PathVariable("user2") int user2) {
        return service.createRoom(user1, session, user2);
    }

    @PostMapping(path="/new/{user1}/{user2}/{session}")
    public ChatRoom createRoom(@PathVariable("user1") int user1, @PathVariable("user2") int user2, @PathVariable("session") String session) {
        return service.createRoom(user1, user2, session);
    }


    // roomId는 바꾸고 싶은 채팅방 식별자, roomName은 바꿧을때 표시되고 싶은 채팅방 이름
    @PatchMapping(path = "/roomId/{roomId}/{roomName}")
    public void changeRoomName(@PathVariable("roomId") String roomId, @PathVariable("roomName") String roomName) {
        service.changeRoomName(roomId, roomName);
    }

    @DeleteMapping(path="/room/{userId}/{roomId}")
    public void leaveRoom(@PathVariable("userId") String userId, @PathVariable("roomId") String roomId) {
        service.leaveRoom(userId, roomId);
    }
}
