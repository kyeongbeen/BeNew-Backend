package com.example.springsecurityexample.PeerReview.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter @Setter
public class PeerReviewIndividualScore {
    private Long userId;
    private int score;

}
