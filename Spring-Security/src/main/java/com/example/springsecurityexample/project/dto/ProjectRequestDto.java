package com.example.springsecurityexample.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {
    private Long userId;
    private String projectName;
    private String projectOneLineIntroduction;
    private String projectIntroduction;
    private String chatroomId;
}
