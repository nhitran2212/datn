package com.poly.service;

import com.poly.dto.DiscountDTO;
import com.poly.entity.Category;
import com.poly.entity.Course;
import com.poly.entity.Discount;
import com.poly.entity.DiscountDetail;
import com.poly.repository.CategoryRepository;
import com.poly.repository.CourseRepository;
import com.poly.repository.DiscountDetailRepository;
import com.poly.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private DiscountRepository discountRepository;
    
    @Autowired
    private DiscountDetailRepository discountDetailRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Override
    public List<DiscountDTO> getAllDiscounts() {
        List<Discount> discounts = discountRepository.findAll();
        return discounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public DiscountDTO getDiscountById(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá với ID: " + id));
        return mapToDTO(discount);
    }
    
    @Override
    @Transactional
    public DiscountDTO createDiscount(DiscountDTO discountDTO) {
        // Tạo đối tượng Discount từ DTO
        Discount discount = new Discount();
        discount.setDiscount_name(discountDTO.discountName());
        discount.setDiscount_value(discountDTO.discountValue());
        discount.setStart_date(discountDTO.startDate());
        discount.setEnd_date(discountDTO.endDate());
        discount.setStatus(discountDTO.status() != null ? discountDTO.status() : true);
        
        // Lưu Discount
        Discount savedDiscount = discountRepository.save(discount);
        
        // Tạo discount details cho danh mục (nếu có)
        if (discountDTO.categoryIds() != null && !discountDTO.categoryIds().isEmpty()) {
            for (Long categoryId : discountDTO.categoryIds()) {
                Category category = categoryRepository.findById(categoryId.intValue())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
                
                DiscountDetail detail = new DiscountDetail();
                detail.setDiscount(savedDiscount);
                detail.setCategory(category);
                detail.setStatus("1"); // Active
                
                discountDetailRepository.save(detail);
            }
        }
        
        // Tạo discount details cho khóa học (nếu có)
        if (discountDTO.courseIds() != null && !discountDTO.courseIds().isEmpty()) {
            for (Long courseId : discountDTO.courseIds()) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + courseId));
                
                DiscountDetail detail = new DiscountDetail();
                detail.setDiscount(savedDiscount);
                detail.setCourse(course);
                detail.setStatus("1"); // Active
                
                discountDetailRepository.save(detail);
            }
        }
        
        return mapToDTO(savedDiscount);
    }
    
    @Override
    @Transactional
    public DiscountDTO updateDiscount(Long id, DiscountDTO discountDTO) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá với ID: " + id));
        
        // Cập nhật thông tin discount
        discount.setDiscount_name(discountDTO.discountName());
        discount.setDiscount_value(discountDTO.discountValue());
        discount.setStart_date(discountDTO.startDate());
        discount.setEnd_date(discountDTO.endDate());
        discount.setStatus(discountDTO.status() != null ? discountDTO.status() : true);
        
        // Lưu discount
        Discount updatedDiscount = discountRepository.save(discount);
        
        // Xóa tất cả discount details cũ
        List<DiscountDetail> details = discountDetailRepository.findAllByDiscountId(id);
        discountDetailRepository.deleteAll(details);
        
        // Tạo lại discount details cho danh mục (nếu có)
        if (discountDTO.categoryIds() != null && !discountDTO.categoryIds().isEmpty()) {
            for (Long categoryId : discountDTO.categoryIds()) {
                Category category = categoryRepository.findById(categoryId.intValue())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
                
                DiscountDetail detail = new DiscountDetail();
                detail.setDiscount(updatedDiscount);
                detail.setCategory(category);
                detail.setStatus("1"); // Active
                
                discountDetailRepository.save(detail);
            }
        }
        
        // Tạo lại discount details cho khóa học (nếu có)
        if (discountDTO.courseIds() != null && !discountDTO.courseIds().isEmpty()) {
            for (Long courseId : discountDTO.courseIds()) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + courseId));
                
                DiscountDetail detail = new DiscountDetail();
                detail.setDiscount(updatedDiscount);
                detail.setCourse(course);
                detail.setStatus("1"); // Active
                
                discountDetailRepository.save(detail);
            }
        }
        
        return mapToDTO(updatedDiscount);
    }
    
    @Override
    @Transactional
    public void deleteDiscount(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá với ID: " + id));
        
        // Xóa tất cả discount details trước
        List<DiscountDetail> details = discountDetailRepository.findAllByDiscountId(id);
        discountDetailRepository.deleteAll(details);
        
        // Xóa discount
        discountRepository.delete(discount);
    }
    
    @Override
    public List<DiscountDTO> getActiveDiscounts() {
        LocalDate today = LocalDate.now();
        List<Discount> activeDiscounts = discountRepository.findActiveDiscounts(today);
        return activeDiscounts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    // Helper để chuyển đổi Entity thành DTO
    private DiscountDTO mapToDTO(Discount discount) {
        List<DiscountDetail> details = discountDetailRepository.findAllByDiscountId(discount.getId());
        
        List<Long> categoryIds = new ArrayList<>();
        List<Long> courseIds = new ArrayList<>();
        
        for (DiscountDetail detail : details) {
            if (detail.getCategory() != null) {
                categoryIds.add(Long.valueOf(detail.getCategory().getId()));
            }
            if (detail.getCourse() != null) {
                courseIds.add(detail.getCourse().getID_khoa_hoc());
            }
        }
        
        return new DiscountDTO(
            discount.getId(),
            discount.getDiscount_name(),
            discount.getDiscount_value(),
            discount.getStart_date(),
            discount.getEnd_date(),
            discount.getStatus(),
            categoryIds,
            courseIds
        );
    }
} 