package com.example.springsecurityexample.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendResponse {
    private Long id;
    private Long memberId;
    private Long friendId;
    private String status;
}