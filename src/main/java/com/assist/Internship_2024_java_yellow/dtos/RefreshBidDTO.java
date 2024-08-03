package com.assist.Internship_2024_java_yellow.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshBidDTO {

    private double latestBid;

    private double overthrownBy;

    private boolean isBiggestBid;

    private boolean isWon;

    private String message;
}
