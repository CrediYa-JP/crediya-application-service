package co.com.crediya.app.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterLoanApplicationRequest {

    @NotBlank(message = "Identity document is required")
    private String identityDocument;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0", message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer term;

    @NotNull(message = "Loan type is required")
    private Long loanTypeId;
}