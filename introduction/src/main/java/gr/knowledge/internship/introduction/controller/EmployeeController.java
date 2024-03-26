package gr.knowledge.internship.introduction.controller;

import gr.knowledge.internship.introduction.dto.EmployeeDTO;
import gr.knowledge.internship.introduction.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employees")
@CrossOrigin
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService){ this.employeeService = employeeService; }

    @GetMapping
    public List<EmployeeDTO> getEmployees(){
        return employeeService.getEmployees();
    }

    @GetMapping("/{employeeId}")
    public EmployeeDTO getEmployeeById(@PathVariable int employeeId) {
        return employeeService.getEmployeeById(employeeId);
    }

    @PostMapping
    public EmployeeDTO createEmployee(@RequestBody EmployeeDTO requestBody){
        return employeeService.createEmployee(requestBody);
    }

    @PutMapping("/{id}")
    public EmployeeDTO updateEmployee(@RequestBody EmployeeDTO requestBody, @PathVariable int id){
        return employeeService.updateEmployee(requestBody, id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteEmployee(@PathVariable int id){
        return employeeService.deleteEmployee(id);
    }
}
