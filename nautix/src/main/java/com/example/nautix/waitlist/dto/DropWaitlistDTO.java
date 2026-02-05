package com.example.nautix.waitlist.dto;

import java.time.LocalDateTime;
import com.example.nautix.waitlist.model.DropStatus;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DropWaitlistDTO {
    private Long dropId;
    private String name;
    private String bannerUrl; // keep as String for DTO
    private String description;
    private DropStatus status;
    private LocalDateTime dropDate;
    private int productCount;
}
