package com.example.nautix.waitlist.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.nautix.waitlist.service.WaitlistService;

@Configuration
@EnableScheduling
public class DropStatusScheduler {

    private final WaitlistService waitlistService;

    public DropStatusScheduler(WaitlistService waitlistService) {
        this.waitlistService = waitlistService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void promoteDueDrops() {
        waitlistService.checkAndUpdateDropStatuses();
    }
}
