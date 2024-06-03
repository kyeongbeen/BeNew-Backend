package com.example.springsecurityexample.PeerReview.Service;

import com.example.springsecurityexample.Chat.Entity.ChatroomParticipants;
import com.example.springsecurityexample.Chat.Repository.ChatroomParticipantsRepository;
import com.example.springsecurityexample.PeerReview.DTO.PeerReviewRequestDTO;
import com.example.springsecurityexample.PeerReview.Entity.PeerReview;
import com.example.springsecurityexample.PeerReview.Repository.PeerReviewRepository;
import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import io.swagger.models.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PeerReviewService {

    private final PeerReviewRepository peerReviewRepository;
    private final ProfileRepository profileRepository;
    private final ChatroomParticipantsRepository chatroomParticipantsRepository;

    public void insertPeerReviewData(Long projectId) {

        List<Long> membersId = peerReviewRepository.findByProjectId(projectId);
        List<PeerReview> peerReviews = new ArrayList<>();

        for (var i :
                membersId) {
            PeerReview peerReview = new PeerReview().builder()
                    .currentReviewerNumber(0)
                    .peerReviewScore(0)
                    .maxReviewerNumber(membersId.size() - 1)
                    .userId(i)
                    .isReviewed(false)
                    .projectId(projectId)
                    .build();
            peerReviews.add(peerReview);
        }
        peerReviewRepository.saveAll(peerReviews);
    }

    public String applyPeerReviewScore(PeerReviewRequestDTO peerReviewRequestDTO) throws Exception {
        try {
            for (var i :
                    peerReviewRequestDTO.getScores()) {
                PeerReview peerReview = peerReviewRepository.findMember(peerReviewRequestDTO.getProjectId(), i.getUserId());
                peerReview.setCurrentReviewerNumber(peerReview.getCurrentReviewerNumber() + 1);
                peerReview.setPeerReviewScore(peerReview.getPeerReviewScore() + i.getScore());

                if(peerReview.getCurrentReviewerNumber() == peerReview.getMaxReviewerNumber()) {
                    applyScoreFromProfileTable(peerReview.getUserId(), peerReview.getPeerReviewScore());
                }
            }
            PeerReview peerReview = peerReviewRepository.findByUserId(peerReviewRequestDTO.getUserId());
            peerReview.setReviewed(true);

        } catch (Exception e) {
            throw new Exception("잘못된 요청입니다.");
        }

        return "동료평가 점수가 반영되었습니다.";
    }

    private void applyScoreFromProfileTable(Long userId, int peerReviewScore) {
        Optional<Profile> profile = profileRepository.findByMember_Id(userId);
        profile.get().setPeer(profile.get().getPeer()*4/5 + peerReviewScore/5);
        ChatroomParticipants chatroomParticipants = chatroomParticipantsRepository.findByUserId(Integer.parseInt(userId.toString()));
        chatroomParticipants.setReviewed(true);
    }

}
