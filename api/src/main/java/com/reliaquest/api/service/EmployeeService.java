package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import java.util.List;

import com.reliaquest.api.request.EmployeeInput;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService {


     List<Employee> getAllEmployees();

    List<Employee> findByNameContaining(String searchString);

    Employee getEmployeeById(String id);

    Integer gethighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(EmployeeInput employeeInput);

    String deleteEmployeeById(String id);
}
