package com.erpbridge.controller;

import com.erpbridge.dto.SignupRequestDto;
import com.erpbridge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erpbridge.dto.LoginRequestDto;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {

		String result = authService.signup(dto);

		if ("DUPLICATE_ID".equals(result)) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 아이디입니다.");
		}
		return ResponseEntity.ok("SUCCESS");
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequestDto dto, HttpSession session) {

		String result = authService.login(dto, session);

		if ("SUCCESS".equals(result)) {
			return ResponseEntity.ok("SUCCESS");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 올바르지 않습니다.");
	}

	@GetMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok("LOGOUT");
	}
}