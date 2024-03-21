package gr.knowledge.internship.introduction.dto;

import gr.knowledge.internship.introduction.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private int id;
    private String name;
    private String surname;
    private String email;
    private LocalDate startDate;
    private int vacationDays;
    private float salary;
    private String employmentType;
    private CompanyDTO company;
}
