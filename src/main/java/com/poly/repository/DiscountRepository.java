package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.Discount;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    
    @Query("SELECT d FROM Discount d WHERE d.status = true AND d.start_date <= :today AND d.end_date >= :today")
    List<Discount> findActiveDiscounts(@Param("today") LocalDate today);
}
