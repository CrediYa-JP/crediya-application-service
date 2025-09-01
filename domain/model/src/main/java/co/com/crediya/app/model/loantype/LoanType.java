package co.com.crediya.app.model.loantype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanType {
    private Long loanTypeId;
    private String name;
    private BigDecimal minimumAmount;
    private BigDecimal maximumAmount;
    private BigDecimal interestRate;
    private Boolean automaticValidation;
}