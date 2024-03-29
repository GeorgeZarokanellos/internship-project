package gr.knowledge.internship.introduction.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeMapDTO {
    private int employeeId;
    private String name;
    private String surname;

    @Override
    public String toString() {
        return this.getName().concat(this.getSurname());
    }
}
