package com.assist.Internship_2024_java_yellow.repository;

import com.assist.Internship_2024_java_yellow.entities.Auction;
import com.assist.Internship_2024_java_yellow.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Integer> {

    Page<Auction> findAuctionsByStatus(StatusEnum status, Pageable pageable);

    Optional<Auction> findByAuctionIdentifier(String auctionIdentifier);

    void deleteByAuctionIdentifier(String auctionIdentifier);

    Page<Auction> findAuctionsByUserId(int userId, Pageable pageable);

    Page<Auction> findAuctionsByUserIdAndStatus(int userId, StatusEnum status, Pageable pageable);

    @Query("SELECT a FROM Auction a " +
            "JOIN Bid b ON a.id = b.auction.id " +
            "WHERE b.user.id = :user_id AND a.status = 'Finished'" +
            "AND a.endTime < current_timestamp ")
    Page<Auction> findFinishedAuctionsByUser(@Param("user_id") int userId, Pageable pageable);

    @Query("SELECT a FROM Auction a " +
            "JOIN Bid b ON a.id = b.auction.id " +
            "WHERE b.user.id = :user_id AND a.status = 'Ongoing' " +
            "AND a.startTime < current_timestamp " +
            "AND a.endTime > current_timestamp ")
    Page<Auction> findOngoingAuctionsByUser(@Param("user_id") int userId, Pageable pageable);

    Optional<List<Auction>> findAllByStatus(StatusEnum statusEnum);

    @Query(value = "SELECT * FROM auctions " +
            "WHERE status IN ('Ongoing', 'Finished') " +
            "AND tsv @@ to_tsquery('english', :keywords)",
            nativeQuery = true)
    Page<Auction> searchAuctionsByTitle(@Param("keywords") String keywords, Pageable pageable);

    @Query("SELECT a FROM Auction a" +
            " WHERE a.status = 'Ongoing' " +
            "AND a.startTime > current_timestamp")
    Page<Auction> findUpcomingAuctionsAndFilterByDate(Pageable pageable);

    @Query("SELECT a FROM Auction a" +
            " WHERE a.status = 'Ongoing' " +
            "AND a.startTime > current_timestamp " +
            "AND a.user.id = :userId")
    Page<Auction> findUpcomingAuctionsAndFilterByDateAndByUser(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT a FROM Auction a " +
            "WHERE a.status = 'Ongoing' " +
            "AND a.startTime < current_timestamp " +
            "AND a.endTime > current_timestamp ")
    Page<Auction> findOngoingAuctionsAndFilterByDate(Pageable pageable);

    @Query("SELECT a FROM Auction a " +
            "WHERE a.status = 'Ongoing' " +
            "AND a.startTime < current_timestamp " +
            "AND a.endTime > current_timestamp " +
            "AND a.user.id = :userId")
    Page<Auction> findOngoingAuctionsAndFilterByDateAndByUser(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT a FROM Auction a " +
            "WHERE a.status = 'Finished' " +
            "AND current_timestamp > a.endTime")
    Page<Auction> findFinishedAuctionsAndFilterByDate(Pageable pageable);

    @Query("SELECT a FROM Auction a " +
            "WHERE a.status = 'Finished' " +
            "AND current_timestamp > a.endTime " +
            "AND a.user.id = :userId")
    Page<Auction> findFinishedAuctionsAndFilterByDateAndByUser(@Param("userId") int userId, Pageable pageable);
}

