package com.example.springsecurityexample.Chat.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter @Setter
public class ChatroomCreateRequest {
    private int user1;
    private int user2;
    private String user1Name;
    private String user2Name;
}
