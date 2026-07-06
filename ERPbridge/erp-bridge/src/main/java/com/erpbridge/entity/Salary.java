package com.erpbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "SALARY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salary_id")
	private Long salaryId;

	@Column(name = "emp_id", nullable = false, length = 20)
	private String empId;

	@Column(name = "`year_month`", nullable = false)
	private Integer yearMonth;

	@Column(name = "base_salary", nullable = false)
	private Long baseSalary;

	@Column(name = "bonus")
	private Long bonus;

	@Column(name = "tax", nullable = false)
	private Long tax;

	@Column(name = "insurance", nullable = false)
	private Long insurance;

	@Column(name = "net_salary", nullable = false)
	private Long netSalary;

	@Column(name = "paid_date")
	private LocalDate paidDate;
}