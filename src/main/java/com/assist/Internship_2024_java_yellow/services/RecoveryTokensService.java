package com.assist.Internship_2024_java_yellow.services;

import com.assist.Internship_2024_java_yellow.entities.RecoveryTokens;

import java.util.Optional;

public interface RecoveryTokensService {

    RecoveryTokens createRecoveryToken(String email);

    Optional<RecoveryTokens> getRecoveryTokenByEmail(String email);

    boolean existsByEmail(String email);

    Optional<RecoveryTokens> validateToken(String token);

    void deleteToken(RecoveryTokens recoveryTokens);

    boolean existsEmailAndValidToken(String email);

    Optional<RecoveryTokens> getRecoveryTokenByValidToken(String token);
}
