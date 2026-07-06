package com.erpbridge.controller;

import com.erpbridge.entity.Attendance;
import com.erpbridge.service.AttendanceService;
import com.erpbridge.service.EmployeeService;
import com.erpbridge.service.LeaveRequestService;
import com.erpbridge.service.SalaryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final EmployeeService     employeeService;
    private final AttendanceService   attendanceService;
    private final LeaveRequestService leaveRequestService;
    private final SalaryService       salaryService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session,
                            Model model) {

        String loginUser =
            (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/erp-bridge-auth.html";
        }

        // ── KPI 1: 재직 직원 수 ──
        long totalEmp = employeeService
                .countActiveEmployees();

        // ── KPI 2: 금일 출근 인원 ──
        List<Attendance> todayAtts =
                attendanceService.getTodayAllAttendance();
        long todayCheckIn = todayAtts.stream()
                .filter(a -> a.getCheckIn() != null)
                .count();

        // ── KPI 3: 대기 중인 휴가 신청 수 ──
        long pendingLeave = leaveRequestService
                .getPendingLeaves().size();

        // ── KPI 4: 이번달 급여 총액 ──
        LocalDate now = LocalDate.now();
        int yearMonth = Integer.parseInt(
                now.format(DateTimeFormatter.ofPattern("yyyyMM")));
        long totalSalary = salaryService
                .getSalariesByMonth(yearMonth)
                .stream()
                .mapToLong(s -> s.getNetSalary())
                .sum();

        // ── 모델에 전달 ──
        model.addAttribute("loginId", loginUser);
        model.addAttribute("role",
                session.getAttribute("userRole"));
        model.addAttribute("totalEmp",     totalEmp);
        model.addAttribute("todayCheckIn", todayCheckIn);
        model.addAttribute("pendingLeave", pendingLeave);
        model.addAttribute("totalSalary",  totalSalary);
        model.addAttribute("currentMonth",
                now.getMonthValue());

        return "dashboard";
    }
}