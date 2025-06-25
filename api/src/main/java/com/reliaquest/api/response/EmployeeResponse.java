// File: api/src/main/java/com/reliaquest/api/model/EmployeeResponse.java
package com.reliaquest.api.response;

import com.reliaquest.api.model.Employee;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
public class EmployeeResponse {

    private List<Employee> data;
    private String status;

}