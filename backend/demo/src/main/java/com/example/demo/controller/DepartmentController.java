package com.example.demo.controller;

import com.example.demo.model.Department;
import com.example.demo.repository.DepartmentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(
            DepartmentRepository departmentRepository) {

        this.departmentRepository = departmentRepository;
    }

    // Department List
    @GetMapping("/departments")
    public String listDepartments(Model model) {

        model.addAttribute(
                "departments",
                departmentRepository.findAll()
        );

        return "departments";
    }

    // Add Department Form
    @GetMapping("/departments/new")
    public String showCreateForm(Model model) {

        model.addAttribute(
                "department",
                new Department()
        );

        return "department-form";
    }

    // Save Department
    @PostMapping("/departments/save")
    public String saveDepartment(
            @ModelAttribute("department")
            Department department) {

        departmentRepository.save(department);

        return "redirect:/departments";
    }

    // Edit Department
    @GetMapping("/departments/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        Department department =
                departmentRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid department ID: " + id
                                )
                        );

        model.addAttribute(
                "department",
                department
        );

        return "department-form";
    }

    // Delete Department
    @PostMapping("/departments/delete/{id}")
    public String deleteDepartment(
            @PathVariable Long id) {

        departmentRepository.deleteById(id);

        return "redirect:/departments";
    }
}