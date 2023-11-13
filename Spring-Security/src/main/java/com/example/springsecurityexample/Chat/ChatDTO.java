package com.example.springsecurityexample.Chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@Getter @Setter
public class ChatDTO {
    public enum MessageType {
        ENTER, TALK, LEAVE
    }
    private MessageType type;
    private String roomId;
    private int sender;
    private String message;
    private Timestamp senddate;


    @JsonCreator
    public ChatDTO(@JsonProperty("type") MessageType type, @JsonProperty("roomId") String roomId, @JsonProperty("sender") int sender, @JsonProperty("message") String message, @JsonProperty("sendDate") Timestamp sendDate) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.senddate = sendDate;
    }
}