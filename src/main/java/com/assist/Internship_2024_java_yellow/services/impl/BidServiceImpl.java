package com.assist.Internship_2024_java_yellow.services.impl;

import com.assist.Internship_2024_java_yellow.dtos.BidDetailsDTO;
import com.assist.Internship_2024_java_yellow.dtos.BidsDTO;
import com.assist.Internship_2024_java_yellow.dtos.RefreshBidDTO;
import com.assist.Internship_2024_java_yellow.entities.Auction;
import com.assist.Internship_2024_java_yellow.entities.Bid;
import com.assist.Internship_2024_java_yellow.entities.User;
import com.assist.Internship_2024_java_yellow.enums.StatusEnum;
import com.assist.Internship_2024_java_yellow.exceptions.AuctionNotFoundException;
import com.assist.Internship_2024_java_yellow.repository.AuctionRepository;
import com.assist.Internship_2024_java_yellow.repository.BidRepository;
import com.assist.Internship_2024_java_yellow.repository.UserRepository;
import com.assist.Internship_2024_java_yellow.services.BidService;
import com.assist.Internship_2024_java_yellow.services.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final ValidationService validationService;

    @Override
    public void addBid(BidsDTO bidsDTO, String auctionId) {
        User contextUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Auction> auction = auctionRepository.findByAuctionIdentifier(auctionId);
        if (auction.isEmpty())
            throw new AuctionNotFoundException("Auction not found with id: " + auctionId);
        if (auction.get().getStatus().equals(StatusEnum.Finished)) {
            throw new RuntimeException("Auction already finished");
        }
        if (contextUser.getId() == auction.get().getUser().getId()) {
            throw new RuntimeException("You can't place a bid on your own auction.");
        }
        Optional<Bid> maxBidDB = bidRepository.findMaxBidByAuctionId(auctionId);
        double maxBid = maxBidDB.map(Bid::getAmountBid).orElse(0.0);
        if (maxBid >= bidsDTO.getAmountBid())
            throw new RuntimeException("Your bid cannot be lower (or equal) than last bid.");
        if(bidsDTO.getAmountBid() < auction.get().getStartingPrice())
            throw new RuntimeException("Your bid cannot be lower than starting price.");
        Bid bid = new Bid();
        bid.setAuction(auction.get());
        bid.setUser(contextUser);
        bid.setAmountBid(bidsDTO.getAmountBid());
        bid.setTimeStamp(OffsetDateTime.now());
        bidRepository.save(bid);
    }

    public Optional<Bid> getLatestBid(String auctionIdentifier) {
        return bidRepository.findMaxBidByAuctionId(auctionIdentifier);
    }

    @Override
    public RefreshBidDTO getRefreshBid(String auctionIdentifier) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User contextUser = userRepository.findByEmail(email);

        Auction auction = auctionRepository.findByAuctionIdentifier(auctionIdentifier).get();

        RefreshBidDTO refreshBidDTO = new RefreshBidDTO();

        if (contextUser != null) {

            BidDetailsDTO bidDetailsDTO = this.getBidDetails(auction.getAuctionIdentifier());

            boolean isWon = Objects.equals(bidDetailsDTO.getYourBid(), bidDetailsDTO.getLatestBid())
                    && bidDetailsDTO.getYourBid() >= auction.getThresholdPrice()
                    && StatusEnum.Finished.equals(auction.getStatus());

            refreshBidDTO.setWon(isWon);

            double overthrownAmount = bidDetailsDTO.getOverthrownBy();

            refreshBidDTO.setOverthrownBy(overthrownAmount);

            boolean biggestBid =  Objects.equals(bidDetailsDTO.getYourBid(), bidDetailsDTO.getLatestBid());

            refreshBidDTO.setBiggestBid(biggestBid);

            bidDetailsDTO.setMessage(bidDetailsDTO.getMessage());

            double latestBid = this.getLatestBid(auction.getAuctionIdentifier()).isPresent()
                    ? this.getLatestBid(auction.getAuctionIdentifier()).get().getAmountBid() : 0;

            refreshBidDTO.setLatestBid(latestBid);
        }
        else
        {
            refreshBidDTO.setWon(false);

            refreshBidDTO.setOverthrownBy(0);

            refreshBidDTO.setBiggestBid(false);

            double latestBid = this.getLatestBid(auction.getAuctionIdentifier()).isPresent()
                    ? this.getLatestBid(auction.getAuctionIdentifier()).get().getAmountBid() : 0;

            refreshBidDTO.setLatestBid(latestBid);

            refreshBidDTO.setMessage("");
        }

        return refreshBidDTO;
    }


    private double getOverthrownValue(String auctionIdentifier, String email) {
        User user = userRepository.findByEmail(email);
        Optional<Bid> latestBid = bidRepository.findMaxBidByAuctionId(auctionIdentifier);
        Optional<Bid> yourBid = bidRepository.findMaxBidByAuctionIdAndUserId(auctionIdentifier, user.getId());
        if (latestBid.isEmpty())
            throw new AuctionNotFoundException("There is no placed bids.");
        if (yourBid.isEmpty())
            throw new AuctionNotFoundException("Current user haven't placed any bids.");
        if (latestBid.get().getAmountBid() > yourBid.get().getAmountBid())
            return (latestBid.get().getAmountBid() - yourBid.get().getAmountBid());
        return 0;
    }


    private String getAuctionBidStatus(String auctionIdentifier, Double yourBid, Double latestBid, String email) {
        Auction auction = auctionRepository.findByAuctionIdentifier(auctionIdentifier).get();
        if (auction.getStatus().equals(StatusEnum.Finished)) {
            if (latestBid < auction.getStartingPrice())
                return "Item was not sold. Latest bid is lower than starting price.";
            if (yourBid == null || yourBid < latestBid) {
                return "Settled at " + latestBid;
            }
            if (yourBid.equals(latestBid) || yourBid >= auction.getStartingPrice()) {
                return "Congrats! You won this auction.";
            }

        } else if (auction.getStatus().equals(StatusEnum.Ongoing)) {
            if (yourBid == null)
                return "Current bid: " + latestBid;
            else if (yourBid < latestBid) {
                return "Your bid was overthrown by " + getOverthrownValue(auctionIdentifier, email);
            } else if (yourBid.equals(latestBid)) {
                return "Your bid is the biggest one!";
            }
        } else if (auction.getStatus().equals(StatusEnum.Pending)) {
            return "Starting at " + auction.getStartingPrice();
        }
        return "Starting at " + auction.getStartingPrice();
    }

    @Override
    public BidDetailsDTO getBidDetails(String auctionIdentifier) {
        User contextUser = validationService.validateUserIsAuthenticated();
        String userEmail = contextUser.getEmail();
        Optional<Bid> yourBid = bidRepository.findMaxBidByAuctionIdAndUserEmail(auctionIdentifier, userEmail);
        Optional<Bid> latestBid = getLatestBid(auctionIdentifier);
        if (latestBid.isEmpty())
            return BidDetailsDTO.builder()
                    .latestBid(0.0)
                    .yourBid(0.0)
                    .overthrownBy(0.0)
                    .message("No bids.")
                    .build();
        if (yourBid.isEmpty()) {
            return BidDetailsDTO.builder()
                    .latestBid(latestBid.get().getAmountBid())
                    .message(getAuctionBidStatus(auctionIdentifier, null, latestBid.get().getAmountBid(), userEmail))
                    .build();
        }
        return BidDetailsDTO.builder()
                .latestBid(latestBid.get().getAmountBid())
                .yourBid(yourBid.get().getAmountBid())
                .overthrownBy(getOverthrownValue(auctionIdentifier, userEmail))
                .message(getAuctionBidStatus(auctionIdentifier, yourBid.get().getAmountBid(), latestBid.get().getAmountBid(), userEmail))
                .build();
    }

    @Override
    public Optional<Bid> getMaxBidByAuctionId(String auctionId) {
        return bidRepository.findMaxBidByAuctionId(auctionId);
    }

}
