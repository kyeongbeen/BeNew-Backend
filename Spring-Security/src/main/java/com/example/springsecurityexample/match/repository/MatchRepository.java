package com.example.springsecurityexample.match.repository;

import com.example.springsecurityexample.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//@Primary

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByUid1AndMatchingRequestIsFalse(Long Uid1);
    Optional<Match> findByUid1AndUid2(Long Uid1, Long Uid2);
}