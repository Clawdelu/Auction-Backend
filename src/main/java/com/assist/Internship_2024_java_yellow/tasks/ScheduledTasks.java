package com.assist.Internship_2024_java_yellow.tasks;

import com.assist.Internship_2024_java_yellow.services.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final AuctionService auctionService;

    @Scheduled(cron = "*/30 * * * * *")
    public void scheduleTaskForPendingStatus()
    {
        auctionService.changeAutoPendingStatus();
    }

    @Scheduled(cron = "*/2 * * * * *")
    public void scheduleTaskForOngoingStatus()
    {
        auctionService.changeAutoOngoingStatus();
    }

}
