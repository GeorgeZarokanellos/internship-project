package gr.knowledge.internship.introduction.controller;

import gr.knowledge.internship.introduction.dto.EmployeeProductDTO;
import gr.knowledge.internship.introduction.service.EmployeeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employee-products")
@CrossOrigin
public class EmployeeProductController {
    private final EmployeeProductService employeeProductService;

    @Autowired
    public EmployeeProductController(EmployeeProductService employeeProductService){ 
        this.employeeProductService = employeeProductService; }

    @GetMapping("/")
    public List<EmployeeProductDTO> getEmployeeProducts(){
        return employeeProductService.getEmployeeProducts();
    }

    @GetMapping("/{id}")
    public EmployeeProductDTO getEmployeeById(@PathVariable int id) {
        return employeeProductService.getEmployeeProductById(id);
    }

    @PostMapping("/")
    public EmployeeProductDTO createEmployee(@RequestBody EmployeeProductDTO requestBody){
        return employeeProductService.createEmployeeProduct(requestBody);
    }

    @PutMapping("/{id}")
    public EmployeeProductDTO updateEmployee(@RequestBody EmployeeProductDTO employeeProductDTO,
                                             @PathVariable int id){
        return employeeProductService.updateEmployeeProduct(employeeProductDTO, id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteEmployee(@PathVariable int id){
        return employeeProductService.deleteEmployeeProduct(id);
    }
}