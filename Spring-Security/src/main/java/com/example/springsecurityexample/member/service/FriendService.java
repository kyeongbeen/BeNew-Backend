package com.example.springsecurityexample.member.service;

import com.example.springsecurityexample.member.Friend;
import com.example.springsecurityexample.member.Member;
import com.example.springsecurityexample.member.repository.FriendRepository;
import com.example.springsecurityexample.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public void addFriend(Long memberId, Long friendId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found"));

        Friend friendRelation = Friend.builder()
                .member(member)
                .friend(friend)
                .status("REQUESTED")
                .build();

        friendRepository.save(friendRelation);
    }

    public void acceptFriendRequest(Long memberId, Long friendId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found"));

        Friend friendRelation = friendRepository.findByMemberAndFriend(friend, member)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found"));

        friendRelation.setStatus("ACCEPTED");

        friendRepository.save(friendRelation);
    }

    public List<Friend> getFriends(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<Friend> friends = friendRepository.findByMemberAndStatus(member, "ACCEPTED");
        List<Friend> friendsAccepted = friendRepository.findByFriendAndStatus(member, "ACCEPTED");

        friends.addAll(friendsAccepted);

        return friends;
    }
}

