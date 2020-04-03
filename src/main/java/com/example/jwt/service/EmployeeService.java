package com.example.jwt.service;

import com.example.jwt.dto.EmployeeDto;
import com.example.jwt.entity.Employee;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.security.RolesAllowed;

public interface EmployeeService {
    Iterable<Employee> getAllEmployees();
    Employee addEmployee(EmployeeDto employeeDto, String createdBy);

    @Secured("ROLE_ADMIN")
    void updateEmployee(EmployeeDto employeeDto, Integer id);

    @RolesAllowed("ROLE_ADMIN")
    void deleteEmployee(Integer id);
}
