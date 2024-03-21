package gr.knowledge.internship.introduction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProductDTO {
    private int id;
    private EmployeeDTO employee;
    private ProductDTO product;
}
