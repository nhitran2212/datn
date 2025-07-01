package com.poly.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.poly.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByTenDanhMuc(String tenDanhMuc);

    @Query("SELECT c FROM Category c WHERE c.status = true AND (LOWER(c.tenDanhMuc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Category> findByTenDanhMucOrDescriptionContainingIgnoreCaseAndStatusTrue(@Param("keyword") String keyword);

}