// com.poly.service.QuizExcelService

package com.poly.service;

import com.poly.entity.Question; // Đảm bảo import đúng entity Question của bạn
import com.poly.entity.Course; // Cần import Course để thiết lập mối quan hệ
import com.poly.repository.CourseRepository; // Cần inject CourseRepository để tìm Course
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class QuizExcelService {

    @Autowired
    private CourseRepository courseRepository;

    private QuestionService questionService;

    // Column indices for import/export
    private static final int COL_QUESTION_ID = 0; // Cột mới cho ID
    private static final int COL_QUESTION_CONTENT = 1;
    private static final int COL_OPTION_A = 2;
    private static final int COL_OPTION_B = 3;
    private static final int COL_OPTION_C = 4;
    private static final int COL_OPTION_D = 5;
    private static final int COL_CORRECT_ANSWER = 6;


    // ... (exportQuestionsToExcel giữ nguyên hoặc điều chỉnh để xuất ID nếu muốn)
    // Để giữ cho việc nhập/xuất đơn giản, chúng ta vẫn không xuất ID trong export
    public byte[] exportQuestionsToExcel(List<Question> questions) throws IOException {
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Questions");

    // Header
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("ID câu hỏi");
    header.createCell(1).setCellValue("Nội dung câu hỏi");
    header.createCell(2).setCellValue("Đáp án A");
    header.createCell(3).setCellValue("Đáp án B");
    header.createCell(4).setCellValue("Đáp án C");
    header.createCell(5).setCellValue("Đáp án D");
    header.createCell(6).setCellValue("Đáp án đúng");

    // Data
    int rowIdx = 1;
    for (Question q : questions) {
        Row row = sheet.createRow(rowIdx++);
        row.createCell(0).setCellValue(q.getID_cau_hoi() != null ? q.getID_cau_hoi().toString() : "");
        row.createCell(1).setCellValue(q.getNoi_dung_cau_hoi());
        row.createCell(2).setCellValue(q.getDap_an_a());
        row.createCell(3).setCellValue(q.getDap_an_b());
        row.createCell(4).setCellValue(q.getDap_an_c());
        row.createCell(5).setCellValue(q.getDap_an_d());
        row.createCell(6).setCellValue(q.getDap_an_dung());
    }

    // Ghi workbook ra ByteArrayOutputStream
    try (java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream()) {
        workbook.write(bos);
        workbook.close();
        return bos.toByteArray();
    }
}


    /**
     * Nhập danh sách câu hỏi từ một file Excel, hỗ trợ cập nhật nếu ID tồn tại.
     * Định dạng file Excel dự kiến:
     * Cột 0 (A): ID_cau_hoi (Để trống hoặc 0 nếu là câu hỏi mới)
     * Cột 1 (B): Nội Dung Câu Hỏi
     * Cột 2 (C): Đáp Án A
     * Cột 3 (D): Đáp Án B
     * Cột 4 (E): Đáp Án C
     * Cột 5 (F): Đáp Án D
     * Cột 6 (G): Đáp Án Đúng
     *
     * @param inputStream InputStream của file Excel.
     * @param courseId ID của khóa học mà các câu hỏi này sẽ thuộc về.
     * @return Danh sách các đối tượng Question đã được phân tích cú pháp từ file Excel.
     * @throws IOException Nếu có lỗi xảy ra trong quá trình đọc file.
     * @throws IllegalArgumentException Nếu ID khóa học không hợp lệ hoặc dữ liệu không đủ.
     */
    public List<Question> importQuestionsFromExcel(InputStream inputStream, Long courseId) throws IOException {
        List<Question> questionsToSave = new ArrayList<>(); // Danh sách chứa cả câu hỏi mới và câu hỏi cập nhật

        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy khóa học với ID: " + courseId);
        }
        Course course = courseOptional.get();

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        // Bỏ qua hàng tiêu đề
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        DataFormatter dataFormatter = new DataFormatter();

        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();

            if (isRowEmpty(currentRow)) {
                continue;
            }

            // Đọc ID_cau_hoi từ cột đầu tiên
            final Long[] idCauHoiWrapper = {null};
            Cell idCell = currentRow.getCell(COL_QUESTION_ID);
            if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                // Đọc ID dạng số và chuyển sang Long
                idCauHoiWrapper[0] = (long) idCell.getNumericCellValue();
            } else if (idCell != null && idCell.getCellType() == CellType.STRING && !idCell.getStringCellValue().trim().isEmpty()) {
                // Đọc ID dạng chuỗi và cố gắng chuyển sang Long (nếu người dùng nhập dạng text)
                try {
                    idCauHoiWrapper[0] = Long.parseLong(idCell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    System.err.println("Cảnh báo: ID câu hỏi không hợp lệ (không phải số) ở hàng " + (currentRow.getRowNum() + 1) + ". Sẽ tạo mới câu hỏi này.");
                    idCauHoiWrapper[0] = null; // Coi như không có ID, sẽ tạo mới
                }
            }
            Long idCauHoi = idCauHoiWrapper[0];


            String noiDungCauHoi = dataFormatter.formatCellValue(currentRow.getCell(COL_QUESTION_CONTENT)).trim();
            String dapAnA = dataFormatter.formatCellValue(currentRow.getCell(COL_OPTION_A)).trim();
            String dapAnB = dataFormatter.formatCellValue(currentRow.getCell(COL_OPTION_B)).trim();
            String dapAnC = dataFormatter.formatCellValue(currentRow.getCell(COL_OPTION_C)).trim();
            String dapAnD = dataFormatter.formatCellValue(currentRow.getCell(COL_OPTION_D)).trim();
            String dapAnDung = dataFormatter.formatCellValue(currentRow.getCell(COL_CORRECT_ANSWER)).trim();

            // Xác thực cơ bản
            if (noiDungCauHoi.isEmpty() || dapAnDung.isEmpty()) {
                System.err.println("Bỏ qua hàng " + (currentRow.getRowNum() + 1) + " do thiếu nội dung câu hỏi hoặc đáp án đúng.");
                continue;
            }

            if (!dapAnDung.equals(dapAnA) &&
                !dapAnDung.equals(dapAnB) &&
                !dapAnDung.equals(dapAnC) &&
                !dapAnDung.equals(dapAnD)) {
                System.err.println("Bỏ qua hàng " + (currentRow.getRowNum() + 1) + " do đáp án đúng không khớp với bất kỳ đáp án nào.");
                continue;
            }

            Question question;
            if (idCauHoi != null) {
                // Nếu có ID, thử tìm câu hỏi cũ để cập nhật
                Optional<Question> existingQuestionOpt = questionsToSave.stream()
                        .filter(q -> q.getID_cau_hoi() != null && q.getID_cau_hoi().equals(idCauHoi))
                        .findFirst();
                // Nếu chưa tìm thấy trong danh sách đã xử lý, thử tìm trong DB
                if (existingQuestionOpt.isEmpty()) {
                    existingQuestionOpt = Optional.ofNullable(questionService.findById(idCauHoi)); // Gọi service để tìm trong DB
                }

                if (existingQuestionOpt.isPresent()) {
                    question = existingQuestionOpt.get();
                    // Cập nhật các thuộc tính
                    question.setNoi_dung_cau_hoi(noiDungCauHoi);
                    question.setDap_an_a(dapAnA);
                    question.setDap_an_b(dapAnB);
                    question.setDap_an_c(dapAnC);
                    question.setDap_an_d(dapAnD);
                    question.setDap_an_dung(dapAnDung);
                    // Đảm bảo course không bị ghi đè nếu đã đúng
                    if (question.getCourse() == null || !question.getCourse().getID_khoa_hoc().equals(courseId)) {
                        question.setCourse(course);
                    }
                } else {
                    // Không tìm thấy câu hỏi với ID này, tạo mới
                    question = new Question();
                    question.setID_cau_hoi(idCauHoi); // Gán ID nếu có
                    question.setNoi_dung_cau_hoi(noiDungCauHoi);
                    question.setDap_an_a(dapAnA);
                    question.setDap_an_b(dapAnB);
                    question.setDap_an_c(dapAnC);
                    question.setDap_an_d(dapAnD);
                    question.setDap_an_dung(dapAnDung);
                    question.setCourse(course);
                }
            } else {
                // Không có ID, tạo câu hỏi mới
                question = new Question();
                question.setNoi_dung_cau_hoi(noiDungCauHoi);
                question.setDap_an_a(dapAnA);
                question.setDap_an_b(dapAnB);
                question.setDap_an_c(dapAnC);
                question.setDap_an_d(dapAnD);
                question.setDap_an_dung(dapAnDung);
                question.setCourse(course);
            }
            questionsToSave.add(question);
        }

        workbook.close();
        return questionsToSave;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        // Kiểm tra cell đầu tiên để tránh lỗi khi row không có cell nào
        if (row.getFirstCellNum() == -1) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    @Autowired // Hoặc setter thủ công nếu muốn rõ ràng hơn
    public void setQuestionService(QuestionService questionService) {
        this.questionService = questionService;
    }
}