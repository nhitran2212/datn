package com.poly.repository;

import com.poly.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;


public interface EnrollmentsRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT SUM(e.price) FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate")
    Double findTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate")
    Long countEnrollments(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e.course, SUM(e.price), COUNT(e) FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate GROUP BY e.course ORDER BY SUM(e.price) DESC")
    List<Object[]> findTopCoursesByRevenue(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e.enrollmentDate, SUM(e.price) FROM Enrollment e WHERE e.enrollmentDate BETWEEN :startDate AND :endDate GROUP BY e.enrollmentDate")
    List<Object[]> findRevenueByDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT e.user.id) FROM Enrollment e JOIN e.course.category c WHERE c.tenDanhMuc = :tenDanhMuc AND e.enrollmentDate BETWEEN :startDate AND :endDate")
    Long countEnrollmentsByCategory(@Param("tenDanhMuc") String tenDanhMuc, @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.course.ID_khoa_hoc = :courseId")
    Enrollment findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
}