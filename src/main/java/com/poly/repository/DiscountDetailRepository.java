package com.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.poly.entity.DiscountDetail;

import java.util.List;
import java.util.Optional;

public interface DiscountDetailRepository extends JpaRepository<DiscountDetail, Long> {

    @Query("SELECT dd FROM DiscountDetail dd WHERE dd.discount.id = :discountId")
    List<DiscountDetail> findAllByDiscountId(@Param("discountId") Long discountId);
}