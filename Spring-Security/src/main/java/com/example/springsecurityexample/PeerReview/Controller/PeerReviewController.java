package com.example.springsecurityexample.PeerReview.Controller;

import com.example.springsecurityexample.PeerReview.DTO.PeerReviewRequestDTO;
import com.example.springsecurityexample.PeerReview.Entity.PeerReview;
import com.example.springsecurityexample.PeerReview.Service.PeerReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.transaction.jta.platform.internal.ResinJtaPlatform;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @ApiOperation(
            value = "해당 프로젝트의 동료평가 클래스를 리턴",
            notes = "프로젝트 목록중 어떤것의 피어리뷰를 했는지, 하지 않았는지 확인하기 위함" +
                    "반환된 데이터가 하나도 없으면 끝난 프로젝트가 없다는 것을 의미함"
    )
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<PeerReview>> findPeerReviewStatus(@PathVariable Long userId) {
        return new ResponseEntity<>(peerReviewService.findPeerReviewStatus(userId), HttpStatus.OK);
    }

}
