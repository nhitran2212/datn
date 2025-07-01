package com.poly.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_cau_hoi")
    private Long ID_cau_hoi;

    @Column(name = "noi_dung_cau_hoi")
    private String noi_dung_cau_hoi;

    @Column(name = "dap_an_dung")
    private String dap_an_dung;

    @Column(name = "dap_an_a")
    private String dap_an_a;

    @Column(name = "dap_an_b")
    private String dap_an_b;

    @Column(name = "dap_an_c")
    private String dap_an_c;

    @Column(name = "dap_an_d")
    private String dap_an_d;
    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonManagedReference // Đánh dấu tham chiếu "chính"
    private Course course;

}
