package com.example.springsecurityexample.team.dto;

import com.example.springsecurityexample.member.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequestDto {
    private Profile teamMember1;
    private Profile teamMember2;
    private int numberOfMembers;
    private String teamName;
    private String teamDescription;
}
