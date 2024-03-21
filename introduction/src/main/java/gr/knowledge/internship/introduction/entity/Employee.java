package gr.knowledge.internship.introduction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.SpringVersion;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
public class Employee {

    @Id @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_employee")
    @SequenceGenerator(name = "seq_employee", sequenceName = "seq_employee")
    @Column(name = "id")
    private int id;

    @Size(max = 255) @NotNull
    private String name;

    @Size(max = 255) @NotNull
    private String surname;

    @Size(max = 255) @NotNull @Email
    private String email;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private int vacationDays;

    @NotNull
    private double salary;

    @Size(max = 20) @NotNull
    private String employmentType;

    @JoinColumn(name = "company_id") @OneToOne(fetch = FetchType.LAZY)
    private Company company;
}

