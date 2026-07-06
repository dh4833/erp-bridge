package com.erpbridge.repository;

import com.erpbridge.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SalaryRepository
        extends JpaRepository<Salary, Long> {

    // 특정 직원의 전체 급여 내역 (최신순)
    List<Salary> findByEmpIdOrderByYearMonthDesc(
            String empId);

    // 특정 직원의 특정 월 급여
    Optional<Salary> findByEmpIdAndYearMonth(
            String empId, Integer yearMonth);

    // 특정 월 전체 급여 내역
    List<Salary> findByYearMonth(Integer yearMonth);
}