CREATE DATABASE LMS;
GO

USE LMS;
GO
-- Bảng User
CREATE TABLE [User] (
    ID_nguoi_dung INT PRIMARY KEY IDENTITY(1,1),
    ten_nguoi_dung NVARCHAR(100) NOT NULL,
    ho_ten NVARCHAR(250) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mat_khau VARCHAR(255) NOT NULL,
    vai_tro BIT,
    anh_dai_dien VARCHAR(255),
    ngay_sinh DATE,
    dia_chi NVARCHAR(255),
    so_dien_thoai VARCHAR(15) UNIQUE,
	status BIT DEFAULT 1
);

-- Bảng danh mục khóa học
CREATE TABLE Category (
    ID_danh_muc INT PRIMARY KEY IDENTITY(1,1),
    ten_danh_muc NVARCHAR(100) NOT NULL,
    Description NVARCHAR(255),
	status BIT DEFAULT 1
);

-- Bảng khóa học
CREATE TABLE Course (
    ID_khoa_hoc INT PRIMARY KEY IDENTITY(1,1),
    ten_khoa_hoc NVARCHAR(100) NOT NULL,
    mo_ta NVARCHAR(MAX),
    diem_dat FLOAT NOT NULL,
    danh_muc_ID INT NOT NULL,
    gia_tien DECIMAL(10,2) NOT NULL,
    anh_dai_dien VARCHAR(255),
    status BIT DEFAULT 1,
    FOREIGN KEY (danh_muc_ID) REFERENCES Category(ID_danh_muc)
);

-- Bảng ghi danh (Enrollments)
CREATE TABLE Enrollments (
    id INT PRIMARY KEY IDENTITY(1,1),
    user_id INT NOT NULL,
    course_id INT NOT NULL,
	price float NOT NULL,
    enrollment_date DATETIME NOT NULL DEFAULT GETDATE(),
    finish_date DATE NULL,
    diem FLOAT,
    FOREIGN KEY (user_id) REFERENCES [User](ID_nguoi_dung),
    FOREIGN KEY (course_id) REFERENCES Course(ID_khoa_hoc),
    CONSTRAINT UQ_user_course UNIQUE(user_id, course_id)
);

-- Bảng Video (1 Course - N Video)
CREATE TABLE Video (
    ID_video INT PRIMARY KEY IDENTITY(1,1),
    ten_video NVARCHAR(100) NOT NULL,
    duong_dan_video VARCHAR(255) NOT NULL,
    course_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Course(ID_khoa_hoc)
);

-- Bảng câu hỏi (1 Course - N Question)
CREATE TABLE Question (
    ID_cau_hoi INT PRIMARY KEY IDENTITY(1,1),
    noi_dung_cau_hoi NVARCHAR(MAX) NOT NULL,
    dap_an_dung VARCHAR(100) NOT NULL,
    dap_an_a VARCHAR(300) NOT NULL,
    dap_an_b VARCHAR(300) NOT NULL,
    dap_an_c VARCHAR(300) NOT NULL,
    dap_an_d VARCHAR(300) NOT NULL,
    course_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Course(ID_khoa_hoc)
);


-- Giỏ hàng
CREATE TABLE cart (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    price FLOAT NOT NULL,
	status BIT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES [User](ID_nguoi_dung),
    FOREIGN KEY (course_id) REFERENCES Course(ID_khoa_hoc) ON UPDATE NO ACTION
);

-- Giảm giá
CREATE TABLE discounts (
    id INT IDENTITY(1,1) PRIMARY KEY,
    discount_name NVARCHAR(MAX) NOT NULL,
    discount_value FLOAT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status BIT DEFAULT 1
);

-- Chi tiết giảm giá
CREATE TABLE discount_details (
    id INT IDENTITY(1,1) PRIMARY KEY,
    discount_id INT NOT NULL,
    categories_id INT NULL,
    course_id INT NULL,
    FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (categories_id) REFERENCES Category(ID_danh_muc) ON DELETE NO ACTION ON UPDATE NO ACTION,
    FOREIGN KEY (course_id) REFERENCES Course(ID_khoa_hoc) ON DELETE NO ACTION ON UPDATE NO ACTION,
    status BIT DEFAULT 1
);
GO

-- Chèn dữ liệu vào bảng User
INSERT INTO [User] (ten_nguoi_dung, ho_ten, email, mat_khau, vai_tro, anh_dai_dien, ngay_sinh, dia_chi, so_dien_thoai, status)
VALUES 
    ('user1', N'Nguyễn Văn A', 'user@example.com', '123456', 0, 'images/avatar1.jpg', '1995-05-15', N'123 Đường Láng, Hà Nội', '0912345678', 1),
    ('admin1', N'Trần Thị B', 'admin@example.com', '123456', 1, 'images/avatar2.jpg', '1990-03-22', N'456 Lê Lợi, TP.HCM', '0987654321', 1);

-- Chèn dữ liệu vào bảng Category
INSERT INTO Category (ten_danh_muc, Description, status)
VALUES 
    (N'Lập trình', N'Các khóa học về lập trình phần mềm', 1),
    (N'Lập trình OOP', N'Các khóa học về lập trình phần mềm', 1),
    (N'Thiết kế', N'Các khóa học về thiết kế đồ họa và UI/UX', 1);

-- Chèn dữ liệu vào bảng Course
INSERT INTO Course (ten_khoa_hoc, mo_ta, diem_dat, danh_muc_ID, gia_tien, anh_dai_dien, Status)
VALUES 
    (N'Lập trình Python cơ bản', N'Khóa học giới thiệu về lập trình Python từ cơ bản đến nâng cao', 7.0, 1, 150.00, 'images/python_course.jpg', 1),
        (N'Java OOP VIP PRO', N'Khóa học java lập trình hướng đối tượng từ cơ bản đến nâng cao', 7.0, 2, 500.00, 'images/python_course.jpg', 1),
    (N'Lập trình JavaScript nâng cao', N'Khóa học chuyên sâu về JavaScript và các framework hiện đại', 7.5, 1, 200.00, 'images/js_course.jpg', 1),
    (N'Thiết kế UI/UX', N'Học cách thiết kế giao diện người dùng đẹp mắt và thân thiện', 6.5, 3, 200.00, 'images/uiux_course.jpg', 1);

-- Chèn dữ liệu vào bảng cart
INSERT INTO cart (user_id, course_id, price, status)
VALUES 
    (1, 2, 200.00, 1);

-- Chèn dữ liệu vào bảng discounts
INSERT INTO discounts (discount_name, discount_value, start_date, end_date, status)
VALUES 
    (N'Giảm giá mùa hè', 20.0, '2025-06-01', '2025-08-31', 1);

-- Chèn dữ liệu vào bảng discount_details
INSERT INTO discount_details (discount_id, categories_id, course_id, status)
VALUES 
    (1, 1, NULL, 1), -- Áp dụng cho danh mục Lập trình
    (1, NULL, 2, 1);  -- Áp dụng cho khóa học JavaScript
GO