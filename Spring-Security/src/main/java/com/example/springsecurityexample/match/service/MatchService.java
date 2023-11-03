package com.example.springsecurityexample.match.service;

import com.example.springsecurityexample.match.Match;
import com.example.springsecurityexample.match.dto.*;
import com.example.springsecurityexample.match.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor //DI
public class MatchService {
    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;



    public MatchResponseDto RecommendUser (MatchRequestDto matchRequestDto){
        int randomUserId;

        Match match = modelMapper.map(matchRequestDto, Match.class);

            //팀 table이 완성되면 TeamRepository에 CountBy() 메소드 만들어서 랜덤의 최댓값을 바꾸자.
            do {
                randomUserId = (int) (Math.random() * 100 + 1);
                match.setUid2(randomUserId);
                //match 테이블에 없는 매칭일 경우 생성, 만약 매칭할 팀이 없다면? << 예외처리 필요
            } while (matchRepository.findAllByUid1AndUid2(match.getUid1(), randomUserId) == null);

        match.setMatchingDate(LocalDateTime.now());
        match.setMatchSuccess(false);
        match.setMatchingRequest(false);

        MatchResponseDto matchResponseDto = modelMapper.map(match, MatchResponseDto.class);
        return matchResponseDto;
    }

    public List<Match> GetMatchingList (int id){
        List<Match> matchList = matchRepository.findAllByUid1AndMatchingRequestIsFalse(id);
        //TODO : null exception 예외처리

            for (int i = 0; i < matchList.size(); i++) {
                if (!matchList.get(i).getIsUid2Team() || matchList.get(i).getMatchSuccess()) {
                    matchList.remove(i);
                    i--;
                }
            }
        return matchList;
    }


}
