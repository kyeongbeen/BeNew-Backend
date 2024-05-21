package com.example.springsecurityexample.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequest {
    private Long memberId;
    private Long friendId;
}
