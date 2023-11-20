package com.example.springsecurityexample.match.service;

import com.example.springsecurityexample.match.Match;
import com.example.springsecurityexample.match.dto.*;
import com.example.springsecurityexample.match.repository.MatchRepository;
import com.example.springsecurityexample.member.Member;
import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.repository.MemberRepository;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor //DI
public class MatchService {
    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;




    public MatchResponseDto RecommendUser (MatchRequestDto matchRequestDto){
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
                return null;
            }

            // 자기 자신이 매칭 상대인 경우 다시 루프 실행
            if (randomUserId.equals(matchRequestDto.getUid1()))
                continue;

            // 이미 매칭된 경우 다시 루프 실행
            if (matchRepository.findByUid1AndUid2(match.getUid1(), randomUserId).isPresent())
                continue;

            // 매칭 상대 확인
            Optional<Member> profile = memberRepository.findById(randomUserId);
            if (profile.isEmpty())
                continue;

            // 매칭 성공
            match.setUid2(randomUserId);
            break;
        }

        // 매칭 정보 삽입
        match.setMatchingDate(LocalDateTime.now());
        match.setMatchSuccess(false);
        match.setMatchingRequest(false);
        match.update();

        MatchResponseDto matchResponseDto = modelMapper.map(match, MatchResponseDto.class);
        return matchResponseDto;
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


}