package com.example.springsecurityexample.member.service;

import com.example.springsecurityexample.member.Profile;
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
        Profile profile = profileRepository.findById(id).orElse(null);
        if (profile == null) {
            // 해당 ID의 프로필이 존재하지 않으면 예외 처리
            throw new ProfileNotFoundException("프로필을 찾을 수 없습니다. ID: " + id);
        }
        // Profile 엔티티를 ProfileResponse DTO로 변환하는 메서드를 호출
        return convertToResponse(profile);
    }

    public ProfileResponse createProfile(ProfileRequest request) {
        // ProfileRequest DTO를 Profile 엔티티로 변환
        Profile profile = convertToEntity(request);
        // Profile 엔티티를 데이터베이스에 저장
        Profile savedProfile = profileRepository.save(profile);
        // 저장된 프로필 엔티티를 ProfileResponse DTO로 변환
        return convertToResponse(savedProfile);
    }

    public ProfileResponse updateProfile(Long id, ProfileRequest request) {
        Profile profile = profileRepository.findById(id).orElse(null);
        if (profile == null) {
            // 해당 ID의 프로필이 존재하지 않으면 예외 처리
            throw new ProfileNotFoundException("프로필을 찾을 수 없습니다. ID: " + id);
        }
        // ProfileRequest DTO를 Profile 엔티티로 업데이트
        updateEntityFromRequest(profile, request);
        // 업데이트된 프로필 엔티티를 데이터베이스에 저장
        Profile updatedProfile = profileRepository.save(profile);
        // 업데이트된 프로필 엔티티를 ProfileResponse DTO로 변환
        return convertToResponse(updatedProfile);
    }

    public void deleteProfile(Long id) {
        Profile profile = profileRepository.findById(id).orElse(null);
        if (profile == null) {
            // 해당 ID의 프로필이 존재하지 않으면 예외 처리
            throw new ProfileNotFoundException("프로필을 찾을 수 없습니다. ID: " + id);
        }
        // 데이터베이스에서 프로필 삭제
        profileRepository.delete(profile);
    }

    // Profile 엔티티를 ProfileResponse DTO로 변환하는 메서드
    private ProfileResponse convertToResponse(Profile profile) {
        // 변환 로직을 구현
        return new ProfileResponse();
    }

    // ProfileRequest DTO를 Profile 엔티티로 변환하는 메서드
    private Profile convertToEntity(ProfileRequest request) {
        // 변환 로직을 구현
        return new Profile();
    }

    // Profile 엔티티를 ProfileRequest DTO로 업데이트하는 메서드
    private void updateEntityFromRequest(Profile profile, ProfileRequest request) {
        // 업데이트 로직을 구현
    }
}