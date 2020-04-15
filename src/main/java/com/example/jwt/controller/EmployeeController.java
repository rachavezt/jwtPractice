package com.example.jwt.controller;

import com.example.jwt.config.security.AuthenticatedUser;
import com.example.jwt.config.security.annotation.CurrentUser;
import com.example.jwt.dto.EmployeeDto;
import com.example.jwt.entity.Employee;
import com.example.jwt.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping(value = "/employee", headers = "Authorization")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PreAuthorize("hasAuthority('USER')")
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public EmployeeDto add(@CurrentUser AuthenticatedUser authenticatedUser, @RequestBody EmployeeDto employeeDto){
        Employee employee = employeeService.addEmployee(employeeDto, authenticatedUser.getUsername());
        BeanUtils.copyProperties(employee, employeeDto);
        return employeeDto;
    }

    @Secured("ROLE_COMMON")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT, produces = "application/json")
    public EmployeeDto update(@PathVariable("id") Integer id, @RequestBody EmployeeDto employeeDto){
        Employee employee = employeeService.updateEmployee(employeeDto, id);
        BeanUtils.copyProperties(employee, employeeDto);
        return employeeDto;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id) {
        employeeService.deleteEmployee(id);
    }
}
