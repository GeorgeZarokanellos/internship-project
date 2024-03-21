package gr.knowledge.internship.introduction.repository;

import gr.knowledge.internship.introduction.entity.EmployeeProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeProductRepository extends JpaRepository<EmployeeProduct, Integer> {
    public List<EmployeeProduct> findByEmployeeId(int employeeId);
}
