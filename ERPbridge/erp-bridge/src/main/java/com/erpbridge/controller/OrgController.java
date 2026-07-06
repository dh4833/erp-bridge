package com.erpbridge.controller;

import com.erpbridge.entity.Employee;
import com.erpbridge.service.EmployeeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrgController {

    private final EmployeeService employeeService;

    @GetMapping("/org")
    public String orgPage(HttpSession session,
                          Model model) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/erp-bridge-auth.html";
        }

        List<Employee> employees =
                employeeService.getActiveEmployees();

        model.addAttribute("employees", employees);
        model.addAttribute("loginId",
                session.getAttribute("loginUser"));
        model.addAttribute("role",
                session.getAttribute("userRole"));

        return "org";
    }
}