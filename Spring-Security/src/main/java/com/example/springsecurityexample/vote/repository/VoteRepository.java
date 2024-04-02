package com.example.springsecurityexample.vote.repository;

import com.example.springsecurityexample.vote.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findVotesByRoomId(String roomId);
    Vote findVotesByVoteId(int voteId);
}
