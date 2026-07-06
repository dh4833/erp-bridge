package com.erpbridge.service;

import com.erpbridge.dto.SalaryRequestDto;
import com.erpbridge.entity.Salary;
import com.erpbridge.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;

    /*
     * 4대보험 요율 (2024년 기준)
     * 국민연금  : 4.5%
     * 건강보험  : 3.545%
     * 장기요양  : 건강보험료의 12.95%
     * 고용보험  : 0.9%
     * 소득세    : 간이세액 약 3% (단순 계산용)
     */
    private static final double NATIONAL_PENSION  = 0.045;
    private static final double HEALTH_INSURANCE  = 0.03545;
    private static final double LONG_TERM_CARE    = 0.1295;
    private static final double EMPLOYMENT_INS    = 0.009;
    private static final double INCOME_TAX_RATE   = 0.03;
    private static final double LOCAL_TAX_RATE    = 0.1;

    /* 4대보험 + 세금 자동 계산 */
    public long calcInsurance(long baseSalary) {
        long pension    = Math.round(baseSalary * NATIONAL_PENSION);
        long health     = Math.round(baseSalary * HEALTH_INSURANCE);
        long longTerm   = Math.round(health * LONG_TERM_CARE);
        long employment = Math.round(baseSalary * EMPLOYMENT_INS);
        return pension + health + longTerm + employment;
    }

    public long calcTax(long baseSalary) {
        long incomeTax = Math.round(baseSalary * INCOME_TAX_RATE);
        long localTax  = Math.round(incomeTax * LOCAL_TAX_RATE);
        return incomeTax + localTax;
    }

    /* 급여 계산 및 저장 */
    public String calculateSalary(SalaryRequestDto dto) {

        // 중복 계산 방지
        if (salaryRepository.findByEmpIdAndYearMonth(
                dto.getEmpId(), dto.getYearMonth())
                .isPresent()) {
            return "DUPLICATE";
        }

        long baseSalary = dto.getBaseSalary();
        long bonus      = dto.getBonus() != null
                          ? dto.getBonus() : 0L;
        long insurance  = calcInsurance(baseSalary);
        long tax        = calcTax(baseSalary);
        long netSalary  = baseSalary + bonus - insurance - tax;

        Salary salary = Salary.builder()
                .empId(dto.getEmpId())
                .yearMonth(dto.getYearMonth())
                .baseSalary(baseSalary)
                .bonus(bonus)
                .insurance(insurance)
                .tax(tax)
                .netSalary(netSalary)
                .paidDate(LocalDate.now())
                .build();

        salaryRepository.save(salary);
        return "SUCCESS";
    }

    /* 내 급여 내역 조회 */
    public List<Salary> getMySalaries(String empId) {
        return salaryRepository
                .findByEmpIdOrderByYearMonthDesc(empId);
    }

    /* 특정 월 전체 급여 조회 (관리자) */
    public List<Salary> getSalariesByMonth(int yearMonth) {
        return salaryRepository.findByYearMonth(yearMonth);
    }

    /* 급여 삭제 */
    public String deleteSalary(Long salaryId) {
        if (!salaryRepository.existsById(salaryId)) {
            return "NOT_FOUND";
        }
        salaryRepository.deleteById(salaryId);
        return "SUCCESS";
    }
    /* 급여 단건 조회 */
    public Salary getSalaryById(Long salaryId) {
        return salaryRepository.findById(salaryId)
                .orElse(null);
    }

    /* emp_id 기준 급여 조회 */
    public List<Salary> getSalariesByEmpId(String empId) {
        return salaryRepository
                .findByEmpIdOrderByYearMonthDesc(empId);
    }
    /* 전체 급여 조회 (관리자용) */
    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }
}