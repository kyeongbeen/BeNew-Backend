package com.example.springsecurityexample.member.controller;

import com.example.springsecurityexample.member.Friend;
import com.example.springsecurityexample.member.service.FriendService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/friend/add")
    @ApiOperation("친구 추가 요청")
    public ResponseEntity<String> addFriend(@RequestParam Long memberId, @RequestParam Long friendId) {
        friendService.addFriend(memberId, friendId);
        return new ResponseEntity<>("친구 요청을 보냈습니다.", HttpStatus.OK);
    }

    @PostMapping("/friend/accept")
    @ApiOperation("친구 요청 수락")
    public ResponseEntity<String> acceptFriendRequest(@RequestParam Long memberId, @RequestParam Long friendId) {
        friendService.acceptFriendRequest(memberId, friendId);
        return new ResponseEntity<>("친구 요청을 수락했습니다.", HttpStatus.OK);
    }

    @GetMapping("/friend/list")
    @ApiOperation("친구 목록 조회")
    public ResponseEntity<List<Friend>> getFriends(@RequestParam Long memberId) {
        List<Friend> friends = friendService.getFriends(memberId);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }
}
