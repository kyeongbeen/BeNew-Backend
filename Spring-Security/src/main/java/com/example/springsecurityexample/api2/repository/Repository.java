package com.example.springsecurityexample.api2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Repository extends JpaRepository {
}
