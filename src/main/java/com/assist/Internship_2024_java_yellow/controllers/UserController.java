package com.assist.Internship_2024_java_yellow.controllers;


import com.assist.Internship_2024_java_yellow.dtos.EditUserDTO;
import com.assist.Internship_2024_java_yellow.dtos.ForgotDTO;
import com.assist.Internship_2024_java_yellow.dtos.ResetPasswordDto;
import com.assist.Internship_2024_java_yellow.dtos.ViewAuction;
import com.assist.Internship_2024_java_yellow.services.AuctionService;
import com.assist.Internship_2024_java_yellow.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log
public class UserController {

    private final UserService userService;

    @Operation(summary = "This method is used to sent an email for reset password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email sent.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "No account with the email address.",
                    content = @Content)
    })
    @PostMapping("/api/user/forgot")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotDTO forgotDto) {
        userService.forgotPassword(forgotDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "This method is used to reset password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordDto.class))}),
            @ApiResponse(responseCode = "400", description = "Password not match.",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Token expired.",
                    content = @Content)
    })
    @PostMapping("/api/user/recover")
    public ResponseEntity<?> recoverPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        userService.recoverPassword(resetPasswordDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset");
        return ResponseEntity.ok(response);

    }

    @PutMapping("/api/legal/user/favouriteAuction/{auctionIdentifier}")
    public ResponseEntity<?> addToFavourites(@PathVariable String auctionIdentifier) {
        userService.addAuctionToFavourites(auctionIdentifier);
        return ResponseEntity.status(200).body("Auction was added to favourite.");
    }

    @DeleteMapping("/api/legal/user/favouriteAuction/{auctionIdentifier}")
    public ResponseEntity<?> removeFromFavourites(@PathVariable String auctionIdentifier) {
        userService.removeAuctionFromFavourites(auctionIdentifier);
        return ResponseEntity.status(200).body("Auction was deleted from favourite.");
    }

    @GetMapping("/api/legal/user/favouriteAuction")
    public ResponseEntity<List<ViewAuction>> getFavouriteAuctions() {
        return ResponseEntity.status(200).body(userService.getFavouriteAuctions());

    }

    @PostMapping(value = "/api/legal/user/uploadProfilePicture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadUserPhoto(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(201).body(userService.createProfilePictureLink(file));
    }

    @PutMapping(value = "/api/legal/user/editUserDetails")
    public ResponseEntity<?> editUserDetails(EditUserDTO editUserDTO)
    {
        return ResponseEntity.status(200).body(userService.editUserDetails(editUserDTO));
    }

}
