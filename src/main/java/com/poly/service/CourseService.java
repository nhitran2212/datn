package com.poly.service;

import com.poly.entity.Category;
import com.poly.entity.Course;
import com.poly.repository.CategoryRepository;
import com.poly.repository.CourseRepository;
import com.poly.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<Category> getAllCate() {
        return categoryRepository.findAll();
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course findById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getRandomCourses(int limit) {
        return courseRepository.findTopNByStatusTrue(limit);
    }

    public List<Course> getCoursesByType(String type) {
        if (type == null || type.equals("all")) {
            return courseRepository.findTopNByStatusTrue(8);
        }
        return courseRepository.findByCategoryTenDanhMucAndStatusTrue(type);
    }

    public long getTotalStudents() {
        return courseRepository.countDistinctStudents();
    }

    public long getTotalCourses() {
        return courseRepository.countByStatusTrue();
    }

    public long getTotalInstructors() {
        return userRepository.countByVaiTroAndStatusTrue(true); // Đếm chuyên gia (vai_tro = true)
    }
}