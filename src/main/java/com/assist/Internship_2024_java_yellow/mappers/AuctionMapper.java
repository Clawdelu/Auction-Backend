package com.assist.Internship_2024_java_yellow.mappers;

import com.assist.Internship_2024_java_yellow.dtos.*;
import com.assist.Internship_2024_java_yellow.entities.Auction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import com.assist.Internship_2024_java_yellow.entities.Auction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuctionMapper {

    AuctionMapper INSTANCE = Mappers.getMapper(AuctionMapper.class);

    AuctionDTO toAuctionDTOFromAuction(Auction auction);

    ViewAuction auctionToDto(Auction auction);

    Auction toAuctionFromCreateAuctionRequest(CreateAuctionRequest createAuctionRequest);

    CreateAuctionResponse toCreateAuctionResponseFromAuction(Auction auction);

    void toAuctionFromUpdateAuctionRequest(UpdateAuctionRequest updateAuctionRequest, @MappingTarget Auction auction);

    UpdateAuctionResponse toUpdateAuctionResponseFromAuction(Auction auction);

    FinishedAuctionUserDTO toFinishedAuctionUserDTOFromAuction(Auction auction);

    OngoingAuctionUserDTO toOngoingAuctionUserDTOFromAuction(Auction auction);

    AuctionPortfolioDTO toAuctionPortfolioDTOFromAuction(Auction auction);
}