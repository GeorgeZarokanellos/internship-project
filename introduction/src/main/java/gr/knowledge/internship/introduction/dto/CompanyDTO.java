package gr.knowledge.internship.introduction.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {
    private int id;
    private String name;
    private String address;
    private String phone;
}
