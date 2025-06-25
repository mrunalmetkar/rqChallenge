package com.reliaquest.api.impl;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.EmployeeInput;
import com.reliaquest.api.response.EmployeeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService.mockApiUrl = "http://localhost:8112/api/v1/employee";
        employee1 = new Employee();
        employee1.setId(UUID.randomUUID());
        employee1.setName("John Doe");
        employee1.setSalary(1000);
        employee1.setAge(30);
        employee1.setTitle("Engineer");
        employee1.setEmail("john@example.com");

        employee2 = new Employee();
        employee2.setId(UUID.randomUUID());
        employee2.setName("Jane Smith");
        employee2.setSalary(2000);
        employee2.setAge(28);
        employee2.setTitle("Manager");
        employee2.setEmail("jane@example.com");

        employeeService.allEmployees = new ArrayList<>(List.of(employee1, employee2));
    }

    @Test
    void testGetAllEmployees() {
        EmployeeResponse response = new EmployeeResponse();
        response.setData(List.of(employee1, employee2));
        ResponseEntity<EmployeeResponse> entity = new ResponseEntity<>(response, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(employeeService.mockApiUrl),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<EmployeeResponse>>any()
        )).thenReturn(entity);

        List<Employee> result = employeeService.getAllEmployees();
        assertEquals(50, result.size());

    }

    @Test
    void testFindByNameContaining() {
        List<Employee> result = employeeService.findByNameContaining("Jane");
        assertEquals(1, result.size());
        assertEquals("Jane Smith", result.get(0).getName());

        result = employeeService.findByNameContaining("doe");
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());

        result = employeeService.findByNameContaining("");
        assertEquals(2, result.size());
    }

    @Test
    void testGetEmployeeById_Success() {
        Employee found = employeeService.getEmployeeById(employee1.getId().toString());
        assertEquals(employee1.getName(), found.getName());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        String randomId = UUID.randomUUID().toString();
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(randomId));
    }

    @Test
    void testGetEmployeeById_InvalidUUID() {
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById("invalid-uuid"));
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        Integer highest = employeeService.gethighestSalaryOfEmployees();
        assertEquals(2000, highest);
    }

    @Test
    void testGetHighestSalaryOfEmployees_NoEmployees() {
        employeeService.allEmployees.clear();
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.gethighestSalaryOfEmployees());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        List<String> top = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(2, top.size());
        assertEquals("Jane Smith", top.get(0));
    }

    @Test
    void testCreateEmployee() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Alice");
        input.setSalary(1500);
        input.setAge(25);
        input.setTitle("Analyst");
        input.setEmail("alice@example.com");

        Employee created = employeeService.createEmployee(input);
        assertEquals("Alice", created.getName());
        assertTrue(employeeService.allEmployees.contains(created));
    }

    @Test
    void testCreateEmployee_NullInput() {
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.createEmployee(null));
    }

    @Test
    void testDeleteEmployeeById_Success() {
        String name = employeeService.deleteEmployeeById(employee1.getId().toString());
        assertEquals(employee1.getName(), name);
        assertFalse(employeeService.allEmployees.contains(employee1));
    }

    @Test
    void testDeleteEmployeeById_NotFound() {
        String randomId = UUID.randomUUID().toString();
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployeeById(randomId));
    }

    @Test
    void testDeleteEmployeeById_InvalidUUID() {
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployeeById("bad-uuid"));
    }
}