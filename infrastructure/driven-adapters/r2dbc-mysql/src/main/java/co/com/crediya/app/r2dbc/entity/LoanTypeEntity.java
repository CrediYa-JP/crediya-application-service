package co.com.crediya.app.r2dbc.entity;

import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loan_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanTypeEntity {

    @Id
    @Column(name = "loan_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanTypeId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "minimum_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal minimumAmount;

    @Column(name = "maximum_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal maximumAmount;

    @Column(name = "interest_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal interestRate;

    @Column(name = "automatic_validation", nullable = false)
    private Boolean automaticValidation;
}
