package com.example.springsecurityexample.project.repository;

import com.example.springsecurityexample.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByProfiles_Id(Long userId);

    boolean existsByProjectName(String teamName);

    @Query("SELECT p FROM Project p " +
            "JOIN p.profiles pr WHERE pr.id = :profileId AND p.projectDeadlineDate >= :currentDate " +
            "ORDER BY p.projectDeadlineDate ASC")
    List<Project> findProjectsWithDeadlineAfterTodayByProfileId(@Param("profileId") Long profileId, @Param("currentDate") LocalDate currentDate);
}
