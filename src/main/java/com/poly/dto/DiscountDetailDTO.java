package com.poly.dto;

public record DiscountDetailDTO(
    Long id,
    Long discountId,
    Long categoryId,
    Long courseId,
    String status
) {
    public DiscountDetailDTO {
        if (discountId == null) {
            throw new IllegalArgumentException("ID giảm giá không được để trống");
        }
        
        if (categoryId == null && courseId == null) {
            throw new IllegalArgumentException("Phải chọn ít nhất một danh mục hoặc một khóa học");
        }
    }
} 