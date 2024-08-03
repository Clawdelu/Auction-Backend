package com.assist.Internship_2024_java_yellow.services;

import com.assist.Internship_2024_java_yellow.dtos.BidDetailsDTO;
import com.assist.Internship_2024_java_yellow.dtos.BidsDTO;
import com.assist.Internship_2024_java_yellow.dtos.RefreshBidDTO;
import com.assist.Internship_2024_java_yellow.entities.Bid;

import java.util.Optional;

public interface BidService {

    void addBid(BidsDTO bid, String auctionId);

    BidDetailsDTO getBidDetails(String auctionIdentifier);

    Optional<Bid> getMaxBidByAuctionId(String auctionId);

    Optional<Bid> getLatestBid(String auctionIdentifier);

    RefreshBidDTO getRefreshBid(String auctionIdentifier);

}
