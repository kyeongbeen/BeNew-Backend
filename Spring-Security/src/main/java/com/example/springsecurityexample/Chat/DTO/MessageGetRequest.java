package com.example.springsecurityexample.Chat.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@Getter
@Setter
public class MessageGetRequest {
    String roomId;
    LocalDate sendDate;
}
