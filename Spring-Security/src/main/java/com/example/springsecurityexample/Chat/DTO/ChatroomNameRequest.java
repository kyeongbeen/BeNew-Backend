package com.example.springsecurityexample.Chat.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ChatroomNameRequest {
    private String roomId;
    private String roomName;
}
