package com.example.springsecurityexample.match.controller;

import com.example.springsecurityexample.match.*;
import com.example.springsecurityexample.match.dto.*;
import com.example.springsecurityexample.match.repository.MatchRepository;
import com.example.springsecurityexample.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
// import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo; 질문 linkTo() method 강의랑 다른데 상관없나?

//TODO : JPA 설정 관련 WARNING LOG 해결
//TODO : URL 규칙에 맞춰서 수정 & service와 controller 분리, link 추가, docs 추가
@Controller
@RequiredArgsConstructor //DI
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class MatchController {

    private final MatchRepository matchRepository;

    private final MatchService matchService;

    private final ModelMapper modelMapper;

    /* create match - POST*/
    @PostMapping("/post/match")
    public ResponseEntity CreateMatch(@RequestBody MatchRequestDto matchRequestDto) {
        //TODO : 에러 관련 코드 추가
        //TODO : 필터링 기능

        MatchResponseDto newMatch = matchService.RecommendUser(matchRequestDto);
        Match match = modelMapper.map(newMatch, Match.class);
        Match currentMatch = matchRepository.save(match);

        var selfLinkBuilder = linkTo(methodOn(this.getClass()).CreateMatch(matchRequestDto));
        URI createdUri = selfLinkBuilder.toUri();
        var getUserMatchesLinkBuilder = linkTo(methodOn(this.getClass()).ReadUserMatch(currentMatch.getUid1()));
        var updateDeleteLinkBuilder = linkTo(methodOn(this.getClass()).DeleteMatch(currentMatch.getMatchId()));
        var updateSuccessLinkBuilder = linkTo(methodOn(this.getClass()).UpdateMatchStatus(currentMatch.getMatchId()));
        var updateLikeLinkBuilder = linkTo(methodOn(this.getClass()).LikeMatch(currentMatch.getMatchId()));

        MatchResource matchResource = new MatchResource(match);
        matchResource.add(selfLinkBuilder.withSelfRel());
        matchResource.add(getUserMatchesLinkBuilder.withRel("get user's matching info list(get)"));
        matchResource.add(updateDeleteLinkBuilder.withRel("delete failure match(delete)"));
        matchResource.add(updateSuccessLinkBuilder.withRel("update successful match(patch)"));
        matchResource.add(updateLikeLinkBuilder.withRel("like to another user(patch)"));

        return ResponseEntity.created(createdUri).body(matchResource);
    }

    /* get user's matching info list - GET*/
    @GetMapping("/get/match/{id}")
    public ResponseEntity ReadUserMatch(@PathVariable int id) { //계정 매개변수 있어야하나?
         List<Match> matchUsers = matchService.GetMatchingList(id);
        if (matchUsers.isEmpty()) { //TODO:서비스에서 처리해야 의미있는 코드가 됨.
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(matchUsers);
    }

    /* delete failure match - DELETE*/
    @DeleteMapping("/delete/match/{matchId}")
    public ResponseEntity DeleteMatch(@PathVariable Integer matchId){
        //TODO : 트렌젝션 관련 오류 처리 공부
        try {
            matchRepository.deleteById(matchId);
            return ResponseEntity
                    .ok(new DeleteResultResponse(true, "success"));
                    //TODO: 좀 더 멀쩡해보이는 빌더 패턴으로 변경
        } catch (EntityNotFoundException ex) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new DeleteResultResponse(false, "Resource not found"));
        }
    }

    //상대방이 매칭 수락 시 matchSuccess = true
    /* update successful match - PATCH*/
    @PatchMapping("/patch/match/success/{matchId}")
    public ResponseEntity UpdateMatchStatus(@PathVariable Integer matchId){
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchSuccess(true);
            // Dto를 사용하여 업데이트 해야함. (현재는 entity를 사용해서 데이터를 옮김)
            matchRepository.save(match);
            return ResponseEntity.ok(match); // 변경된 Match 객체를 반환
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    //T사용자가 매칭 상대가 마음에 들어 매칭 요청을 보낼 때 likeByUid1 = true
    /* like to another user - PATCH*/
    @PatchMapping("/patch/match/like/{matchId}")
    public ResponseEntity LikeMatch(@PathVariable Integer matchId){
        Optional<Match> matchOptional = matchRepository.findById(matchId);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchingRequest(true);
            // updatedMatchDto를 사용하여 업데이트 해야함. (현재는 entity를 사용해서 데이터를 옮김)
            matchRepository.save(match);
            return ResponseEntity.ok(match); // 변경된 Match 객체를 반환
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}

