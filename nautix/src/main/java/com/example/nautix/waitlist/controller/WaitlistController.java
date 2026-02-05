package com.example.nautix.waitlist.controller;

import com.example.nautix.waitlist.dto.DropStatisticsDTO;
import com.example.nautix.waitlist.dto.DropWaitlistDTO;
import com.example.nautix.waitlist.mapper.WaitlistMapper;
import com.example.nautix.waitlist.service.WaitlistService;
import com.example.nautix.waitlist.model.Drop;
import com.example.nautix.waitlist.model.Waitlist;
import com.example.nautix.waitlist.model.DropStatus;
import com.example.nautix.user.model.UserRole;
import com.example.nautix.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/waitlist")
public class WaitlistController {

    private final WaitlistService waitlistService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public WaitlistController(WaitlistService waitlistService, UserRepository userRepository, ObjectMapper objectMapper) {
        this.waitlistService = waitlistService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    // ===== CUSTOMER ENDPOINTS =====

    /**
     * Join the global waitlist
     */
    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> joinWaitlist(
            @RequestHeader("Authorization") String bearer) throws FirebaseAuthException {
        String idToken = bearer.replaceFirst("^Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String firebaseUid = decoded.getUid();
        
        String waitlistId = waitlistService.joinWaitlist(firebaseUid);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully joined waitlist");
        response.put("waitlistId", waitlistId);

        return ResponseEntity.ok(response);
    }

    /**
     * Leave the global waitlist
     */
    @DeleteMapping("/leave")
    public ResponseEntity<Map<String, Object>> leaveWaitlist(
            @RequestHeader("Authorization") String bearer) throws FirebaseAuthException {
        String idToken = bearer.replaceFirst("^Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String firebaseUid = decoded.getUid();
        
        waitlistService.leaveWaitlist(firebaseUid);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully left waitlist");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if user is on waitlist
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getWaitlistStatus(
            @RequestHeader("Authorization") String bearer) throws FirebaseAuthException {
        String idToken = bearer.replaceFirst("^Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String firebaseUid = decoded.getUid();
        
        boolean isOnWaitlist = waitlistService.isUserOnWaitlist(firebaseUid);
        
        Map<String, Object> response = new HashMap<>();
        response.put("onWaitlist", isOnWaitlist);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all drops visible to waitlist users (upcoming and live)
     */
    @GetMapping("/drops")
    public ResponseEntity<List<DropWaitlistDTO>> getWaitlistDrops(
            @RequestHeader("Authorization") String bearer) throws FirebaseAuthException {
        String idToken = bearer.replaceFirst("^Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String firebaseUid = decoded.getUid();
        
        return ResponseEntity.ok(waitlistService.getWaitlistDrops(firebaseUid));
    }

    // ===== ADMIN ENDPOINTS =====

    /**
     * Get all waitlist entries (Admin only)
     */
    @GetMapping("/admin/entries")
    public ResponseEntity<List<Waitlist>> getAllWaitlistEntries() {
        return ResponseEntity.ok(waitlistService.getAllWaitlistEntries());
    }

    /**
     * Get waitlist statistics (Admin only)
     */
    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getWaitlistStats() {
        return ResponseEntity.ok(waitlistService.getWaitlistStats());
    }

    /**
     * Remove a user from waitlist (Admin only)
     */
    @DeleteMapping("/admin/entries/{waitlistId}")
    public ResponseEntity<Map<String, Object>> removeUserFromWaitlist(
            @PathVariable Long waitlistId) {
        waitlistService.removeUserFromWaitlist(waitlistId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User removed from waitlist");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Assign/replace products for a drop (Admin)
     * Accepts either:
     *  - a raw JSON array: [1,2,3]
     *  - an object with productIds: { "productIds": [1,2,3], ... }
     */
    @PostMapping("/admin/drops/{dropId}/assign-products")
    public ResponseEntity<DropWaitlistDTO> assignProducts(
            @PathVariable Long dropId,
            @RequestBody byte[] body) throws IOException {
        List<Long> productIds = parseProductIds(body);
        if (productIds.isEmpty()) {
            throw new IllegalArgumentException("productIds array is empty");
        }
        Drop updated = waitlistService.assignProducts(dropId, productIds);
        return ResponseEntity.ok(WaitlistMapper.toDto(updated));
    }

    /**
     * Create a new drop (Admin only) - multipart variant
     * Accept "drop" as JSON string regardless of its part Content-Type.
     */
    @PostMapping(value = "/admin/drops", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DropWaitlistDTO> createDropMultipart(
            @RequestPart(value = "drop", required = false) String dropJson,
            @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage,
            @RequestPart(value = "productIds", required = false) List<Long> productIds
    ) throws IOException {
        Drop drop = (dropJson == null || dropJson.isBlank()) ? null : objectMapper.readValue(dropJson, Drop.class);
        // Only override when part is present
        if (drop != null && productIds != null) {
            drop.setProductIds(productIds);
        }
        Drop createdDrop = waitlistService.createDrop(drop, bannerImage);
        return ResponseEntity.ok(WaitlistMapper.toDto(createdDrop));
    }

    /**
     * Create a new drop (Admin only) - JSON or octet-stream variant
     */
    @PostMapping(value = "/admin/drops", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity<DropWaitlistDTO> createDropRaw(@RequestBody byte[] body) throws IOException {
        if (body == null || body.length == 0) throw new IllegalArgumentException("Request body is empty");
        Drop drop = objectMapper.readValue(body, Drop.class);
        Drop createdDrop = waitlistService.createDrop(drop, null);
        return ResponseEntity.ok(WaitlistMapper.toDto(createdDrop));
    }

    /**
     * Get all drops (Admin only)
     */
    @GetMapping("/admin/drops")
    public ResponseEntity<List<DropWaitlistDTO>> getAllDrops() {
        List<DropWaitlistDTO> list = waitlistService.getAllDrops()
                .stream()
                .map(WaitlistMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    /**
     * Update a drop (Admin only) - multipart variant
     * Accept "drop" as JSON string regardless of its part Content-Type.
     */
    @PutMapping(value = "/admin/drops/{dropId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DropWaitlistDTO> updateDropMultipart(
            @PathVariable Long dropId,
            @RequestPart(value = "drop", required = false) String dropJson,
            @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage,
            @RequestPart(value = "productIds", required = false) List<Long> productIds
    ) throws IOException {
        Drop drop = (dropJson == null || dropJson.isBlank()) ? null : objectMapper.readValue(dropJson, Drop.class);
        // Only override when part is present
        if (drop != null && productIds != null) {
            drop.setProductIds(productIds);
        }
        Drop updatedDrop = waitlistService.updateDrop(dropId, drop, bannerImage);
        return ResponseEntity.ok(WaitlistMapper.toDto(updatedDrop));
    }

    /**
     * Update a drop (Admin only) - JSON or octet-stream variant
     */
    @PutMapping(value = "/admin/drops/{dropId}", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE })
    public ResponseEntity<DropWaitlistDTO> updateDropRaw(
            @PathVariable Long dropId,
            @RequestBody byte[] body
    ) throws IOException {
        if (body == null || body.length == 0) throw new IllegalArgumentException("Request body is empty");
        Drop drop = objectMapper.readValue(body, Drop.class);
        Drop updatedDrop = waitlistService.updateDrop(dropId, drop, null);
        return ResponseEntity.ok(WaitlistMapper.toDto(updatedDrop));
    }

    /**
     * Update drop status (Admin only) - This triggers notifications when going live
     */
    @PatchMapping("/admin/drops/{dropId}/status")
    public ResponseEntity<DropWaitlistDTO> updateDropStatus(
            @PathVariable Long dropId,
            @RequestBody Map<String, String> statusUpdate) {
        DropStatus newStatus = DropStatus.valueOf(statusUpdate.get("status"));
        Drop updatedDrop = waitlistService.updateDropStatus(dropId, newStatus);
        return ResponseEntity.ok(WaitlistMapper.toDto(updatedDrop));
    }

    /**
     * Delete a drop (Admin only)
     */
    @DeleteMapping("/admin/drops/{dropId}")
    public ResponseEntity<Map<String, Object>> deleteDrop(
            @PathVariable Long dropId) {
        waitlistService.deleteDrop(dropId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Drop deleted successfully");
        
        return ResponseEntity.ok(response);
    }

    // ===== DROP STATISTICS ENDPOINTS (Admin only) =====

    /**
     * Get statistics for all drops (Admin only)
     */
    @GetMapping("/admin/drops/statistics")
    public ResponseEntity<List<DropStatisticsDTO>> getAllDropStatistics() {
        return ResponseEntity.ok(waitlistService.getAllDropStatistics());
    }

    /**
     * Get statistics for a specific drop (Admin only)
     */
    @GetMapping("/admin/drops/{dropId}/statistics")
    public ResponseEntity<DropStatisticsDTO> getDropStatistics(
            @PathVariable Long dropId) {
        return ResponseEntity.ok(waitlistService.getDropStatistics(dropId));
    }

    /**
     * Get drop performance metrics (Admin only)
     */
    @GetMapping("/admin/drops/metrics")
    public ResponseEntity<Map<String, Object>> getDropPerformanceMetrics() {
        return ResponseEntity.ok(waitlistService.getDropPerformanceMetrics());
    }

    /**
     * Get user statistics (Admin only)
     */
    @GetMapping("/admin/user-stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get total number of users
        long totalUsers = userRepository.count();
        
        // Count admin users
        long adminCount = userRepository.findAll().stream()
                .filter(user -> user.getRole() == UserRole.ADMIN)
                .count();
        
        stats.put("totalUsers", totalUsers);
        stats.put("adminCount", adminCount);
        stats.put("totalCustomers", totalUsers - adminCount);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Get assigned products for a drop (Admin only)
     */
    @GetMapping("/admin/drops/{dropId}/products")
    public ResponseEntity<List<Map<String,Object>>> getDropProducts(@PathVariable Long dropId) {
        List<Map<String,Object>> products = waitlistService.getDropProducts(dropId);
        return ResponseEntity.ok(products);
    }

    // ===== NEW: PUBLIC (authenticated) endpoint to view only visible & available products for a drop =====
    @GetMapping("/drops/{dropId}/products")
    public ResponseEntity<List<Map<String,Object>>> getDropProductsPublic(@PathVariable Long dropId) {
        // Reuse existing service, then filter
        List<Map<String,Object>> products = waitlistService.getDropProducts(dropId);
        List<Map<String,Object>> filtered = products.stream()
                .filter(p -> Boolean.TRUE.equals(p.get("visible")) && Boolean.TRUE.equals(p.get("available")))
                .toList();
        return ResponseEntity.ok(filtered);
    }

    // Helper: Parse product IDs from either an array or an object payload
    private List<Long> parseProductIds(byte[] body) throws IOException {
        if (body == null || body.length == 0) return List.of();
        JsonNode node = objectMapper.readTree(body);

        // Case 1: raw array
        if (node.isArray()) {
            List<Long> ids = new ArrayList<>();
            for (JsonNode n : node) {
                ids.add(n.isNumber() ? n.longValue() : Long.parseLong(n.asText()));
            }
            return ids;
        }

        // Case 2: object with productIds (support a few common keys)
        if (node.isObject()) {
            JsonNode idsNode = null;
            for (String key : new String[]{"productIds", "ids", "products"}) {
                if (node.has(key)) { idsNode = node.get(key); break; }
            }
            if (idsNode != null && idsNode.isArray()) {
                List<Long> ids = new ArrayList<>();
                for (JsonNode n : idsNode) {
                    ids.add(n.isNumber() ? n.longValue() : Long.parseLong(n.asText()));
                }
                return ids;
            }
        }
        throw new IllegalArgumentException("Body must be a JSON array of IDs or an object containing a 'productIds' array");
    }
}
