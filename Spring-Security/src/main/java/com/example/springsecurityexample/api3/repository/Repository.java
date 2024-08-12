package com.example.springsecurityexample.api3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Repository extends JpaRepository {
}
