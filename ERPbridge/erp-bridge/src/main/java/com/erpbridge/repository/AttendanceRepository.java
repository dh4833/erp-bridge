package com.erpbridge.repository;

import com.erpbridge.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    // 특정 직원의 전체 근태 조회
    List<Attendance> findByEmpIdOrderByAttDateDesc(
            String empId);

    // 특정 직원의 특정 날짜 근태 조회
    Optional<Attendance> findByEmpIdAndAttDate(
            String empId, LocalDate attDate);

    // 특정 날짜 전체 직원 근태 조회
    List<Attendance> findByAttDate(LocalDate attDate);

    // 특정 직원의 특정 월 근태 조회
    List<Attendance> findByEmpIdAndAttDateBetween(
            String empId,
            LocalDate startDate,
            LocalDate endDate);
}