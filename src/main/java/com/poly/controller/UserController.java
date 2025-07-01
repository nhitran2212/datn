package com.poly.controller;

import com.poly.entity.User;
import com.poly.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/Crud_User")
    public String showUserManagement(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = userService.getAllUsers(PageRequest.of(page, size));
        model.addAttribute("userPage", userPage);
        model.addAttribute("user", new User());
        return "Crud_User";
    }

    @PostMapping("/Crud_User/add")
    public String addUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (result.hasErrors()) {
            Page<User> userPage = userService.getAllUsers(PageRequest.of(page, size));
            model.addAttribute("userPage", userPage);
            model.addAttribute("showAddModal", true);
            return "Crud_User";
        }
        try {
            userService.createUser(user);
        } catch (RuntimeException e) {
            result.addError(new org.springframework.validation.FieldError("user", "email", e.getMessage()));
            Page<User> userPage = userService.getAllUsers(PageRequest.of(page, size));
            model.addAttribute("userPage", userPage);
            model.addAttribute("showAddModal", true);
            return "Crud_User";
        }
        return "redirect:/Crud_User";
    }

    @PostMapping("/Crud_User/update/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") User user, BindingResult result,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (result.hasErrors()) {
            Page<User> userPage = userService.getAllUsers(PageRequest.of(page, size));
            model.addAttribute("userPage", userPage);
            model.addAttribute("showEditModal", true);
            return "Crud_User";
        }
        try {
            userService.updateUser(id, user);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Email")) {
                result.addError(new org.springframework.validation.FieldError("user", "email", e.getMessage()));
            } else {
                model.addAttribute("error", e.getMessage());
            }
            Page<User> userPage = userService.getAllUsers(PageRequest.of(page, size));
            model.addAttribute("userPage", userPage);
            model.addAttribute("showEditModal", true);
            return "Crud_User";
        }
        return "redirect:/Crud_User";
    }

    @GetMapping("/Crud_User/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/Crud_User";
    }

    // Thêm phương thức nhập Excel
    @PostMapping("/Crud_User/import")
    public String importUsersFromExcel(@RequestParam("file") MultipartFile file, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            String result = userService.importUsersFromExcel(file);
            model.addAttribute("message", result);
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi nhập file Excel: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        Page<User> userPage = userService.getAllUsers(PageRequest.of(page, size));
        model.addAttribute("userPage", userPage);
        model.addAttribute("user", new User());
        return "Crud_User";
    }

    // Thêm phương thức xuất Excel
    @GetMapping("/Crud_User/export")
    public ResponseEntity<ByteArrayResource> exportUsersToExcel() throws IOException {
        byte[] excelBytes = userService.exportUsersToExcel();
        ByteArrayResource resource = new ByteArrayResource(excelBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelBytes.length)
                .body(resource);
    }
}