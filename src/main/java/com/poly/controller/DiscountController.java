package com.poly.controller;

import com.poly.dto.DiscountDTO;
import com.poly.entity.Category;
import com.poly.entity.Course;
import com.poly.service.CategoryService;
import com.poly.service.CourseService;
import com.poly.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/discount")
public class DiscountController {

    @Autowired
    private DiscountService discountService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CourseService courseService;
    
    @GetMapping("")
    public String showDiscountPage(Model model) {
        try {
            List<DiscountDTO> discounts = discountService.getAllDiscounts();
            List<Category> categories = categoryService.getAllCategories();
            List<Course> courses = courseService.findAll();
            
            model.addAttribute("discounts", discounts);
            model.addAttribute("categories", categories);
            model.addAttribute("courses", courses);
            model.addAttribute("isEdit", false);
            
            return "Crud_Discount";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải trang: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/save")
    public String saveDiscount(
            @RequestParam("discountName") String discountName,
            @RequestParam("discountValue") Double discountValue,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("discountType") String discountType,
            @RequestParam(value = "categoryIds[]", required = false) List<Long> categoryIds,
            @RequestParam(value = "courseIds[]", required = false) List<Long> courseIds,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Tạo DTO từ các tham số form
            DiscountDTO discountDTO = new DiscountDTO(
                    null, // ID (sẽ được tạo mới)
                    discountName,
                    discountValue,
                    startDate,
                    endDate,
                    true, // Mặc định là active
                    discountType.equals("category") ? categoryIds : null,
                    discountType.equals("course") ? courseIds : null
            );
            
            // Lưu discount
            DiscountDTO savedDiscount = discountService.createDiscount(discountDTO);
            
            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm chương trình giảm giá thành công!");
            
            return "redirect:/admin/discount";
        } catch (Exception e) {
            // Xử lý lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu giảm giá: " + e.getMessage());
            // Giữ lại thông tin đã nhập
            redirectAttributes.addFlashAttribute("discountName", discountName);
            redirectAttributes.addFlashAttribute("discountValue", discountValue);
            redirectAttributes.addFlashAttribute("startDate", startDate);
            redirectAttributes.addFlashAttribute("endDate", endDate);
            redirectAttributes.addFlashAttribute("discountType", discountType);
            
            return "redirect:/admin/discount";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            // Lấy thông tin discount cần edit
            DiscountDTO discount = discountService.getDiscountById(id);
            
            // Lấy danh sách tất cả các discount để hiển thị ở bảng
            List<DiscountDTO> discounts = discountService.getAllDiscounts();
            
            // Lấy danh sách categories và courses
            List<Category> categories = categoryService.getAllCategories();
            List<Course> courses = courseService.findAll();
            
            // Thêm vào model
            model.addAttribute("discount", discount);
            model.addAttribute("discounts", discounts);
            model.addAttribute("categories", categories);
            model.addAttribute("courses", courses);
            model.addAttribute("isEdit", true);
            
            // Xác định loại giảm giá (category hoặc course)
            String discountType = "";
            if (discount.categoryIds() != null && !discount.categoryIds().isEmpty()) {
                discountType = "category";
            } else if (discount.courseIds() != null && !discount.courseIds().isEmpty()) {
                discountType = "course";
            }
            
            model.addAttribute("discountType", discountType);
            
            return "Crud_Discount";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi tải thông tin giảm giá: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/update/{id}")
    public String updateDiscount(
            @PathVariable Long id,
            @RequestParam("discountName") String discountName,
            @RequestParam("discountValue") Double discountValue,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("discountType") String discountType,
            @RequestParam(value = "categoryIds[]", required = false) List<Long> categoryIds,
            @RequestParam(value = "courseIds[]", required = false) List<Long> courseIds,
            @RequestParam(value = "status", required = false) Boolean status,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Tạo DTO từ các tham số form
            DiscountDTO discountDTO = new DiscountDTO(
                    id,
                    discountName,
                    discountValue,
                    startDate,
                    endDate,
                    status != null ? status : true,
                    discountType.equals("category") ? categoryIds : null,
                    discountType.equals("course") ? courseIds : null
            );
            
            // Cập nhật discount
            DiscountDTO updatedDiscount = discountService.updateDiscount(id, discountDTO);
            
            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật chương trình giảm giá thành công!");
            
            return "redirect:/admin/discount";
        } catch (Exception e) {
            // Xử lý lỗi
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi cập nhật giảm giá: " + e.getMessage());
            return "redirect:/admin/discount/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            discountService.deleteDiscount(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa chương trình giảm giá thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi xóa giảm giá: " + e.getMessage());
        }
        return "redirect:/admin/discount";
    }
    
    @GetMapping("/active")
    @ResponseBody
    public List<DiscountDTO> getActiveDiscounts() {
        return discountService.getActiveDiscounts();
    }
}