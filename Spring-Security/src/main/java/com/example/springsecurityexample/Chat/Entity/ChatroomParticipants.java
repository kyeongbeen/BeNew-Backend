package com.example.springsecurityexample.Chat.Entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ChatroomParticipants {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int sequence;
    private int userId;
    private String roomId;
    private Timestamp enterDate;
}
