package com.poly.dto;

import lombok.Data;

@Data
public class CartDTO {
    private Long cartId;
    private Long userId;
    private Long courseId;
    private String courseName;
    private Double price;
    private String status;

    // Constructors
    public CartDTO() {}

    public CartDTO(Long cartId, Long userId, Long courseId, String courseName, Double price, String status) {
        this.cartId = cartId;
        this.userId = userId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.price = price;
        this.status = status;
    }

    // Getters & Setters
    // (You can use Lombok @Data for brevity)
}
