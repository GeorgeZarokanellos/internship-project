package gr.knowledge.internship.introduction.service;

import gr.knowledge.internship.introduction.dto.BonusDTO;
import gr.knowledge.internship.introduction.dto.CompanyDTO;
import gr.knowledge.internship.introduction.dto.EmployeeProductDTO;
import gr.knowledge.internship.introduction.dto.ProductDTO;
import gr.knowledge.internship.introduction.entity.*;
import gr.knowledge.internship.introduction.repository.BonusRepository;
import gr.knowledge.internship.introduction.repository.CompanyRepository;
import gr.knowledge.internship.introduction.repository.EmployeeProductRepository;
import gr.knowledge.internship.introduction.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final BonusRepository bonusRepository;
    private final EmployeeProductRepository employeeProductRepository;
    private final BonusService bonusService;
    private final ModelMapper modelMapper;

    @Autowired
    public CompanyService(CompanyRepository companyRepository,
                          EmployeeRepository employeeRepository,
                          BonusRepository bonusRepository,
                          EmployeeProductRepository employeeProductRepository,
                          BonusService bonusService,
                          ModelMapper modelMapper)
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

    public Map<String, List<ProductDTO>> getCompanyProducts(int companyId){
        //retrieve all the employee-product entities
        List<EmployeeProductDTO> employeeProductList = modelMapper.map(employeeProductRepository.findAll(), new TypeToken<List<EmployeeProductDTO>>(){}.getType());
        //filter the list to get the employee-product entities of the specific company
        return employeeProductList.stream().filter(ep -> ep.getEmployee().getCompany().getId() == companyId)
                //group the products by the concatenated name and surname of the employee
                .collect(Collectors.groupingBy(ep -> ep.getEmployee().getName().concat(ep.getEmployee().getSurname()),
                        //map the products to a list where the key is the name and surname of the employee and the value is the list of products
                        Collectors.mapping(EmployeeProductDTO::getProduct, Collectors.toList())));
    }
}
