package com.example.springsecurityexample.match.repository;

import com.example.springsecurityexample.match.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//@Primary

public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findAllByUid1AndMatchingRequestIsFalse(int Uid1);
    List<Match> findAllByUid1AndUid2(int Uid1, int Uid2);
}
