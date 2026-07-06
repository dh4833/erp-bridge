package com.erpbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ATTENDANCE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "att_id")
	private Long attId;

	@Column(name = "emp_id", nullable = false, length = 20)
	private String empId;

	@Column(name = "att_date", nullable = false)
	private LocalDate attDate;

	@Column(name = "check_in")
	private LocalDateTime checkIn;

	@Column(name = "check_out")
	private LocalDateTime checkOut;

	@Column(name = "att_type", nullable = false, length = 20)
	private String attType;

	@Column(name = "status", nullable = false, length = 20)
	private String status;
}