package co.com.crediya.app.model.capacityevaluation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapacityEvaluationMessage {
    private Long applicationId;
    private String userIdentityDocument;
    private BigDecimal userBaseSalary;
    private BigDecimal currentMonthlyDebt;
    private BigDecimal newLoanAmount;
    private Integer newLoanTerm;
    private BigDecimal newLoanInterestRate;
}