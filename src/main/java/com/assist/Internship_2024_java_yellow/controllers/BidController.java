package com.assist.Internship_2024_java_yellow.controllers;

import com.assist.Internship_2024_java_yellow.dtos.BidDetailsDTO;
import com.assist.Internship_2024_java_yellow.dtos.BidsDTO;
import com.assist.Internship_2024_java_yellow.dtos.RefreshBidDTO;
import com.assist.Internship_2024_java_yellow.entities.Bid;
import com.assist.Internship_2024_java_yellow.services.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;


    @PostMapping("/api/legal/bids/{auctionId}")
    public ResponseEntity<?> addBid(@RequestBody BidsDTO bidsDTO, @PathVariable String auctionId)
    {
        bidService.addBid(bidsDTO, auctionId);
        return ResponseEntity.status(201).body("Bid added successfully");
    }

    @GetMapping("/api/bids/{auctionId}")
    public ResponseEntity<?> getBidDetails(@PathVariable("auctionId") String auctionId) {
        RefreshBidDTO refreshBidDTO = bidService.getRefreshBid(auctionId);
        return ResponseEntity.status(200).body(refreshBidDTO);
    }

}
