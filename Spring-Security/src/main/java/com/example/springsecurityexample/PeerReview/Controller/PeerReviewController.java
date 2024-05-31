package com.example.springsecurityexample.PeerReview.Controller;

import com.example.springsecurityexample.PeerReview.DTO.PeerReviewRequestDTO;
import com.example.springsecurityexample.PeerReview.Service.PeerReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.transaction.jta.platform.internal.ResinJtaPlatform;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping(value = "/peer-review")
@RequiredArgsConstructor
public class PeerReviewController {

    private final PeerReviewService peerReviewService;

    @ApiOperation(
            value = "동료평가 실시",
            notes = "프로젝트가 끝나고나서 실행합니다.\n" +
                    "각각의 인원을 1 ~ 100점으로 평가합니다 \n"
    )
    @PostMapping("/do")
    public ResponseEntity<String> doPeerReview(@RequestBody PeerReviewRequestDTO peerReviewRequestDTO) throws Exception {
        return new ResponseEntity<>(peerReviewService.applyPeerReviewScore(peerReviewRequestDTO), HttpStatus.OK);
    }

}
