package com.example.springsecurityexample.member.controller;

import com.example.springsecurityexample.member.dto.AlarmResponse;
import org.springframework.context.ApplicationEvent;

public class AlarmCreatedEvent extends ApplicationEvent {
    private final AlarmResponse alarmResponse;

    public AlarmCreatedEvent(Object source, AlarmResponse alarmResponse) {
        super(source);
        this.alarmResponse = alarmResponse;
    }

    public AlarmResponse getAlarmResponse() {
        return alarmResponse;
    }
}
