package com.erpbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LEAVE_REQUEST")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "leave_id")
	private Long leaveId;

	@Column(name = "emp_id", nullable = false, length = 20)
	private String empId;

	@Column(name = "leave_type", nullable = false, length = 30)
	private String leaveType;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	@Column(name = "leave_days", nullable = false, precision = 4, scale = 1)
	private BigDecimal leaveDays;

	@Column(name = "reason", length = 500)
	private String reason;

	@Column(name = "status", nullable = false, length = 20)
	private String status;

	@Column(name = "approver_id", length = 20)
	private String approverId;

	@Column(name = "created_at")
	private LocalDateTime createdAt;
}