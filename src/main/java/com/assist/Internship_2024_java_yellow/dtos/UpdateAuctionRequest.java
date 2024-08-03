package com.assist.Internship_2024_java_yellow.dtos;

import com.assist.Internship_2024_java_yellow.enums.CurrencyEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAuctionRequest {

    private String title;

    private double startingPrice;

    private double thresholdPrice;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private String descriptionDetails;

    private boolean defaultShipping;

    private String firstName;

    private String lastName;

    private String email;

    private CurrencyEnum currency;

    private List<MediaFilesToDeleteDTO> files;
}
