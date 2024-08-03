package com.assist.Internship_2024_java_yellow.dtos;

import com.assist.Internship_2024_java_yellow.enums.CurrencyEnum;
import com.assist.Internship_2024_java_yellow.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ViewAuction {

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

    private boolean isWon;

    private double overthrownAmount;

    private boolean isFavourite;

    private boolean isBiggestBid;
}
