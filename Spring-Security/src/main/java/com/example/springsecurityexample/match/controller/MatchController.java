package com.example.springsecurityexample.match.controller;

import com.example.springsecurityexample.match.*;
import com.example.springsecurityexample.match.Error;
import com.example.springsecurityexample.match.dto.*;
import com.example.springsecurityexample.match.repository.MatchRepository;
import com.example.springsecurityexample.match.service.MatchService;
import com.example.springsecurityexample.member.Member;
import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.repository.MemberRepository;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import com.example.springsecurityexample.member.service.ProfileService;
import com.example.springsecurityexample.security.CustomUserDetails;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequiredArgsConstructor //DI
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class MatchController {

    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final ProfileService profileService;

    // 사용 중인 user Id 찾아오기.
    Long GetLoginUserId () {
        Long id;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Member member = userDetails.getMember();
            id = member.getId();
            return id;
        }
        else return null;
    }

    @ApiOperation(
            value = "매칭 생성"
            , notes = "json을 통해 받은 사용자의 ID와 원하는 점수범위를 통해 매칭을 생성한다.\n" +
            "참고 : 샘플 데이터가 없어 현재는 0~100 사이 랜덤한 uid2를 생성합니다.")
    @PostMapping("/post/match")
    public ResponseEntity<MatchResource> CreateMatch(@RequestBody MatchRequestDto matchRequestDto/*, 기술 스택 dto 파라미터*/) {
        //TODO : 에러 관련 코드 추가(하는 중)
        //TODO : 필터링 기능 (후 순위)

        //요청 body가 비어있는 경우
        if (matchRequestDto.getUid1() == null) { //uid1 body에 담아서 요청했는지 확인
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new Error(false, "parameter is null in body"));
//        }

        //다른 유저의 id로 매칭을 만드려고 하는 경우
        if (!Objects.equals(matchRequestDto.getUid1(), GetLoginUserId())) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new Error(
//                            false,
//                            "다른 사용자의 매칭을 만들 수 없습니다. 현재 사용자의 id를 파라미터로 보내주세요"
//                    ));
//        }

        //매칭 생성
        MatchResponseDto newMatch = matchService.RecommendUser(matchRequestDto);

        //매칭된 유저가 없는 경우 (남은 유저가 너무 적거나 없는 경우, 프로필 카드가 없는 경우)
        if(newMatch == null)
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new Error(false, "더 이상 만들 수 있는 매칭 유저가 없습니다."));

        Match match = modelMapper.map(newMatch, Match.class);
        Match currentMatch = matchRepository.save(match);

        var selfLinkBuilder = linkTo(methodOn(MatchController.class).CreateMatch(matchRequestDto));
        URI createdUri = selfLinkBuilder.toUri();
        var getUserMatchesLinkBuilder = linkTo(methodOn(MatchController.class).ReadUserMatch(currentMatch.getUid1()));
        var updateDeleteLinkBuilder = linkTo(methodOn(MatchController.class).DeleteMatch(currentMatch.getMatchId()));
        var updateSuccessLinkBuilder = linkTo(methodOn(MatchController.class).UpdateMatchStatus(currentMatch.getMatchId()));
        var updateLikeLinkBuilder = linkTo(methodOn(MatchController.class).LikeMatch(currentMatch.getMatchId()));

        MatchResource matchResource = new MatchResource(match);
        matchResource.add(selfLinkBuilder.withSelfRel());
        matchResource.add(getUserMatchesLinkBuilder.withRel("get user's matching info list(get)"));
        matchResource.add(updateDeleteLinkBuilder.withRel("delete failure match(delete)"));
        matchResource.add(updateSuccessLinkBuilder.withRel("update successful match(patch)"));
        matchResource.add(updateLikeLinkBuilder.withRel("like to another user(patch)"));

        return ResponseEntity.created(createdUri).body(matchResource);
    }

    @ApiOperation(
            value = "사용자 매칭 조회"
            , notes = "URI를 통해 받은 사용자의 ID로 사용자의 매칭 리스트를 조회한다.")
    @GetMapping("/get/match/{id}")
    public ResponseEntity<List<Profile>> ReadUserMatch(@PathVariable Long id) { //계정 매개변수 있어야하나?
        List<Match> matchUsers = matchService.GetMatchingList(id);
        if (matchUsers.isEmpty()) { //TODO:서비스에서 처리해야 의미있는 코드가 됨.
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new Error(false, "uid1's matching is not founded"));
//        }

        //매칭 상대의 uid2를 저장
        List<Long> uid2List = matchUsers.stream()
                .map(Match::getUid2)
                .collect(Collectors.toList());

        // 모든 매칭 상대의 프로필 카드 리스트들을 저장
        List<Profile> profiles = new ArrayList<>();
        for (Long uid2 : uid2List) {
            Profile profile = profileService.getProfileByMemberId(uid2);
            if (profile != null) {
                profiles.add(profile);
            }
        }
        return ResponseEntity.ok(profiles);
    }

    @ApiOperation(
            value = "매칭 삭제"
            , notes = "URI를 통해 받은 매칭 ID로 매칭을 삭제한다. " +
            "\n매칭이 성사되고 팀이 만들어진 후 또는 매칭을 거절하는 경우 사용 권장")
    @DeleteMapping("/delete/match/{matchId}")
    public ResponseEntity<Error> DeleteMatch(@PathVariable Long matchId){
        //TODO : 트렌젝션 관련 오류 처리 공부
        try {
            matchRepository.deleteById(matchId);
            return ResponseEntity
                    .ok(new Error(true, "success"));
            //TODO: 좀 더 멀쩡해보이는 빌더 패턴으로 변경(후 순위)
        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new Error(false, "Resource not found"));
        }
    }

    @ApiOperation(
            value = "매칭 성사"
            , notes = "URI를 통해 받은 매칭 ID로 매칭 성사 여부를 저장하는 칼럼을 true로 변경" +
            "\n매칭 성사 시 사용 권장.")
    @PatchMapping("/patch/match/success/{matchId}")
    public ResponseEntity<Match> UpdateMatchStatus(@PathVariable Long matchId){
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchSuccess(true);
            matchRepository.save(match);
            return ResponseEntity.ok(match); // 변경된 Match 객체를 반환
        }
        else {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new Error(false, "Matching not found"));
        }
    }

    @ApiOperation(
            value = "매칭 요청"
            , notes = "URI를 통해 받은 매칭 ID로 매칭 요청 여부를 저장하는 칼럼을 true로 변경" +
            "\n매칭 요청 시 사용 권장.")
    @PatchMapping("/patch/match/like/{matchId}")
    public ResponseEntity<Match> LikeMatch(@PathVariable Long matchId){
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchingRequest(true);
            matchRepository.save(match);
            return ResponseEntity.ok(match); // 변경된 Match 객체를 반환
        }
        else {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(new Error(false, "Matching not found"));
        }
    }
}
