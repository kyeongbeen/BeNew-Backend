package com.example.springsecurityexample.Chat.Entity;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
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
    @Nullable
    private Integer isProjectStarted;
    @Nullable
    private Long projectId;


}
