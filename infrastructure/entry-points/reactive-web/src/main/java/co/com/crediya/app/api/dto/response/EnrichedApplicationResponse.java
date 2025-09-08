package co.com.crediya.app.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichedApplicationResponse {

    private BigDecimal amount;
    private Integer term;
    private String email;
    private String fullName;
    private String loanTypeName;
    private BigDecimal interestRate;
    private String applicationState;
    private BigDecimal baseSalary;
    private BigDecimal monthlyPayment;
    private LocalDateTime creationDate;
}