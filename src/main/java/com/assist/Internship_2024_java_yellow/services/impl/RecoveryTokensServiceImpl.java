package com.assist.Internship_2024_java_yellow.services.impl;

import com.assist.Internship_2024_java_yellow.entities.RecoveryTokens;
import com.assist.Internship_2024_java_yellow.repository.RecoveryTokensRepository;
import com.assist.Internship_2024_java_yellow.services.RecoveryTokensService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecoveryTokensServiceImpl implements RecoveryTokensService {

    private final RecoveryTokensRepository recoveryTokensRepository;

    @Override
    public RecoveryTokens createRecoveryToken(String email) {
        var recoveryToken = RecoveryTokens.builder()
                .email(email)
                .token(UUID.randomUUID().toString())
                .expiration(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        return recoveryTokensRepository.save(recoveryToken);
    }

    @Override
    public Optional<RecoveryTokens> getRecoveryTokenByEmail(String email) {
        return recoveryTokensRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return recoveryTokensRepository.existsByEmail(email);
    }

    @Override
    public Optional<RecoveryTokens> validateToken(String token) {
        Optional<RecoveryTokens> recoveryToken = recoveryTokensRepository.findByToken(token);

        if (recoveryToken.isPresent()) {
            LocalDateTime expirationTime = LocalDateTime.parse(recoveryToken.get().getExpiration(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (LocalDateTime.now().isBefore(expirationTime)) {
                return recoveryToken;
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteToken(RecoveryTokens recoveryTokens) {
        recoveryTokensRepository.delete(recoveryTokens);
    }

    @Override
    public boolean existsEmailAndValidToken(String email) {
        if (existsByEmail(email)) {

            if (getRecoveryTokenByEmail(email).isPresent()) {
                var recoveryToken = getRecoveryTokenByEmail(email).get();
                LocalDateTime expirationTime = LocalDateTime.parse(recoveryToken.getExpiration(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return LocalDateTime.now().isBefore(expirationTime);
            }
        }
        return false;
    }

    @Override
    public Optional<RecoveryTokens> getRecoveryTokenByValidToken(String token) {
        return validateToken(token);
    }
}
