package com.poly.controller;

import com.poly.entity.User;
import com.poly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfileForm(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/Login_Sigin";
        }
        User user = userService.getUserById(sessionUser.getIdNguoiDung());
        String[] hoTenParts = user.getHoTen() != null ? user.getHoTen().trim().split("\\s+", 2)
                : new String[] { "", "" };
        model.addAttribute("user", user);
        model.addAttribute("ho", hoTenParts.length > 0 ? hoTenParts[0] : "");
        model.addAttribute("ten", hoTenParts.length > 1 ? hoTenParts[1] : "");
        return "Profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam("ho") String ho,
            @RequestParam("ten") String ten,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam("email") String email,
            @RequestParam("ngaySinh") String ngaySinh,
            HttpSession session,
            Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/Login_Sigin";
        }

        User user = userService.getUserById(sessionUser.getIdNguoiDung());
        String hoTen = (ho.trim() + " " + ten.trim()).trim();
        if (hoTen.isEmpty()) {
            model.addAttribute("error", "Họ và tên không được để trống");
            model.addAttribute("user", user);
            model.addAttribute("ho", ho);
            model.addAttribute("ten", ten);
            return "Profile";
        }
        user.setHoTen(hoTen);
        user.setSoDienThoai(soDienThoai);
        user.setEmail(email);
        user.setNgaySinh(ngaySinh);

        try {
            userService.updateUser(user.getIdNguoiDung(), user);
            session.setAttribute("user", user);
            model.addAttribute("success", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi cập nhật thông tin: " + e.getMessage());
            model.addAttribute("ho", ho);
            model.addAttribute("ten", ten);
        }

        model.addAttribute("user", user);
        return "redirect:/index";
    }
}