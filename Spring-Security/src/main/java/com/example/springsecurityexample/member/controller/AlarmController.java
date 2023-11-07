package com.example.springsecurityexample.member.controller;

import com.example.springsecurityexample.member.Alarm;
import com.example.springsecurityexample.member.dto.AlarmRequest;
import com.example.springsecurityexample.member.dto.AlarmResponse;
import com.example.springsecurityexample.member.repository.MemberRepository;
import com.example.springsecurityexample.member.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmController {
    private final AlarmService alarmService;

    @PostMapping("/alarms")
    public ResponseEntity<AlarmResponse> createAlarm(@RequestBody AlarmRequest alarmRequest) {
        AlarmResponse createdAlarm = alarmService.createAlarm(alarmRequest);
        return new ResponseEntity<>(createdAlarm, HttpStatus.CREATED);
    }

    // 사용자의 알람목록 조회
    @GetMapping("/alarms/{responseId}")
    public ResponseEntity<List<AlarmResponse>> getAlarmsForUser(@PathVariable Long responseId) {
        List<AlarmResponse> alarms = alarmService.getAlarmsForUser(responseId);
        return new ResponseEntity<>(alarms, HttpStatus.OK);
    }

    // 알람 읽음표시
    // userid까지 함께 매핑
    @PutMapping("alarms/{userId}/{alarmId}/read")
    public ResponseEntity<String> markAlarmAsRead(@PathVariable Long userId, @PathVariable Long alarmId) {
        try {
            alarmService.markAlarmAsRead(userId, alarmId);
            return new ResponseEntity<>("Alarm marked as read", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }



}