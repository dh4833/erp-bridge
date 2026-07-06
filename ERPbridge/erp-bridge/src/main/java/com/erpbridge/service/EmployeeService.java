package com.erpbridge.service;

import com.erpbridge.dto.EmployeeRequestDto;
import com.erpbridge.entity.Employee;
import com.erpbridge.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	/* 전체 직원 목록 조회 */
	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	/* 재직 중인 직원만 조회 */
	public List<Employee> getActiveEmployees() {
		return employeeRepository.findByStatus("재직");
	}

	/* 직원 단건 조회 */
	public Employee getEmployee(String empId) {
		return employeeRepository.findById(empId).orElse(null);
	}

	/* 직원 등록 */
	public String createEmployee(EmployeeRequestDto dto) {

		// 사번 자동 생성 (중복 방지)
		String empId;
		long count = employeeRepository.count();
		do {
			count++;
			empId = String.format("EMP%03d", count);
		} while (employeeRepository.existsById(empId));

		Employee employee = Employee.builder().empId(empId).empName(dto.getEmpName()).empEmail(dto.getEmpEmail())
				.empPhone(dto.getEmpPhone()).empAddress(dto.getEmpAddress()).deptId(dto.getDeptId())
				.position(dto.getPosition()).role(dto.getRole() != null ? dto.getRole() : "USER")
				.hireDate(dto.getHireDate()).status("재직").createdAt(LocalDateTime.now()).build();

		employeeRepository.save(employee);
		return empId;
	}

	/* 직원 수정 */
	public String updateEmployee(String empId, EmployeeRequestDto dto) {
		Employee employee = employeeRepository.findById(empId).orElse(null);
		if (employee == null)
			return "NOT_FOUND";

		employee.setEmpName(dto.getEmpName());
		employee.setEmpEmail(dto.getEmpEmail());
		employee.setEmpPhone(dto.getEmpPhone());
		employee.setEmpAddress(dto.getEmpAddress());
		employee.setDeptId(dto.getDeptId());
		employee.setPosition(dto.getPosition());
		employee.setHireDate(dto.getHireDate());
		/* 상태값 수정 추가 */
		if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
			employee.setStatus(dto.getStatus());
		}
		employeeRepository.save(employee);
		return "SUCCESS";
	}

	/* 직원 삭제 (퇴직 처리) */
	public String deleteEmployee(String empId) {
		Employee employee = employeeRepository.findById(empId).orElse(null);
		if (employee == null)
			return "NOT_FOUND";

		employee.setStatus("퇴직");
		employeeRepository.save(employee);
		return "SUCCESS";
	}

	/* 재직 직원 수 */
	public long countActiveEmployees() {
		return employeeRepository.findByStatus("재직").size();
	}

	/* 직원 저장 (수정용) */
	public void saveEmployee(Employee employee) {
		employeeRepository.save(employee);
	}
}