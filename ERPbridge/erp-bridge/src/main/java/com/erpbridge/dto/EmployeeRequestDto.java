package com.erpbridge.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeRequestDto {
    private String empName;
    private String empEmail;
    private String empPhone;
    private String empAddress;
    private Long deptId;
    private String position;
    private String role;
    private LocalDate hireDate;
    private String status;
}