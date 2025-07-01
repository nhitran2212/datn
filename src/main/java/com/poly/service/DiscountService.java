package com.poly.service;

import com.poly.dto.DiscountDTO;
import com.poly.entity.Discount;

import java.util.List;

public interface DiscountService {
    List<DiscountDTO> getAllDiscounts();
    
    DiscountDTO getDiscountById(Long id);
    
    DiscountDTO createDiscount(DiscountDTO discountDTO);
    
    DiscountDTO updateDiscount(Long id, DiscountDTO discountDTO);
    
    void deleteDiscount(Long id);
    
    List<DiscountDTO> getActiveDiscounts();
} 