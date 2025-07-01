package com.poly.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.poly.entity.Cart;

@Data
public class PaymentDTO {
    private Long userId;
    private List<Long> courseIds;
    private Double totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentTime;

    // Constructors
    public PaymentDTO() {}

    public PaymentDTO(Long userId, List<Long> courseIds, Double totalAmount, String paymentMethod, String paymentStatus, LocalDateTime paymentTime) {
        this.userId = userId;
        this.courseIds = courseIds;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentTime = paymentTime;
    }

    // Getters & Setters
    // (You can use Lombok @Data for brevity)
}
