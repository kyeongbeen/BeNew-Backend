package com.example.springsecurityexample.member.service;

import com.example.springsecurityexample.member.dto.ProfileRequest;
import com.example.springsecurityexample.member.dto.ProfileResponse;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileResponse getProfile(Long id) {
        // 실제 프로필을 데이터베이스에서 검색하여 ProfileResponse 객체로 변환하는 로직을 구현
        return new ProfileResponse();
    }

    public ProfileResponse createProfile(ProfileRequest request) {
        // 실제 프로필을 데이터베이스에 저장하고, 생성된 프로필을 ProfileResponse 객체로 변환하는 로직을 구현
        return new ProfileResponse();
    }

    public ProfileResponse updateProfile(Long id, ProfileRequest request) {
        // 실제 프로필을 데이터베이스에서 업데이트하고, 업데이트된 프로필을 ProfileResponse 객체로 변환하는 로직을 구현
        return new ProfileResponse();
    }

    public void deleteProfile(Long id) {
        // 실제 프로필을 데이터베이스에서 삭제하는 로직을 구현
    }
}
