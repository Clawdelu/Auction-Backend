package com.assist.Internship_2024_java_yellow.dtos;

import jakarta.annotation.Nullable;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BidDetailsDTO {
    private Double latestBid;
    private Double yourBid;
    private Double overthrownBy;
    private String message;
}
