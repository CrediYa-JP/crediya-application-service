package co.com.crediya.app.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Identity document number", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Identity document is required")
    private String identityDocument;

    @Schema(description = "Loan amount", example = "1000000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0", message = "Amount must be positive")
    private BigDecimal amount;

    @Schema(description = "Loan term in months", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer term;

    @Schema(description = "Loan type ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Loan type is required")
    private Long loanTypeId;
}