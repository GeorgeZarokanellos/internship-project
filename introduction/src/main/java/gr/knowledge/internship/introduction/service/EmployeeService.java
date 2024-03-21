package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.EmployeeDTO;
import gr.knowledge.internship.introduction.entity.Employee;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper){
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    public List<EmployeeDTO> getEmployees(){
        List<Employee> employeeList = employeeRepository.findAll();
        return modelMapper.map(employeeList, new TypeToken<List<EmployeeDTO>>(){}.getType());
    }

    public EmployeeDTO getEmployeeById(int id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee with id: " + id + " not found!"));
        return modelMapper.map(employee, new TypeToken<EmployeeDTO>(){}.getType());
    }
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO){
        employeeRepository.save(modelMapper.map(employeeDTO, Employee.class));
        return employeeDTO;
    }

    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO, int id){
        employeeDTO.setId(id);
        employeeRepository.save(modelMapper.map(employeeDTO, Employee.class));
        return employeeDTO;
    }

    public boolean deleteEmployee(int id){
        employeeRepository.deleteById(id);
        return true;
    }
}
