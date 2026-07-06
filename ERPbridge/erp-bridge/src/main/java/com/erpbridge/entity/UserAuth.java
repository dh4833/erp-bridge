package com.erpbridge.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "USER_AUTH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_id")
	private Long authId;

	@Column(name = "emp_id", unique = true, nullable = false, length = 20)
	private String empId;

	@Column(name = "login_id", unique = true, nullable = false, length = 50)
	private String loginId;

	@Column(nullable = false, length = 200)
	private String password;

	@Column(nullable = false, length = 20)
	private String role;

	@Column(name = "last_login")
	private LocalDate lastLogin;
}