package gr.knowledge.internship.introduction.repository;

import gr.knowledge.internship.introduction.entity.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacationRequestRepository extends JpaRepository<VacationRequest, Integer> {
}
