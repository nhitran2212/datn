package com.poly.controller;

import com.poly.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticRevenueController {

    @Autowired
    private RevenueService revenueService;

    @GetMapping("/statistic-revenue")
    public String statisticRevenue(
            @RequestParam(defaultValue = "7days") String period,
            Model model) {

        LocalDate end = LocalDate.now();
        LocalDate start;

        switch (period) {
            case "7days":
                start = end.minusDays(6); // 7 ngày: hôm nay và 6 ngày trước
                break;
            case "30days":
                start = end.minusDays(29); // 30 ngày: hôm nay và 29 ngày trước
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

        // Tổng doanh thu theo filter
        Double totalRevenue = revenueService.getTotalRevenueBetween(start, end);
        // Tổng số đơn hàng theo filter
        Long totalOrders = revenueService.getTotalEnrollmentsBetween(start, end);
        // Doanh thu trung bình/đơn theo filter
        Double avgRevenue = (totalOrders != null && totalOrders > 0) ? totalRevenue / totalOrders : 0.0;
        // Tăng trưởng doanh thu theo filter
        Double growth = revenueService.getRevenueGrowth(period);

        // Top 5 khóa học doanh thu cao nhất theo filter
        List<Map<String, Object>> topCourses = revenueService.getTopCoursesBetween(start, end);

        // Dữ liệu biểu đồ doanh thu
        Map<String, Object> chartData = revenueService.getRevenueChartData(period);

        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        model.addAttribute("totalOrders", totalOrders != null ? totalOrders : 0L);
        model.addAttribute("growth", growth != null ? growth : 0.0);
        model.addAttribute("avgRevenue", avgRevenue != null ? avgRevenue : 0.0);
        model.addAttribute("topCourses", topCourses);
        model.addAttribute("chartLabels", chartData.get("labels"));
        model.addAttribute("chartData", chartData.get("data"));
        model.addAttribute("period", period);

        return "Statistic_Revenue";
    }
}