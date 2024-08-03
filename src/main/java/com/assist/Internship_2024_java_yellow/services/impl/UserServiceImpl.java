package com.assist.Internship_2024_java_yellow.services.impl;

import com.assist.Internship_2024_java_yellow.auth.AuthenticationResponse;
import com.assist.Internship_2024_java_yellow.auth.UserRegisterRequest;
import com.assist.Internship_2024_java_yellow.config.JwtService;
import com.assist.Internship_2024_java_yellow.dtos.*;
import com.assist.Internship_2024_java_yellow.entities.*;
import com.assist.Internship_2024_java_yellow.enums.RoleEnum;
import com.assist.Internship_2024_java_yellow.enums.StatusEnum;
import com.assist.Internship_2024_java_yellow.exceptions.*;
import com.assist.Internship_2024_java_yellow.mappers.AuctionMapper;
import com.assist.Internship_2024_java_yellow.mappers.CompanyMapper;
import com.assist.Internship_2024_java_yellow.mappers.MediaFilesMapper;
import com.assist.Internship_2024_java_yellow.mappers.UserMapper;
import com.assist.Internship_2024_java_yellow.repository.UserRepository;
import com.assist.Internship_2024_java_yellow.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;


@Service
@RequiredArgsConstructor
@Log
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AmazonS3Service amazonS3Service;

    private final AuctionService auctionService;

    private final AuctionMapper auctionMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final RoleService roleService;

    private final CompanyService companyService;

    private final RecoveryTokensService recoveryTokenService;

    private final ValidationService validationService;

    private final SMTPGmailService smtpGmailService;

    private final MediaFilesService mediaFilesService;

    private final MediaFilesMapper mediaFilesMapper;

    private static final String URL = "http://internship2024-frontend-yellow.dev.assist.ro/resetpassword?token=";
    private final JwtService jwtService;
    private final BidService bidService;
    private final CompanyMapper companyMapper;


    @Override
    public MediaFilesDTO addProfilePicture(MultipartFile file) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User contextUser = userRepository.findByEmail(authentication.getName());

        if (contextUser == null) {

            throw new UserNotAuthenticatedException("User is not authenticated.");
        }

        String fileName = String.format("%s-%s-%s", contextUser.getEmail(), System.currentTimeMillis(), file.getOriginalFilename());

        String url = amazonS3Service.uploadFileToAmazonS3(file, fileName);

        contextUser.setProfilePicture(url);

        User savedUser = userRepository.save(contextUser);

        return MediaFilesDTO.builder()
                .fileName(fileName)
                .fileUrl(url)
                .mediaType(file.getContentType())
                .build();
    }

    @Override

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void addAuctionToFavourites(String auctionIdentifier) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User contextUser = userRepository.findByEmail(authentication.getName());

        if (contextUser == null) {

            throw new UserNotAuthenticatedException("User is not authenticated.");
        }

        Optional<Auction> auction = auctionService.getAuctionEntityByIdentifier(auctionIdentifier);

        if (!auction.isPresent())
            throw new AuctionNotFoundException(auctionIdentifier);

        List<Auction> favouriteAuctions = contextUser.getFavouriteAuctions();

        if (favouriteAuctions.contains(auction.get()))
            throw new RuntimeException("Auction is already is favourite list.");

        favouriteAuctions.add(auction.get());

        contextUser.setFavouriteAuctions(favouriteAuctions);

        userRepository.save(contextUser);
    }

    @Override
    public void removeAuctionFromFavourites(String auctionIdentifier) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User contextUser = userRepository.findByEmail(authentication.getName());

        if (contextUser == null) {

            throw new UserNotAuthenticatedException("User is not authenticated.");
        }

        Optional<Auction> auction = auctionService.getAuctionEntityByIdentifier(auctionIdentifier);

        if (!auction.isPresent())
            throw new AuctionNotFoundException(auctionIdentifier);

        List<Auction> favouriteAuctions = contextUser.getFavouriteAuctions();

        favouriteAuctions.remove(auction.get());

        contextUser.setFavouriteAuctions(favouriteAuctions);

        userRepository.save(contextUser);
    }

    @Override
    public List<ViewAuction> getFavouriteAuctions() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User contextUser = userRepository.findByEmail(authentication.getName());

        if (contextUser == null) {
            throw new UserNotAuthenticatedException("User is not authenticated.");
        }
        List<Auction> favouriteAuctions = contextUser.getFavouriteAuctions();

        List<ViewAuction> viewAuctions = new ArrayList<>();

        favouriteAuctions.forEach((auction) -> {
                    var mediaFilesDtoList = mediaFilesMapper.INSTANCE.toMediaFilesDto(auction.getMediaFiles());
                    var userDto = userMapper.INSTANCE.toUserDTO(auction.getUser());
                    var viewAuction = auctionMapper.INSTANCE.auctionToDto(auction);
                    viewAuction.setMediaFilesList(mediaFilesDtoList);
                    viewAuction.setUser(userDto);
                    viewAuctions.add(viewAuction);
                }
        );

        viewAuctions.forEach((v) ->
        {
            v.setFavourite(true);
            BidDetailsDTO bid = bidService.getBidDetails(v.getAuctionIdentifier());
            if (Objects.equals(bid.getLatestBid(), bid.getYourBid()))
                v.setBiggestBid(true);
            if (!Objects.isNull(bid.getOverthrownBy()))
                v.setOverthrownAmount(bid.getOverthrownBy());
            else
                v.setOverthrownAmount(0);
            if (!Objects.isNull(bid.getLatestBid()))
                v.setLatestBid(bid.getLatestBid());
            else
                v.setLatestBid(0);
            if (StatusEnum.Finished.equals(v.getStatus())) {
                if (Objects.equals(v.getLatestBid(), bid.getYourBid()))
                    v.setWon(true);
                v.setSettledBid(v.getLatestBid());
            } else {
                v.setWon(false);
                v.setSettledBid(0);
            }
        });

        return viewAuctions;
    }


    @Override
    public User createUser(UserRegisterRequest userRegisterRequest) {
        validateUserFields(userRegisterRequest);
        String encryptedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());
        User newUser = userMapper.INSTANCE.toUser(userRegisterRequest, encryptedPassword);
        Set<Role> roles = new HashSet<>();
        Role role = roleService.findByRoleName(RoleEnum.LEGAL);

        if (role != null) {
            roles.add(role);
        } else throw new RoleNotFoundException("Role not found");

        newUser.setRoles(roles);
        companyService.createCompany(userRegisterRequest, newUser);

        return userRepository.save(newUser);
    }

    @Override
    public void forgotPassword(String email) {
        if (!existsByEmail(email)) throw new UserNotFoundException("No account with the email address");
        RecoveryTokens recoveryTokenObject;
        if (recoveryTokenService.existsEmailAndValidToken(email) &&
                recoveryTokenService.getRecoveryTokenByEmail(email).isPresent()) {

            recoveryTokenObject = recoveryTokenService.getRecoveryTokenByEmail(email).get();
        } else {
            recoveryTokenObject = recoveryTokenService.createRecoveryToken(email);
        }
        String subject = "Password Reset Request";
        String url = URL + recoveryTokenObject.getToken();

        String text = "Hello " + recoveryTokenObject.getEmail() + ",\n\n" +
                "Please click the link below to reset your password:\n\n" +
                url + "\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Cheers,\n" +
                "Con-X Team";
        System.out.println(text);
        smtpGmailService.sendEmail(email, subject, text);
    }

    @Override
    public void recoverPassword(ResetPasswordDto resetPasswordDto) {
        if (!Objects.equals(resetPasswordDto.getPassword(), resetPasswordDto.getRepeatPassword()))
            throw new PasswordNotMatchException("Password not match.");

        if (recoveryTokenService.getRecoveryTokenByValidToken(resetPasswordDto.getToken()).isPresent()) {
            RecoveryTokens recoveryToken = recoveryTokenService.getRecoveryTokenByValidToken(resetPasswordDto.getToken()).get();
            resetUserPassword(resetPasswordDto.getPassword(), recoveryToken.getEmail());
            recoveryTokenService.deleteToken(recoveryToken);
        }
    }

    @Override
    public void resetUserPassword(String newPassword, String email) {
        var user = getUserByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else throw new UserNotFoundException(" No account with the email address");
    }

    @Override
    public void validateUserFields(UserRegisterRequest userRegisterRequest) {
        if (!validationService.validateEmail(userRegisterRequest.getEmail()))
            throw new InvalidEmailException("Invalid email");

        if (!validationService.validateFirstName(userRegisterRequest.getFirstName()))
            throw new InvalidFirstNameException("Invalid first name");

        if (!validationService.validateLastName(userRegisterRequest.getLastName()))
            throw new InvalidLastNameException("Invalid last name");

        if (!validationService.validatePassword(userRegisterRequest.getPassword()))
            throw new InvalidPasswordException("Invalid password");
    }

    @Override
    public User createAdminUser(AdminDto adminDto) {
        String encryptedPassword = passwordEncoder.encode(adminDto.getPassword());
        User newUser = userMapper.INSTANCE.toUser(adminDto, encryptedPassword);
        Set<Role> roles = new HashSet<>();
        Role role = roleService.findByRoleName(RoleEnum.ADMIN);
        if (role != null) {
            roles.add(role);
        }
        newUser.setRoles(roles);
        return userRepository.save(newUser);
    }

    private void validateUserFields(EditUserDTO userDTO) {

        if (!userDTO.getEmail().isEmpty() && !validationService.validateEmail(userDTO.getEmail()))
            throw new InvalidEmailException("Invalid email");

        if (!userDTO.getFirstName().isEmpty() && !validationService.validateFirstName(userDTO.getFirstName()))
            throw new InvalidFirstNameException("Invalid first name");

        if (!userDTO.getLastName().isEmpty() && !validationService.validateLastName(userDTO.getLastName()))
            throw new InvalidLastNameException("Invalid last name");

        if (!userDTO.getPassword().isEmpty() && !validationService.validatePassword(userDTO.getPassword()))
            throw new InvalidPasswordException("Invalid password");

        if (!userDTO.getRepeatPassword().isEmpty() && !validationService.validatePassword(userDTO.getRepeatPassword()))
            throw new InvalidPasswordException("Invalid password");

        if (!userDTO.getPassword().isEmpty() && !userDTO.getRepeatPassword().isEmpty() && Objects.equals(userDTO.getPassword(), userDTO.getRepeatPassword()))
            throw new InvalidPasswordException("Passwords do not match");
    }

    @Override
    public AuthenticationResponse editUserDetails(EditUserDTO editUserDTO) {

        validateUserFields(editUserDTO);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User contextUser = userRepository.findByEmail(authentication.getName());

        if (contextUser == null) {

            throw new UserNotAuthenticatedException("User is not authenticated.");
        }

        if (!contextUser.getFirstName().isEmpty())
            contextUser.setFirstName(editUserDTO.getFirstName());

        if (!contextUser.getLastName().isEmpty())
            contextUser.setLastName(editUserDTO.getLastName());

        if (!editUserDTO.getEmail().isEmpty())
            contextUser.setEmail(editUserDTO.getEmail());

        if (!editUserDTO.getPassword().isEmpty())
            contextUser.setPassword(passwordEncoder.encode(editUserDTO.getPassword()));

        User user = userRepository.save(contextUser);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Successfully edited user")
                .build();
    }

    private void validateFiles(MultipartFile files) {

        if (!validationService.validateFileTypeProfilePicture(files.getContentType())) {

            throw new InvalidFileTypeAuctionFilesException("The file type for file " + files.getOriginalFilename() + " is not valid. Only .svg, .png, .jpg, .jpeg and .gif are accepted. ");
        }
    }

    @Override
    public String createProfilePictureLink(MultipartFile files) throws IOException {

        validateFiles(files);

        return addProfilePicture(files).getFileUrl();
    }

    @Override
    public CompanyDTO getCompany() {
        User contextUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Company company = contextUser.getCompany();
        return companyMapper.INSTANCE.companyToCompanyDTO(company);
    }
}
