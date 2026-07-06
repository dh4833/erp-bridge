package com.erpbridge.service;

import com.erpbridge.dto.SignupRequestDto;
import com.erpbridge.entity.UserAuth;
import com.erpbridge.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.erpbridge.dto.LoginRequestDto;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserAuthRepository userAuthRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public String signup(SignupRequestDto dto) {

		if (userAuthRepository.existsByLoginId(dto.getLoginId())) {
			return "DUPLICATE_ID";
		}

		long count = userAuthRepository.count();
		String empId = String.format("EMP%03d", count + 1);

		UserAuth user = UserAuth.builder().empId(empId).loginId(dto.getLoginId())
				.password(passwordEncoder.encode(dto.getPassword())).role("USER").build();

		userAuthRepository.save(user);
		return "" + "SUCCESS";
	}

	public String login(LoginRequestDto dto, HttpSession session) {

		UserAuth user = userAuthRepository.findByLoginId(dto.getLoginId()).orElse(null);

// 아이디 없음
		if (user == null)
			return "FAIL";

// 비밀번호 불일치
		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			return "FAIL";
		}

// 세션에 로그인 정보 저장
		session.setAttribute("loginUser", user.getLoginId());
		session.setAttribute("userRole", user.getRole());

// 마지막 로그인 날짜 갱신
		user.setLastLogin(LocalDate.now());
		userAuthRepository.save(user);

		return "SUCCESS";
	}
}