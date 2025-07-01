package com.poly.service;

import com.poly.entity.Cart;
import com.poly.entity.Course;
import com.poly.entity.User;
import com.poly.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    public List<Cart> getCartItemsByUser(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    public void removeFromCart(Integer cartId) {
        cartRepository.deleteById(cartId);
    }

    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    public void addToCart(Long userId, Long courseId) {
        User user = null;
        try {
            user = userService.getUserById(userId);
        } catch (RuntimeException e) {
            return;
        }
        Course course = courseService.findById(courseId);
        if (user != null && course != null) {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setCourse(course);
            cart.setPrice(course.getGia_tien());
            cart.setStatus(true);
            save(cart);
        }
    }
}