package com.assist.Internship_2024_java_yellow.auth;

import com.assist.Internship_2024_java_yellow.config.JwtService;
import com.assist.Internship_2024_java_yellow.dtos.AdminDto;
import com.assist.Internship_2024_java_yellow.entities.User;
import com.assist.Internship_2024_java_yellow.exceptions.EmailExistsException;
import com.assist.Internship_2024_java_yellow.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (!userService.existsByEmail(request.getEmail()))
            throw new UsernameNotFoundException("Wrong credentials");

        var user = userService.getUserByEmail(request.getEmail());


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("User logged in")
                .build();
    }

    public AuthenticationResponse register(UserRegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new EmailExistsException("User already exists");
        }
        User user = userService.createUser(request);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("User registered")
                .build();
    }

    public AuthenticationResponse registerAdmin(AdminDto adminDto) {
        if (userService.existsByEmail(adminDto.getEmail())) {
            throw new EmailExistsException("User already exists");
        }
        User user = userService.createAdminUser(adminDto);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("User registered")
                .build();
    }
}
