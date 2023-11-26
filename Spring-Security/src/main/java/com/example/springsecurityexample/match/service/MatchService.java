package com.example.springsecurityexample.match.service;

import com.example.springsecurityexample.match.Error;
import com.example.springsecurityexample.match.Match;
import com.example.springsecurityexample.match.dto.*;
import com.example.springsecurityexample.match.repository.MatchRepository;
import com.example.springsecurityexample.member.Member;
import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.repository.MemberRepository;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import com.example.springsecurityexample.member.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor //DI
public class MatchService {
    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    public Match RecommendUser (MatchRequestDto matchRequestDto){
        Long randomUserId;

        long memberCount = memberRepository.count();
        int i = 0;

        Match match = modelMapper.map(matchRequestDto, Match.class);

//        int userScore = uid1의 점수
//        int range
//        do {
//            list<evalution> matchingCandidates = evalutionRepository.findAllBy평가총점(userScore +- range);
//            if(userScore.isEmpty()){ // 해당 점수대의 유저가 없음
//                  range += 5;
//                  continue;
//                }
//
//            recommenedUser = (long) (Math.random() * matchingCandidates.size());
//            match.setUid2(recommenedUser.getId());
//            //match 테이블에 없는 매칭일 경우 생성,
//
//            만약 매칭할 팀이 없다면? << 언제 루프를 탈출할지 정해야 함
//                if(range + userScore == 100) break;
//        } while (matchRepository.findAllByUid1AndUid2(match.getUid1(), recommenedUser.getId()) != null);

        while (true) {
            randomUserId = (long) (Math.random() * memberCount + 1);
            i++;

            // 매칭하기 위해 남은 유저를 찾기 힘들 때
            if (i >= 2 * memberCount) {
                randomUserId = null;
                break;
            }

            // 자기 자신이 매칭 상대인 경우 다시 루프 실행
            if (randomUserId.equals(matchRequestDto.getUid1()))
                continue;

            // 이미 매칭된 경우 다시 루프 실행
            if (matchRepository.findByUid1AndProfile_Id(match.getUid1(), randomUserId).isPresent())
                continue;

            // 매칭 상대 확인
            Optional<Profile> optionalProfile = profileRepository.findById(randomUserId);
            if (optionalProfile.isEmpty())
                continue;


            // 매칭 성공, profile card 연결
            Profile profile = optionalProfile.get();
            match.setProfile(profile);
            break;
        }

        //매칭된 유저가 없는 경우 (남은 유저가 너무 적거나 없는 경우, 프로필 카드가 없는 경우)
        if(randomUserId == null)
            return Match.builder().build();

        // 매칭 정보 삽입
        match.setMatchingDate(LocalDateTime.now());
        match.setMatchSuccess(false);
        match.setMatchingRequest(false);
        match.update();
        matchRepository.save(match);
        return match;
    }

    public List<Match> GetMatchingList (Long id){
        List<Match> matchList = matchRepository.findAllByUid1AndMatchingRequestIsFalse(id);
        //TODO : null exception 예외처리

        for (int i = 0; i < matchList.size(); i++) {
            matchList.get(i).update();
            if (matchList.get(i).getMatchingRequest() || matchList.get(i).getMatchSuccess()) {
                matchList.remove(i);
                i--;
            }
        }
        return matchList;
    }

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
    public Error DeleteMatchById (Long id){
        try {
            matchRepository.deleteById(id);
            return new Error(true, "success");
            //TODO: 좀 더 멀쩡해보이는 빌더 패턴으로 변경(후 순위)
        } catch (EntityNotFoundException ex) {
            return new Error(false, "Resource not found");
        }
    }

    public Match UpdateMatchStatusById (Long id){
        Optional<Match> matchOptional = matchRepository.findById(id);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchSuccess(true);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return Match.builder().build();
    }

    public Match LikeMatchStatusById (Long id){
        Optional<Match> matchOptional = matchRepository.findById(id);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchingRequest(true);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return Match.builder().build();
    }

}