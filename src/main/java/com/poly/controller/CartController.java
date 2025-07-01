package com.poly.controller;

import com.poly.entity.Cart;
import com.poly.entity.Course;
import com.poly.entity.User;
import com.poly.service.CartService;
import com.poly.service.CourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CourseService courseService;

    @GetMapping
    public String viewCart(Model model, HttpSession session,
            @RequestParam(value = "logout", required = false) String logout) {
        User user = (User) session.getAttribute("user");
        List<Cart> cartItems = List.of();
        int cartCount = 0;
        double totalPrice = 0;

        if (user != null) {
            Long userId = user.getIdNguoiDung();
            cartItems = cartService.getCartItemsByUser(userId);
            cartCount = cartItems.size();
            totalPrice = cartItems.stream()
                    .mapToDouble(Cart::getPrice)
                    .sum();
        } else {
            // Nếu logout, xóa cartCount khỏi session và model
            if ("true".equals(logout)) {
                session.setAttribute("cartCount", 0);
                model.addAttribute("cartCount", 0);
                model.addAttribute("message", "Bạn đã đăng xuất. Giỏ hàng đã được làm mới.");
            } else {
                session.setAttribute("cartCount", 0);
                model.addAttribute("cartCount", 0);
            }
        }

        session.setAttribute("cartCount", cartCount);
        model.addAttribute("cartCount", cartCount);

        List<Course> suggestedCourses = courseService.getRandomCourses(4);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("suggestedCourses", suggestedCourses);

        System.out.println("ViewCart - Session ID: " + session.getId() + ", User: "
                + (user != null ? user.getIdNguoiDung() : "null") + ", CartCount: " + cartCount + ", Logout: "
                + logout);

        return "Cart";
    }

    @PostMapping("/add/{courseId}")
    public String addToCart(@PathVariable Long courseId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("cartCount", 0);
            model.addAttribute("error", "Vui lòng đăng nhập để thêm vào giỏ hàng.");
            return "redirect:/Login_Sigin";
        }
        try {
            cartService.addToCart(user.getIdNguoiDung(), courseId);
            int cartCount = cartService.getCartItemsByUser(user.getIdNguoiDung()).size();
            session.setAttribute("cartCount", cartCount);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/course/buy/{courseId}")
    public String buyCoursePost(@PathVariable Long courseId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("cartCount", 0);
            model.addAttribute("error", "Vui lòng đăng nhập để mua khóa học.");
            return "redirect:/Login_Sigin";
        }
        try {
            cartService.addToCart(user.getIdNguoiDung(), courseId);
            int cartCount = cartService.getCartItemsByUser(user.getIdNguoiDung()).size();
            session.setAttribute("cartCount", cartCount);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/course/buy/{courseId}")
    public String buyCourseGet(@PathVariable Long courseId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("cartCount", 0);
            model.addAttribute("error", "Vui lòng đăng nhập để mua khóa học.");
            return "redirect:/Login_Sigin";
        }
        try {
            cartService.addToCart(user.getIdNguoiDung(), courseId);
            int cartCount = cartService.getCartItemsByUser(user.getIdNguoiDung()).size();
            session.setAttribute("cartCount", cartCount);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Integer id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("cartCount", 0);
            model.addAttribute("error", "Vui lòng đăng nhập để xóa khỏi giỏ hàng.");
            return "redirect:/Login_Sigin";
        }
        try {
            cartService.removeFromCart(id);
            int cartCount = cartService.getCartItemsByUser(user.getIdNguoiDung()).size();
            session.setAttribute("cartCount", cartCount);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            session.setAttribute("cartCount", 0);
            model.addAttribute("error", "Vui lòng đăng nhập để thanh toán.");
            return "redirect:/Login_Sigin";
        }
        return "redirect:/payment/success";
    }
}