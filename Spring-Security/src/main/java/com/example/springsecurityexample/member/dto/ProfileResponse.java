package com.example.springsecurityexample.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileResponse {
    private Long id;
    private String nickname;
    private String instruction;
    private String role;
    private boolean projectExperience;
    private String personalLink;
    private String photo;
}
