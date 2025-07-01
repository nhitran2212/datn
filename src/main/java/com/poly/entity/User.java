package com.poly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "[User]")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_nguoi_dung")
    private Long idNguoiDung;

    @NotBlank(message = "Tên người dùng không được để trống")
    @Column(name = "ten_nguoi_dung")
    private String tenNguoiDung;

    @NotBlank(message = "Họ tên không được để trống")
    @Column(name = "ho_ten")
    private String hoTen;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "vai_tro")
    private Boolean vaiTro;

    @Column(name = "status")
    private Boolean status; // BIT: 1 là "Hoạt động", 0 là "Không hoạt động"

    @Column(name = "anh_dai_dien")
    private String anhDaiDien;

    @Column(name = "ngay_sinh")
    private String ngaySinh;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    // Getter/setter cho vai_tro
    public String getVaiTroAsString() {
        return vaiTro != null && vaiTro ? "Quản trị viên" : "Học viên";
    }

    public void setVaiTroAsString(String vaiTro) {
        this.vaiTro = vaiTro.equals("Quản trị viên");
    }

    // Getter/setter cho status
    public String getStatusAsString() {
        return status != null && status ? "Hoạt động" : "Không hoạt động";
    }

    public void setStatusAsString(String status) {
        this.status = status.equals("Hoạt động");
    }
}