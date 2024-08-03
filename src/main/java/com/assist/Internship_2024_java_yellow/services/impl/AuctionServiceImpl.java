package com.assist.Internship_2024_java_yellow.services.impl;


import com.assist.Internship_2024_java_yellow.dtos.*;
import com.assist.Internship_2024_java_yellow.entities.Auction;
import com.assist.Internship_2024_java_yellow.entities.MediaFiles;
import com.assist.Internship_2024_java_yellow.entities.User;
import com.assist.Internship_2024_java_yellow.enums.StatusEnum;
import com.assist.Internship_2024_java_yellow.exceptions.*;
import com.assist.Internship_2024_java_yellow.mappers.AuctionMapper;
import com.assist.Internship_2024_java_yellow.mappers.MediaFilesMapper;
import com.assist.Internship_2024_java_yellow.mappers.UserMapper;
import com.assist.Internship_2024_java_yellow.repository.AuctionRepository;
import com.assist.Internship_2024_java_yellow.repository.UserRepository;
import com.assist.Internship_2024_java_yellow.services.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MediaFilesService mediaFilesService;
    private final MediaFilesMapper mediaFilesMapper;
    private final AmazonS3Service amazonS3Service;
    private final ValidationService validationService;
    private final SMTPGmailService smtpGmailService;
    private final BidService bidService;

    private boolean validateAuction(Auction auction) {

        if (!validationService.validateEmail(auction.getEmail())) {

            throw new InvalidEmailException("The email is not valid.");
        }

        if (!validationService.validateFirstName(auction.getFirstName())) {

            throw new InvalidFirstNameException("The first name is not valid.");
        }

        if (!validationService.validateLastName(auction.getLastName())) {

            throw new InvalidLastNameException("The last name is not valid.");
        }

        if (!validationService.validateStartingDate(auction.getStartTime(), auction.getEndTime())) {

            throw new InvalidStartingDateException("The starting date is not valid. It can't be earlier than the current date, later than the ending date or equal to the current date or ending date.");
        }

        if (!validationService.validateEndingDate(auction.getEndTime())) {

            throw new InvalidEndingDateException("The ending date is not valid. It can't be earlier than the current date.");
        }

        if (!validationService.validateStartingPrice(auction.getStartingPrice(), auction.getThresholdPrice())) {

            throw new InvalidStartingPriceException("The starting price is not valid. It can't be higher than the threshold price, a negative number or zero.");
        }

        if (!validationService.validateThresholdPrice(auction.getThresholdPrice())) {

            throw new InvalidThresholdPriceException("The threshold price is not valid. It can't be a negative number or zero.");
        }

        return true;
    }

    private boolean validateFiles(List<MultipartFile> files) {

        for (MultipartFile file : files) {

            if (!validationService.validateFileTypeAuctionFiles(file.getContentType())) {

                throw new InvalidFileTypeAuctionFilesException("The file type for file " + file.getOriginalFilename() + " is not valid. Only .svg, .png, .jpg, .jpeg and .gif are accepted. ");
            }
        }
        return true;
    }

    public AuctionListPaginationResponse<Object> searchForAuctions(String keywords, int page, int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);

        String keywordsForQuery = Arrays
                .stream(keywords.split("\\s+"))
                .map(o -> o + ":*")
                .collect(Collectors.joining(" & "));

        System.out.println(keywordsForQuery);

        Page<Auction> auctions = auctionRepository.searchAuctionsByTitle(keywordsForQuery, pageable);

        User contextUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        List<ViewAuction> auctionList = auctions.getContent()
                .stream()
                .map(o -> {

                    UserDTO userDTO = userMapper.toUserDTO(o.getUser());

                    ViewAuction viewAuction = auctionMapper.auctionToDto(o);

                    viewAuction.setUser(userDTO);

                    List<MediaFilesDTO> mediaFilesDTOList = o.getMediaFiles()
                            .stream()
                            .map(mediaFilesMapper::toMediaFilesDTOFromMediaFiles)
                            .toList();

                    viewAuction.setMediaFilesList(mediaFilesDTOList);

                    boolean isFavourite = contextUser != null && contextUser.getFavouriteAuctions().contains(o);

                    viewAuction.setFavourite(isFavourite);

                    if (contextUser != null) {

                        BidDetailsDTO bidDetailsDTO = bidService.getBidDetails(o.getAuctionIdentifier());

                        boolean isWon = Objects.equals(bidDetailsDTO.getYourBid(), bidDetailsDTO.getLatestBid())
                                && bidDetailsDTO.getYourBid() >= o.getThresholdPrice();

                        viewAuction.setWon(isWon);

                        double overthrownAmount = bidDetailsDTO.getOverthrownBy();

                        viewAuction.setOverthrownAmount(overthrownAmount);

                        boolean biggestBid =  Objects.equals(bidDetailsDTO.getYourBid(), bidDetailsDTO.getLatestBid());

                        viewAuction.setBiggestBid(biggestBid);
                    }

                    viewAuction.setWon(false);

                    viewAuction.setOverthrownAmount(0);

                    viewAuction.setBiggestBid(false);

                    double latestBid = bidService.getLatestBid(o.getAuctionIdentifier()).isPresent()
                                       ? bidService.getLatestBid(o.getAuctionIdentifier()).get().getAmountBid() : 0;

                    viewAuction.setLatestBid(latestBid);

                    viewAuction.setSettledBid(latestBid >= o.getThresholdPrice()
                                              && StatusEnum.Finished.equals(o.getStatus())
                                              ? latestBid : 0);


                    if (!StatusEnum.Pending.equals(o.getStatus())) {

                        viewAuction.setStatus(OffsetDateTime.now().isBefore(o.getStartTime())
                                && o.getRejectReason() == null
                                ? StatusEnum.Starting : OffsetDateTime.now().isAfter(o.getEndTime())
                                ? StatusEnum.Finished : StatusEnum.Ongoing);
                    }

                    return viewAuction;

                }).toList();

        return AuctionListPaginationResponse.builder()
                .auctions(auctionList)
                .page(auctions.getNumber())
                .pageSize(auctions.getSize())
                .totalPages(auctions.getTotalPages())
                .count((int) auctions.getTotalElements())
                .build();
    }

    @Override
    public AuctionListPaginationResponse<Object> getAdminPendingAuctions(int page, int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Auction> auctions = auctionRepository.findAuctionsByStatus(StatusEnum.Pending, pageable);

        return getAuctionListPaginationResponse(auctions);
    }

    @Override
    public AuctionListPaginationResponse<Object> getAdminAllAuctions(int page, int pageSize, StatusEnum status) {

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Auction> auctions = auctionRepository.findAll(pageable);

        if (status != null) {

            auctions = auctionRepository.findAuctionsByStatus(status, pageable);

            if (StatusEnum.Ongoing.equals(status)) {

                auctions = auctionRepository.findOngoingAuctionsAndFilterByDate(pageable);
            }

            if (StatusEnum.Starting.equals(status)) {

                auctions = auctionRepository.findUpcomingAuctionsAndFilterByDate(pageable);
            }

            if (StatusEnum.Finished.equals(status)) {

                auctions = auctionRepository.findFinishedAuctionsAndFilterByDate(pageable);
            }
        }

        return getAuctionListPaginationResponse(auctions);
    }

    private AuctionListPaginationResponse<Object> getAuctionListPaginationResponse(Page<Auction> auctions) {

        List<AuctionDTO> auctionList = auctions.getContent()
                .stream()
                .map(getAuctionDTOFromAuctionFunction()).toList();

        return AuctionListPaginationResponse.builder()
                .auctions(auctionList)
                .page(auctions.getNumber())
                .pageSize(auctions.getSize())
                .totalPages(auctions.getTotalPages())
                .count((int) auctions.getTotalElements())
                .build();
    }

    @Override
    public AuctionListPaginationResponse<Object> getAuctionListHomePage(int page, int pageSize, StatusEnum status) {

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Auction> auctions = auctionRepository.findAuctionsByStatus(status, pageable);

        List<AuctionDTO> auctionList = new ArrayList<>();

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User contextUser = userRepository.findByEmail(email);

        if (StatusEnum.Ongoing.equals(status)) {

            auctions = auctionRepository.findOngoingAuctionsAndFilterByDate(pageable);

            auctionList = auctions.getContent().stream()
                    .map(getAuctionDTOFromAuctionFunction()).toList();
        }

        if (StatusEnum.Finished.equals(status)) {

            auctions = auctionRepository.findFinishedAuctionsAndFilterByDate(pageable);

            auctionList = auctions.getContent()
                    .stream()
                    .map(getAuctionDTOFromAuctionFunction()).toList();
        }

        if (StatusEnum.Starting.equals(status)) {

            auctions = auctionRepository.findUpcomingAuctionsAndFilterByDate(pageable);

            auctionList = auctions.getContent()
                    .stream()
                    .map(getAuctionDTOFromAuctionFunction()).toList();
        }

        return AuctionListPaginationResponse.builder()
                .auctions(auctionList)
                .page(auctions.getNumber())
                .pageSize(auctions.getSize())
                .totalPages(auctions.getTotalPages())
                .count((int) auctions.getTotalElements())
                .build();
    }

    private Function<Auction, AuctionDTO> getAuctionDTOFromAuctionFunction() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User contextUser = userRepository.findByEmail(email);

        return auction -> {

            UserDTO userDTO = userMapper.toUserDTO(auction.getUser());

            AuctionDTO auctionDTO = auctionMapper.toAuctionDTOFromAuction(auction);

            auctionDTO.setUser(userDTO);

            List<MediaFilesDTO> mediaFilesDTOList = auction.getMediaFiles()
                    .stream()
                    .map(mediaFilesMapper::toMediaFilesDTOFromMediaFiles)
                    .toList();

            boolean isFavourite = contextUser != null && contextUser.getFavouriteAuctions().contains(auction);

            auctionDTO.setFavourite(isFavourite);

            auctionDTO.setMediaFilesList(mediaFilesDTOList);

            if (!StatusEnum.Pending.equals(auction.getStatus())) {

                auctionDTO.setStatus(OffsetDateTime.now().isBefore(auction.getStartTime())
                        && auction.getRejectReason() == null
                        ? StatusEnum.Starting : OffsetDateTime.now().isAfter(auction.getEndTime())
                        ? StatusEnum.Finished : StatusEnum.Ongoing);
            }

            return auctionDTO;
        };
    }

    @Override
    public ViewAuction getAuctionByIdentifier(String auctionIdentifier) {

        Optional<Auction> auction = auctionRepository.findByAuctionIdentifier(auctionIdentifier);

        User contextUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (auction.isPresent()) {

            ViewAuction viewAuction = AuctionMapper.INSTANCE.auctionToDto(auction.get());

            UserDTO userDTO = userMapper.toUserDTO(auction.get().getUser());

            viewAuction.setUser(userDTO);

            List<MediaFilesDTO> mediaFilesDTOList = auction.get().getMediaFiles()
                    .stream()
                    .map(mediaFilesMapper::toMediaFilesDTOFromMediaFiles)
                    .toList();

            viewAuction.setMediaFilesList(mediaFilesDTOList);

            boolean isFavourite = contextUser != null && contextUser.getFavouriteAuctions().contains(auction.get());

            viewAuction.setFavourite(isFavourite);

            if (contextUser != null) {

                BidDetailsDTO bidDetailsDTO = bidService.getBidDetails(auction.get().getAuctionIdentifier());

                boolean isWon = Objects.equals(bidDetailsDTO.getYourBid(), bidDetailsDTO.getLatestBid())
                        && bidDetailsDTO.getYourBid() >= auction.get().getThresholdPrice()
                        && StatusEnum.Finished.equals(auction.get().getStatus());

                viewAuction.setWon(isWon);

                double overthrownAmount = bidDetailsDTO.getOverthrownBy();

                viewAuction.setOverthrownAmount(overthrownAmount);

                boolean biggestBid =  Objects.equals(bidDetailsDTO.getYourBid(), bidDetailsDTO.getLatestBid());

                viewAuction.setBiggestBid(biggestBid);
            }
            else {

                viewAuction.setWon(false);

                viewAuction.setOverthrownAmount(0);

                viewAuction.setBiggestBid(false);
            }

            if (!StatusEnum.Pending.equals(auction.get().getStatus())) {

                viewAuction.setStatus(OffsetDateTime.now().isBefore(auction.get().getStartTime())
                        && auction.get().getRejectReason() == null
                        ? StatusEnum.Starting : OffsetDateTime.now().isAfter(auction.get().getEndTime())
                        ? StatusEnum.Finished : StatusEnum.Ongoing);
            }


            double latestBid = bidService.getLatestBid(auction.get().getAuctionIdentifier()).isPresent()
                    ? bidService.getLatestBid(auction.get().getAuctionIdentifier()).get().getAmountBid() : 0;

            viewAuction.setLatestBid(latestBid);

            viewAuction.setSettledBid(latestBid >= auction.get().getThresholdPrice()
                    && StatusEnum.Finished.equals(auction.get().getStatus())
                    ? latestBid : 0);

            return viewAuction;

        } else

            throw new AuctionNotFoundException("Auction not found with id " + auctionIdentifier);
    }

    @Transactional
    @Override
    public void deleteAuction(String auctionIdentifier) {

        Auction auction = auctionRepository.findByAuctionIdentifier(auctionIdentifier).get();

        if (auctionRepository.findByAuctionIdentifier(auctionIdentifier).isEmpty()) {

            throw new AuctionNotFoundException("There is no auction with id " + auctionIdentifier);
        }

        if (auction.getStatus() == StatusEnum.Ongoing || auction.getStatus() == StatusEnum.Rejected) {

            throw new AuctionNotDeletedException("The auction could not be deleted. Only pending and finished auctions can be deleted.");
        }

        List<MediaFiles> mediaFiles = auction.getMediaFiles();

        for (MediaFiles mediaFile : mediaFiles) {

            amazonS3Service.deleteFileFromAmazonS3(mediaFile.getFileName());
        }

        auctionRepository.deleteByAuctionIdentifier(auctionIdentifier);
    }

    @Override
    public CreateAuctionResponse createAuction(CreateAuctionRequest createAuctionRequest) {

        Auction auction = auctionMapper.toAuctionFromCreateAuctionRequest(createAuctionRequest);

        auction.setUser(validationService.validateUserIsAuthenticated());

        auction.setStatus(StatusEnum.Pending);

        auction.setAuctionIdentifier(UUID.randomUUID().toString());

        Auction savedAuction = validateAuction(auction) ? auctionRepository.save(auction) : null;

        if (savedAuction == null) {

            throw new AuctionNotSavedException("The auction could not be saved.");
        }

        return auctionMapper.toCreateAuctionResponseFromAuction(savedAuction);
    }

    @Override
    public UpdateAuctionResponse updateAuction(UpdateAuctionRequest updateAuctionRequest, String auctionIdentifier) {

        Optional<Auction> auction = auctionRepository.findByAuctionIdentifier(auctionIdentifier);

        if (auction.isEmpty()) {

            throw new AuctionNotFoundException("Auction not found with id " + auctionIdentifier);
        }

        Auction existingAuction = auction.get();

        auctionMapper.toAuctionFromUpdateAuctionRequest(updateAuctionRequest, existingAuction);

        for (MediaFilesToDeleteDTO file : updateAuctionRequest.getFiles()) {

            amazonS3Service.deleteFileFromAmazonS3(file.getFileName());

            mediaFilesService.deleteMediaFile(file.getFileName());
        }

        Auction savedAuction = (validateAuction(existingAuction)) ? auctionRepository.save(existingAuction) : null;

        if (savedAuction == null) {

            throw new AuctionNotUpdatedException("The auction could not be updated.");
        }

        return auctionMapper.toUpdateAuctionResponseFromAuction(savedAuction);
    }

    @Override
    public List<MediaFilesDTO> addAuctionFiles(List<MultipartFile> files, String auctionIdentifier) throws IOException {

        Auction auction = auctionRepository.findByAuctionIdentifier(auctionIdentifier).get();

        List<MediaFilesDTO> savedMediaFiles = new ArrayList<>();

        if (validateFiles(files)) {

            for (MultipartFile file : files) {

                String fileName = mediaFilesService.createFileName(auctionIdentifier, file.getOriginalFilename());

                String mediaType = file.getContentType();

                String url = amazonS3Service.uploadFileToAmazonS3(file, fileName);

                MediaFilesDTO mediaFilesDTO = MediaFilesDTO.builder()
                        .fileName(fileName)
                        .mediaType(mediaType)
                        .fileUrl(url)
                        .build();

                MediaFiles mediaFile = mediaFilesMapper.toMediaFilesFromMediaFilesDTO(mediaFilesDTO);
                mediaFile.setAuction(auction);

                MediaFilesDTO savedMediaFile = mediaFilesService.addMediaFile(mediaFile);

                if (savedMediaFile == null) {

                    amazonS3Service.deleteFileFromAmazonS3(fileName);

                    throw new FileNotSavedException("The file " + fileName + " could not be saved.");
                }

                savedMediaFiles.add(savedMediaFile);
            }
        }

        return savedMediaFiles;
    }

    @Override
    public Optional<Auction> getAuctionEntityByIdentifier(String auctionIdentifier) {
        Optional<Auction> auction = auctionRepository.findByAuctionIdentifier(auctionIdentifier);
        if (auction.isPresent()) return auction;
        else throw new AuctionNotFoundException("Auction not found with id " + auctionIdentifier);
    }

    @Override
    public AuctionListPaginationResponse<Object> getFinishedAuctionsByUser(int page, int pageSize) {

        User contextUser = validationService.validateUserIsAuthenticated();

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Auction> auctions = auctionRepository.findFinishedAuctionsByUser(contextUser.getId(), pageable);

        List<FinishedAuctionUserDTO> finishedAuctions = auctions
                .stream()
                .map(o -> {

                    FinishedAuctionUserDTO finishedAuctionUserDTO = auctionMapper.toFinishedAuctionUserDTOFromAuction(o);

                    UserDTO userDTO = userMapper.toUserDTO(o.getUser());

                    finishedAuctionUserDTO.setUser(userDTO);

                    List<MediaFilesDTO> mediaFilesDTOList = o.getMediaFiles()
                            .stream()
                            .map(mediaFilesMapper::toMediaFilesDTOFromMediaFiles)
                            .toList();

                    finishedAuctionUserDTO.setMediaFilesList(mediaFilesDTOList);

                    BidDetailsDTO bidDetailsDTO = bidService.getBidDetails(o.getAuctionIdentifier());

                    boolean isWon = Objects.equals(bidDetailsDTO.getYourBid(), bidDetailsDTO.getLatestBid())
                                    && bidDetailsDTO.getYourBid() >= o.getThresholdPrice();

                    double settleBid = bidDetailsDTO.getLatestBid() >= o.getThresholdPrice()
                                       ? bidDetailsDTO.getLatestBid() : 0;

                    finishedAuctionUserDTO.setWon(isWon);

                    finishedAuctionUserDTO.setSettledBid(settleBid);

                    boolean isFavourite = contextUser.getFavouriteAuctions().contains(o);

                    finishedAuctionUserDTO.setFavourite(isFavourite);

                    return finishedAuctionUserDTO;

                }).toList();

        return AuctionListPaginationResponse.builder()
                .auctions(finishedAuctions)
                .page(auctions.getNumber())
                .pageSize(auctions.getSize())
                .totalPages(auctions.getTotalPages())
                .count((int) auctions.getTotalElements())
                .build();
    }

    @Override
    public AuctionListPaginationResponse<Object> getOngoingAuctionsByUser(int page, int pageSize) {

        User contextUser = validationService.validateUserIsAuthenticated();

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Auction> auctions = auctionRepository.findOngoingAuctionsByUser(contextUser.getId(), pageable);

        List<OngoingAuctionUserDTO> ongoingAuctions = auctions
                .stream()
                .map(o -> {

                    OngoingAuctionUserDTO ongoingAuctionUserDTO = auctionMapper.toOngoingAuctionUserDTOFromAuction(o);

                    UserDTO userDTO = userMapper.toUserDTO(o.getUser());

                    ongoingAuctionUserDTO.setUser(userDTO);

                    List<MediaFilesDTO> mediaFilesDTOList = o.getMediaFiles()
                            .stream()
                            .map(mediaFilesMapper::toMediaFilesDTOFromMediaFiles)
                            .toList();

                    ongoingAuctionUserDTO.setMediaFilesList(mediaFilesDTOList);

                    BidDetailsDTO bidDetailsDTO = bidService.getBidDetails(o.getAuctionIdentifier());

                    double overthrownAmount = bidDetailsDTO.getOverthrownBy();

                    boolean biggestBid = Objects.equals(bidDetailsDTO.getYourBid(), bidDetailsDTO.getLatestBid());

                    ongoingAuctionUserDTO.setOverthrownAmount(overthrownAmount);

                    ongoingAuctionUserDTO.setBiggestBid(biggestBid);

                    boolean isFavourite = contextUser.getFavouriteAuctions().contains(o);

                    ongoingAuctionUserDTO.setFavourite(isFavourite);

                    return ongoingAuctionUserDTO;

                }).toList();

        return AuctionListPaginationResponse.builder()
                .auctions(ongoingAuctions)
                .page(auctions.getNumber())
                .pageSize(auctions.getSize())
                .totalPages(auctions.getTotalPages())
                .count((int) auctions.getTotalElements())
                .build();
    }

    @Override
    public AuctionListPaginationResponse<Object> getPortfolioAuctions(int page, int pageSize, StatusEnum status) {

        User contextUser = validationService.validateUserIsAuthenticated();

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Auction> auctions = auctionRepository.findAuctionsByUserId(contextUser.getId(), pageable);

        if (status != null) {

            auctions = auctionRepository.findAuctionsByUserIdAndStatus(contextUser.getId(), status, pageable);

            if (StatusEnum.Starting.equals(status)) {

                auctions = auctionRepository.findUpcomingAuctionsAndFilterByDateAndByUser(contextUser.getId(), pageable);
            }

            if (StatusEnum.Ongoing.equals(status)) {

                auctions = auctionRepository.findOngoingAuctionsAndFilterByDateAndByUser(contextUser.getId(), pageable);
            }

            if (StatusEnum.Finished.equals(status)) {

                auctions = auctionRepository.findFinishedAuctionsAndFilterByDateAndByUser(contextUser.getId(), pageable);
            }
        }

        List<AuctionPortfolioDTO> allPortfolioAuctions = auctions
                .stream().map(o -> {

                    AuctionPortfolioDTO portfolioDTO = auctionMapper.toAuctionPortfolioDTOFromAuction(o);

                    UserDTO userDTO = userMapper.toUserDTO(o.getUser());

                    portfolioDTO.setUser(userDTO);

                    boolean isFavourite = contextUser.getFavouriteAuctions().contains(o);

                    portfolioDTO.setFavourite(isFavourite);

                    List<MediaFilesDTO> mediaFilesDTOList = o.getMediaFiles()
                            .stream()
                            .map(mediaFilesMapper::toMediaFilesDTOFromMediaFiles)
                            .toList();

                    portfolioDTO.setMediaFilesList(mediaFilesDTOList);


                    if (!StatusEnum.Pending.equals(o.getStatus())) {

                        portfolioDTO.setStatus(OffsetDateTime.now().isBefore(o.getStartTime())
                                && o.getRejectReason() == null
                                ? StatusEnum.Starting : OffsetDateTime.now().isAfter(o.getEndTime())
                                ? StatusEnum.Finished : StatusEnum.Ongoing);
                    }


                    return portfolioDTO;

                })
                .toList();

        for (AuctionPortfolioDTO portfolioDTO : allPortfolioAuctions) {

            switch (portfolioDTO.getStatus()) {

                case StatusEnum.Ongoing -> {

                    if (OffsetDateTime.now().isAfter(portfolioDTO.getStartTime())
                            && OffsetDateTime.now().isBefore(portfolioDTO.getEndTime())) {

                        BidDetailsDTO bidDetailsDTO = bidService.getBidDetails(portfolioDTO.getAuctionIdentifier());

                        portfolioDTO.setLatestBid(bidDetailsDTO.getLatestBid());
                        portfolioDTO.setSettledBid(0);
                    }

                    if (OffsetDateTime.now().isBefore(portfolioDTO.getStartTime())) {

                        portfolioDTO.setLatestBid(0);
                        portfolioDTO.setSettledBid(0);
                    }

                }

                case StatusEnum.Rejected, StatusEnum.Pending -> {

                    portfolioDTO.setLatestBid(0);
                    portfolioDTO.setSettledBid(0);

                }

                case StatusEnum.Finished -> {

                    BidDetailsDTO bidDetailsDTO = bidService.getBidDetails(portfolioDTO.getAuctionIdentifier());

                    if (bidDetailsDTO.getLatestBid() >= portfolioDTO.getThresholdPrice()) {

                        portfolioDTO.setLatestBid(bidDetailsDTO.getLatestBid());
                        portfolioDTO.setSettledBid(bidDetailsDTO.getLatestBid());
                    }

                    if (bidDetailsDTO.getLatestBid() < portfolioDTO.getThresholdPrice()) {

                        portfolioDTO.setLatestBid(bidDetailsDTO.getLatestBid());
                        portfolioDTO.setSettledBid(0);
                    }

                    if (bidDetailsDTO.getLatestBid() == 0) {

                        portfolioDTO.setLatestBid(0);
                        portfolioDTO.setSettledBid(0);
                    }

                }
            }
        }

        return AuctionListPaginationResponse.builder()
                .auctions(allPortfolioAuctions)
                .page(auctions.getNumber())
                .pageSize(auctions.getSize())
                .totalPages(auctions.getTotalPages())
                .count((int) auctions.getTotalElements())
                .build();
    }

    public void changeAuctionStatus(AcceptanceAuctionDto acceptanceAuctionDto, String auctionIdentifier) {
        var auction = auctionRepository.findByAuctionIdentifier(auctionIdentifier);

        if (auction.isEmpty()) throw new AuctionNotFoundException("Auction not found with id " + auctionIdentifier);

        if (StatusEnum.Pending.equals(auction.get().getStatus())) {

            if (acceptanceAuctionDto.getAccepted()) {
                auction.get().setStatus(StatusEnum.Ongoing);
                auctionRepository.save(auction.get());
                String subject = "Auction accepted";
                String text = "Hello " + auction.get().getEmail() + ",\n\n" + "The auction has been accepted.\n\n" +
                        "Title: " + auction.get().getTitle() +
                        "\nCheers,\nCon-X Team";
                smtpGmailService.sendEmail(auction.get().getEmail(), subject, text);
            } else {
                auction.get().setStatus(StatusEnum.Rejected);
                auction.get().setRejectReason(acceptanceAuctionDto.getRejectReason());
                auctionRepository.save(auction.get());
                String subject = "Auction rejected";
                String text = "Hello " + auction.get().getEmail() + ",\n\n" + "The auction has been rejected.\n\n" +
                        "Title: " + auction.get().getTitle() + "\nReject reason: " + auction.get().getRejectReason() +
                        "\nCheers,\nCon-X Team";
                smtpGmailService.sendEmail(auction.get().getEmail(), subject, text);
            }

        }
    }

    @Override
    public void changeAutoPendingStatus() {
        var auctions = auctionRepository.findAllByStatus(StatusEnum.Pending);
        if (auctions.isPresent()) {
            for (Auction auction : auctions.get()) {

                if (auction.getStartTime().isBefore(OffsetDateTime.now())) {
                    auction.setStatus(StatusEnum.Ongoing);
                    auctionRepository.save(auction);
                    String subject = "Auction accepted";
                    String text = "Hello " + auction.getEmail() + ",\n\n" +
                            "The auction has been accepted.\n\n" +
                            "Title: " + auction.getTitle() +
                            "\nCheers,\nCon-X Team";
                    smtpGmailService.sendEmail(auction.getEmail(), subject, text);
                }
            }
        }
    }

    @Override
    public void changeAutoOngoingStatus() {
        var auctions = auctionRepository.findAllByStatus(StatusEnum.Ongoing);
        if (auctions.isPresent() && !auctions.get().isEmpty()) {

            for (Auction auction : auctions.get()) {
                if (OffsetDateTime.now().isAfter(auction.getEndTime())) {
                    auction.setStatus(StatusEnum.Finished);
                    auctionRepository.save(auction);

                    var bidDetails = bidService.getMaxBidByAuctionId(auction.getAuctionIdentifier());
                    if (bidDetails.isPresent()) {
                      if(bidDetails.get().getAmountBid() >= auction.getThresholdPrice()){
                          String subject = "You won this auction";
                          String text = "Dear " + bidDetails.get().getUser().getEmail()+",\n\n" +
                                  "I am pleased to inform you that your bid has been successful, and you have won the auction for the item titled "+
                          auction.getTitle() + "." +
                                  "\nThe winning bid amount is " + bidDetails.get().getAmountBid() +
                                  "\nCongratulations on your successful bid!" +
                                  "\nCheers,\nCon-X Team";
                          smtpGmailService.sendEmail(bidDetails.get().getUser().getEmail(), subject, text);

                           subject = "The auction was sold";
                           text = "Dear " + auction.getEmail()+",\n\n" +
                                  "We are excited to inform you that your item titled "+
                                  auction.getTitle() + " has been sold successfully." +
                                  "\nThe final bid amount was " + bidDetails.get().getAmountBid() +
                                  "\nThank you for using our auction platform. Congratulations on your sale!" +
                                  "\nCheers,\nCon-X Team";
                          smtpGmailService.sendEmail(auction.getEmail(), subject, text);

                      }
                      else{
                          String subject = "Auction Result Notification";
                          String text = "Dear " + bidDetails.get().getUser().getEmail()+",\n\n" +
                                  "We regret to inform you that the auction for the item titled  "+
                                  auction.getTitle() + " did not result in a sale." +
                                  "\nThe bids did not meet the minimum sale price required." +
                                  "\nThank you for your participation, and we hope to see you in future auctions." +
                                  "\nCheers,\nCon-X Team";
                          smtpGmailService.sendEmail(bidDetails.get().getUser().getEmail(), subject, text);

                          subject = "Auction Result Notification";
                          text = "Dear " + auction.getEmail()+",\n\n" +
                                  "We regret to inform you that the auction for your item titled "+
                                  auction.getTitle() + " did not result in a sale." +
                                  "\nThe highest bid received was " + bidDetails.get().getAmountBid() +
                                  ", which did not meet your minimum sale price of " + auction.getThresholdPrice() + "."+
                                  "\nThank you for using our auction platform, and we hope to assist you in future auctions." +
                                  "\nCheers,\nCon-X Team";
                          smtpGmailService.sendEmail(auction.getEmail(), subject, text);
                      }
                    }
                    else {
                        String subject = "Auction Result Notification";
                        String text = "Dear " + auction.getEmail()+",\n\n" +
                                "We regret to inform you that the auction for your item titled "+
                                auction.getTitle() + " did not result in a sale, as no bids were placed." +
                                "\nThank you for using our auction platform, and we hope to assist you in future auctions." +
                                "\nCheers,\nCon-X Team";
                        smtpGmailService.sendEmail(auction.getEmail(), subject, text);
                    }
                }
            }
        }

    }
}
