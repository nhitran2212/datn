package com.poly.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.poly.entity.Course;
import com.poly.repository.CategoryRepository;
import com.poly.service.CourseService;

@Controller
@RequestMapping("/Crud_Course")
public class CrudCourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CategoryRepository categoryRepo;

    private final String UPLOAD_DIR = "src/main/resources/static/upload/";

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("categories", categoryRepo.findAll());
        model.addAttribute("course", new Course());
        return "Crud_Course"; // Tên file HTML không có .html
    }

    
    @PostMapping("/add")
    public String addCourse(@ModelAttribute Course course,
                            @RequestParam("fileAnhDaiDien") MultipartFile file) throws IOException {

        if (file != null && !file.isEmpty()) {
            String originalFileName = file.getOriginalFilename();
            Path uploadDir = Paths.get(UPLOAD_DIR); // Using the defined UPLOAD_DIR constant

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path targetPath = uploadDir.resolve(originalFileName);
            try {
                Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                course.setAnh_dai_dien(originalFileName); // Corrected: use setAnh_dai_dien
            } catch (IOException e) {
                // Lưu thất bại => báo lỗi
                // It's good practice to log the exception for debugging
                e.printStackTrace(); 
                return "redirect:/Crud_Course?error=upload";
            }
        } else {
            // Không có ảnh => gán ảnh mặc định
            course.setAnh_dai_dien("default.png"); // Corrected: use setAnh_dai_dien
        }

        courseService.save(course);
        return "redirect:/Crud_Course";
    }

    @PostMapping("/update")
    public String updateCourse(@ModelAttribute Course course,
                               @RequestParam("imageFile") MultipartFile file,
                               @RequestParam("oldImage") String oldImage) throws IOException {

        if (file != null && !file.isEmpty()) {
            // Có ảnh mới => lưu ảnh mới
            String originalFileName = file.getOriginalFilename();
            Path uploadDir = Paths.get(UPLOAD_DIR); // Using the defined UPLOAD_DIR constant
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path targetPath = uploadDir.resolve(originalFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            course.setAnh_dai_dien(originalFileName); // Corrected: use setAnh_dai_dien
        } else {
            // Không chọn ảnh mới => giữ lại ảnh cũ
            course.setAnh_dai_dien(oldImage); // Corrected: use setAnh_dai_dien
        }

        courseService.save(course);
        return "redirect:/Crud_Course";
    }




    @GetMapping("/edit/{id}")
    public String editCourse(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("course", course);
        model.addAttribute("courses", courseService.findAll());
        model.addAttribute("categories", categoryRepo.findAll());
        return "Crud_Course";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.delete(id);
        return "redirect:/Crud_Course";
    }

    private void saveFile(MultipartFile file, String fileName) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());
    }
}
