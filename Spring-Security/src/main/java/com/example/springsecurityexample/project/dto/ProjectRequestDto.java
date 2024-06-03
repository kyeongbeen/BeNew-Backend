package com.example.springsecurityexample.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {
    private Long userId;
    private Long projectManager;
    private String projectName;
    private String projectOneLineIntroduction;
    private String projectIntroduction;

    @Column
    @Nullable
    private String chatroomId;
}
