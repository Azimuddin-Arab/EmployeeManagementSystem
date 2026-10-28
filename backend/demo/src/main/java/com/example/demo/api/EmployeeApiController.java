package com.example.demo.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Department;
import com.example.demo.model.Employee;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EmployeeRepository;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeApiController {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeApiController(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // GET /api/employees
    // Example:
    // /api/employees?page=0&size=10
    // /api/employees?search=ahmad&page=0&size=10
    @GetMapping
    public Page<Employee> getEmployees(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (size < 1) {
            size = 10;
        }

        if (page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.isBlank()) {
            return employeeRepository.findAll(pageable);
        }

        return employeeRepository.findByFullNameContainingIgnoreCase(
                search.trim(),
                pageable
        );
    }

    // GET /api/employees/{id}
    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {

        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found: " + id
                        ));
    }

    // POST /api/employees
    @PostMapping
    public Employee createEmployee(
            @RequestBody EmployeeRequest request) {

        Department department =
                departmentRepository.findById(request.departmentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Department not found: "
                                                + request.departmentId()
                                ));

        Employee employee = new Employee();

        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setPosition(request.position());
        employee.setDepartment(department);

        return employeeRepository.save(employee);
    }

    // PUT /api/employees/{id}
    @PutMapping("/{id}")
    public Employee updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeRequest request) {

        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Employee not found: " + id
                                ));

        Department department =
                departmentRepository.findById(request.departmentId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Department not found: "
                                                + request.departmentId()
                                ));

        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setPosition(request.position());
        employee.setDepartment(department);

        return employeeRepository.save(employee);
    }

    // DELETE /api/employees/{id}
    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {

        employeeRepository.deleteById(id);
    }

    public record EmployeeRequest(
            String fullName,
            String email,
            String position,
            Long departmentId
    ) {
    }
}
