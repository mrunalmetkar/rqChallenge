package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.parser.Entity;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController implements IEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Override
    @GetMapping()
    public ResponseEntity<List> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List> getEmployeesByNameSearch(String searchString) {
        List<Employee> employees = employeeService.findByNameContaining(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary=employeeService.gethighestSalaryOfEmployees();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> empList=employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok(empList);
    }

    @Override
    public ResponseEntity createEmployee(EmployeeInput employeeInput) {
        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.ok(createdEmployee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        String deletedEmployeeName = employeeService.deleteEmployeeById(id);
        if (deletedEmployeeName == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee not found for id: " + id);
        }
        return ResponseEntity.ok(deletedEmployeeName);
    }
}
