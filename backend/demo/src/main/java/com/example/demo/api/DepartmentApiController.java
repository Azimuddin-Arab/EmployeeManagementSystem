
package com.example.demo.api;

import com.example.demo.model.Department;
import com.example.demo.repository.DepartmentRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "http://localhost:5173")
public class DepartmentApiController {

    private final DepartmentRepository departmentRepository;

    public DepartmentApiController(
            DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // GET /api/departments?search=IT&page=0&size=10
    // Search and paginate departments
    @GetMapping
    public Page<Department> getDepartments(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Validate pagination parameters
        if (size < 1) {
            size = 10;
        }

        if (page < 0) {
            page = 0;
        }

        // Create pagination settings
        Pageable pageable = PageRequest.of(page, size);

        // Return all departments when search is empty
        if (search == null || search.isBlank()) {
            return departmentRepository.findAll(pageable);
        }

        // Search by department name
        return departmentRepository.findByNameContainingIgnoreCase(
                search.trim(),
                pageable
        );
    }

    // GET /api/departments/{id}
    @GetMapping("/{id}")
    public Department getDepartment(@PathVariable Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Department not found: " + id
                        ));
    }

    // POST /api/departments
    @PostMapping
    public Department createDepartment(
            @RequestBody Department department) {
        return departmentRepository.save(department);
    }

    // PUT /api/departments/{id}
    @PutMapping("/{id}")
    public Department updateDepartment(
            @PathVariable Long id,
            @RequestBody Department request) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Department not found: " + id
                        ));

        department.setName(request.getName());

        return departmentRepository.save(department);
    }

    // DELETE /api/departments/{id}
    @DeleteMapping("/{id}")
    public void deleteDepartment(@PathVariable Long id) {
        departmentRepository.deleteById(id);
    }
}
