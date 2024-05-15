package com.example.springsecurityexample.Chat.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Builder
@Getter
@Setter
public class MessageGetRequest {
    String roomId;
    Timestamp sendDate;
}
