package com.example.springsecurityexample.Chat.Controller;

import com.example.springsecurityexample.Chat.DTO.MessageRequest;
import com.example.springsecurityexample.Chat.Entity.Message;
import com.example.springsecurityexample.Chat.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    //    @MessageMapping("/message")
//    @SendTo("/topic/message")
//    public ResponseEntity<MessageRequest> sendMessage(MessageRequest message) throws Exception{
//        chatService.insertData(message);
//
//        return new ResponseEntity<>(message, HttpStatus.OK);
//    }
    @MessageMapping("/message/{roomId}")
    public void sendMessage(MessageRequest messageDTO) throws Exception{
        Message message = messageService.insertData(messageDTO);
//        template.convertAndSend("/sub/message/" + message.getRoomId(), message);
        template.convertAndSend("/sub/message/" + message.getRoomId(), message);
    }
//    @GetMapping(path = "/message")
//    public ResponseEntity<List<Message>> getMessages(@RequestBody String roomId, @RequestBody Timestamp sendDate) {
//        return new ResponseEntity<> (messageService.getMessages(roomId, sendDate), HttpStatus.OK);
//    }


}
