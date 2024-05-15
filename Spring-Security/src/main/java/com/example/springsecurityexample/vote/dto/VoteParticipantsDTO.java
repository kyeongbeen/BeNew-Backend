package com.example.springsecurityexample.vote.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Data @Builder
public class VoteParticipantsDTO {
    private int voteId;
    private long userId;
    private int voteContentsId; // 투표항목
}
