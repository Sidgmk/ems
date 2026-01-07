package com.ems.ems.repository;

import com.ems.ems.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    List<Employee> findByNameContainingIgnoreCase(String name);
    List<Employee> findByDepartmentContainingIgnoreCase(String department);
}
