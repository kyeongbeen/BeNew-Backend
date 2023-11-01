package com.example.springsecurityexample.member.controller;

import com.example.springsecurityexample.member.dto.ProfileRequest;
import com.example.springsecurityexample.member.dto.ProfileResponse;
import com.example.springsecurityexample.member.repository.ProfileRepository;
import com.example.springsecurityexample.member.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long id) {
        ProfileResponse profile = profileService.getProfile(id);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(@RequestBody ProfileRequest request) {
        ProfileResponse profile = profileService.createProfile(request);
        return new ResponseEntity<>(profile, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponse> updateProfile(@PathVariable Long id, @RequestBody ProfileRequest request) {
        ProfileResponse profile = profileService.updateProfile(id, request);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
