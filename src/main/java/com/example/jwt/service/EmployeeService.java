package com.example.jwt.service;

import com.example.jwt.dto.EmployeeDto;
import com.example.jwt.entity.Employee;

public interface EmployeeService {
    Iterable<Employee> getAllEmployees();
    Employee addEmployee(EmployeeDto employeeDto);
}
