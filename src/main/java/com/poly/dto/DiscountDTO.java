package com.poly.dto;

import java.time.LocalDate;
import java.util.List;

public record DiscountDTO(
    Long id,
    String discountName,
    Double discountValue,
    LocalDate startDate,
    LocalDate endDate,
    Boolean status,
    List<Long> categoryIds,
    List<Long> courseIds
) {
    public DiscountDTO {
        if (discountName == null || discountName.isBlank()) {
            throw new IllegalArgumentException("Tên giảm giá không được để trống");
        }
        
        if (discountValue == null || discountValue < 0 || discountValue > 100) {
            throw new IllegalArgumentException("Giá trị giảm giá phải từ 0 đến 100");
        }
        
        if (startDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }
        
        if (endDate == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống");
        }
        
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
    }
} 