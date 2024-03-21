package gr.knowledge.internship.introduction.repository;

import gr.knowledge.internship.introduction.entity.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BonusRepository extends JpaRepository<Bonus, Integer> {

}
