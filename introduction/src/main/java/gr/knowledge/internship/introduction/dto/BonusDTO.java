package gr.knowledge.internship.introduction.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BonusDTO {
    private int id;
    private int employeeId;
    private int companyId;
    private double amount;
}
