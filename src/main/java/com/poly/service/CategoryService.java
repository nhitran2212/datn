package com.poly.service;

import com.poly.entity.Category;
import com.poly.repository.CategoryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));
    }

    public Category createCategory(Category category) {
        if (categoryRepository.existsByTenDanhMuc(category.getTenDanhMuc())) {
            throw new RuntimeException("Tên danh mục đã tồn tại, vui lòng nhập tên khác");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, Category categoryDetails) {
        Category category = getCategoryById(id);
        if (!category.getTenDanhMuc().equals(categoryDetails.getTenDanhMuc()) &&
                categoryRepository.existsByTenDanhMuc(categoryDetails.getTenDanhMuc())) {
            throw new RuntimeException("Tên danh mục đã tồn tại, vui lòng nhập tên khác");
        }
        category.setTenDanhMuc(categoryDetails.getTenDanhMuc());
        category.setDescription(categoryDetails.getDescription());
        category.setStatus(categoryDetails.getStatus());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Integer id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    public ByteArrayInputStream exportCategoriesToExcel() throws IOException {
        List<Category> categories = getAllCategories();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Categories");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Tên danh mục", "Mô tả", "Trạng thái"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Create data rows
            int rowNum = 1;
            for (Category category : categories) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(category.getId());
                row.createCell(1).setCellValue(category.getTenDanhMuc());
                row.createCell(2).setCellValue(category.getDescription());
                row.createCell(3).setCellValue(category.getStatus() ? "Hoạt động" : "Không hoạt động");
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public void importCategoriesFromExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Category category = new Category();
                category.setTenDanhMuc(getCellValue(row.getCell(1)));
                category.setDescription(getCellValue(row.getCell(2)));
                String status = getCellValue(row.getCell(3));
                category.setStatus(status.equalsIgnoreCase("Hoạt động"));

                // Check for existing category before saving
                if (!categoryRepository.existsByTenDanhMuc(category.getTenDanhMuc())) {
                    categoryRepository.save(category);
                }
            }
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }
}