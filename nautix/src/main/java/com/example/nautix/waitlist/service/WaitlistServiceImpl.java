package com.example.nautix.waitlist.service;

import com.example.nautix.exception.AlreadyExistsException;
import com.example.nautix.exception.ResourceNotFoundException;
import com.example.nautix.product.model.Product;
import com.example.nautix.product.repository.ProductRepository;
import com.example.nautix.user.model.User;
import com.example.nautix.user.repository.UserRepository;
import com.example.nautix.waitlist.dto.DropWaitlistDTO;
import com.example.nautix.waitlist.dto.DropStatisticsDTO;
import com.example.nautix.waitlist.model.Drop;
import com.example.nautix.waitlist.model.Waitlist;
import com.example.nautix.waitlist.model.DropStatus;
import com.example.nautix.waitlist.repository.DropRepository;
import com.example.nautix.waitlist.repository.WaitlistRepository;
import com.example.nautix.waitlist.model.DropBanner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.nautix.file.FileStorageService;
import org.springframework.beans.factory.annotation.Value;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.lang.reflect.Method;

@Service
public class WaitlistServiceImpl implements WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final DropRepository dropRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    
    @Value("${app.base-url}")
    private String baseUrl;

    @PersistenceContext
    private EntityManager entityManager;

    public WaitlistServiceImpl(
            WaitlistRepository waitlistRepository,
            DropRepository dropRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            FileStorageService fileStorageService) {
        this.waitlistRepository = waitlistRepository;
        this.dropRepository = dropRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    // ===== WAITLIST MANAGEMENT =====
    
    @Override
    @Transactional
    public String joinWaitlist(String firebaseUid) {
        if (firebaseUid == null || firebaseUid.trim().isEmpty()) {
            throw new IllegalArgumentException("Firebase UID cannot be null or empty");
        }
        
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Firebase UID: " + firebaseUid));
        
        // Check if user is already on waitlist
        if (waitlistRepository.existsByUser(user)) {
            throw new AlreadyExistsException("User already on waitlist");
        }
        
        Waitlist waitlistEntry = new Waitlist();
        waitlistEntry.setUser(user);
        Waitlist savedEntry = waitlistRepository.save(waitlistEntry);
        
        return savedEntry.getId().toString();
    }

    @Override
    @Transactional
    public void leaveWaitlist(String firebaseUid) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Waitlist waitlistEntry = waitlistRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("User not on waitlist"));
        
        waitlistRepository.delete(waitlistEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserOnWaitlist(String firebaseUid) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return waitlistRepository.existsByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DropWaitlistDTO> getWaitlistDrops(String firebaseUid) {
        // Opportunistic refresh
        checkAndUpdateDropStatuses();
        // Previously enforced: throw if user not on waitlist.
        // Now: allow any authenticated user (firebaseUid still validated indirectly if you wish).
        // If you still want to ensure the user exists in DB (but not necessarily on waitlist), keep lookup:
        userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Drop> drops = dropRepository.findByStatusInOrderByDropDateAsc(
            List.of(DropStatus.UPCOMING, DropStatus.LIVE)
        );
        
        return drops.stream()
                .map(this::convertToWaitlistDTO)
                .collect(Collectors.toList());
    }

    // ===== DROP MANAGEMENT =====
    
    // Overloaded createDrop for multipart banner image
    @Override
    @Transactional
    public Drop createDrop(Drop drop, MultipartFile bannerImage) throws IOException {
        // Validate required input (admin passes name/description/date/banner image/products)
        if (drop == null) {
            throw new IllegalArgumentException("Drop payload is required");
        }
        if (drop.getDropDate() == null) {
            throw new IllegalArgumentException("Drop date is required");
        }

        // Status is always derived from the drop date: if date is now/past => LIVE, else UPCOMING
        LocalDateTime now = LocalDateTime.now();
        boolean goLiveNow = !drop.getDropDate().isAfter(now); // (now or earlier)
        drop.setStatus(goLiveNow ? DropStatus.LIVE : DropStatus.UPCOMING);

        List<Product> resolvedProducts =
            (drop.getProductIds() != null && !drop.getProductIds().isEmpty())
                ? resolveProductsByIds(drop.getProductIds())
                : resolveProducts(drop.getProducts());

        if (drop.getProductIds() != null && !drop.getProductIds().isEmpty() && resolvedProducts.isEmpty()) {
            throw new IllegalArgumentException("Provided productIds did not resolve to any existing products");
        }

        // Attach products (defensive: create mutable list & ensure managed relationship)
        drop.setProducts(new java.util.ArrayList<>());
        if (!resolvedProducts.isEmpty()) {
            drop.getProducts().addAll(resolvedProducts);
        }

        // Persist & flush so join table rows are written before banner logic
        Drop savedDrop = dropRepository.saveAndFlush(drop);

        // Handle banner image upload AFTER drop has an ID
        if (bannerImage != null && !bannerImage.isEmpty()) {
            String filename = fileStorageService.storeProductImage(bannerImage);
            String url = baseUrl + "/uploads/products/" + filename;

            DropBanner banner = new DropBanner();
            banner.setUrl(url);
            banner.setDrop(savedDrop);       // owning side sets FK drop_id

            entityManager.persist(banner);   // insert into drop_banner with drop_id
            savedDrop.setBanner(banner);     // inverse link for serialization/domain

            // Update Drop to reflect association (no FK here, but keeps relation consistent)
            savedDrop = dropRepository.save(savedDrop);
        }

        // No extra auto-adjust here since status is already derived from date above
        // Drop savedDrop = dropRepository.save(drop); // already saved above

        if (!savedDrop.getProducts().isEmpty()) {
            for (Product product : savedDrop.getProducts()) {
                product.setVisible(true);
                productRepository.save(product);
            }
        }

        // Force flush to guarantee drop_products entries exist before returning
        dropRepository.flush();
        return savedDrop;
    }

    // Overloaded updateDrop for multipart banner image
    @Override
    @Transactional
    public Drop updateDrop(Long dropId, Drop drop, MultipartFile bannerImage) throws IOException {
        Drop existingDrop = dropRepository.findById(dropId)
                .orElseThrow(() -> new ResourceNotFoundException("Drop not found"));

        existingDrop.setName(drop.getName());
        // If a new banner image is uploaded, replace the URL
        if (bannerImage != null && !bannerImage.isEmpty()) {
            String filename = fileStorageService.storeProductImage(bannerImage);
            String url = baseUrl + "/uploads/products/" + filename;

            DropBanner banner = existingDrop.getBanner();
            if (banner == null) {
                banner = new DropBanner();
                banner.setUrl(url);
                banner.setDrop(existingDrop);   // owning side
                entityManager.persist(banner);
                existingDrop.setBanner(banner); // inverse link
            } else {
                banner.setUrl(url);             // managed entity, will be updated
            }
        }
        existingDrop.setDescription(drop.getDescription());
        existingDrop.setDropDate(drop.getDropDate());

        List<Product> resolvedProducts =
            (drop.getProductIds() != null && !drop.getProductIds().isEmpty())
                ? resolveProductsByIds(drop.getProductIds())
                : resolveProducts(drop.getProducts());

        if (drop.getProductIds() != null && !drop.getProductIds().isEmpty() && resolvedProducts.isEmpty()) {
            throw new IllegalArgumentException("Provided productIds did not resolve to any existing products");
        }

        // Replace relationship explicitly (clear then add) to ensure join table updates
        if (existingDrop.getProducts() != null) {
            existingDrop.getProducts().clear();
        } else {
            existingDrop.setProducts(new java.util.ArrayList<>());
        }
        existingDrop.getProducts().addAll(resolvedProducts);

        adjustStatusForDropDate(existingDrop);

        Drop updated = dropRepository.saveAndFlush(existingDrop);

        if (!updated.getProducts().isEmpty()) {
            for (Product p : updated.getProducts()) {
                p.setVisible(true);
                productRepository.save(p);
            }
        }
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Drop> getAllDrops() {
        // Opportunistic refresh for admin views
        checkAndUpdateDropStatuses();
        return dropRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public void deleteDrop(Long dropId) {
        Drop drop = dropRepository.findById(dropId)
                .orElseThrow(() -> new ResourceNotFoundException("Drop not found"));

        // Mark products unavailable (defensive null checks)
        if (drop.getProducts() != null && !drop.getProducts().isEmpty()) {
            for (Product product : drop.getProducts()) {
                if (product != null) {
                    product.setAvailable(false);
                    productRepository.save(product);
                }
            }
            // Unlink products first and flush to clear drop_products join rows
            drop.getProducts().clear();
            dropRepository.saveAndFlush(drop);
        }

        // Clear inverse link; owning FK is on DropBanner, but cascade+orphanRemoval will remove it
        if (drop.getBanner() != null) {
            drop.getBanner().setDrop(null);
            drop.setBanner(null);
        }

        // Delete the drop; CascadeType.ALL + orphanRemoval will remove the banner
        dropRepository.delete(drop);
        dropRepository.flush();
    }

    @Override
    @Transactional
    public Drop updateDropStatus(Long dropId, DropStatus newStatus) {
        Drop drop = dropRepository.findById(dropId)
                .orElseThrow(() -> new ResourceNotFoundException("Drop not found"));
        
        DropStatus oldStatus = drop.getStatus();
        drop.setStatus(newStatus);
        Drop updatedDrop = dropRepository.save(drop);
        
        // Handle product availability based on drop status
        if (updatedDrop.getProducts() != null && !updatedDrop.getProducts().isEmpty()) {
            for (Product product : updatedDrop.getProducts()) {
                if (newStatus == DropStatus.LIVE) {
                    product.setAvailable(true);
                } else {
                    product.setAvailable(false);
                }
                productRepository.save(product);
            }
        }
        
        // If drop is going live, notify all waitlist users
        if (oldStatus != DropStatus.LIVE && newStatus == DropStatus.LIVE) {
            notifyWaitlistUsers(updatedDrop);
        }
        
        return updatedDrop;
    }

    // ===== ADMIN METHODS =====
    
    @Override
    @Transactional(readOnly = true)
    public List<Waitlist> getAllWaitlistEntries() {
        return waitlistRepository.findAllByOrderByJoinDateAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getWaitlistStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total waitlist users
        long totalUsers = waitlistRepository.count();
        stats.put("totalWaitlistUsers", totalUsers);
        
        // Total drops by status
        List<Drop> allDrops = dropRepository.findAll();
        Map<DropStatus, Long> dropsByStatus = allDrops.stream()
                .collect(Collectors.groupingBy(Drop::getStatus, Collectors.counting()));
        
        stats.put("totalDrops", allDrops.size());
        stats.put("upcomingDrops", dropsByStatus.getOrDefault(DropStatus.UPCOMING, 0L));
        stats.put("liveDrops", dropsByStatus.getOrDefault(DropStatus.LIVE, 0L));
        return stats;
    }

    @Override
    @Transactional
    public void removeUserFromWaitlist(Long waitlistId) {
        Waitlist waitlistEntry = waitlistRepository.findById(waitlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Waitlist entry not found"));
        
        waitlistRepository.delete(waitlistEntry);
    }

    // ===== DROP STATISTICS =====
    
    @Override
    @Transactional(readOnly = true)
    public List<DropStatisticsDTO> getAllDropStatistics() {
        List<Drop> allDrops = dropRepository.findAll();
        return allDrops.stream()
                .map(this::buildDropStatistics)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DropStatisticsDTO getDropStatistics(Long dropId) {
        Drop drop = dropRepository.findById(dropId)
                .orElseThrow(() -> new ResourceNotFoundException("Drop not found"));
        return buildDropStatistics(drop);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDropPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        List<Drop> allDrops = dropRepository.findAll();
        long totalWaitlistUsers = waitlistRepository.count();
        
        // Basic metrics
        metrics.put("totalDrops", allDrops.size());
        metrics.put("totalWaitlistUsers", totalWaitlistUsers);
        
        // Drops by status
        Map<DropStatus, Long> statusCounts = allDrops.stream()
                .collect(Collectors.groupingBy(Drop::getStatus, Collectors.counting()));
        
        metrics.put("upcomingDrops", statusCounts.getOrDefault(DropStatus.UPCOMING, 0L));
        metrics.put("liveDrops", statusCounts.getOrDefault(DropStatus.LIVE, 0L));
        // Average products per drop
        double avgProductsPerDrop = allDrops.stream()
                .filter(drop -> drop.getProducts() != null)
                .mapToInt(drop -> drop.getProducts().size())
                .average()
                .orElse(0.0);
        metrics.put("averageProductsPerDrop", Math.round(avgProductsPerDrop * 100.0) / 100.0);
        
        return metrics;
    }

    @Override
    @Transactional
    public void checkAndUpdateDropStatuses() {
        // Find all upcoming drops whose drop date has passed
        List<Drop> upcomingDrops = dropRepository.findByStatus(DropStatus.UPCOMING);
        LocalDateTime now = LocalDateTime.now();
        
        for (Drop drop : upcomingDrops) {
            if (drop.getDropDate() != null && !drop.getDropDate().isAfter(now)) {
                // Automatically transition to LIVE status
                updateDropStatus(drop.getId(), DropStatus.LIVE);
            }
        }
    }

    // ===== PRIVATE HELPER METHODS =====
    
    private List<Product> resolveProducts(List<Product> incoming) {
        if (incoming == null || incoming.isEmpty()) return Collections.emptyList();
        return incoming.stream()
                .map(p -> productRepository.findById(p.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + p.getId())))
                .toList();
    }

    // New: resolve by IDs (used when UI sends productIds)
    private List<Product> resolveProductsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        List<Product> found = productRepository.findAllById(ids);
        // Validate all IDs exist
        Set<Long> foundIds = found.stream().map(Product::getId).collect(java.util.stream.Collectors.toSet());
        for (Long id : ids) {
            if (!foundIds.contains(id)) {
                throw new ResourceNotFoundException("Product not found: " + id);
            }
        }
        return found;
    }

    private void adjustStatusForDropDate(Drop drop) {
        if (drop.getDropDate() != null
                && !drop.getDropDate().isAfter(LocalDateTime.now())
                && drop.getStatus() == DropStatus.UPCOMING) {
            drop.setStatus(DropStatus.LIVE);
        }
    }

    private DropWaitlistDTO convertToWaitlistDTO(Drop drop) {
        DropWaitlistDTO dto = new DropWaitlistDTO();
        dto.setDropId(drop.getId());
        dto.setName(drop.getName());
        dto.setBannerUrl(drop.getBanner() != null ? drop.getBanner().getUrl() : null);
        dto.setDescription(drop.getDescription());
        dto.setStatus(drop.getStatus());
        dto.setDropDate(drop.getDropDate());
        // Avoid multiple lazy loads by reading once
        List<Product> products = drop.getProducts();
        dto.setProductCount(products != null ? products.size() : 0);
        return dto;
    }

    private DropStatisticsDTO buildDropStatistics(Drop drop) {
        DropStatisticsDTO stats = new DropStatisticsDTO();
        stats.setDropId(drop.getId());
        stats.setDropName(drop.getName());
        stats.setStatus(drop.getStatus());
        stats.setLive(drop.getStatus() == DropStatus.LIVE);
        stats.setDropDate(drop.getDropDate());
        
        // For the new model, waitlist count is always the total waitlist users
        long totalWaitlistUsers = waitlistRepository.count();
        stats.setWaitlistCount((int) totalWaitlistUsers);
        stats.setUniqueUsersCount(totalWaitlistUsers);
        
        // Conversion rate based on drop status and products
        if (drop.getProducts() != null && !drop.getProducts().isEmpty()) {
            // This could be enhanced with actual purchase data
            stats.setConversionRate(totalWaitlistUsers > 0 ? 5.0 : 0.0); // Placeholder
        } else {
            stats.setConversionRate(0.0);
        }
        
        // First and last waitlist join dates
        List<Waitlist> waitlistEntries = waitlistRepository.findAllByOrderByJoinDateAsc();
        if (!waitlistEntries.isEmpty()) {
            stats.setFirstWaitlistJoin(waitlistEntries.get(0).getJoinDate());
            stats.setLastWaitlistJoin(waitlistEntries.get(waitlistEntries.size() - 1).getJoinDate());
        }
        
        return stats;
    }

    private void notifyWaitlistUsers(Drop drop) {
        List<Waitlist> waitlistUsers = waitlistRepository.findAll();
        
        for (Waitlist waitlistEntry : waitlistUsers) {
            User user = waitlistEntry.getUser();
            sendNotification(user, drop);
        }
    }

    private void sendNotification(User user, Drop drop) {
        // Log notification
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("to", user.getEmail());
        emailData.put("subject", "🔥 New Drop is LIVE: " + drop.getName());
        emailData.put("html", buildEmailTemplate(user, drop));

    }

    private String buildEmailTemplate(User user, Drop drop) {
        String bannerUrl = drop.getBanner() != null ? drop.getBanner().getUrl() : "#";
        return "<h1>🔥 New Drop is LIVE!</h1>" +
               "<img src='" + bannerUrl + "' alt='Drop Banner' style='max-width:100%;height:auto;'/><br/>" +
               "<p>Hi " + user.getFullName() + ",</p>" +
               "<p>The drop you've been waiting for is now LIVE: <strong>" + drop.getName() + "</strong></p>" +
               "<p>" + drop.getDescription() + "</p>" +
               "<p>Shop now before it's gone!</p>" +
               "<a href='#' style='background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Shop Now</a>";
    }

    @Override
    @Transactional
    public Drop assignProducts(Long dropId, List<Long> productIds) {
        Drop drop = dropRepository.findById(dropId)
            .orElseThrow(() -> new ResourceNotFoundException("Drop not found"));
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("productIds cannot be empty");
        }
        List<Product> resolved = resolveProductsByIds(productIds);
        if (resolved.isEmpty()) {
            throw new IllegalArgumentException("No valid products resolved from provided IDs");
        }

        if (drop.getProducts() != null) {
            drop.getProducts().clear();
        } else {
            drop.setProducts(new java.util.ArrayList<>());
        }
        drop.getProducts().addAll(resolved);

        for (Product p : resolved) {
            p.setVisible(true);
            productRepository.save(p);
        }

        Drop saved = dropRepository.saveAndFlush(drop);
        return saved;
    }

    // ===== NEW: FETCH ASSIGNED PRODUCTS FOR A DROP (ADMIN) =====
    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDropProducts(Long dropId) {
        Drop drop = dropRepository.findById(dropId)
                .orElseThrow(() -> new ResourceNotFoundException("Drop not found"));
        if (drop.getProducts() == null || drop.getProducts().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return drop.getProducts().stream()
                .map(this::mapProductForAssignedList)
                .toList();
    }

    // Build a lightweight structure expected by frontend (_assignedProducts usage)
    private Map<String, Object> mapProductForAssignedList(Product p) {
	    Map<String, Object> m = new HashMap<>();
	    // Basic fields (safe access)
	    m.put("id", safeGetLong(() -> p.getId(), null));
	    m.put("name", safeGetString(() -> p.getName(), ""));
	
	    // basePrice: try common getters in order
	    Double basePrice = null;
	    basePrice = safeGetDouble(() -> {
	        try { return ((Number)p.getClass().getMethod("getPrice").invoke(p)).doubleValue(); } catch (Exception ex) { return null; }
	    }, null);
	    if (basePrice == null) {
	        basePrice = safeGetDouble(() -> {
	            try { return ((Number)p.getClass().getMethod("getBasePrice").invoke(p)).doubleValue(); } catch (Exception ex) { return null; }
	        }, null);
	    }
	    if (basePrice == null) basePrice = 0.0;
	    m.put("basePrice", basePrice);
	
	    // visible / available flags (defensive)
	    m.put("visible", safeBool(() -> p.isVisible()));
	    m.put("available", safeBool(() -> p.isAvailable()));
	
	    // bannerUrl: try getBannerUrl(), then getBanner().getUrl()
	    String bannerUrl = null;
	    try {
	        Method mb = p.getClass().getMethod("getBannerUrl");
	        Object val = mb.invoke(p);
	        if (val instanceof String s && !s.isBlank()) bannerUrl = s;
	    } catch (Exception ignored) {}
	    if (bannerUrl == null) {
	        try {
	            Method gb = p.getClass().getMethod("getBanner");
	            Object bannerObj = gb.invoke(p);
	            if (bannerObj != null) {
	                try {
	                    Method gurl = bannerObj.getClass().getMethod("getUrl");
	                    Object v = gurl.invoke(bannerObj);
	                    if (v instanceof String s2 && !s2.isBlank()) bannerUrl = s2;
	                } catch (Exception ignored) {}
	            }
	        } catch (Exception ignored) {}
	    }
	    m.put("bannerUrl", bannerUrl);
	
	    // Variants/colors: best-effort extraction with safe fallbacks
	    List<Map<String, Object>> colors = new java.util.ArrayList<>();
	    try {
	        Method getVariants = p.getClass().getMethod("getVariants");
	        Object variantsObj = getVariants.invoke(p);
	        if (variantsObj instanceof java.util.Collection<?> variants) {
	            for (Object v : variants) {
	                Map<String, Object> cv = new HashMap<>();
	                // variantId
	                cv.put("variantId", safeInvokeLong(v, "getId", null));
	                // color
	                cv.put("color", safeInvokeString(v, "getColor", null));
	                // price override -> price
	                Double price = safeInvokeDouble(v, "getPriceOverride", null);
	                if (price == null) price = basePrice;
	                cv.put("price", price);
	
	                // images -> imageUrls
	                List<String> imageUrls = new java.util.ArrayList<>();
	                // try getImages() -> each image.getUrl()
	                try {
	                    Method getImages = v.getClass().getMethod("getImages");
	                    Object imgs = getImages.invoke(v);
	                    if (imgs instanceof java.util.Collection<?> col) {
	                        for (Object img : col) {
	                            String url = safeInvokeString(img, "getUrl", null);
	                            if (url != null && !url.isBlank()) imageUrls.add(url);
	                        }
	                    }
	                } catch (Exception ignored) {}
	                // fallback to getImagesUrls()
	                if (imageUrls.isEmpty()) {
	                    try {
	                        Method getImgsUrls = v.getClass().getMethod("getImagesUrls");
	                        Object urls = getImgsUrls.invoke(v);
	                        if (urls instanceof java.util.Collection<?> col2) {
	                            for (Object u : col2) {
	                                if (u instanceof String su && !su.isBlank()) imageUrls.add(su);
	                            }
	                        }
	                    } catch (Exception ignored) {}
	                }
	                cv.put("imageUrls", imageUrls);
	                colors.add(cv);
	            }
	        }
	    } catch (Exception ignored) {}
	    m.put("colors", colors);
	    return m;
	}
	
	// small reflection helpers used above
	private String safeGetString(java.util.concurrent.Callable<String> c, String def) {
	    try { String s = c.call(); return s != null ? s : def; } catch (Exception e) { return def; }
	}
	private Long safeGetLong(java.util.concurrent.Callable<Long> c, Long def) {
	    try { Long v = c.call(); return v != null ? v : def; } catch (Exception e) { return def; }
	}
	private Double safeGetDouble(java.util.concurrent.Callable<Double> c, Double def) {
	    try { Double v = c.call(); return v != null ? v : def; } catch (Exception e) { return def; }
	}
	private String safeInvokeString(Object target, String methodName, String def) {
	    try { Method m = target.getClass().getMethod(methodName); Object v = m.invoke(target); return v instanceof String s ? s : def; } catch (Exception e) { return def; }
	}
	private Long safeInvokeLong(Object target, String methodName, Long def) {
	    try { Method m = target.getClass().getMethod(methodName); Object v = m.invoke(target); if (v instanceof Number n) return n.longValue(); return def; } catch (Exception e) { return def; }
	}
    private Double safeInvokeDouble(Object target, String methodName, Double def) {
        try { Method m = target.getClass().getMethod(methodName); Object v = m.invoke(target); if (v instanceof Number n) return n.doubleValue(); return def; } catch (Exception e) { return def; }
    }
    private Boolean safeBool(java.util.concurrent.Callable<Boolean> c) {
        try { Boolean b = c.call(); return b != null ? b : false; } catch (Exception e) { return false; }
    }
}
