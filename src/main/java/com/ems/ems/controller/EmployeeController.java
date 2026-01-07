package com.ems.ems.controller;

import com.ems.ems.dto.EmployeeDto;
import com.ems.ems.entity.Employee;
import com.ems.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @PostMapping("/add")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto){
        EmployeeDto savedEmployee = employeeService.saveEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployee,HttpStatus.CREATED);
    }

    @GetMapping("getAll")
    public ResponseEntity<List<EmployeeDto>> getAll(){
        List<EmployeeDto> employees = employeeService.getEmployees();
        return new ResponseEntity<>(employees,HttpStatus.OK);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id){
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employeeDto,HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id ,@Valid @RequestBody Employee employee){
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id,employee);
        return new ResponseEntity<>(updatedEmployee,HttpStatus.OK);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id){
        employeeService.deleteByid(id);
        return new ResponseEntity<>("Employee delated with the ID "+id,HttpStatus.OK);

    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeDto>> searchEmployees(@RequestParam(required = false) String name,
                                                             @RequestParam(required = false) String department) {
        if (name != null) {
            return ResponseEntity.ok(employeeService.searchByName(name));
        } else if (department != null) {
            return ResponseEntity.ok(employeeService.searchByDepartment(department));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/pagination")
    public ResponseEntity<Page<EmployeeDto>> getEmployeesWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(employeeService.getEmployeesWithPagination(page, size, sortField, sortDirection));
    }



}
