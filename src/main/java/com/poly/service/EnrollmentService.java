package com.poly.service;

import com.poly.entity.Enrollment;
import com.poly.repository.EnrollmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentsRepository enrollmentsRepository;

    public List<Enrollment> findAll() {
        return enrollmentsRepository.findAll();
    }

    public Enrollment findById(Long id) {
        return enrollmentsRepository.findById(id).orElse(null);
    }

    public Enrollment save(Enrollment enrollment) {
        return enrollmentsRepository.save(enrollment);
    }

    public void delete(Long id) {
        enrollmentsRepository.deleteById(id);
    }

    public Enrollment findByUserAndCourse(Long userId, Long courseId) {
        return enrollmentsRepository.findByUserIdAndCourseId(userId, courseId);
    }

    public Double findTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return enrollmentsRepository.findTotalRevenue(startDate, endDate);
    }

    public Long countEnrollments(LocalDateTime startDate, LocalDateTime endDate) {
        return enrollmentsRepository.countEnrollments(startDate, endDate);
    }

    public List<Object[]> findTopCoursesByRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return enrollmentsRepository.findTopCoursesByRevenue(startDate, endDate);
    }

    public List<Object[]> findRevenueByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return enrollmentsRepository.findRevenueByDate(startDate, endDate);
    }

    public Long countEnrollmentsByCategory(String tenDanhMuc, LocalDateTime startDate, LocalDateTime endDate) {
        return enrollmentsRepository.countEnrollmentsByCategory(tenDanhMuc, startDate, endDate);
    }
}