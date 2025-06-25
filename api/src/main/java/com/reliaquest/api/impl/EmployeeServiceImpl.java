package com.reliaquest.api.impl;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeInput;
import com.reliaquest.api.response.EmployeeResponse;
import com.reliaquest.api.service.EmployeeService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Value("${mock.api.url}")
    String mockApiUrl;

    private final RestTemplate restTemplate;

    List<Employee> allEmployees = new ArrayList<>();


    @Autowired
    public EmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @PostConstruct
    public void init() {
        this.allEmployees = getAllEmployees();
    }
    @Override
    public List<Employee> getAllEmployees() {
        logger.info("EmployeeServiceImpl :: start :: Fetching all employees from mock API ");
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<EmployeeResponse> responseEntity = restTemplate.exchange(
                    mockApiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<EmployeeResponse>() {}
            );
            EmployeeResponse response = responseEntity.getBody();
            List<Employee> employees = response != null ? response.getData() : List.of();
            logger.info("Checking employee :: {}", employees);
            logger.info("EmployeeServiceImpl :: end :: Fetched all employees from mock API");
            return employees;
        } catch (Exception e) {
            logger.error("Error fetching all employees: {}", e.getMessage(), e);
            throw new EmployeeNotFoundException("Failed to fetch all employees: " + e.getMessage());
        }
    }

    @Override
    public List<Employee> findByNameContaining(String searchString) {
        logger.info("EmployeeServiceImpl :: start :: Searching employees by name containing: {}", searchString);
        try {
            if (searchString == null || searchString.isBlank()) {
                return allEmployees;
            }
            String normalizedSearch = searchString.replaceAll("\\s+", "").toLowerCase();

            List<Employee> result = allEmployees.stream()
                    .filter(e -> {
                        String name = e.getName();
                        if (name == null) return false;
                        String normalizedName = name.replaceAll("\\s+", "").toLowerCase();
                        return normalizedName.contains(normalizedSearch);
                    })
                    .toList();

            logger.info("EmployeeServiceImpl :: end :: Found {} employees matching search criteria", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error searching employees by name: {}", e.getMessage(), e);
            throw new EmployeeNotFoundException("Failed to search employees by name: " + e.getMessage());
        }
    }
    @Override
    public Employee getEmployeeById(String id) {
        logger.info("EmployeeServiceImpl :: start :: Fetching employee by ID: {}", id);
        try {
            if (id == null || id.isBlank()) {
                throw new EmployeeNotFoundException("Invalid employee ID: " + id);
            }
            UUID uuid = UUID.fromString(id.trim());
            Employee emp = allEmployees.stream()
                    .filter(e -> uuid.equals(e.getId()))
                    .findFirst()
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found for ID: " + id));
            logger.info("EmployeeServiceImpl :: end :: Fetched employee by ID: {}", emp);
            return emp;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID string: {}", id, e);
            throw new EmployeeNotFoundException("Invalid UUID string: " + id);
        } catch (EmployeeNotFoundException e) {
            logger.error("Employee not found: {}", id, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching employee by ID: {}", id, e);
            throw new EmployeeNotFoundException("Failed to fetch employee by ID: " + id);
        }
    }

    @Override
    public Integer gethighestSalaryOfEmployees() {
        logger.info("EmployeeServiceImpl :: start :: Fetching highest salary of employees");
        try {
            Integer highestSalary = allEmployees.stream()
                    .mapToInt(Employee::getSalary)
                    .max()
                    .orElseThrow(() -> new EmployeeNotFoundException("No employees found to determine highest salary"));
            logger.info("EmployeeServiceImpl :: end :: Highest salary of employees: {}", highestSalary);
            return highestSalary;
        } catch (Exception e) {
            logger.error("Error fetching highest salary of employees: {}", e.getMessage(), e);
            throw new EmployeeNotFoundException("Failed to fetch highest salary of employees: " + e.getMessage());
        }
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.info("EmployeeServiceImpl :: start :: Fetching top ten highest earning employee names");
        try {
            List<String> topTenEmployees = allEmployees.stream()
                    .sorted((e1, e2) -> e2.getSalary().compareTo(e1.getSalary()))
                    .limit(10)
                    .map(Employee::getName)
                    .toList();
            logger.info("EmployeeServiceImpl :: end :: Fetched top ten highest earning employee names: {}", topTenEmployees);
            return topTenEmployees;
        } catch (Exception e) {
            logger.error("Error fetching top ten highest earning employee names: {}", e.getMessage(), e);
            throw new EmployeeNotFoundException("Failed to fetch top ten highest earning employee names: " + e.getMessage());
        }
    }

    @Override
    public Employee createEmployee(EmployeeInput employeeInput) {
        logger.info("EmployeeServiceImpl :: start :: Creating new employee with input: {}", employeeInput);
        try {
            if (employeeInput == null) {
                throw new EmployeeNotFoundException("Employee input cannot be null");
            }
            Employee employee = new Employee();
            employee.setId(UUID.randomUUID());
            employee.setName(employeeInput.getName());
            employee.setSalary(employeeInput.getSalary());
            employee.setAge(employeeInput.getAge());
            employee.setTitle(employeeInput.getTitle());
            employee.setEmail(employeeInput.getEmail());

            allEmployees.add(employee);

            logger.info("EmployeeServiceImpl :: Created new employee: {}", employee);
            return employee;
        } catch (Exception e) {
            logger.error("Error creating employee: {}", e.getMessage(), e);
            throw new EmployeeNotFoundException("Failed to create employee: " + e.getMessage());
        }
    }

    @Override
    public String deleteEmployeeById(String id) {
        logger.info("EmployeeServiceImpl :: start :: Deleting employee by ID: {}", id);
        if (id == null || id.isBlank()) {
            throw new EmployeeNotFoundException("Invalid employee ID: " + id);
        }
        try {
            UUID uuid = UUID.fromString(id.trim());
            Employee employee = allEmployees.stream()
                    .filter(e -> uuid.equals(e.getId()))
                    .findFirst()
                    .orElse(null);
            if (employee != null) {
                allEmployees.remove(employee);
                logger.info("EmployeeServiceImpl :: Deleted employee: {}", employee);
                return employee.getName();
            } else {
                throw new EmployeeNotFoundException("Employee not found for ID: {}" + id);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid UUID string: {}", id, e);
            throw new EmployeeNotFoundException("Invalid UUID string: {}" + id);
        }
    }

}

