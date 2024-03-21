package gr.knowledge.internship.introduction.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bonus")
public class Bonus {

    @Id @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_bonus")
    @SequenceGenerator(name = "seq_bonus")
    private int id;

    @JoinColumn(name = "employee_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @JoinColumn(name = "company_id") @OneToOne(fetch = FetchType.LAZY)
    private Company company;

    @NotNull
    private double amount;

    public Bonus(Employee e, Company c, double amount){
        this.employee = e;
        this.company = c;
        this.amount = amount;
    }
}
