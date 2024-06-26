package com.example.springsecurityexample.Chat.Controller;

import com.example.springsecurityexample.Chat.DTO.MessageGetRequest;
import com.example.springsecurityexample.Chat.DTO.MessageRequest;
import com.example.springsecurityexample.Chat.Entity.Message;
import com.example.springsecurityexample.Chat.Service.MessageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    @MessageMapping("/message/{roomId}")
    public void sendMessage(MessageRequest messageDTO) throws Exception{
        Message message = messageService.insertData(messageDTO);
//        template.convertAndSend("/sub/message/" + message.getRoomId(), message);
        template.convertAndSend("/sub/message/" + message.getRoomId(), message);
    }
    @GetMapping(path = "/message/{roomId}/{sendDate}")
    @ApiOperation(
            value = "메세지 조회"
            , notes = "특정 날짜의 메세지를 조회한다.. \n특정 날짜의 메세지만을 조회하기 때문에 이전 메세지를 조회하고 싶을 때는 추가적으로 API를 호출해야 함.\n")
//    public ResponseEntity<List<Message>> getMessages(@RequestBody MessageGetRequest messageGetRequest) {
//        return new ResponseEntity<>(messageService.getMessages(messageGetRequest.getRoomId(), messageGetRequest.getSendDate()), HttpStatus.OK);
//    }

    public ResponseEntity<List<Message>> getMessages(@PathVariable("roomId") String roomId, @PathVariable("sendDate") String sendDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(sendDate, formatter);
        return new ResponseEntity<>(messageService.getMessages(roomId, date), HttpStatus.OK);
    }

}
