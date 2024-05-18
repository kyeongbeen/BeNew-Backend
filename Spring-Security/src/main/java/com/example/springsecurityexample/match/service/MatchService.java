package com.example.springsecurityexample.match.service;

import com.example.springsecurityexample.match.Error;
import com.example.springsecurityexample.match.Match;
import com.example.springsecurityexample.match.MatchRequestType;
import com.example.springsecurityexample.match.MatchSuccessType;
import com.example.springsecurityexample.match.dto.MatchProjectDto;
import com.example.springsecurityexample.match.dto.MatchRequestDto;
import com.example.springsecurityexample.match.repository.MatchRepository;
import com.example.springsecurityexample.member.Profile;
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
    private final ProfileRepository profileRepository;
    private static final Logger logger = LoggerFactory.getLogger(MatchService.class);

    public List<Profile> getProfilesInRange(Integer userPeer, Integer rangeVar, List<Long> technologyId) {
        List<Profile> profilesInRange;

        // peer 점수와 기술 스택을 통해 추천할 프로필 list 만들기
        if (technologyId != null && !technologyId.isEmpty()) {
            profilesInRange = profileRepository.findByTechnologyLevel_Technology_IdInAndPeerBetween(
                    technologyId, userPeer - rangeVar, userPeer + rangeVar);
        } else {
            profilesInRange = profileRepository.findByPeerBetween(userPeer - rangeVar, userPeer + rangeVar);
        }

        return profilesInRange;
    }

    //매칭 생성
    public List<Match> RecommendUsers(MatchRequestDto matchRequestDto) {
        //반환할 프로필 리스트 (10개 ~ 0개)
        List<Match> matches = new ArrayList<>();

        //사용자의 프로필 카드를 Optional로 가져오기
        Optional<Profile> userInfo = profileRepository.findById(matchRequestDto.getUid1());

        //Optional에서 Profile로 변경
        Profile userProfile;
        if (userInfo.isPresent()) {
            userProfile = userInfo.get();
        } else {
            return Collections.emptyList(); // 프로필이 없으면 빈 리스트 반환
        }

        // 유저 피어점수
        int userPeer = userInfo.get().getPeer();

        // 범위 선언을 밖으로 빼서 반복횟수 줄이기
        int rangeVar = 10;

        //만약 전체 프로필이 10개 이상 없으면? 어쩌지?
        while (matches.size() != 10){

            List<Profile> profilesInRange = getProfilesInRange(userPeer, rangeVar, matchRequestDto.getTechnologyId());

            List<Profile> filteredProfiles;

            //2번정도 실패했으면 중복 허용
            if (rangeVar <30) {
                // 이미 매칭된 프로필 및 앱 사용자의 프로필 거르기
                filteredProfiles = profilesInRange.stream()
                        // *** isPresent() 절대 수정 금지 ***
                        .filter(profile ->
                                !matchRepository.existsByUid1AndProfile(matchRequestDto.getUid1(), profile)
                                        && !Objects.equals(profile, userProfile)
                        )
                        .collect(Collectors.toList());
            }
            else {
                filteredProfiles = profilesInRange.stream()
                        .filter(profile -> !Objects.equals(profile, userProfile))
                        .collect(Collectors.toList());
            }

            // 만약 피어점수 최대 범위를 넘어도 추천할 프로필이 10개 밑이면 그냥 반환
            if (userPeer - rangeVar < 0 && userPeer + rangeVar > 100 && filteredProfiles.isEmpty())
            {
                return matches;
            }

            // 필터링 후 남은 프로필 카드가 없으면 범위 늘리기
            if (filteredProfiles.isEmpty()) {
                rangeVar += 10;
                continue;
            }

            // 필터링 된 프로필 카드 중 랜덤한 프로필 카드를 가져옴
            int memberCount = filteredProfiles.size();
            int randomNum = (int) (Math.random() * memberCount);
            Profile recommendedProfile = filteredProfiles.get(randomNum);

            // 매칭 정보 삽입
            Match match = new Match();
            match.setUid1(matchRequestDto.getUid1());
            match.setProfile(recommendedProfile);
            match.setMatchingDate(LocalDateTime.now());
            match.setMatchSuccess(MatchSuccessType.PENDING);
            match.setMatchingRequest(MatchRequestType.PENDING);
            matchRepository.save(match);

            matches.add(match);
        }

        return matches;
    }
    /*
        public Match SelectStudentsWithRange(Profile userProfile // *** ?범위 안 학생 *** ) {
            // 자기 제외


            // 한명 고르기
            return *** student profile ***;
        }

        */
//매칭 조회 (return type : Match)
    public List<Match> GetMatchingList (Long id){
        List<Match> matchList = matchRepository.findAllByUid1(id);
        //TODO : null exception 예외처리

        for (int i = 0; i < matchList.size(); i++) {
            if ( false
//                    원하는 조건을 붙혀서 원하는 매치 정보만 볼 수 있다.
//                    ex) matchList.get(i).getMatchingRequest() == MatchRequestType.REJECTED

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

    public void DeleteAllMatches() {
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

    //매칭 스케줄러용 함수
    public void DeleteFalseMatches() {
        try {
            matchRepository.deleteByMatchSuccess(MatchSuccessType.FALSE);
        } catch (Exception e) {
            // 거절된 매칭 삭제
            logger.error("만들어진 매칭이 한 개도 없습니다. (10분 주기로 실행)", e);
        }
    }

    //매칭 성사
    public Match UpdateMatchStatusIsSuccess(Long uid1, Long uid2){
        Optional<Match> matchOptional = matchRepository.findByUid1AndProfile_Id(uid1, uid2);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchSuccess(MatchSuccessType.SUCCESS);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return null;
    }

    //매칭 실패
    public Match UpdateMatchStatusIsFalse(Long uid1, Long uid2){
        Optional<Match> matchOptional = matchRepository.findByUid1AndProfile_Id(uid1, uid2);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setMatchSuccess(MatchSuccessType.FALSE);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return null;
    }

    //매칭 요청
    public Match LikeMatchStatusById (Long id, MatchProjectDto projectId){
        Optional<Match> matchOptional = matchRepository.findById(id);

        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            match.setProjectId(projectId.getProjectId());
            match.setMatchingRequest(MatchRequestType.REQUESTED);
            matchRepository.save(match);
            return match; // 변경된 Match 객체를 반환
        }
        return null;
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
        return null;
    }

}