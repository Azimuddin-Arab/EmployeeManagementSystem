package com.example.demo.controller;

import com.example.demo.model.Department;
import com.example.demo.model.Employee;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EmployeeRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmployeeController {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // READ
    @GetMapping("/employees")
    public String listEmployees(Model model) {

        model.addAttribute(
                "employees",
                employeeRepository.findAll()
        );

        return "employees";
    }

    // CREATE - Show form
    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {

        model.addAttribute("employee", new Employee());

        model.addAttribute("departments", departmentRepository.findAll());

        return "employee-form";
    }


        // CREATE / UPDATE - Save Employee
        @PostMapping("/employees/save")
        public String saveEmployee(
                @ModelAttribute("employee") Employee employee,
                @RequestParam("departmentId") Long departmentId,
                Model model,
                RedirectAttributes redirectAttributes) {

            // Find the selected department in PostgreSQL
            var departmentOptional =
                    departmentRepository.findById(departmentId);

            if (departmentOptional.isEmpty()) {
                model.addAttribute("departments",
                        departmentRepository.findAll());

                model.addAttribute("error",
                        "Please select a valid department.");

                return "employee-form";
            }

            // Attach the selected department to the employee
            Department department = departmentOptional.get();
            employee.setDepartment(department);

            // Save new employee or update existing employee
            employeeRepository.save(employee);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Employee saved successfully!");

            return "redirect:/employees";
        }


    // UPDATE - Show edit form
    @GetMapping("/employees/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid employee ID: " + id
                        )
                );

        model.addAttribute("employee", employee);

        model.addAttribute("departments", departmentRepository.findAll());

        return "employee-form";
    }

    // DELETE
    @PostMapping("/employees/delete/{id}")
    public String deleteEmployee(
            @PathVariable Long id) {

        employeeRepository.deleteById(id);

        return "redirect:/employees";
    }
}