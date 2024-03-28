package gr.knowledge.internship.introduction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class Company {

    @Id @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_company")
    @SequenceGenerator(name = "seq_company", sequenceName = "seq_company")
    private int id;

    @NotNull @Size(max = 255)
    private String name;

    @NotNull @Size(max = 255)
    private String address;

    @NotNull @Size(max = 20)
    private String phone;
}
