package gr.knowledge.internship.introduction.entity;

import gr.knowledge.internship.introduction.enums.VacationStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vacation_request")
public class VacationRequest {

    @Id @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vacation_request_seq")
    @SequenceGenerator(name = "vacation_request_seq")
    private int id;

    @JoinColumn(name = "employee_id")@ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Employee employee;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull @Enumerated(EnumType.STRING)
    private VacationStatusEnum status;

    @NotNull
    private int days;
}
