package com.example.springsecurityexample.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDto {
    private Integer matchId;
    private int uid1;// replace uid
    private int uid2;// replace matchingId
    private Boolean isUid2Team;
    private LocalDateTime matchingDate;
    private Boolean matchingRequest;
    private Boolean matchSuccess;
}
