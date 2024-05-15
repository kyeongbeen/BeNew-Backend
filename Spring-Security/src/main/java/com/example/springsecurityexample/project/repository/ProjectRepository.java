package com.example.springsecurityexample.project.repository;

import com.example.springsecurityexample.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByProfiles_Id(Long userId);

    boolean existsByProjectName(String teamName);
}
