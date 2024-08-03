package com.assist.Internship_2024_java_yellow.dtos;

import com.assist.Internship_2024_java_yellow.enums.CurrencyEnum;
import com.assist.Internship_2024_java_yellow.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuctionPortfolioDTO {

    private String title;

    private double startingPrice;

    private double thresholdPrice;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private List<MediaFilesDTO> mediaFilesList;

    private String descriptionDetails;

    private boolean defaultShipping;

    private String firstName;

    private String lastName;

    private String email;

    private CurrencyEnum currency;

    private StatusEnum status;

    private String rejectReason;

    private String auctionIdentifier;

    private UserDTO user;

    private double latestBid;

    private double settledBid;

    private boolean isFavourite;
}
