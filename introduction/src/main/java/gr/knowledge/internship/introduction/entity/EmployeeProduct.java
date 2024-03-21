package gr.knowledge.internship.introduction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee_product")
public class EmployeeProduct {

    @Id @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_product_seq")
    @SequenceGenerator(name = "employee_product_seq")
    private int id;

    @JoinColumn(name = "employee_id") @ManyToOne
    private Employee employee;

    @JoinColumn(name = "product_id") @ManyToOne
    private Product product;
}
