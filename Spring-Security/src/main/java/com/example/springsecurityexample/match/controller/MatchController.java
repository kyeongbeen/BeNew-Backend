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

    private final MatchService matchService;

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
            , notes = "json을 통해 받은 사용자의 ID를 통해 매칭을 생성한다.\n")
    @PostMapping("/post/match")
    public ResponseEntity<MatchResource> CreateMatch(@RequestBody MatchRequestDto matchRequestDto) {
        //TODO : 에러 관련 코드 추가(하는 중)
        //TODO : 필터링 기능 (후 순위)

        //요청 body가 비어있는 경우
        if (matchRequestDto.getUid1() == null) { //uid1 body에 담아서 요청했는지 확인
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        //다른 유저의 id로 매칭을 만드려고 하는 경우
        if (!Objects.equals(matchRequestDto.getUid1(), GetLoginUserId())) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        //매칭 생성
        Match match = matchService.RecommendUser(matchRequestDto);

        var selfLinkBuilder = linkTo(methodOn(MatchController.class).CreateMatch(matchRequestDto));
        URI createdUri = selfLinkBuilder.toUri();
        var getUserMatchesLinkBuilder = linkTo(methodOn(MatchController.class).ReadUserMatch(match.getUid1()));
        var updateDeleteLinkBuilder = linkTo(methodOn(MatchController.class).DeleteMatch(match.getMatchId()));
        var updateSuccessLinkBuilder = linkTo(methodOn(MatchController.class).UpdateMatchStatus(match.getMatchId()));
        var updateLikeLinkBuilder = linkTo(methodOn(MatchController.class).LikeMatch(match.getMatchId()));

        MatchResource matchResource = new MatchResource(match);
        matchResource.add(selfLinkBuilder.withSelfRel());
        matchResource.add(getUserMatchesLinkBuilder.withRel("get user's matching info list(get)"));
        matchResource.add(updateDeleteLinkBuilder.withRel("delete failure match(delete)"));
        matchResource.add(updateSuccessLinkBuilder.withRel("update successful match(patch)"));
        matchResource.add(updateLikeLinkBuilder.withRel("like to another user(patch)"));

        return match.getProfile().getId() != null
                ? ResponseEntity.created(createdUri).body(matchResource)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @ApiOperation(
            value = "사용자 매칭 조회"
            , notes = "URI를 통해 받은 사용자의 ID로 사용자의 매칭상대의 프로필 리스트를 조회한다.")
    @GetMapping("/get/match-profile/{id}")
    public ResponseEntity<List<Profile>> ReadUserMatch(@PathVariable Long id) { //계정 매개변수 있어야하나?
        List<Profile> profiles = matchService.GetMatchingUserProfileList(id);
        return ResponseEntity
                .status(profiles.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(profiles.isEmpty() ? null : profiles);
    }

    @ApiOperation(
            value = "사용자 매칭 조회"
            , notes = "URI를 통해 받은 사용자의 ID로 사용자의 매칭 리스트를 조회한다.")
    @GetMapping("/get/match/{id}")
    public ResponseEntity<List<Match>> ReadMatch(@PathVariable Long id) { //계정 매개변수 있어야하나?
        List<Match> Matches = matchService.GetMatchingList(id);
        return ResponseEntity
                .status(Matches.isEmpty() ? HttpStatus.NO_CONTENT : HttpStatus.OK)
                .body(Matches.isEmpty() ? null : Matches);
    }

    @ApiOperation(
            value = "매칭 삭제"
            , notes = "URI를 통해 받은 매칭 ID로 매칭을 삭제한다. " +
            "\n매칭이 성사되고 팀이 만들어진 후 또는 매칭을 거절하는 경우 사용 권장")
    @DeleteMapping("/delete/match/{matchId}")
    public ResponseEntity<Error> DeleteMatch(@PathVariable Long matchId){
        Error error = matchService.DeleteMatchById(matchId);
        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @ApiOperation(
            value = "매칭 성사"
            , notes = "URI를 통해 받은 매칭 ID로 매칭 성사 여부를 저장하는 칼럼을 true로 변경" +
            "\n매칭 성사 시 사용 권장.")
    @PatchMapping("/patch/match/success/{matchId}")
    public ResponseEntity<Match> UpdateMatchStatus(@PathVariable Long matchId){
        Match match = matchService.UpdateMatchStatusById(matchId);
        return ResponseEntity
                .status(match.getMatchSuccess() ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .body(match.getMatchSuccess() ? match : null);
    }

    @ApiOperation(
            value = "매칭 요청"
            , notes = "URI를 통해 받은 매칭 ID로 매칭 요청 여부를 저장하는 칼럼을 true로 변경" +
            "\n매칭 요청 시 사용 권장.")
    @PatchMapping("/patch/match/like/{matchId}")
    public ResponseEntity<Match> LikeMatch(@PathVariable Long matchId){
        Match match = matchService.LikeMatchStatusById(matchId);
        return ResponseEntity
                .status(match.getMatchingRequest() ? HttpStatus.OK : HttpStatus.NO_CONTENT)
                .body(match.getMatchingRequest() ? match : null);

    }
}
