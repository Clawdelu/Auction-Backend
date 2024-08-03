package com.assist.Internship_2024_java_yellow.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class BidsDTO {

    private Double amountBid;

    private OffsetDateTime timeStamp;
}
