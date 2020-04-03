package com.example.jwt.service.impl;

import com.example.jwt.dao.EmployeeDao;
import com.example.jwt.dto.EmployeeDto;
import com.example.jwt.entity.Employee;
import com.example.jwt.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public Iterable<Employee> getAllEmployees() {
        return employeeDao.findAll();
    }

    @Override
    public Employee addEmployee(EmployeeDto employeeDto, String createdBy) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDto, employee);
        employee.setCreatedBy(createdBy);
        return employeeDao.save(employee);
    }

    @Override
    public void updateEmployee(EmployeeDto employeeDto, Integer id) {
        Employee updateEmployee = employeeDao.findById(id).orElse(null);
        BeanUtils.copyProperties(employeeDto, updateEmployee);
        employeeDao.save(updateEmployee);
    }

    @Override
    public void deleteEmployee(Integer id) {
        Employee deleteEmployee = employeeDao.findById(id).orElse(null);
        employeeDao.delete(deleteEmployee);
    }
}
