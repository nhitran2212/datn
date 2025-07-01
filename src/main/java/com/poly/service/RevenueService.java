package com.poly.service;

import com.poly.entity.Course;
import com.poly.repository.EnrollmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RevenueService {
    @Autowired
    private EnrollmentsRepository enrollmentsRepository;

    // Tổng doanh thu theo khoảng thời gian
    public Double getTotalRevenueBetween(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59, 999999999);
        Double sum = enrollmentsRepository.findTotalRevenue(startDateTime, endDateTime);
        return sum != null ? sum : 0.0;
    }

    // Tổng số đơn hàng theo khoảng thời gian
    public Long getTotalEnrollmentsBetween(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59, 999999999);
        Long count = enrollmentsRepository.countEnrollments(startDateTime, endDateTime);
        return count != null ? count : 0L;
    }

    // Doanh thu trung bình/đơn theo khoảng thời gian
    public Double getAverageRevenuePerOrder(LocalDate start, LocalDate end) {
        Long totalOrders = getTotalEnrollmentsBetween(start, end);
        Double totalRevenue = getTotalRevenueBetween(start, end);
        return totalOrders != 0 ? totalRevenue / totalOrders : 0.0;
    }

    // Tăng trưởng doanh thu theo filter (so với khoảng trước đó)
    public Double getRevenueGrowth(String period) {
        LocalDate end = LocalDate.now();
        LocalDate start, prevEnd, prevStart;
        switch (period) {
            case "7days":
                start = end.minusDays(6);
                prevEnd = start.minusDays(1);
                prevStart = prevEnd.minusDays(6);
                break;
            case "30days":
                start = end.minusDays(29);
                prevEnd = start.minusDays(1);
                prevStart = prevEnd.minusDays(29);
                break;
            case "3months":
                start = end.minusMonths(3);
                prevEnd = start.minusDays(1);
                prevStart = prevEnd.minusMonths(3);
                break;
            case "6months":
                start = end.minusMonths(6);
                prevEnd = start.minusDays(1);
                prevStart = prevEnd.minusMonths(6);
                break;
            case "1year":
            default:
                start = end.minusYears(1);
                prevEnd = start.minusDays(1);
                prevStart = prevEnd.minusYears(1);
                break;
        }
        Double revenueThis = getTotalRevenueBetween(start, end);
        Double revenuePrev = getTotalRevenueBetween(prevStart, prevEnd);
        return revenuePrev == 0 ? 0.0 : ((revenueThis - revenuePrev) / revenuePrev) * 100;
    }

    // Top 5 khóa học doanh thu cao nhất theo khoảng thời gian
    public List<Map<String, Object>> getTopCoursesBetween(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59, 999999999);
        List<Object[]> results = enrollmentsRepository.findTopCoursesByRevenue(startDateTime, endDateTime);
        List<Map<String, Object>> topCourses = new ArrayList<>();
        double totalRevenue = getTotalRevenueBetween(start, end);

        if (results.isEmpty()) {
            Map<String, Object> defaultCourse = new HashMap<>();
            defaultCourse.put("rank", 1);
            defaultCourse.put("name", "Không có dữ liệu");
            defaultCourse.put("enrollments", 0L);
            defaultCourse.put("revenue", 0.0);
            defaultCourse.put("ratio", 0.0);
            defaultCourse.put("trend", "Thấp");
            topCourses.add(defaultCourse);
        } else {
            for (int i = 0; i < Math.min(5, results.size()); i++) {
                Object[] row = results.get(i);
                Course course = (Course) row[0];
                Double revenue = (Double) row[1];
                Long enrollments = (Long) row[2];

                Map<String, Object> courseData = new HashMap<>();
                courseData.put("rank", i + 1);
                courseData.put("name", course.getTen_khoa_hoc());
                courseData.put("enrollments", enrollments != null ? enrollments : 0L);
                courseData.put("revenue", revenue != null ? revenue : 0.0);
                courseData.put("ratio",
                        totalRevenue > 0 ? (revenue != null ? revenue : 0.0) / totalRevenue * 100 : 0.0);
                courseData.put("trend", revenue != null && revenue > 400_000_000 ? "Cao"
                        : revenue != null && revenue > 300_000_000 ? "Trung bình" : "Thấp");

                topCourses.add(courseData);
            }
        }
        return topCourses;
    }

    // Dữ liệu biểu đồ doanh thu theo khoảng thời gian
    public Map<String, Object> getRevenueChartData(String period) {
        LocalDate end = LocalDate.now();
        LocalDate start;
        switch (period) {
            case "7days":
                start = end.minusDays(6);
                break;
            case "30days":
                start = end.minusDays(29);
                break;
            case "3months":
                start = end.minusMonths(3);
                break;
            case "6months":
                start = end.minusMonths(6);
                break;
            case "1year":
            default:
                start = end.minusYears(1);
                break;
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59, 999999999);
        List<Object[]> revenueData = enrollmentsRepository.findRevenueByDate(startDateTime, endDateTime);
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        if (revenueData.isEmpty()) {
            labels.add("No data");
            data.add(0.0);
        } else {
            for (Object[] row : revenueData) {
                labels.add(row[0] != null ? row[0].toString() : "Unknown");
                data.add((Double) (row[1] != null ? row[1] : 0.0));
            }
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        return chartData;
    }
}