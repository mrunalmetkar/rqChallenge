package com.reliaquest.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class EmployeeInput extends Object{


    private String name;
    private Integer salary;

    private Integer age;

    private String title;

    private String email;
}
