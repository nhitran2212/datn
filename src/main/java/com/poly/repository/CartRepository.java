package com.poly.repository;

import com.poly.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    @Query("SELECT c FROM Cart c WHERE c.user.idNguoiDung = :userId AND c.status = true")
    List<Cart> findByUserId(Long userId);
}