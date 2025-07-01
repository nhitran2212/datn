package com.poly.repository;

import com.poly.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(value = "SELECT TOP (:limit) * FROM Course c WHERE c.status = 1 ORDER BY NEWID()", nativeQuery = true)
    List<Course> findTopNByStatusTrue(@Param("limit") int limit);

    @Query("SELECT c FROM Course c WHERE c.status = true AND c.category.tenDanhMuc = :tenDanhMuc")
    List<Course> findByCategoryTenDanhMucAndStatusTrue(@Param("tenDanhMuc") String tenDanhMuc);

    List<Course> findByStatusTrueAndCategoryTenDanhMuc(String tenDanhMuc);

    long countByStatusTrue();

    @Query("SELECT COUNT(DISTINCT e.user.id) FROM Enrollment e WHERE e.course.status = true")
    long countDistinctStudents();
}