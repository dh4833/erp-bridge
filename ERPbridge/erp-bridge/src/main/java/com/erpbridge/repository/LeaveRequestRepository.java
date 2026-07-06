package com.erpbridge.repository;

import com.erpbridge.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository
        extends JpaRepository<LeaveRequest, Long> {

    // 특정 직원의 전체 신청 내역 (최신순)
    List<LeaveRequest> findByEmpIdOrderByCreatedAtDesc(
            String empId);

    // 상태별 조회 (관리자용)
    List<LeaveRequest> findByStatusOrderByCreatedAtDesc(
            String status);

    // 특정 직원의 상태별 조회
    List<LeaveRequest> findByEmpIdAndStatus(
            String empId, String status);
}