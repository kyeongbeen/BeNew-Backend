package com.example.springsecurityexample.team.controller;

import com.example.springsecurityexample.team.Team;
import com.example.springsecurityexample.team.dto.TeamRequestDto;
import com.example.springsecurityexample.team.service.TeamService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor //DI
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class TeamController {


    private final TeamService teamService;

    @ApiOperation(
            value = "팀 생성 (매칭 성사 시)"
            , notes = " api 문서 링크 : \n")
    @PostMapping("/post/team")
    public ResponseEntity<Team> RegisterTeam(@RequestBody TeamRequestDto teamRequestDto) {
        // 팀 등록 로직 수행
        Team team = teamService.RegisterTeam(teamRequestDto);
        return ResponseEntity.ok(team);
    }

    @ApiOperation(
            value = "유저가 속한 팀 조회"
            , notes = " api 문서 링크 : \n")
    @GetMapping("/get/team/{userId}")
    public ResponseEntity<List<Team>> GetUserTeams(@PathVariable Long userId) {
        // 사용자의 ID를 가져와 해당 사용자가 속한 팀 조회
        List<Team> userTeams = teamService.GetUserTeams(userId);
        return ResponseEntity.ok(userTeams);
    }

    @ApiOperation(
            value = "팀원 추가"
            , notes = " api 문서 링크 : \n")
    @PutMapping("/put/{teamId}/{userId}")
    public ResponseEntity<Team> AddTeamMember(@PathVariable Long teamId, @PathVariable Long userId) {
        // 팀원 추가 로직 수행
        Team team = teamService.AddTeamMember(teamId, userId);
        return ResponseEntity.ok(team);
    }

    // 팀 이름 중복 확인
    @ApiOperation(
            value = "팀 이름 중복 확인(true : 중복 or 빈 문자열 / false : 중복 아님)",
            notes = "팀 이름이 이미 존재하는지 확인합니다."
    )
    @GetMapping("/check/teamName")
    public ResponseEntity<Boolean> CheckTeamNameDuplicate(@RequestParam String teamName) {
        boolean isDuplicate = teamService.CheckTeamNameDuplicate(teamName);
        return ResponseEntity.ok(isDuplicate);
    }

    // 팀 정보 수정
    @ApiOperation(
            value = "팀 정보 수정",
            notes = "팀의 정보를 수정합니다."
    )
    @PutMapping("/update/team/{teamId}")
    public ResponseEntity<Team> UpdateTeamInfo(@PathVariable Long teamId, @RequestBody TeamRequestDto teamRequestDto) {
        Team updatedTeam = teamService.UpdateTeamInfo(teamId, teamRequestDto);
        return ResponseEntity.ok(updatedTeam);
    }

    // 생성된 팀 전체 조회 ** 팀을 어떤 기준으로 보여줘야 하나 (최신순, 인기순, 사용언어 + a)
    @ApiOperation(
            value = "생성된 팀 전체 조회",
            notes = "모든 생성된 팀을 조회합니다."
    )
    @GetMapping("/get/teams")
    public ResponseEntity<List<Team>> GetAllTeams() {
        List<Team> teams = teamService.GetAllTeams();
        return ResponseEntity.ok(teams);
    }

    // 팀원 강퇴
    @ApiOperation(
            value = "팀원 강퇴",
            notes = "팀에서 특정 팀원을 강퇴합니다."
    )
    @DeleteMapping("/delete/{teamId}/member/{memberId}")
    public ResponseEntity<Void> RemoveTeamMember(@PathVariable Long teamId, @PathVariable Long memberId) {
        teamService.RemoveTeamMember(teamId, memberId);
        return ResponseEntity.ok().build();
    }

    // 팀 탈퇴
    @ApiOperation(
            value = "팀 탈퇴",
            notes = "특정 팀에서 사용자가 팀을 탈퇴합니다."
    )
    @DeleteMapping("/delete/team/{teamId}/leave")
    public ResponseEntity<Void> LeaveTeam(@PathVariable Long teamId) {
        teamService.LeaveTeam(teamId);
        return ResponseEntity.ok().build();
    }

    // 팀 상세 정보
    @ApiOperation(
            value = "팀 상세 정보",
            notes = "특정 팀의 상세 정보를 조회합니다."
    )
    @GetMapping("/get/team/{teamId}")
    public ResponseEntity<Team> GetTeamDetails(@PathVariable Long teamId) {
        Team team = teamService.GetTeamDetails(teamId);
        return ResponseEntity.ok(team);
    }

    // 팀 해체 (프로젝트 종료)
    @ApiOperation(
            value = "팀 해체 (프로젝트 종료)",
            notes = "특정 팀을 해체하여 프로젝트를 종료합니다."
    )
    @DeleteMapping("/delete/team/{teamId}/disband")
    public ResponseEntity<Void> DisbandTeam(@PathVariable Long teamId) {
        teamService.DisbandTeam(teamId);
        return ResponseEntity.ok().build();
    }

    // 신청한 팀 목록 조회
    @ApiOperation(
            value = "신청한 팀 목록 조회",
            notes = "사용자가 신청한 팀 목록을 조회합니다."
    )
    @GetMapping("/get/applied-teams/{userId}")
    public ResponseEntity<List<Team>> GetAppliedTeams(@PathVariable Long userId) {
        List<Team> appliedTeams = teamService.GetAppliedTeams(userId);
        return ResponseEntity.ok(appliedTeams);
    }
}
