package com.example.springsecurityexample.member.repository;

import com.example.springsecurityexample.member.Friend;
import com.example.springsecurityexample.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByMember(Member member);
    List<Friend> findByMemberAndStatus(Member member, String status);
    Optional<Friend> findByMemberAndFriend(Member member, Member friend);

    List<Friend> findByFriendAndStatus(Member member, String status);
}

