package com.example.springsecurityexample.PeerReview.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter @Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class PeerReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int peerReviewId;
    private Long projectId;
    private Long userId;
    private int peerReviewScore;
    private int maxReviewerNumber;
    private int currentReviewerNumber;
    @Column
    private boolean isReviewed;
}
