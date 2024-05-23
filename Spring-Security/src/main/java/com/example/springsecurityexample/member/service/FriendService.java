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

    public void deleteFriend(Long memberId, Long friendId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend not found"));

        // 멤버가 프렌드를 친구로 추가한 관계 찾기
        Friend friendRelation = friendRepository.findByMemberAndFriend(member, friend)
                .orElse(null);  // 관계가 없다면 null 반환

        // 프렌드가 멤버를 친구로 추가한 관계 찾기
        Friend reverseFriendRelation = friendRepository.findByMemberAndFriend(friend, member)
                .orElse(null);  // 관계가 없다면 null 반환

        // 찾은 관계가 있으면 삭제
        if (friendRelation != null) {
            friendRepository.delete(friendRelation);
        }
        if (reverseFriendRelation != null) {
            friendRepository.delete(reverseFriendRelation);
        }
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

