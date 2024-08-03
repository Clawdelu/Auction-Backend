package com.assist.Internship_2024_java_yellow.repository;

import com.assist.Internship_2024_java_yellow.entities.RecoveryTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RecoveryTokensRepository extends JpaRepository<RecoveryTokens,Integer> {

    Optional<RecoveryTokens> findByToken(String token);
    Optional<RecoveryTokens> findByEmail(String email);
    boolean existsByEmail(String email);
}
