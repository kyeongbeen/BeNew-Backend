package com.example.springsecurityexample.project.repository;

import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByProfiles_Id(Long userId);


    @Query("select p " +
            "from Profile p " +
            "where p.id in(select pj.projectId " +
                            "from Project pj " +
                            "where pj.projectId = :projectId)")
    List<Profile> findMembers(@Param("projectId") Long projectId);

    boolean existsByProjectName(String teamName);

    Page<Project> findAllByOrderByCreationDateDesc(Pageable pageable);
    Page<Project> findAllByOrderByViewsDesc(Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.projectStartDate IS NULL ORDER BY p.creationDate ASC")
    List<Project> findUnstartedProjectsOrderedByCreationDate();

    @Query("SELECT p FROM Project p " +
            "JOIN p.profiles pr WHERE pr.id = :profileId AND p.projectDeadlineDate >= :currentDate " +
            "ORDER BY p.projectDeadlineDate ASC")
    List<Project> findProjectsWithDeadlineAfterTodayByProfileId(@Param("profileId") Long profileId, @Param("currentDate") LocalDate currentDate);
}
