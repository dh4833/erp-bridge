package com.erpbridge.service;

import com.erpbridge.dto.LeaveRequestDto;
import com.erpbridge.entity.LeaveRequest;
import com.erpbridge.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository
            leaveRequestRepository;

    /* 휴가 신청 */
    public String createLeave(String empId,
                               LeaveRequestDto dto) {

        // 날짜 유효성 검사
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            return "INVALID_DATE";
        }

        // 휴가 일수 계산 (주말 제외하지 않는 단순 계산)
        long days = dto.getEndDate().toEpochDay()
                  - dto.getStartDate().toEpochDay() + 1;

        LeaveRequest leave = LeaveRequest.builder()
                .empId(empId)
                .leaveType(dto.getLeaveType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .leaveDays(BigDecimal.valueOf(days))
                .reason(dto.getReason())
                .status("대기")
                .createdAt(LocalDateTime.now())
                .build();

        leaveRequestRepository.save(leave);
        return "SUCCESS";
    }

    /* 내 휴가 신청 내역 조회 */
    public List<LeaveRequest> getMyLeaves(String empId) {
        return leaveRequestRepository
                .findByEmpIdOrderByCreatedAtDesc(empId);
    }

    /* 전체 대기 중인 신청 조회 (관리자용) */
    public List<LeaveRequest> getPendingLeaves() {
        return leaveRequestRepository
                .findByStatusOrderByCreatedAtDesc("대기");
    }

    /* 휴가 취소 (본인만 가능, 대기 상태만) */
    public String cancelLeave(Long leaveId, String empId) {
        LeaveRequest leave = leaveRequestRepository
                .findById(leaveId).orElse(null);

        if (leave == null) return "NOT_FOUND";
        if (!leave.getEmpId().equals(empId))
            return "UNAUTHORIZED";
        if (!"대기".equals(leave.getStatus()))
            return "CANNOT_CANCEL";

        leave.setStatus("취소");
        leaveRequestRepository.save(leave);
        return "SUCCESS";
    }

    /* 승인 처리 (관리자) */
    public String approveLeave(Long leaveId,
                                String approverId) {
        LeaveRequest leave = leaveRequestRepository
                .findById(leaveId).orElse(null);

        if (leave == null) return "NOT_FOUND";

        leave.setStatus("승인");
        leave.setApproverId(approverId);
        leaveRequestRepository.save(leave);
        return "SUCCESS";
    }

    /* 반려 처리 (관리자) */
    public String rejectLeave(Long leaveId,
                               String approverId) {
        LeaveRequest leave = leaveRequestRepository
                .findById(leaveId).orElse(null);

        if (leave == null) return "NOT_FOUND";

        leave.setStatus("반려");
        leave.setApproverId(approverId);
        leaveRequestRepository.save(leave);
        return "SUCCESS";
    }
}