package gr.knowledge.internship.introduction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BonusDTO {
    private int id;
    private int employeeId;
    private int companyId;
    private double amount;
}
