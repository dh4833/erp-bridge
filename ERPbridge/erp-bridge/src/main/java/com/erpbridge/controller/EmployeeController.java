package com.erpbridge.controller;

import com.erpbridge.dto.EmployeeRequestDto;
import com.erpbridge.entity.Employee;
import com.erpbridge.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;

	/* 직원 목록 화면 */
	@GetMapping("/employees")
	public String employeeList(HttpSession session, Model model) {
		if (session.getAttribute("loginUser") == null) {
			return "redirect:/erp-bridge-auth.html";
		}
		List<Employee> employees = employeeService.getAllEmployees();
		model.addAttribute("employees", employees);
		model.addAttribute("loginId", session.getAttribute("loginUser"));
		model.addAttribute("role", session.getAttribute("userRole"));
		return "employee-list";
	}

	/* 직원 전체 목록 API */
	@GetMapping("/api/employees")
	@ResponseBody
	public ResponseEntity<List<Employee>> getEmployees() {
		return ResponseEntity.ok(employeeService.getAllEmployees());
	}

	/* 직원 등록 API */
	@PostMapping("/api/employees")
	@ResponseBody
	public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequestDto dto) {
		String empId = employeeService.createEmployee(dto);
		return ResponseEntity.ok(empId);
	}

	/* 직원 수정 API */
	@PutMapping("/api/employees/{empId}")
	@ResponseBody
	public ResponseEntity<String> updateEmployee(@PathVariable String empId, @RequestBody EmployeeRequestDto dto) {
		String result = employeeService.updateEmployee(empId, dto);
		if ("NOT_FOUND".equals(result)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("직원을 찾을 수 없습니다.");
		}
		return ResponseEntity.ok("SUCCESS");
	}

	/* 직원 삭제 API (퇴직 처리) */
	@DeleteMapping("/api/employees/{empId}")
	@ResponseBody
	public ResponseEntity<String> deleteEmployee(@PathVariable String empId) {
		String result = employeeService.deleteEmployee(empId);
		if ("NOT_FOUND".equals(result)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("직원을 찾을 수 없습니다.");
		}
		return ResponseEntity.ok("SUCCESS");
	}

	/* 부서 변경 API */
	@PutMapping("/api/employees/{empId}/dept")
	@ResponseBody
	public ResponseEntity<String> updateDept(@PathVariable String empId,
			@RequestBody java.util.Map<String, Object> body, HttpSession session) {

		if (session.getAttribute("loginUser") == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}

		Employee employee = employeeService.getEmployee(empId);
		if (employee == null) {
			return ResponseEntity.badRequest().body("직원을 찾을 수 없습니다.");
		}

		Object deptIdObj = body.get("deptId");
		if (deptIdObj != null && !deptIdObj.toString().isEmpty()) {
			employee.setDeptId(Long.parseLong(deptIdObj.toString()));
		} else {
			employee.setDeptId(null);
		}

		Object positionObj = body.get("position");
		if (positionObj != null && !positionObj.toString().isEmpty()) {
			employee.setPosition(positionObj.toString());
		}

		employeeService.saveEmployee(employee);
		return ResponseEntity.ok("SUCCESS");
	}

	@GetMapping("/api/employees/{empId}")
	@ResponseBody
	public ResponseEntity<Employee> getEmployee(@PathVariable String empId) {
		Employee emp = employeeService.getEmployee(empId);
		if (emp == null) {
		 	return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(emp);
	}
}