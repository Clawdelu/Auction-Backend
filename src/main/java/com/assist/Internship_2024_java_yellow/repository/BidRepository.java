package com.assist.Internship_2024_java_yellow.repository;

import com.assist.Internship_2024_java_yellow.entities.Auction;
import com.assist.Internship_2024_java_yellow.entities.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Integer> {

    @Query("SELECT b FROM Bid b WHERE b.auction.auctionIdentifier = :auctionId " +
            "AND b.amountBid = (SELECT MAX(b2.amountBid) FROM Bid b2 WHERE b2.auction.auctionIdentifier = :auctionId)")
    Optional<Bid> findMaxBidByAuctionId(@Param("auctionId") String auctionId);

    @Query("SELECT b FROM Bid b WHERE b.auction.auctionIdentifier = :auctionId AND b.user.id = :userId AND " +
            "b.amountBid = (SELECT MAX(b2.amountBid) FROM Bid b2 WHERE b2.auction.auctionIdentifier = :auctionId AND b2.user.id = :userId)")
    Optional<Bid> findMaxBidByAuctionIdAndUserId(@Param("auctionId") String auctionId, @Param("userId") int userId);

    @Query("SELECT b FROM Bid b WHERE b.auction.auctionIdentifier = :auctionId AND b.user.email = :email AND " +
            "b.amountBid = (SELECT MAX(b2.amountBid) FROM Bid b2 WHERE b2.auction.auctionIdentifier = :auctionId AND b2.user.email = :email)")
    Optional<Bid> findMaxBidByAuctionIdAndUserEmail(@Param("auctionId") String auctionId, @Param("email") String email);

}

