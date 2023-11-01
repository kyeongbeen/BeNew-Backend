package com.example.springsecurityexample.member.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Test
    void getProfile() {

    }

    @Test
    void createProfile() {
    }

    @Test
    void updateProfile() {
    }

    @Test
    void deleteProfile() {
    }
}