package com.example.springsecurityexample.member.controller;

import com.example.springsecurityexample.member.dto.CheckResponse;
import com.example.springsecurityexample.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CheckController {

    private final MemberRepository memberRepository;

    @GetMapping("/check/account")
    public ResponseEntity<CheckResponse> checkAccountDuplication(@RequestParam String account) {
        boolean isDuplicate = memberRepository.findByAccount(account).isPresent();
        String message = isDuplicate ? "이미 존재하는 계정입니다." : "사용 가능한 계정입니다.";
        return ResponseEntity.ok(new CheckResponse(isDuplicate, message));
    }

    @GetMapping("/check/email")
    public ResponseEntity<CheckResponse> checkEmailDuplication(@RequestParam String email) {
        boolean isDuplicate = memberRepository.findByEmail(email).isPresent();
        String message = isDuplicate ? "이미 등록된 이메일입니다." : "사용 가능한 이메일입니다.";
        return ResponseEntity.ok(new CheckResponse(isDuplicate, message));
    }

    @GetMapping("/check/phone")
    public ResponseEntity<CheckResponse> checkPhoneDuplication(@RequestParam String phone) {
        boolean isDuplicate = memberRepository.findByPhoneNumber(phone).isPresent();
        String message = isDuplicate ? "이미 등록된 전화번호입니다." : "사용 가능한 전화번호입니다.";
        return ResponseEntity.ok(new CheckResponse(isDuplicate, message));
    }
}


