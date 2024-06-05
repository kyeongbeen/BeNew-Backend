package com.example.springsecurityexample.Chat.DTO;

import lombok.*;

@Data
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ChatroomBindingRequest {
    private String roomId;
    private Long projectId;

}
