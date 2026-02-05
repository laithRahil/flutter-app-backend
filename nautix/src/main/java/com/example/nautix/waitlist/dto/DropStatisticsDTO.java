package com.example.nautix.waitlist.dto;

import com.example.nautix.waitlist.model.DropStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropStatisticsDTO {
    private Long dropId;
    private String dropName;
    private DropStatus status;
    private boolean live; // Convenience field for backward compatibility
    private LocalDateTime dropDate;
    private long waitlistCount;
    private long uniqueUsersCount;
    private Double conversionRate; // Percentage of users who joined waitlist vs total users
    private LocalDateTime lastWaitlistJoin;
    private LocalDateTime firstWaitlistJoin;
}

