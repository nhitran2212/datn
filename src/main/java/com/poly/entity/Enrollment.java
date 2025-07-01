package com.poly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "enrollment_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime enrollmentDate;

    @Column(name = "finish_date")
    private LocalDate finishDate;

    @Column(name = "diem")
    private Double diem; // Trường lưu điểm quiz
}