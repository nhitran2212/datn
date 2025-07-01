package com.poly.repository;

import com.poly.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    // Using @Query to explicitly define the JPQL query
    // 'v.course.ID_khoa_hoc' directly references the field in the entity
    @Query("SELECT v FROM Video v WHERE v.course.ID_khoa_hoc = :courseId")
    List<Video> findVideosByCourseId(@Param("courseId") Long courseId); // Method name can be anything now, but descriptive is best
}