package com.example.jwt.controller;

import com.example.jwt.dto.EmployeeDto;
import com.example.jwt.entity.Employee;
import com.example.jwt.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    public List<EmployeeDto> getAll(){
        List<EmployeeDto> employeeDtoList = new LinkedList<>();
        employeeService.getAllEmployees().forEach(employee -> {
            EmployeeDto employeeDto = new EmployeeDto();
            BeanUtils.copyProperties(employee, employeeDto);
            employeeDtoList.add(employeeDto);
        });
        return employeeDtoList;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public EmployeeDto add(@RequestBody EmployeeDto employeeDto){
        Employee employee = employeeService.addEmployee(employeeDto);
        BeanUtils.copyProperties(employee, employeeDto);
        return employeeDto;
    }
}
