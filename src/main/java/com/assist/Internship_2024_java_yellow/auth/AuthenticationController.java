package com.assist.Internship_2024_java_yellow.auth;

import com.assist.Internship_2024_java_yellow.dtos.AdminDto;
import com.assist.Internship_2024_java_yellow.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserService userService;

    @Operation(summary = "This method is used to log in.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationRequest.class))}),
            @ApiResponse(responseCode = "401", description = "Wrong credentials",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> Authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate((request)));
    }

    @Operation(summary = "This method is used to create an account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRegisterRequest.class))}),
            @ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid CIF",
                    content = @Content)
    })

    @PostMapping("/users")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        return new ResponseEntity<>(service.register(request), HttpStatus.CREATED);
    }

}
