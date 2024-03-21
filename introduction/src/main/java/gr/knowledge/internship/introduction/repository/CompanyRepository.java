package gr.knowledge.internship.introduction.repository;

import gr.knowledge.internship.introduction.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

}

