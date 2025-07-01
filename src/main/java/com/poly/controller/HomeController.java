package com.poly.controller;

import com.poly.entity.Category;
import com.poly.entity.Course;
import com.poly.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CourseService courseService;

    @GetMapping({ "/", "/index" })
    public String home(@RequestParam(value = "type", required = false, defaultValue = "all") String type, Model model) {
        List<Course> courses = courseService.getCoursesByType(type);
        List<Category> categories = courseService.getAllCate();
        long totalStudents = courseService.getTotalStudents();
        long totalCourses = courseService.getTotalCourses();
        long totalInstructors = courseService.getTotalInstructors();
        model.addAttribute("courses", courses);
        model.addAttribute("categories", categories);
        model.addAttribute("activeType", type);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalInstructors", totalInstructors);
        return "index";
    }
}