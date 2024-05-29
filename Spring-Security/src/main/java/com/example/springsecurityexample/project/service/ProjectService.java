package com.example.springsecurityexample.project.service;

import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import com.example.springsecurityexample.project.Project;
import com.example.springsecurityexample.project.dto.ProjectDeadlineRequestDto;
import com.example.springsecurityexample.project.dto.ProjectRequestDto;
import com.example.springsecurityexample.project.dto.ProjectResponseDto;
import com.example.springsecurityexample.project.error.RequestException;
import com.example.springsecurityexample.project.repository.ProjectRepository;
import com.example.springsecurityexample.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor //DI
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProfileRepository profileRepository;

    public boolean HasEmptyFields(ProjectRequestDto projectRequestDto) {
        // 하나라도 null이면 true
        return projectRequestDto.getUserId() == null ||
                projectRequestDto.getProjectName() == null ||
                projectRequestDto.getProjectIntroduction() == null ||
                projectRequestDto.getProjectOneLineIntroduction() == null
                ;
    }

    // 추후 프로젝트 관리 권한을 위해 사용 예정
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getMember().getId();
        }
        return null;  // 인증되지 않은 경우
    }

    public Project RegisterProject(ProjectRequestDto projectRequestDto) {
        if (HasEmptyFields(projectRequestDto))
            throw new RequestException("projectRequestDto의 필드값이 하나 이상 비어있음");

        Profile projectMember = profileRepository.findById(projectRequestDto.getUserId()).orElse(null);


        if (projectMember == null) {
            throw new RequestException("projectRequestDto의 userId 필드값과 일치하는 사용자가 없음");
        }

        if (projectRequestDto.getProjectManager() == null){
            projectRequestDto.setProjectManager(projectRequestDto.getUserId());
        }

        Project project = Project.builder()
                .projectName(projectRequestDto.getProjectName())
                .creationDate(LocalDate.now())
                .numberOfMembers(1)
                .projectManager(projectRequestDto.getProjectManager())
                .projectIntroduction(projectRequestDto.getProjectIntroduction())
                .projectOneLineIntroduction(projectRequestDto.getProjectOneLineIntroduction())
                .projectStarted(false)
                .profiles(List.of(projectMember))
                .build();

        return projectRepository.save(project);
    }

    public List<Project> GetUserProjects(Long userId) {
        if (userId == null) {
            throw new RequestException("URI의 userId이 비어있음");
        }

        Profile profile = profileRepository.findById(userId).orElse(null);
        if (profile == null) {
            throw new RequestException("URI의 userId와 일치하는 사용자가 없음");
        }

        return projectRepository.findAllByProfiles_Id(userId);
    }

    public Project AddProjectMember(Long projectId, Long userId) {
        if (projectId == null || userId == null) {
            throw new RequestException("URI의 projectId 또는 userId이 비어있음");
        }

        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new RequestException("URI의 projectId와 일치하는 프로젝트가 없음");
        }

        Profile profile = profileRepository.findById(userId).orElse(null);
        if (profile == null) {
            throw new RequestException("URI의 userId과 일치하는 사용자가 없음");
        }

        // 이미 해당 유저가 팀에 속해 있는 경우
        List<Profile> profiles = project.getProfiles();
        if (profiles.contains(profile)) {
            throw new RequestException("이미 존재하는 맴버입니다");
        }

        // 팀에 프로필을 추가하고 저장
        profiles.add(profile);
        project.setNumberOfMembers(project.getNumberOfMembers() + 1);
        project.setProfiles(profiles);
        return projectRepository.save(project);

    }

    public boolean CheckProjectNameDuplicate(String projectName) {
        // 팀 이름이 null이거나 빈 문자열인 경우에는 팀 이름에 부적합. 때문에 true 반환
        if (projectName == null || projectName.isEmpty()) {
            return true;
        }

        // 중복인 경우 true, 중복 아닌 경우 false 반환
        return projectRepository.existsByProjectName(projectName);
    }

    public Boolean CheckProjectManager(Long projectId, Long projectManager) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new RequestException("URI의 projectId와 일치하는 프로젝트가 없음");
        }
        return Objects.equals(project.getProjectManager(), projectManager);
    }


    public Project UpdateProjectInfo(Long projectId, ProjectRequestDto projectRequestDto) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new RequestException("URI의 projectId와 일치하는 프로젝트가 없음");
        }
        String projectName = projectRequestDto.getProjectName();
        String projectOneLineIntroduction = projectRequestDto.getProjectOneLineIntroduction();
        String projectIntroduction = projectRequestDto.getProjectIntroduction();
        Long projectManager = projectRequestDto.getProjectManager();

        if (projectName == null && projectOneLineIntroduction == null && projectIntroduction == null && projectManager == null)
            return project;

        if (projectName != null) {
            project.setProjectName(projectName);
        }
        if (projectManager != null) {
            project.setProjectManager(projectManager);
        }
        if (projectOneLineIntroduction != null) {
            project.setProjectOneLineIntroduction(projectOneLineIntroduction);
        }
        if (projectIntroduction != null) {
            project.setProjectIntroduction(projectIntroduction);
        }

        return projectRepository.save(project);
    }

    public List<Project> GetAllProjects() {
        return projectRepository.findAll();
    }

    //인원수 변경해야함
    public Project RemoveProjectMember(Long projectId, Long userId) {

        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new RequestException("URI의 projectId와 일치하는 프로젝트가 없음");
        }

        Profile profile = profileRepository.findById(userId).orElse(null);
        if (profile == null) {
            throw new RequestException("URI의 userId과 일치하는 사용자가 없음");
        }

        List<Profile> profiles = project.getProfiles();

        if (!profiles.contains(profile))
            throw new RequestException("프로젝트에 소속되지 않은 사용자를 프로젝트에서 제거할 수 없음");

        profiles.removeIf(targetProfile -> targetProfile.getId().equals(userId));
        project.setNumberOfMembers(project.getNumberOfMembers() - 1);
        project.setProfiles(profiles);

        return projectRepository.save(project);
    }

    //remove project member func으로 대체?
//    public void Leaveproject(Long projectId) {
//        return;
//    }

    public Project GetProjectDetails(Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new RequestException("URI의 projectId와 일치하는 프로젝트가 없음");
        }

        if (project.getViews() == null)
            project.setViews(0L);

        project.setViews(project.getViews() + 1); // 조회수 증가
        projectRepository.save(project); // 변경사항 저장

        return project;
    }

    public String DisbandProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new RequestException("URI의 projectId와 일치하는 프로젝트가 없음");
        }

        // 팀을 삭제
        projectRepository.delete(project);
        return "팀 해제 성공";
    }

    public Project StartProject(Long projectId, ProjectDeadlineRequestDto projectDeadlineDto) {
        if (projectDeadlineDto.getProjectDeadlineDate() == null){
            throw new RequestException("프로젝트 마감 날짜가 비어있음");
        }
        if (projectDeadlineDto.getProjectDeadlineDate().isBefore(LocalDate.now())){
            throw new RequestException("마감일이 현재 날짜보다 이전입니다.");
        }
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            throw new RequestException("URI의 projectId와 일치하는 프로젝트가 없음");
        }
        if (project.isProjectStarted())
            throw new RequestException("이미 시작된 프로젝트입니다.");

        project.setProjectStarted(true);
        project.setProjectStartDate(LocalDate.now());
        project.setProjectDeadlineDate(projectDeadlineDto.getProjectDeadlineDate());
        return projectRepository.save(project);
    }

    public ProjectResponseDto GetProjectClosestToDeadline(Long userId) {
        if (userId == null) {
            throw new RequestException("URI의 userId이 비어있음");
        }

        Profile profile = profileRepository.findById(userId).orElse(null);
        if (profile == null) {
            throw new RequestException("URI의 userId와 일치하는 사용자가 없음");
        }

        List<Project> orderByDeadlineProjects = projectRepository.findProjectsWithDeadlineAfterTodayByProfileId(userId, LocalDate.now());

        if (orderByDeadlineProjects.isEmpty()) {
            List<Project> unstartedProjects = projectRepository.findUnstartedProjectsOrderedByCreationDate();
            if (!unstartedProjects.isEmpty()) {
                Project project = unstartedProjects.get(0);
                return ProjectResponseDto.builder()
                        .projectName(project.getProjectName())
                        .projectRateOfProgress(-1)
                        .build();
            }else return null; // 204 No Content 응답을 위한 null 반환
        }

        Project project = orderByDeadlineProjects.get(0);


        // 프로젝트 전체 일자
        long totalDays = ChronoUnit.DAYS.between(
                project.getProjectStartDate(),
                project.getProjectDeadlineDate()
        );

        // 프로젝트 진행 일자
        long elapsedDays = ChronoUnit.DAYS.between(
                project.getProjectStartDate(),
                LocalDate.now()
        );

        double progressPercent;

        if (totalDays == elapsedDays)
            progressPercent = 100;
        else
            progressPercent = ((double) elapsedDays / totalDays) * 100;

        System.out.println("Project Start Date: " + project.getProjectStartDate());
        System.out.println("Project Deadline Date: " + project.getProjectDeadlineDate());
        System.out.println("Total Days: " + totalDays);
        System.out.println("Elapsed Days: " + elapsedDays);

        return ProjectResponseDto.builder()
                .projectName(project.getProjectName())
                .projectRateOfProgress(progressPercent)
                .build();
    }


    public Page<Project> GetRecentProjects(int page, int size) {
        if (page < 0) {
            throw new RequestException("page 가 0보다 크거나 같아야 함.");
        }
        if (size <= 0) {
            throw new RequestException("Page size 가 0보다 커야함.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate"));
        return projectRepository.findAllByOrderByCreationDateDesc(pageable);
    }

    public Page<Project> GetPopularityProjects(int page, int size) {
        if (page < 0) {
            throw new RequestException("page 가 0보다 크거나 같아야 함.");
        }
        if (size <= 0) {
            throw new RequestException("Page size 가 0보다 커야함.");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "views"));
        return projectRepository.findAllByOrderByViewsDesc(pageable);
    }

//    public List<Project> GetAppliedProjects(Long userId) {
//        return null;
//    }
}
