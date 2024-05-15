package com.example.springsecurityexample.vote.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data @Getter @Setter @Builder
public class VoteContentsDTO {

    private int voteContentsId;
    private int voteId;
    private String content;
    private int voteNum;

}
