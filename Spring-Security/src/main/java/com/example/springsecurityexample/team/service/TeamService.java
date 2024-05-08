package com.example.springsecurityexample.team.service;

import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import com.example.springsecurityexample.team.Team;
import com.example.springsecurityexample.team.dto.TeamRequestDto;
import com.example.springsecurityexample.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor //DI
public class TeamService {
    private final TeamRepository teamRepository;
    private final ProfileRepository profileRepository;

    public boolean HasEmptyFields(TeamRequestDto teamRequestDto) {
        // 하나라도 null이면 true
        return teamRequestDto.getTeamMember1() == null || teamRequestDto.getTeamMember2() == null ||
                teamRequestDto.getTeamName() == null || teamRequestDto.getTeamDescription() == null;
    }

    public Team RegisterTeam(TeamRequestDto teamRequestDto) {
        if (HasEmptyFields(teamRequestDto)) return null;

        Profile member1 = profileRepository.findById(teamRequestDto.getTeamMember1().getId()).orElse(null);
        Profile member2 = profileRepository.findById(teamRequestDto.getTeamMember2().getId()).orElse(null);

        if (member1 == null || member2 == null) {
            return null;
        }

        Team team = Team.builder()
                .teamName(teamRequestDto.getTeamName())
                .creationDate(LocalDate.now())
                .numberOfMembers(teamRequestDto.getNumberOfMembers())
                .teamDescription(teamRequestDto.getTeamDescription())
                .projectStarted(false)
                .profiles(Arrays.asList(member1, member2))
                .build();

        return teamRepository.save(team);
    }

    public List<Team> GetUserTeams(Long userId) {
        if (userId == null) {
            return null;
        }

        return teamRepository.findAllByProfiles_Id(userId);
    }

    public Team AddTeamMember(Long teamId, Long userId) {
        if (teamId == null || userId == null) {
            return null;
        }

        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            return null;
        }

        Profile profile = profileRepository.findById(userId).orElse(null);
        if (profile == null) {
            return null;
        }

        // 이미 해당 유저가 팀에 속해 있는 경우
        List<Profile> profiles = team.getProfiles();
        if (profiles.contains(profile)) {
            return team;
        }

        // 팀에 프로필을 추가하고 저장
        profiles.add(profile);
        team.setProfiles(profiles);
        return teamRepository.save(team);

    }

    public boolean CheckTeamNameDuplicate(String teamName) {
        // 팀 이름이 null이거나 빈 문자열인 경우에는 팀 이름에 부적합. 때문에 true 반환
        if (teamName == null || teamName.isEmpty()) {
            return true;
        }

        // 중복인 경우 true, 중복 아닌 경우 false 반환
        return teamRepository.existsByTeamName(teamName);
    }

    public Team UpdateTeamInfo(Long teamId, TeamRequestDto teamRequestDto) {
        return null;
    }

    public List<Team> GetAllTeams() {
        return teamRepository.findAll();
    }

    public void RemoveTeamMember(Long teamId, Long memberId) {
        return;
    }

    public void LeaveTeam(Long teamId) {
        return;
    }

    public Team GetTeamDetails(Long teamId) {
        return null;
    }

    public void DisbandTeam(Long teamId) {
        return;
    }

    public List<Team> GetAppliedTeams(Long userId) {
        return null;
    }
}
