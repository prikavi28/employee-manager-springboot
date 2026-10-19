package com.example.employee_manager.controller;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public String listEmployees(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("employees", service.getAllEmployees(keyword));
        model.addAttribute("employee", new Employee());
        model.addAttribute("keyword", keyword);
        return "employees";
    }

    @PostMapping
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee, 
                               BindingResult result, Model model) {
        if (service.existsByEmail(employee.getEmail())) {
            result.rejectValue("email", "error.employee", "This email is already registered");
        }
        if (result.hasErrors()) {
            model.addAttribute("employees", service.getAllEmployees(null));
            return "employees";
        }
        service.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = service.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "edit-employee";
    }

    @PostMapping("/{id}/update")
    public String updateEmployee(@PathVariable Long id, 
                                 @Valid @ModelAttribute("employee") Employee employee, 
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "edit-employee";
        }
        employee.setId(id);
        service.saveEmployee(employee);
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id) {
        service.deleteEmployee(id);
        return "redirect:/employees";
    }
}