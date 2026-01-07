package com.ems.ems.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @Email(message = "provided email is invalid ")
    private String email;
    @NotBlank(message = "Department can't be blank")
    private String department;
//    private Double salary;  // optional include or remove if needed
}
