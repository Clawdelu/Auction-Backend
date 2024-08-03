package com.assist.Internship_2024_java_yellow.services;


import com.assist.Internship_2024_java_yellow.dtos.*;
import com.assist.Internship_2024_java_yellow.entities.Auction;
import com.assist.Internship_2024_java_yellow.enums.StatusEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AuctionService {

    AuctionListPaginationResponse<Object> getAuctionListHomePage(int page, int pageSize, StatusEnum status);

    AuctionListPaginationResponse<Object> getAdminPendingAuctions(int page, int pageSize);

    AuctionListPaginationResponse<Object> getAdminAllAuctions(int page, int pageSize, StatusEnum status);

    ViewAuction getAuctionByIdentifier(String auctionIdentifier);

    void deleteAuction(String auctionIdentifier);

    CreateAuctionResponse createAuction(CreateAuctionRequest createAuctionRequest);

    UpdateAuctionResponse updateAuction(UpdateAuctionRequest updateAuctionRequest, String auctionIdentifier);

    List<MediaFilesDTO> addAuctionFiles(List<MultipartFile> files, String auctionIdentifier) throws IOException;

    Optional<Auction> getAuctionEntityByIdentifier(String auctionIdentifier);

    AuctionListPaginationResponse<Object> getFinishedAuctionsByUser(int page, int pageSize);

    AuctionListPaginationResponse<Object> getOngoingAuctionsByUser(int page, int pageSize);

    AuctionListPaginationResponse<Object> getPortfolioAuctions(int page, int pageSize, StatusEnum status);

    AuctionListPaginationResponse<Object> searchForAuctions(String keyword, int page, int pageSize);

    void changeAuctionStatus(AcceptanceAuctionDto acceptanceAuctionDto, String auctionIdentifier);

    void changeAutoPendingStatus();

    void changeAutoOngoingStatus();

}
