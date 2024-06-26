package com.example.springsecurityexample.Chat.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
public class MessageRequest {
    private String roomId;
    private int sender;
    private String senderName;
    private String message;
    private LocalDate sendDate;
}
