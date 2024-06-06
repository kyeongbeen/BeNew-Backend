package com.example.springsecurityexample.match.controller;

import com.example.springsecurityexample.match.Error;
import com.example.springsecurityexample.match.Match;
import com.example.springsecurityexample.match.MatchResource;
import com.example.springsecurityexample.match.dto.MatchProjectDto;
import com.example.springsecurityexample.match.dto.MatchRequestDto;
import com.example.springsecurityexample.match.service.MatchService;
import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.controller.ProfileController;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequiredArgsConstructor //DI
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class MatchController {

    private final MatchService matchService;

    @ApiOperation(
            value = "매칭 생성 (input : matchRequestDto { uid1 (필수), technologyId(선택) })"
            , notes = "json을 통해 받은 사용자의 정보를 통해 매칭을 생성한다.\n")
    @PostMapping("/post/match")
    public ResponseEntity<List<Match>> CreateMatch(@RequestBody MatchRequestDto matchRequestDto) {

        //요청 body가 비어있는 경우
        if (matchRequestDto.getUid1() == null) { //uid1 body에 담아서 요청했는지 확인
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        List<Match> matches = matchService.RecommendUsers(matchRequestDto);

        //매치 생성 실패
        if (matches == null || matches.get(0).getMatchId() == null) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        //matches.update(); 이건 서비스에서 (팀 여부 업데이트)

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matches);
    }

    @ApiOperation(
            value = "알람 기능에서 쓰면 좋을것 같아요. 매칭 id를 통한 매칭 조회(응답형식 : Match 엔티티)"
            , notes = "알람 기능에서 쓸 수 있을까 싶어서 만들었어요.")
    @GetMapping("/get/match-info/match/{matchId}")
    public ResponseEntity<MatchResource> ReadMatchByMatchId(@PathVariable Long matchId) {
        Match match = matchService.GetMatch(matchId);

        //링크 생성
        var selfLinkBuilder = linkTo(methodOn(MatchController.class).ReadMatchByMatchId(matchId));
        URI createdUri = selfLinkBuilder.toUri();
        var FalseMatchLinkBuilder = linkTo(methodOn(MatchController.class).FalseMatch(match.getUid1(), match.getProfile().getId()));
        var SuccessMatchLinkBuilder = linkTo(methodOn(MatchController.class).SuccessMatch(match.getUid1(), match.getProfile().getId()));
        var GetRequesterInformation = linkTo(methodOn(ProfileController.class).getProfileByMemberId(matchId));

        MatchResource matchResource = new MatchResource(match);
        matchResource.add(selfLinkBuilder.withSelfRel());
        matchResource.add(GetRequesterInformation.withRel("get requester's information(매칭 요청자 정보, get)"));
        matchResource.add(FalseMatchLinkBuilder.withRel("false match(매칭 실패, patch)"));
        matchResource.add(SuccessMatchLinkBuilder.withRel("success match(매칭 성공, patch)"));


        return ResponseEntity
                .status(match == null ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .location(createdUri)
                .body(matchResource);
    }

    @ApiOperation(
            value = "사용자 매칭 조회(응답형식 : List<프로필>)"
            , notes = "URI를 통해 받은 사용자의 ID로 사용자의 매칭상대의 프로필 리스트를 조회한다.")
    @GetMapping("/get/match-info/profile/{uid1}")
    public ResponseEntity<List<Profile>> ReadMatchingPartnerProfile(@PathVariable Long uid1) {
        List<Profile> profiles = matchService.GetMatchingUserProfileList(uid1);
        return ResponseEntity
                .status(profiles.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(profiles.isEmpty() ? null : profiles);
    }

    @ApiOperation(
            value = "사용자 매칭 조회(응답형식 : List<매치>)"
            , notes = "URI를 통해 받은 사용자의 ID로 사용자의 매칭 리스트를 조회한다.")
    @GetMapping("/get/match-info/match-list/{uid1}")
    public ResponseEntity<List<Match>> ReadMatch(@PathVariable Long uid1) { //계정 매개변수 있어야하나?
        List<Match> Matches = matchService.GetMatchingList(uid1);
        return ResponseEntity
                .status(Matches.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(Matches.isEmpty() ? null : Matches);
    }

    @ApiOperation(
            value = "매칭 삭제"
            , notes = "URI를 통해 받은 매칭 ID로 매칭을 삭제한다. " +
            "\n매칭 삭제는 사용할 일 없을것 같습니다")
    @DeleteMapping("/delete/match/{matchId}")
    public ResponseEntity<Error> DeleteMatch(@PathVariable Long matchId){
        Error error = matchService.DeleteMatchById(matchId);
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ApiOperation(
            value = "매칭 성사(매칭 요청을 알람에서 수락)"
            , notes = "URI를 통해 받은 매칭 ID로 매칭 성사 여부를 저장하는 칼럼을 SUCCESS로 변경" +
            "\n매칭 성사 시 사용.")
    @PatchMapping("/patch/match/success/{sender}/{receiver}")
    public ResponseEntity<Match> SuccessMatch(@PathVariable Long sender, @PathVariable Long receiver){
//        Match match = matchService.UpdateMatchStatusIsSuccess(sender ,receiver);
//        return ResponseEntity
//                .status(
//                        match != null
//                                ? HttpStatus.OK
//                                : HttpStatus.NO_CONTENT
//                )
//                .body(
//                        match
//                );
        return ResponseEntity.ok(null);
    }

    @ApiOperation(
            value = "매칭 실패(매칭 요청을 알람에서 거절)"
            , notes = "URI를 통해 받은 매칭 ID로 매칭 성사 여부를 저장하는 칼럼을 FALSE로 변경" +
            "\n매칭 실패 시 사용.")
    @PatchMapping("/patch/match/false/{sender}/{receiver}")
    public ResponseEntity<Match> FalseMatch(@PathVariable Long sender, @PathVariable Long receiver){
//        Match match = matchService.UpdateMatchStatusIsFalse(sender ,receiver);
//        return ResponseEntity
//                .status(
//                        match != null
//                                ? HttpStatus.OK
//                                : HttpStatus.NO_CONTENT
//                )
//                .body(
//                        match
//                );
        return ResponseEntity.ok(null);
    }

    @ApiOperation(
            value = "매칭 요청(스와이프에서 매칭을 수락하는 경우)"
            , notes = "URI를 통해 받은 매칭 ID로 매칭 요청 여부를 저장하는 칼럼을 REQUESTED로 변경" +
            "\n매칭 요청 시 사용 권장.")
    @PatchMapping("/patch/match/like/{matchId}")
    public ResponseEntity<Match> LikeMatch(@PathVariable Long matchId, @RequestBody MatchProjectDto projectId){
//        Match match = matchService.LikeMatchStatusById(matchId, projectId);
//        return ResponseEntity
//                .status(
//                        match != null
//                                ? HttpStatus.OK
//                                : HttpStatus.NO_CONTENT
//                )
//                .body(
//                        match
//                );
        return ResponseEntity.ok(null);
    }

    @ApiOperation(
            value = "매칭 거절(스와이프에서 매칭을 거절하는 경우)"
            , notes = "URI를 통해 받은 매칭 ID로 매칭 요청 여부를 저장하는 칼럼을 REJECTED로 변경" +
            "\n매칭 거절 시 사용 권장.")
    @PatchMapping("/patch/match/dislike/{matchId}")
    public ResponseEntity<Match> DislikeMatch(@PathVariable Long matchId){
//        Match match = matchService.DislikeMatchStatusById(matchId);
//        return ResponseEntity
//                .status(
//                        match != null
//                                ? HttpStatus.OK
//                                : HttpStatus.NO_CONTENT
//                )
//                .body(
//                        match
//                );
        return ResponseEntity.ok(null);
    }
}