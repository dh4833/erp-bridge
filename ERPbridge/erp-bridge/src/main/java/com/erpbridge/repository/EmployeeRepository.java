package com.erpbridge.repository;

import com.erpbridge.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeRepository
        extends JpaRepository<Employee, String> {

    List<Employee> findByStatus(String status);
    List<Employee> findByDeptId(Long deptId);
}