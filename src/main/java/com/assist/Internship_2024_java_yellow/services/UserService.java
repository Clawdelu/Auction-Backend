package com.assist.Internship_2024_java_yellow.services;

import com.assist.Internship_2024_java_yellow.auth.AuthenticationResponse;
import com.assist.Internship_2024_java_yellow.auth.UserRegisterRequest;
import com.assist.Internship_2024_java_yellow.dtos.*;
import com.assist.Internship_2024_java_yellow.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    User getUserByEmail(String email);

    boolean existsByEmail(String email);

    MediaFilesDTO addProfilePicture(MultipartFile file) throws IOException;

    void addAuctionToFavourites(String auctionIdentifier);

    void removeAuctionFromFavourites(String auctionIdentifier);

    List<ViewAuction> getFavouriteAuctions();

    User createUser(UserRegisterRequest userRegisterRequest);

    void forgotPassword(String email);

    void recoverPassword(ResetPasswordDto resetPasswordDto);

    void resetUserPassword(String newPassword, String email);

    void validateUserFields(UserRegisterRequest userRegisterRequest);

    User createAdminUser(AdminDto adminDto);

    AuthenticationResponse editUserDetails(EditUserDTO editUserDTO);

    String createProfilePictureLink(MultipartFile files) throws IOException;

    CompanyDTO getCompany();

    }
