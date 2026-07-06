package com.erpbridge.controller;

import com.erpbridge.dto.SalaryRequestDto;
import com.erpbridge.entity.Employee;
import com.erpbridge.entity.Salary;
import com.erpbridge.service.EmployeeService;
import com.erpbridge.service.SalaryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SalaryController {

	private final SalaryService salaryService;
	private final EmployeeService employeeService;

	/* 급여 화면 */
	@GetMapping("/salary")
	public String salaryPage(HttpSession session, Model model) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/erp-bridge-auth.html";
		}

		// 내 급여 내역
		List<Salary> mySalaries = salaryService.getMySalaries(loginUser);

		// 전체 직원 목록 (관리자 급여 계산용)
		List<Employee> employees = employeeService.getActiveEmployees();

		model.addAttribute("loginId", loginUser);
		model.addAttribute("role", session.getAttribute("userRole"));
		model.addAttribute("mySalaries", mySalaries);
		model.addAttribute("employees", employees);

		return "salary";
	}

	/* 급여 계산 API (관리자) */
	@PostMapping("/api/salary/calculate")
	@ResponseBody
	public ResponseEntity<String> calculateSalary(@RequestBody SalaryRequestDto dto, HttpSession session) {

		if (session.getAttribute("loginUser") == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}

		String result = salaryService.calculateSalary(dto);

		return switch (result) {
		case "SUCCESS" -> ResponseEntity.ok("급여가 계산되었습니다.");
		case "DUPLICATE" -> ResponseEntity.badRequest().body("해당 월 급여가 이미 계산되었습니다.");
		default -> ResponseEntity.internalServerError().body("오류가 발생했습니다.");
		};
	}

	/* 내 급여 내역 API */
	@GetMapping("/api/salary/my")
	@ResponseBody
	public ResponseEntity<List<Salary>> getMySalaries(HttpSession session) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return ResponseEntity.status(401).build();
		}
		return ResponseEntity.ok(salaryService.getMySalaries(loginUser));
	}

	/* 급여 삭제 API */
	@DeleteMapping("/api/salary/{salaryId}")
	@ResponseBody
	public ResponseEntity<String> deleteSalary(@PathVariable Long salaryId, HttpSession session) {

		if (session.getAttribute("loginUser") == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}

		String result = salaryService.deleteSalary(salaryId);
		return "SUCCESS".equals(result) ? ResponseEntity.ok("삭제되었습니다.")
				: ResponseEntity.badRequest().body("급여 내역을 찾을 수 없습니다.");
	}

	/* 급여명세서 화면 */
	@GetMapping("/salary/detail/{salaryId}")
	public String salaryDetail(@PathVariable Long salaryId, HttpSession session, Model model) {

		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/erp-bridge-auth.html";
		}

		Salary salary = salaryService.getSalaryById(salaryId);

		if (salary == null) {
			return "redirect:/salary";
		}

		model.addAttribute("loginId", loginUser);
		model.addAttribute("role", session.getAttribute("userRole"));
		model.addAttribute("salary", salary);

		return "salary-detail";
	}

	/* 급여명세서 목록 화면 */
	@GetMapping("/salary/list")
	public String salaryList(HttpSession session, Model model) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/erp-bridge-auth.html";
		}

		String role = (String) session.getAttribute("userRole");
		List<Salary> mySalaries;

		// ADMIN이면 전체 급여 조회, 일반 직원이면 본인 emp_id로 조회
		if ("ADMIN".equals(role)) {
			mySalaries = salaryService.getAllSalaries();
		} else {
			mySalaries = salaryService.getMySalaries(loginUser);
		}

		model.addAttribute("loginId", loginUser);
		model.addAttribute("role", role);
		model.addAttribute("mySalaries", mySalaries);

		return "salary-list";
	}
}
