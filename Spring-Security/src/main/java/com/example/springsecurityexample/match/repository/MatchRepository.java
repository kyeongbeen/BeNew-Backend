package com.example.springsecurityexample.match.repository;

import com.example.springsecurityexample.match.Match;
import com.example.springsecurityexample.match.MatchRequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//@Primary

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByUid1AndMatchingRequestIsFalse(Long Uid1);
    List<Match> findAllByUid1(Long Uid1);
    Optional<Match> findByUid1AndProfile_Id(Long Uid1, Long Uid2);
    Optional<Match> findByUid1(Long Uid1);
    void deleteByMatchingRequest(MatchRequestType matchingRequest);
}