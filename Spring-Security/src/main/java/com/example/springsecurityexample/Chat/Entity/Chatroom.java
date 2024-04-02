package com.example.springsecurityexample.Chat.Entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chatroom {
    @Id
    private String roomId;
    private String roomName;
    private Timestamp createDate;
}
