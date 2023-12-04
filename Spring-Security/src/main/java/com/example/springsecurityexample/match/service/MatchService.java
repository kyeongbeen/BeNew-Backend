package com.example.springsecurityexample.match.service;

import com.example.springsecurityexample.match.Match;
import com.example.springsecurityexample.match.Error;
import com.example.springsecurityexample.match.MatchRequestType;
import com.example.springsecurityexample.match.MatchSuccessType;
import com.example.springsecurityexample.match.dto.*;
import com.example.springsecurityexample.match.repository.MatchRepository;
import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.repository.MemberRepository;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor //DI
public class MatchService {
    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    //매칭 생성
    public Match RecommendUser(MatchRequestDto matchRequestDto) {
        //사용자의 프로필 정보 가져오기
        Optional<Profile> userInfo = profileRepository.findByMember_Id(matchRequestDto.getUid1());

        int userPeer = 0;
        if (userInfo.isEmpty()) {
            return Match.builder().build();
        }

        userPeer = userInfo.get().getPeer();
        Profile userProfile = userInfo.get();


        List<Profile> profilesInRange = profileRepository.findByPeerBetween(userPeer - 5, userPeer + 5);
        int memberCount = profilesInRange.size();

        //탈출을 위한 변수
        int i = 0;


        //매칭을 위한 랜덤 숫자
        int randomNum = -1;

        //중복 제거를 위한 배열
        List<Integer> randomNumbers =  new ArrayList<Integer>();
        randomNumbers.add(randomNum);

        //DTO를 match 엔티티로 변경
        Match match = modelMapper.map(matchRequestDto, Match.class);

        //매칭 시작
        while (true) {
            long randomUserProfileId; // profile card id 이다.
            i = 0;

            do {
                randomNum = (int) (Math.random() * memberCount);
                i++;
                if(i > memberCount)
                    return Match.builder().build();

            } while (randomNumbers.contains(randomNum));
            randomNumbers.add(randomNum);

            randomUserProfileId = profilesInRange.get(randomNum).getId();

            // 자기 자신이 매칭 상대인 경우 다시 루프 실행
            if (randomUserProfileId == userProfile.getId()) {
                continue;
            }


            // 이미 매칭된 경우 다시 루프 실행
            if (matchRepository.findByUid1AndProfile_Id(matchRequestDto.getUid1(), randomUserProfileId).isPresent()) {
                continue;
            }

            // 매칭 성공, profile card 연결
            Profile profile = profilesInRange.get(randomNum);
            match.setProfile(profile);
            break;
        }

        // 매칭 정보 삽입
        match.setMatchingDate(LocalDateTime.now());
        match.setMatchSuccess(MatchSuccessType.PENDING);
        match.setMatchingRequest(MatchRequestType.PENDING);
        match.update();
        matchRepository.save(match);
        return match;
    }

    //매칭 조회 (return type : Match)
    public List<Match> GetMatchingList (Long id){
        List<Match> matchList = matchRepository.findAllByUid1(id);
        //TODO : null exception 예외처리

        for (int i = 0; i < matchList.size(); i++) {
            matchList.get(i).update();
            if ( false
//                    원하는 조건을 붙혀서 원하는 매치 정보만 볼 수 있다.
//                    matchList.get(i).getMatchingRequest() == MatchRequestType.REJECTED
//                    || matchList.get(i).getMatchSuccess() == MatchSuccessType.SUCCESS
            )
            {
                matchList.remove(i);
                i--;
            }
        }
        return matchList;
    }

    public Match GetMatch (Long id){
        Optional<Match> matchUser = matchRepository.findById(id);
        if (matchUser.isEmpty()) {
            return null;
        } else {
            return matchUser.get();
        }
    }

    //매칭 조회 (return type : profile)
    public List<Profile> GetMatchingUserProfileList (Long id){
        List<Match> matchUsers = GetMatchingList(id);
        if (matchUsers.isEmpty()) { //TODO:서비스에서 처리해야 의미있는 코드가 됨.
            return null;
        }

        List<Profile> profiles = new ArrayList<>();
        for (Match match : matchUsers) {
            Optional<Profile> optionalProfile = profileRepository.findById(match.getProfile().getId());
            if (optionalProfile.isPresent()){
                Profile profile = optionalProfile.get();
                profiles.add(profile);
            }
        }
        return profiles;
    }

    //매칭 삭제
    public Error DeleteMatchById (Long id){
        try {
            matchRepository.deleteById(id);
            return new Error(true, "success");
            //TODO: 좀 더 멀쩡해보이는 빌더 패턴으로 변경(후 순위)
        } catch (EntityNotFoundException ex) {
            return new Error(false, "Resource not found");
        }
    }

    public void deleteAllMatches() {
        matchRepository.deleteAll();
    }

    //매칭 스케줄러용 함수
    public void DeleteRejectedMatches() {
        try {
            matchRepository.deleteByMatchingRequest(MatchRequestType.REJECTED);
        } catch (Exception e) {
            // 거절된 매칭 삭제
            logger.error("만들어진 매칭이 한 개도 없습니다. (10분 주기로 실행)", e);
        }
    }

    //매칭 성사
    public Match UpdateMatchStatusIsSuccess(Long id){
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchSuccess(MatchSuccessType.SUCCESS);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return Match.builder().build();
    }

    //매칭 실패
    public Match UpdateMatchStatusIsFalse(Long id){
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchSuccess(MatchSuccessType.FALSE);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return Match.builder().build();
    }

    //매칭 요청
    public Match LikeMatchStatusById (Long id){
        Optional<Match> matchOptional = matchRepository.findById(id);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchingRequest(MatchRequestType.REQUESTED);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return Match.builder().build();
    }

    //매칭 거절
    public Match DislikeMatchStatusById (Long id){
        Optional<Match> matchOptional = matchRepository.findById(id);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchingRequest(MatchRequestType.REJECTED);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return Match.builder().build();
    }

}