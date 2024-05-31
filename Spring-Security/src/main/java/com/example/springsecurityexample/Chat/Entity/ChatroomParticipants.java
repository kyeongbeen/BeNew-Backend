package com.example.springsecurityexample.Chat.Entity;


import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class ChatroomParticipants {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int sequence;
    private int userId;
    private String roomId;
    private Timestamp enterDate;
    @Column
    private boolean isReviewed = true;
}
