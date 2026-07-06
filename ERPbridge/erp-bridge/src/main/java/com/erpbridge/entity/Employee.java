package com.erpbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "EMPLOYEE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

	@Id
	@Column(name = "emp_id", length = 20)
	private String empId;

	@Column(name = "emp_name", nullable = false, length = 50)
	private String empName;

	@Column(name = "emp_email", nullable = false, length = 100)
	private String empEmail;

	@Column(name = "emp_phone", length = 20)
	private String empPhone;

	@Column(name = "emp_address", length = 200)
	private String empAddress;

	@Column(name = "dept_id")
	private Long deptId;

	@Column(name = "position", nullable = false, length = 50)
	private String position;

	@Column(name = "role", nullable = false, length = 20)
	private String role;

	@Column(name = "hire_date", nullable = false)
	private LocalDate hireDate;

	@Column(name = "status", nullable = false, length = 10)
	private String status;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}