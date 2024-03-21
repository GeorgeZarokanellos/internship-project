package gr.knowledge.internship.introduction.repository;

import gr.knowledge.internship.introduction.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    public List<Employee> findByCompanyId(int companyId);

}
