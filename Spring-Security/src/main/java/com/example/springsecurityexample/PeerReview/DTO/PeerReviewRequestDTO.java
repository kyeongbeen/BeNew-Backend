package com.example.springsecurityexample.PeerReview.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Builder
@Getter  @Setter
public class PeerReviewRequestDTO {
    Long projectId;
    List<PeerReviewIndividualScore> scores;
}
