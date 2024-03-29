package gr.knowledge.internship.introduction.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
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
