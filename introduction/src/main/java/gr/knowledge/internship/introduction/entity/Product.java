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
@Table(name = "product")
public class Product {

    @Id @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    @SequenceGenerator(name = "product_seq")
    private int id;

    @Size(max = 255) @NotNull
    private String name;

    @Size(max = 1000) @NotNull
    private String description;

    @Size(max = 255) @NotNull
    private String barcode;
}
