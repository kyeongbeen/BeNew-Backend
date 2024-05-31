package com.example.springsecurityexample.project.controller;

import com.example.springsecurityexample.Chat.Service.ChatroomService;
import com.example.springsecurityexample.PeerReview.Service.PeerReviewService;
import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.project.Project;
import com.example.springsecurityexample.project.dto.ProjectDeadlineRequestDto;
import com.example.springsecurityexample.project.dto.ProjectRequestDto;
import com.example.springsecurityexample.project.dto.ProjectResponseDto;
import com.example.springsecurityexample.project.service.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor //DI
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class ProjectController {


    private final ProjectService projectService;
    private final ChatroomService chatroomService;
    private final PeerReviewService peerReviewService;

    @ApiOperation(
            value = "프로젝트 생성 (!! 추가됨 - projectManager : 방장의 id를 저장하는 값)"
            , notes = " api 문서 링크 : \n")
    @PostMapping("/post/project")
    public ResponseEntity<Project> RegisterProject(@RequestBody ProjectRequestDto projectRequestDto) {
        Project project = projectService.RegisterProject(projectRequestDto);
        chatroomService.registerProject(project.getProjectId(), projectRequestDto.getChatroomId());
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    @ApiOperation(
            value = "유저가 속한 project 조회"
            , notes = " api 문서 링크 : \n")
    @GetMapping("/get/project/include/{userId}")
    public ResponseEntity<List<Project>> GetUserProjects(@PathVariable Long userId) {
        // 사용자의 ID를 가져와 해당 사용자가 속한 팀 조회
        return new ResponseEntity<>(projectService.GetUserProjects(userId), HttpStatus.OK);
    }

    @ApiOperation(
            value = "project 팀원 추가"
            , notes = " api 문서 링크 : \n")
    @PatchMapping("/patch/project/{projectId}/member/{userId}")
    public ResponseEntity<Project> AddProjectMember(@PathVariable Long projectId, @PathVariable Long userId) {
        // 팀원 추가 로직 수행
        return new ResponseEntity<>(projectService.AddProjectMember(projectId, userId), HttpStatus.OK);
    }

    // 팀 이름 중복 확인
    @ApiOperation(
            value = "project 이름 중복 확인(true : 중복 or 빈 문자열 / false : 중복 아님)",
            notes = "project 이름이 이미 존재하는지 확인합니다."
    )
    @GetMapping("/get/project/check/project-name")
    public ResponseEntity<Boolean> CheckProjectNameDuplicate(@RequestParam String projectName) {
        return new ResponseEntity<>(projectService.CheckProjectNameDuplicate(projectName), HttpStatus.OK);
    }

    // 프로젝트 방장 확인
    @ApiOperation(
            value = "project 방장 확인(true : 방장임 / false : 방장 아님)",
            notes = "project 방장 확인"
    )
    @GetMapping("/get/project/check/project-manager/{projectId}/{projectManager}")
    public ResponseEntity<Boolean> CheckProjectManager(@PathVariable Long projectId, @PathVariable Long projectManager) {
        return new ResponseEntity<>(projectService.CheckProjectManager(projectId, projectManager), HttpStatus.OK);
    }

    // 팀 정보 수정
    @ApiOperation(
            value = "팀 정보 수정",
            notes = "팀의 정보를 수정합니다."
    )
    @PatchMapping("/patch/project/{projectId}")
    public ResponseEntity<Project> UpdateProjectInfo(@PathVariable Long projectId, @RequestBody ProjectRequestDto projectRequestDto) {
        return new ResponseEntity<>(projectService.UpdateProjectInfo(projectId, projectRequestDto), HttpStatus.OK);
    }

    // 생성된 팀 전체 조회 ** 팀을 어떤 기준으로 보여줘야 하나 (최신순, 인기순, 사용언어 + a)
    @ApiOperation(
            value = "생성된 프로젝트 전체 조회",
            notes = "모든 생성된 프로젝트을 조회합니다."
    )
    @GetMapping("/get/projects")
    public ResponseEntity<List<Project>> GetAllProjects() {
        return new ResponseEntity<>(projectService.GetAllProjects(), HttpStatus.OK);
    }

    @ApiOperation(
            value = "생성일 기준 내림차순으로 정렬된 프로젝트 가져오기",
            notes = "page : 페이지 (0 부터 시작) / size : 가져올 프로젝트의 수."
    )
    @GetMapping("/get/projects/recent")
    public ResponseEntity<Page<Project>> GetRecentProjects(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<Project> projects = projectService.GetRecentProjects(page, size);
        if (projects.hasContent()) {
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @ApiOperation(
            value = "조회수 기준 인기 프로젝트 조회",
            notes = "page : 페이지 (0 부터 시작) / size : 가져올 프로젝트의 수."
    )
    @GetMapping("/get/projects/popular")
    public ResponseEntity<Page<Project>> GetPopularityProjects(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<Project> projects = projectService.GetPopularityProjects(page, size);
        if (projects.hasContent()) {
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    // 프로젝트 맴버 강퇴
    @ApiOperation(
            value = "프로젝트 맴버 제거",
            notes = "프로젝트에서 특정 팀원을 제거합니다."
    )
    @DeleteMapping("/delete/project/{projectId}/member/{userId}")
    public ResponseEntity<Project> RemoveProjectMember(@PathVariable Long projectId, @PathVariable Long userId) {
        return new ResponseEntity<>(projectService.RemoveProjectMember(projectId, userId), HttpStatus.OK);
    }

    // 프로젝트 시작
    @ApiOperation(
            value = "프로젝트 시작",
            notes = "프로젝트를 시작합니다."
    )
    @PatchMapping("/patch/project/start/{projectId}")
    public ResponseEntity<Project> StartProject(
            @PathVariable Long projectId,
            @RequestBody ProjectDeadlineRequestDto projectDeadlineDto
    ) {
        Project project = projectService.StartProject(projectId, projectDeadlineDto);
        chatroomService.startProject(project.getProjectId());
        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    // 메인 화면에 올릴 프로젝트 정보
    @ApiOperation(
            value = "메인 페이지 프로젝트"
            , notes = " 유저가 진행중인 프로젝트 중 마감일이 가장 가까운 프로젝트의 이름과 진행도를 반환 \n")
    @GetMapping("/get/project/main-page/{userId}")
    public ResponseEntity<ProjectResponseDto> GetProjectClosestToDeadline(@PathVariable Long userId) {
        // 사용자의 ID를 가져와 해당 사용자가 속한 팀 조회
        ProjectResponseDto projectResponseDto = projectService.GetProjectClosestToDeadline(userId);

        if (projectResponseDto == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content 응답
        }
        return new ResponseEntity<>(projectResponseDto, HttpStatus.OK);
    }

    // 팀 상세 정보
    @ApiOperation(
            value = "프로젝트 상세 정보",
            notes = "특정 프로젝트의 상세 정보를 조회합니다."
    )
    @GetMapping("/get/project/{projectId}/detail")
    public ResponseEntity<Project> GetProjectDetails(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.GetProjectDetails(projectId), HttpStatus.OK);
    }

    // 프로젝트 참여 멤버 조회
    @ApiOperation(
            value = "프로젝트 참여 멤버 조회",
            notes = "특정 프로젝트에 참여하는 멤버들을 조회합니다."
    )
    @GetMapping("/get/project/{projectId}/members")
    public ResponseEntity<List<Profile>> getMembers(@PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.getMembers(projectId), HttpStatus.OK);
    }

    // 팀 해체 (프로젝트 종료)
    @ApiOperation(
            value = "팀 해체 (프로젝트 종료)",
            notes = "특정 팀을 해체하여 프로젝트를 종료합니다."
    )
    @DeleteMapping("/delete/project/{projectId}/disband")
    public ResponseEntity<String> DisbandProject(@PathVariable Long projectId) {
        peerReviewService.insertPeerReviewData(projectId);
        return new ResponseEntity<>(projectService.DisbandProject(projectId), HttpStatus.OK);
    }

//    // 신청한 팀 목록 조회 (나중에 구현)
//    @ApiOperation(
//            value = "신청한 팀 목록 조회",
//            notes = "사용자가 신청한 팀 목록을 조회합니다."
//    )
//    @GetMapping("/get/applied-projects/{userId}")
//    public ResponseEntity<List<Project>> GetAppliedProjects(@PathVariable Long userId) {
//        List<Project> appliedProjects = projectService.GetAppliedProjects(userId);
//        return ResponseEntity.ok(appliedProjects);
//    }
}
