package com.ems.ems.service.impl;

import com.ems.ems.dto.EmployeeDto;
import com.ems.ems.entity.Employee;
import com.ems.ems.exception.ResourceNotFoundException;
import com.ems.ems.repository.EmployeeRepository;
import com.ems.ems.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }


    public EmployeeDto convertToDto(Employee employee){
        return modelMapper.map(employee,EmployeeDto.class);

    }
    public Employee convertToEntity(EmployeeDto employeeDto){
        return modelMapper.map(employeeDto,Employee.class);
    }
    @Override
    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        Employee employee = convertToEntity(employeeDto);
        Employee saved = employeeRepository.save(employee);
        return convertToDto(saved);
    }

    @Override
    public List<EmployeeDto> getEmployees() {
       return employeeRepository.findAll()
               .stream()
               .map(this::convertToDto)
               .collect(Collectors.toList());
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
       Employee employee =  employeeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("emp id not found"));
       return convertToDto(employee);
    }

    @Override
    public EmployeeDto updateEmployee(Long id, Employee employee) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Employee not found with id "+id));

        //update feilds
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setName(employee.getName());
        existingEmployee.setSalary(employee.getSalary());

        Employee upadated = employeeRepository.save(existingEmployee);
        return convertToDto(upadated);
    }

    @Override
    public void deleteByid(Long id) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Data not found for given id "+id));

        employeeRepository.deleteById(id);
    }

    @Override
    public List<EmployeeDto> searchByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> searchByDepartment(String name) {
        return employeeRepository.findByDepartmentContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EmployeeDto> getEmployeesWithPagination(int page, int size, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return employeeRepository.findAll(pageable)
                .map(this::convertToDto);
    }
}
