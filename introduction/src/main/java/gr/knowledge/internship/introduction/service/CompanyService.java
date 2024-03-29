package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.*;
import gr.knowledge.internship.introduction.entity.*;
import gr.knowledge.internship.introduction.repository.BonusRepository;
import gr.knowledge.internship.introduction.repository.CompanyRepository;
import gr.knowledge.internship.introduction.repository.EmployeeProductRepository;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Constructor for CompanyService.
     *
     * @param companyRepository Repository for companies.
     * @param employeeRepository Repository for employees.
     * @param bonusRepository Repository for bonuses.
     * @param employeeProductRepository Repository for employee-product entities.
     * @param bonusService Service for calculating bonuses.
     * @param modelMapper Model mapper for converting between DTOs and entities.
     */
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

    /**
     * Retrieves all companies.
     *
     * @return List of CompanyDTOs.
     */
    @Transactional(readOnly = true)
    public List<CompanyDTO> getCompanies(){
        List<Company> companyList = companyRepository.findAll();
        return modelMapper.map(companyList, new TypeToken<List<CompanyDTO>>(){}.getType());
    }

    /**
     * Retrieves a company by its id.
     *
     * @param companyId Id of the company.
     * @return CompanyDTO.
     */
    @Transactional(readOnly = true)
    public CompanyDTO getCompanyById(int companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new EntityNotFoundException("Company with id: " + companyId + " not found!"));
        return modelMapper.map(company, new TypeToken<CompanyDTO>(){}.getType());
    }

    /**
     * Retrieves all employees of a company along with the products they are responsible for.
     *
     * @param companyId Id of the company.
     * @return Map of EmployeeMapDTOs and List of ProductDTOs.
     */
    @Transactional(readOnly = true)
    public Map<EmployeeMapDTO, List<ProductDTO>> getCompanyProducts(int companyId){
        //retrieve all the employee-product entities
        Map<EmployeeMapDTO, List<ProductDTO>> employeeProductMap = new HashMap<>();
        List<EmployeeProductDTO> employeeProductList = modelMapper.map(employeeProductRepository.findAll(), new TypeToken<List<EmployeeProductDTO>>(){}.getType());
        List<EmployeeProductDTO> companySpecificProducts = new ArrayList<>();
        List<ProductDTO> employeeSpecificProducts = new ArrayList<>();
        Set<EmployeeMapDTO> employees = new HashSet<>();
        retrieveCompanySpecificEmployeeProducts(employeeProductList,companySpecificProducts, companyId, employees);
        return retrieveEmployeeSpecificProducts(employees, companySpecificProducts, employeeSpecificProducts, employeeProductMap);

    }

    /**
     * Creates a company.
     *
     * @param companyDTO CompanyDTO to be created.
     * @return CompanyDTO.
     */
    public CompanyDTO createCompany(CompanyDTO companyDTO){
        companyRepository.save(modelMapper.map(companyDTO, Company.class));
        return companyDTO;
    }

    /**
     * Updates a company.
     *
     * @param companyDTO CompanyDTO to be updated.
     * @param id Id of the company.
     * @return CompanyDTO.
     */
    public CompanyDTO updateCompany(CompanyDTO companyDTO, int id){
        companyDTO.setId(id);
        companyRepository.save(modelMapper.map(companyDTO, Company.class));
        return companyDTO;
    }

    /**
     * Deletes a company.
     *
     * @param id Id of the company.
     * @return boolean.
     */
    public boolean deleteCompany(int id){
        companyRepository.deleteById(id);
        return true;
    }

    /**
     * Calculates the monthly expenses of a company.
     *
     * @param companyId Id of the company.
     * @return double.
     */
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

    /**
     * Adds a bonus to each employee of a company.
     *
     * @param companyId Id of the company.
     * @param season Season for which the bonus is calculated.
     * @return List of BonusDTOs.
     */
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

    /**
     * Retrieves all bonuses of a company.
     *
     * @param companyId Id of the company.
     * @return List of BonusDTOs.
     */
    private void retrieveCompanySpecificEmployeeProducts(List<EmployeeProductDTO> epList,List<EmployeeProductDTO> companySpecificList,
                                                         int companyId, Set<EmployeeMapDTO> employees){
        for(EmployeeProductDTO epDTO: epList){
            if(epDTO.getEmployee().getCompany().getId() == companyId) {
                companySpecificList.add(epDTO);
                employees.add(new EmployeeMapDTO(epDTO.getEmployee().getId(), epDTO.getEmployee().getName(), epDTO.getEmployee().getSurname()));
            }
        }
        log.info("from company specific " + companySpecificList.toString());
    }

    /**
     * Retrieves the products that each employee of a company is responsible for.
     *
     * @param employees Set of EmployeeMapDTOs.
     * @param companySpecificProducts List of EmployeeProductDTOs.
     * @param employeeSpecificProducts List of ProductDTOs.
     * @param employeeProductMap Map of EmployeeMapDTOs and List of ProductDTOs.
     * @return Map of EmployeeMapDTOs and List of ProductDTOs.
     */
    private Map<EmployeeMapDTO, List<ProductDTO>> retrieveEmployeeSpecificProducts(Set<EmployeeMapDTO> employees, List<EmployeeProductDTO> companySpecificProducts,
                                                                           List<ProductDTO> employeeSpecificProducts, Map<EmployeeMapDTO, List<ProductDTO>> employeeProductMap){
        log.info("from retrieve employee specific " + companySpecificProducts.toString());
        for(EmployeeMapDTO employee: employees){
            for(EmployeeProductDTO epDTO: companySpecificProducts){
                if(epDTO.getEmployee().getId() == employee.getEmployeeId()){
                    employeeSpecificProducts.add(epDTO.getProduct());
                }
            }
            employeeProductMap.put(employee, new ArrayList<>(employeeSpecificProducts));
            employeeSpecificProducts.clear();
        }
        log.info("Employee product map: " + employeeProductMap.toString());
        return employeeProductMap;
    }
}


