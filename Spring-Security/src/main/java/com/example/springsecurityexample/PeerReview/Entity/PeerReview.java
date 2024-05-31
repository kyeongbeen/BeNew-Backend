package com.example.springsecurityexample.PeerReview.Entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
}
