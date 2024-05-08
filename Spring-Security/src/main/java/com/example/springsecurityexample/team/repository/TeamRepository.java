package com.example.springsecurityexample.team.repository;

import com.example.springsecurityexample.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByProfiles_Id(Long userId);

    boolean existsByTeamName(String teamName);
}
