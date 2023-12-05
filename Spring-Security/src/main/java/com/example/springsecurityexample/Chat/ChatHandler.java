package com.example.springsecurityexample.Chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final ChatService service;
    private ChatRoom chatroom = new ChatRoom();

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = (String) message.getPayload();
//        TextMessage intialGretting = new TextMessage("Welcome to Chat Server");
//        JSON -> Java Object
        ChatDTO chatMessage = mapper.readValue(payload, ChatDTO.class);

//        chatroom = service.findRoom(chatMessage.getRoomId());
        chatroom.handleAction(session,chatMessage, service);
//        ChatRoom room = service.findRoom(chatMessage.getRoomId());
//        room.handleAction(session,chatMessage, service);

        service.insertMessage(chatMessage, chatroom);
    }

    /** Client가 접속 시 호출되는 메서드*/
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        // 특정 채팅방에 입장하면 해당
//        log.info(session + " 클라이언트 접속");
//        //chatroom.getSessions().add(session);
//        chatroom.enterAction(session, service);
//
//
//    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 특정 채팅방에 입장하면 해당
        log.info(session + " 클라이언트 접속");
        //chatroom.getSessions().add(session);
        String path = session.getUri().getPath();
        String roomId = path.substring(path.lastIndexOf("/") + 1);


        log.info("\n\n\n\n\n roomId = " + roomId);
//        chatroom = service.findRoom(chatMessage.getRoomId());
        chatroom = service.findRoom(roomId);
        chatroom.getSessions().add(session);

//        chatroom.enterAction(session, service);
    }

    /** client가 퇴장 시 호출되는 메서드*/
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info(session + " 클라이언트 접속 해제");
        chatroom.getSessions().remove(session);
    }
}
