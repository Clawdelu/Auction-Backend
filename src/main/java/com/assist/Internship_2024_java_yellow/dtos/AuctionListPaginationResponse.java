package com.assist.Internship_2024_java_yellow.dtos;

import com.assist.Internship_2024_java_yellow.entities.Auction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionListPaginationResponse<T>  {

    private int count;

    private int page;

    private int pageSize;

    private int totalPages;

    private T auctions;
}
