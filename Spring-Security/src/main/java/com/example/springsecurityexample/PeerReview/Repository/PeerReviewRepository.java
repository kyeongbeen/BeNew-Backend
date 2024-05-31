package com.example.springsecurityexample.PeerReview.Repository;

import com.example.springsecurityexample.PeerReview.Entity.PeerReview;
import com.example.springsecurityexample.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeerReviewRepository extends JpaRepository<PeerReview, Long> {

    List<Long> findByProjectId(Long projectId);

    @Query("select p " +
            "from PeerReview p " +
            "where p.projectId = :projectId and p.userId = :userId")
    PeerReview findMember(Long projectId, Long userId);
}
