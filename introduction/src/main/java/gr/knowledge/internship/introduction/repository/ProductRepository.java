package gr.knowledge.internship.introduction.repository;

import gr.knowledge.internship.introduction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
