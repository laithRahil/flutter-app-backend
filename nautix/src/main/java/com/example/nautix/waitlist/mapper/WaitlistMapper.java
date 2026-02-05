package com.example.nautix.waitlist.mapper;

import com.example.nautix.waitlist.dto.DropWaitlistDTO;
import com.example.nautix.waitlist.model.Drop;

public class WaitlistMapper {
    public static DropWaitlistDTO toDto(Drop drop) {
        DropWaitlistDTO dto = new DropWaitlistDTO();
        dto.setDropId(drop.getId());
        dto.setName(drop.getName());
        dto.setBannerUrl(drop.getBanner() != null ? drop.getBanner().getUrl() : null);
        dto.setDescription(drop.getDescription());
        dto.setStatus(drop.getStatus());
        dto.setDropDate(drop.getDropDate());
        dto.setProductCount(drop.getProducts() != null ? drop.getProducts().size() : 0);
        return dto;
    }
}
