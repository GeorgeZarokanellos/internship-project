package gr.knowledge.internship.introduction.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProductDTO {
    private int id;
    private EmployeeDTO employee;
    private ProductDTO product;
}
