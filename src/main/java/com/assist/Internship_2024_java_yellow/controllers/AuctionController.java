package com.assist.Internship_2024_java_yellow.controllers;

import com.assist.Internship_2024_java_yellow.dtos.*;
import com.assist.Internship_2024_java_yellow.enums.StatusEnum;
import com.assist.Internship_2024_java_yellow.services.AmazonS3Service;
import com.assist.Internship_2024_java_yellow.services.AuctionService;
import com.assist.Internship_2024_java_yellow.services.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    private final AmazonS3Service amazonS3Service;

    @GetMapping("/api/auctions")
    public AuctionListPaginationResponse<Object> getAllAuctionsHomePage(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                        @RequestParam(value = "page_size", defaultValue = "9", required = false) int pageSize,
                                                                        @RequestParam(value = "status", required = false) StatusEnum status) {

        return auctionService.getAuctionListHomePage(page, pageSize, status);
    }

    @GetMapping("/api/admin/auctions")
    public AuctionListPaginationResponse<Object> getAllAuctionsAdminPage(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                         @RequestParam(value = "page_size", defaultValue = "9", required = false) int pageSize,
                                                                         @RequestParam(value = "status", required = false) StatusEnum status) {

        return auctionService.getAdminAllAuctions(page, pageSize, status);
    }

    @Operation(summary = "Get auction info by UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auction found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ViewAuction.class)) }),
            @ApiResponse(responseCode = "404", description = "Auction not found",
                    content = @Content)
    })
    @GetMapping("/api/auctions/{auctionIdentifier}")
    public ResponseEntity<?> getAuction(@PathVariable String auctionIdentifier) {
        return ResponseEntity.status(200).body(auctionService.getAuctionByIdentifier(auctionIdentifier));
    }

    @Operation(summary = "Delete auction by auctionIdentifier")
    @DeleteMapping("/api/legal/auctions/{auctionIdentifier}")
    public ResponseEntity<?> deleteAuction(@PathVariable String auctionIdentifier) {
        auctionService.deleteAuction(auctionIdentifier);
        return ResponseEntity.ok().body("Auction deleted successfully.");
    }

    @PostMapping("/api/legal/auctions")
    public CreateAuctionResponse createAuction(@RequestBody CreateAuctionRequest createAuctionRequest) {

        return auctionService.createAuction(createAuctionRequest);
    }

    @PostMapping(value = "/api/legal/auctions/{auctionIdentifier}/files/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<MediaFilesDTO> addAuctionFiles(@RequestPart("files") List<MultipartFile> files, @PathVariable String auctionIdentifier) throws IOException {

        return auctionService.addAuctionFiles(files, auctionIdentifier);
    }

    @PutMapping(value = "/api/legal/auctions/{auctionIdentifier}")
    public UpdateAuctionResponse updateAuction(@RequestBody UpdateAuctionRequest updateAuctionRequest, @PathVariable String auctionIdentifier) {

        return auctionService.updateAuction(updateAuctionRequest, auctionIdentifier);
    }

    @GetMapping(value = "/api/legal/auctions/user/finished")
    public AuctionListPaginationResponse<Object> getFinishedAuctionByUser(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                          @RequestParam(value = "page_size", defaultValue = "9", required = false) int pageSize) {

        return auctionService.getFinishedAuctionsByUser(page, pageSize);
    }

    @GetMapping(value = "/api/legal/auctions/user/ongoing")
    public AuctionListPaginationResponse<Object> getOngoingAuctionByUser(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                         @RequestParam(value = "page_size", defaultValue = "9", required = false) int pageSize) {

        return auctionService.getOngoingAuctionsByUser(page, pageSize);
    }

    @GetMapping(value = "/api/legal/auctions/user/allAuctions")
    public AuctionListPaginationResponse<Object> getPortfolioAuctions(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                      @RequestParam(value = "page_size", defaultValue = "9", required = false) int pageSize,
                                                                      @RequestParam(value = "status", required = false) StatusEnum status) {

        return auctionService.getPortfolioAuctions(page, pageSize, status);
    }

    @GetMapping("/api/admin/auctions/pending")
    public AuctionListPaginationResponse<Object> getPendingAuctions(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                    @RequestParam(value = "page_size", defaultValue = "9", required = false) int pageSize) {

        return auctionService.getAdminPendingAuctions(page, pageSize);
    }

    @Operation(summary = "This method is used for reject or accept an auction.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status have been changed successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ViewAuction.class)) })
    })
    @PutMapping("/api/admin/auctions/{auctionIdentifier}/accepted")
    public ResponseEntity<?> updateAuctionStatus(@PathVariable String auctionIdentifier, @RequestBody AcceptanceAuctionDto acceptanceAuctionDto){
        auctionService.changeAuctionStatus(acceptanceAuctionDto,auctionIdentifier);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/auctions/search")
    public AuctionListPaginationResponse<Object> searchByKeywordAuctions(@RequestParam(value = "keyword") String keyword,
                                                                         @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                         @RequestParam(value = "page_size", defaultValue = "9", required = false) int pageSize) {

        return auctionService.searchForAuctions(keyword, page, pageSize);
    }
}
