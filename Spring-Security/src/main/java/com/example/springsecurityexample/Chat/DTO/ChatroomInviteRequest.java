package com.example.springsecurityexample.Chat.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class ChatroomInviteRequest {
    private int user1;
    private String roomId;
    private int invitedUser;
}