package com.erpbridge.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryRequestDto {
    private String empId;
    private Integer yearMonth;
    private Long baseSalary;
    private Long bonus;
}