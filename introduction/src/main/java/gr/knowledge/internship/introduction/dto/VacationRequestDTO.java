package gr.knowledge.internship.introduction.dto;

import gr.knowledge.internship.introduction.enums.VacationStatusEnum;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VacationRequestDTO {
    private int id;
    private EmployeeDTO employee;
    private LocalDate startDate;
    private LocalDate endDate;
    private int days;
    private VacationStatusEnum status;

}
