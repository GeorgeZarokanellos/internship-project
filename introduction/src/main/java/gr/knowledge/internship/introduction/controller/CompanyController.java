package gr.knowledge.internship.introduction.controller;

import gr.knowledge.internship.introduction.dto.BonusDTO;
import gr.knowledge.internship.introduction.dto.CompanyDTO;
import gr.knowledge.internship.introduction.dto.ProductDTO;
import gr.knowledge.internship.introduction.parameter.EmployeeBonusParameter;
import gr.knowledge.internship.introduction.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/companies")
@CrossOrigin
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    private CompanyController(CompanyService companyService){
        this.companyService = companyService;
    }

    @GetMapping
    public List<CompanyDTO> getCompanies(){
        return companyService.getCompanies();
    }

    @GetMapping("/{companyId}")
    public CompanyDTO getCompanyById(@PathVariable int companyId) { return companyService.getCompanyById(companyId); }

    @GetMapping("/{companyId}/monthlyExpenses")
    public double getCompanyMonthlyExpense(@PathVariable int companyId) {
        return companyService.calcMonthlyExpenses(companyId);
    }

    @GetMapping("/{companyId}/products")
    public Map<String, List<ProductDTO>> getCompanyProducts(@PathVariable int companyId){
        return companyService.getCompanyProducts(companyId);
    }

    @PostMapping
    public CompanyDTO createCompany(@RequestBody CompanyDTO companyDTO){
        return companyService.createCompany(companyDTO);
    }

    @PostMapping("/create-employee-bonus")
    public List<BonusDTO> createBonusForEmployees(@ModelAttribute EmployeeBonusParameter eBP){
        return companyService.addBonusToEachEmployee(eBP.getCompanyId(), eBP.getSeason());
    }

    @PutMapping("/{id}")
    public CompanyDTO updateCompany(@RequestBody CompanyDTO companyDTO, @PathVariable int id){
        return companyService.updateCompany(companyDTO, id);
    }

    @DeleteMapping("/{id}")
    public boolean deleteCompany(@PathVariable int id){
        return companyService.deleteCompany(id);
    }
}
