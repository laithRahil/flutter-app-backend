package com.example.nautix.waitlist.service;

import java.util.List;
import java.util.Map;
import com.example.nautix.waitlist.dto.DropWaitlistDTO;
import com.example.nautix.waitlist.dto.DropStatisticsDTO;
import com.example.nautix.waitlist.model.Drop;
import com.example.nautix.waitlist.model.Waitlist;
import com.example.nautix.waitlist.model.DropStatus;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface WaitlistService {
    // ===== WAITLIST MANAGEMENT =====
    /**
     * Join the global waitlist
     */
    String joinWaitlist(String firebaseUid);
    
    /**
     * Leave the global waitlist
     */
    void leaveWaitlist(String firebaseUid);
    
    /**
     * Check if user is on waitlist
     */
    boolean isUserOnWaitlist(String firebaseUid);
    
    /**
     * Get all upcoming/live drops for any authenticated user
     * (no longer restricted to users already on the waitlist).
     */
    List<DropWaitlistDTO> getWaitlistDrops(String firebaseUid);
    
    // ===== DROP MANAGEMENT (Admin) =====
    /**
     * Create a new drop
     */
    Drop createDrop(Drop drop, MultipartFile bannerImage) throws IOException;
    
    /**
     * Get all drops (Admin view)
     */
    List<Drop> getAllDrops();
    
    /**
     * Update a drop
     */
    Drop updateDrop(Long dropId, Drop drop, MultipartFile bannerImage) throws IOException;
    
    /**
     * Delete a drop
     */
    void deleteDrop(Long dropId);
    
    /**
     * Change drop status and notify waitlist users if going live
     */
    Drop updateDropStatus(Long dropId, DropStatus newStatus);
    
    // Assign/replace products for a drop by ID list
    Drop assignProducts(Long dropId, java.util.List<Long> productIds);
    
    /**
     * Get products for a specific drop
     */
    List<Map<String,Object>> getDropProducts(Long dropId);
    
    // ===== ADMIN STATISTICS =====
    /**
     * Get all waitlist entries
     */
    List<Waitlist> getAllWaitlistEntries();
    
    /**
     * Get waitlist statistics
     */
    Map<String, Object> getWaitlistStats();
    
    /**
     * Remove a user from waitlist (Admin)
     */
    void removeUserFromWaitlist(Long waitlistId);
    
    /**
     * Get drop statistics
     */
    List<DropStatisticsDTO> getAllDropStatistics();
    
    /**
     * Get specific drop statistics
     */
    DropStatisticsDTO getDropStatistics(Long dropId);
    
    /**
     * Get drop performance metrics
     */
    Map<String, Object> getDropPerformanceMetrics();
    
    /**
     * Check and update drop statuses based on current time
     */
    void checkAndUpdateDropStatuses();
}
