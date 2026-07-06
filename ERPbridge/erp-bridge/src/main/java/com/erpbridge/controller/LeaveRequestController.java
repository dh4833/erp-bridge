package com.erpbridge.controller;

import com.erpbridge.dto.LeaveRequestDto;
import com.erpbridge.entity.LeaveRequest;
import com.erpbridge.service.LeaveRequestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LeaveRequestController {

	private final LeaveRequestService leaveRequestService;

	/* 연차·휴가 화면 */
	@GetMapping("/leave")
	public String leavePage(HttpSession session, Model model) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return "redirect:/erp-bridge-auth.html";
		}

		String role = (String) session.getAttribute("userRole");

		List<LeaveRequest> myLeaves = leaveRequestService.getMyLeaves(loginUser);

		long pendingCount = myLeaves.stream().filter(l -> "대기".equals(l.getStatus())).count();

		model.addAttribute("loginId", loginUser);
		model.addAttribute("role", role);
		model.addAttribute("myLeaves", myLeaves);
		model.addAttribute("pendingCount", pendingCount);

		// 관리자면 전체 대기 목록도 전달
		if ("ADMIN".equals(role)) {
			List<LeaveRequest> pendingLeaves = leaveRequestService.getPendingLeaves();
			model.addAttribute("pendingLeaves", pendingLeaves);
		}

		return "leave";
	}

	/* 휴가 신청 API */
	@PostMapping("/api/leave")
	@ResponseBody
	public ResponseEntity<String> createLeave(@RequestBody LeaveRequestDto dto, HttpSession session) {

		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}

		String result = leaveRequestService.createLeave(loginUser, dto);

		return switch (result) {
		case "SUCCESS" -> ResponseEntity.ok("휴가 신청이 완료되었습니다.");
		case "INVALID_DATE" -> ResponseEntity.badRequest().body("시작일이 종료일보다 늦을 수 없습니다.");
		default -> ResponseEntity.internalServerError().body("오류가 발생했습니다.");
		};
	}

	/* 내 휴가 내역 API */
	@GetMapping("/api/leave/my")
	@ResponseBody
	public ResponseEntity<List<LeaveRequest>> getMyLeaves(HttpSession session) {
		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return ResponseEntity.status(401).build();
		}
		return ResponseEntity.ok(leaveRequestService.getMyLeaves(loginUser));
	}

	/* 휴가 취소 API */
	@DeleteMapping("/api/leave/{leaveId}")
	@ResponseBody
	public ResponseEntity<String> cancelLeave(@PathVariable Long leaveId, HttpSession session) {

		String loginUser = (String) session.getAttribute("loginUser");
		if (loginUser == null) {
			return ResponseEntity.status(401).body("로그인이 필요합니다.");
		}

		String result = leaveRequestService.cancelLeave(leaveId, loginUser);

		return switch (result) {
		case "SUCCESS" -> ResponseEntity.ok("취소되었습니다.");
		case "NOT_FOUND" -> ResponseEntity.badRequest().body("신청 내역을 찾을 수 없습니다.");
		case "UNAUTHORIZED" -> ResponseEntity.status(403).body("본인 신청만 취소할 수 있습니다.");
		case "CANNOT_CANCEL" -> ResponseEntity.badRequest().body("대기 상태만 취소할 수 있습니다.");
		default -> ResponseEntity.internalServerError().body("오류가 발생했습니다.");
		};
	}

	/* 승인 API (관리자) */
	@PutMapping("/api/leave/{leaveId}/approve")
	@ResponseBody
	public ResponseEntity<String> approveLeave(@PathVariable Long leaveId, HttpSession session) {

		String loginUser = (String) session.getAttribute("loginUser");
		String result = leaveRequestService.approveLeave(leaveId, loginUser);

		return "SUCCESS".equals(result) ? ResponseEntity.ok("승인되었습니다.")
				: ResponseEntity.badRequest().body("처리 중 오류가 발생했습니다.");
	}

	/* 반려 API (관리자) */
	@PutMapping("/api/leave/{leaveId}/reject")
	@ResponseBody
	public ResponseEntity<String> rejectLeave(@PathVariable Long leaveId, HttpSession session) {

		String loginUser = (String) session.getAttribute("loginUser");
		String result = leaveRequestService.rejectLeave(leaveId, loginUser);

		return "SUCCESS".equals(result) ? ResponseEntity.ok("반려되었습니다.")
				: ResponseEntity.badRequest().body("처리 중 오류가 발생했습니다.");
	}
}