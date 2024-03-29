package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.EmployeeProductDTO;
import gr.knowledge.internship.introduction.entity.EmployeeProduct;
import gr.knowledge.internship.introduction.repository.EmployeeProductRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeProductService {
    private final EmployeeProductRepository employeeProductRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeProductService(EmployeeProductRepository employeeProductRepository, ModelMapper modelMapper){
        this.employeeProductRepository = employeeProductRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<EmployeeProductDTO> getEmployeeProducts(){
        List<EmployeeProduct> employeeProductList = employeeProductRepository.findAll();
        return modelMapper.map(employeeProductList, new TypeToken<List<EmployeeProductDTO>>(){}.getType());
    }

    @Transactional(readOnly = true)
    public EmployeeProductDTO getEmployeeProductById(int id){
        EmployeeProduct employeeProduct = employeeProductRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee product with id: " + id + " not found"));
        return modelMapper.map(employeeProduct, new TypeToken<EmployeeProductDTO>(){}.getType());
    }

    public EmployeeProductDTO createEmployeeProduct(EmployeeProductDTO employeeProductDTO){
        employeeProductRepository.save(modelMapper.map(employeeProductDTO, EmployeeProduct.class));
        return employeeProductDTO;
    }

    public EmployeeProductDTO updateEmployeeProduct(EmployeeProductDTO employeeProductDTO, int id){
        employeeProductDTO.setId(id);
        employeeProductRepository.save(modelMapper.map(employeeProductDTO, EmployeeProduct.class));
        return employeeProductDTO;
    }
    public boolean deleteEmployeeProduct(int id){
        employeeProductRepository.deleteById(id);
        return true;
    }
}
