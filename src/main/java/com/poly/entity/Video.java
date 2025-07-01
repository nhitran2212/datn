package com.poly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID_video;

    private String ten_video;
    private String duong_dan_video;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
