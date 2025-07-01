package com.poly.controller;

import com.poly.entity.Category;
import com.poly.service.CategoryService;
import jakarta.validation.Valid;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/Crud_Categories")
    public String showCategoryManagement(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Category> categoryPage = categoryService.getAllCategories(PageRequest.of(page, size));
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("category", new Category());
        return "Crud_Categories";
    }

    @PostMapping("/Crud_Categories/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult result, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (result.hasErrors()) {
            Page<Category> categoryPage = categoryService.getAllCategories(PageRequest.of(page, size));
            model.addAttribute("categoryPage", categoryPage);
            model.addAttribute("showAddModal", true);
            return "Crud_Categories";
        }
        try {
            categoryService.createCategory(category);
        } catch (RuntimeException e) {
            model.addAttribute("tenDanhMucError", e.getMessage());
            Page<Category> categoryPage = categoryService.getAllCategories(PageRequest.of(page, size));
            model.addAttribute("categoryPage", categoryPage);
            model.addAttribute("showAddModal", true);
            return "Crud_Categories";
        }
        return "redirect:/Crud_Categories";
    }

    @PostMapping("/Crud_Categories/update/{id}")
    public String updateCategory(@PathVariable Integer id, @Valid @ModelAttribute("category") Category category,
            BindingResult result, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (result.hasErrors()) {
            Page<Category> categoryPage = categoryService.getAllCategories(PageRequest.of(page, size));
            model.addAttribute("categoryPage", categoryPage);
            model.addAttribute("showEditModal", true);
            return "Crud_Categories";
        }
        try {
            categoryService.updateCategory(id, category);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Tên danh mục")) {
                model.addAttribute("tenDanhMucError", e.getMessage());
            } else {
                model.addAttribute("error", e.getMessage());
            }
            Page<Category> categoryPage = categoryService.getAllCategories(PageRequest.of(page, size));
            model.addAttribute("categoryPage", categoryPage);
            model.addAttribute("showEditModal", true);
            return "Crud_Categories";
        }
        return "redirect:/Crud_Categories";
    }

    @GetMapping("/Crud_Categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return "redirect:/Crud_Categories";
    }

    // Thêm endpoint xuất Excel
    @GetMapping("/Crud_Categories/export")
    public ResponseEntity<InputStreamResource> exportCategories() throws IOException {
        ByteArrayInputStream in = categoryService.exportCategoriesToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=categories.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    // Thêm endpoint nhập Excel
    @PostMapping("/Crud_Categories/import")
    public String importCategories(@RequestParam("file") MultipartFile file, Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            categoryService.importCategoriesFromExcel(file);
        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi nhập file Excel: " + e.getMessage());
            Page<Category> categoryPage = categoryService.getAllCategories(PageRequest.of(page, size));
            model.addAttribute("categoryPage", categoryPage);
            model.addAttribute("category", new Category());
            return "Crud_Categories";
        }
        return "redirect:/Crud_Categories";
    }
}