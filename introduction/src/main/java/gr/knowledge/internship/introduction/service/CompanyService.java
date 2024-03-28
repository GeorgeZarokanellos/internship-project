package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.*;
import gr.knowledge.internship.introduction.entity.*;
import gr.knowledge.internship.introduction.repository.BonusRepository;
import gr.knowledge.internship.introduction.repository.CompanyRepository;
import gr.knowledge.internship.introduction.repository.EmployeeProductRepository;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This service class is responsible for handling operations related to companies.
 * It includes methods for retrieving, creating, updating, and deleting companies, 
 * as well as calculating monthly expenses and adding bonuses to each employee of a company.
 */

@Service
@Transactional
@Log4j2
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final BonusRepository bonusRepository;
    private final EmployeeProductRepository employeeProductRepository;
    private final BonusService bonusService;
    private final ModelMapper modelMapper;

    @Autowired
    public CompanyService(CompanyRepository companyRepository,EmployeeRepository employeeRepository,
                          BonusRepository bonusRepository,EmployeeProductRepository employeeProductRepository,
                          BonusService bonusService,ModelMapper modelMapper)
    {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.employeeProductRepository = employeeProductRepository;
        this.bonusRepository = bonusRepository;
        this.bonusService = bonusService;
        this.modelMapper = modelMapper;
    }

    public List<CompanyDTO> getCompanies(){
        List<Company> companyList = companyRepository.findAll();
        return modelMapper.map(companyList, new TypeToken<List<CompanyDTO>>(){}.getType());
    }

    public CompanyDTO getCompanyById(int companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new EntityNotFoundException("Company with id: " + companyId + " not found!"));
        return modelMapper.map(company, new TypeToken<CompanyDTO>(){}.getType());
    }

    public Map<String, List<ProductDTO>> getCompanyProducts(int companyId){
        //retrieve all the employee-product entities
        Map<String, List<ProductDTO>> employeeProductMap = new HashMap<>();
        List<EmployeeProductDTO> employeeProductList = modelMapper.map(employeeProductRepository.findAll(), new TypeToken<List<EmployeeProductDTO>>(){}.getType());
        List<EmployeeProductDTO> companySpecificProducts = new ArrayList<>();
        List<ProductDTO> employeeSpecificProducts = new ArrayList<>();
        Set<EmployeeDTO> employees = new HashSet<>();
        retrieveCompanySpecificEmployeeProducts(employeeProductList,companySpecificProducts, companyId, employees);
        return retrieveEmployeeSpecificProducts(employees, companySpecificProducts, employeeSpecificProducts, employeeProductMap);

    }

    public CompanyDTO createCompany(CompanyDTO companyDTO){
        companyRepository.save(modelMapper.map(companyDTO, Company.class));
        return companyDTO;
    }

    public CompanyDTO updateCompany(CompanyDTO companyDTO, int id){
        companyDTO.setId(id);
        companyRepository.save(modelMapper.map(companyDTO, Company.class));
        return companyDTO;
    }
    public boolean deleteCompany(int id){
        companyRepository.deleteById(id);
        return true;
    }

    public double calcMonthlyExpenses(int companyId){
        //retrieve all employees of the company
        List<Employee> employeeList = employeeRepository.findByCompanyId(companyId);
        double monthlyExpense = 0;
        //iterate through the employees and sum their salaries
        for(Employee employee: employeeList){
            monthlyExpense += employee.getSalary();
        }
        return monthlyExpense;
    }

    public List<BonusDTO> addBonusToEachEmployee(int companyId, String season){
        double bonusAmount;
        //retrieve the company entity
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company with id: " + companyId + " not found!"));

        List<Bonus> employeeBonusList = new ArrayList<>();
        List<Employee> employeeList = employeeRepository.findByCompanyId(companyId);
        //iterate through the employees and calculate the bonus amount
        for (Employee employee: employeeList){
            bonusAmount = bonusService.calcBonus(employee.getSalary(), season);
            //create new bonus for each employee
            Bonus bonus = new Bonus(employee, company, bonusAmount);
            //add it to the list
            employeeBonusList.add(bonus);
        }
        //save all the bonuses
        return modelMapper.map(bonusRepository.saveAll(employeeBonusList), new TypeToken<List<BonusDTO>>(){}.getType());
    }

    
    private void retrieveCompanySpecificEmployeeProducts(List<EmployeeProductDTO> epList,List<EmployeeProductDTO> companySpecificList,
                                                         int companyId, Set<EmployeeDTO> employees){
        for(EmployeeProductDTO epDTO: epList){
            if(epDTO.getEmployee().getCompany().getId() == companyId) {
                companySpecificList.add(epDTO);
                employees.add(epDTO.getEmployee());
            }
        }
        log.info("from company specific " + companySpecificList.toString());
    }
    
    private Map<String, List<ProductDTO>> retrieveEmployeeSpecificProducts(Set<EmployeeDTO> employees, List<EmployeeProductDTO> companySpecificProducts,
                                                                           List<ProductDTO> employeeSpecificProducts, Map<String, List<ProductDTO>> employeeProductMap){
        log.info("from retrieve employee specific " + companySpecificProducts.toString());
        for(EmployeeDTO employee: employees){
            for(EmployeeProductDTO epDTO: companySpecificProducts){
                if(epDTO.getEmployee().getId() == employee.getId()){
                    employeeSpecificProducts.add(epDTO.getProduct());
                }
            }
            log.info("Before adding employee specific to map " + employeeSpecificProducts.toString());
            employeeProductMap.put(employee.getName().concat(employee.getSurname()), new ArrayList<>(employeeSpecificProducts));
            log.info("Map after adding the employee" + employeeProductMap);
            employeeSpecificProducts.clear();
        }
        log.info(employeeProductMap.toString());
        return employeeProductMap;
    }
}


