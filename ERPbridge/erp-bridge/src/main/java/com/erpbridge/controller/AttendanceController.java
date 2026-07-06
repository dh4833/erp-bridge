package com.erpbridge.controller;

import com.erpbridge.entity.Attendance;
import com.erpbridge.service.AttendanceService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AttendanceController {

	private final AttendanceService attendanceService;

	/* 근태 화면 */
	@GetMapping("/attendance")
	public String attendancePage(HttpSession session, Model model) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/erp-bridge-auth.html";
		}

		// 오늘 근태 정보
		Attendance today = attendanceService.getTodayAttendance(loginUser);

		// 이번달 근태 내역
		LocalDate now = LocalDate.now();
		List<Attendance> monthly = attendanceService.getMonthlyAttendance(loginUser, now.getYear(),
				now.getMonthValue());

		model.addAttribute("loginId", loginUser);
		model.addAttribute("role", session.getAttribute("userRole"));
		model.addAttribute("todayAtt", today);
		model.addAttribute("monthlyAtt", monthly);
		model.addAttribute("today", now.toString());

		return "attendance";
	}

	/* 출근 API */
	@PostMapping("/api/attendance/checkin")
	@ResponseBody
	public ResponseEntity<String> checkIn(HttpSession session) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}
		String result = attendanceService.checkIn(loginUser);

		return switch (result) {
		case "SUCCESS" -> ResponseEntity.ok("출근이 등록되었습니다.");
		case "ALREADY_CHECKED_IN" -> ResponseEntity.badRequest().body("이미 출근 처리되었습니다.");
		default -> ResponseEntity.internalServerError().body("오류가 발생했습니다.");
		};
	}

	/* 퇴근 API */
	@PostMapping("/api/attendance/checkout")
	@ResponseBody
	public ResponseEntity<String> checkOut(HttpSession session) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}
		String result = attendanceService.checkOut(loginUser);

		return switch (result) {
		case "SUCCESS" -> ResponseEntity.ok("퇴근이 등록되었습니다.");
		case "NOT_CHECKED_IN" -> ResponseEntity.badRequest().body("출근 기록이 없습니다.");
		case "ALREADY_CHECKED_OUT" -> ResponseEntity.badRequest().body("이미 퇴근 처리되었습니다.");
		default -> ResponseEntity.internalServerError().body("오류가 발생했습니다.");
		};
	}

	/* 내 근태 내역 API */
	@GetMapping("/api/attendance/my")
	@ResponseBody
	public ResponseEntity<List<Attendance>> getMyAttendance(HttpSession session) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return ResponseEntity.status(401).build();
		}
		return ResponseEntity.ok(attendanceService.getMyAttendance(loginUser));
	}
}